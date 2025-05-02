package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.SituacaoMarcaFogo;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class MarcaFogoFacade extends AbstractFacade<MarcaFogo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroMarcaFogoFacade parametroMarcaFogoFacade;
    @EJB
    private CalculoTaxasDiversasFacade calculoTaxasDiversasFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private DAMFacade damFacade;

    public MarcaFogoFacade() {
        super(MarcaFogo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(MarcaFogo entidade) {
        entidade.realizarValidacoes();
        if (entidade.getId() == null) {
            entidade.setCodigo(singletonGeradorCodigo.getProximoCodigo(MarcaFogo.class, "codigo"));
        }
    }

    @Override
    public MarcaFogo recuperar(Object id) {
        MarcaFogo marcaFogo = super.recuperar(id);
        if (marcaFogo != null) {
            if (marcaFogo.getLogo() != null) {
                Hibernate.initialize(marcaFogo.getLogo().getPartes());
            }
            Hibernate.initialize(marcaFogo.getAnexos());
            Hibernate.initialize(marcaFogo.getDebitos());
            Hibernate.initialize(marcaFogo.getCadastrosRurais());
            recuperarDebitos(marcaFogo);
            marcaFogo.setTemProcurador(marcaFogo.getProcurador() != null);
        }
        return marcaFogo;
    }

    private void recuperarDebitos(MarcaFogo marcaFogo) {
        if (marcaFogo.getDebitos() == null) return;
        for (DebitoMarcaFogo debitoMarcaFogo : marcaFogo.getDebitos()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CALCULO_ID,
                br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL,
                debitoMarcaFogo.getCalculoTaxasDiversas().getId());
            List<DAMResultadoParcela> listaDAMResultadoParcela = consultaParcela.executaConsulta().getResultados().stream().map(rp -> {
                DAMResultadoParcela damResultadoParcela = new DAMResultadoParcela();
                damResultadoParcela.setResultadoParcela(rp);
                damResultadoParcela.setDam(damFacade.recuperaUltimoDamParcela(rp.getIdParcela()));
                return damResultadoParcela;
            }).collect(Collectors.toList());
            debitoMarcaFogo.setListaDAMResultadoParcela(listaDAMResultadoParcela);
        }
    }

    public void processar(UsuarioSistema usuarioSistema, MarcaFogo marcaFogo) throws Exception {
        ParametroMarcaFogo parametro = buscarParametroDoExercicio(marcaFogo.getExercicio());
        List<TaxaMarcaFogo> taxasPrimeiraVia = parametro.getTaxasPorTipoEmissao(TipoEmissaoMarcaFogo.PRIMEIRA_VIA);
        if (taxasPrimeiraVia == null || taxasPrimeiraVia.isEmpty()) {
            throw new ValidacaoException("Nenhuma taxa configurada no parâmetro de marca a fogo.");
        }

        CalculoTaxasDiversas calculoTaxasDiversas = criarCalculoTaxasDiversas(usuarioSistema,
            marcaFogo, taxasPrimeiraVia);

        adicionarDebitoMarcaFogo(marcaFogo, TipoEmissaoMarcaFogo.PRIMEIRA_VIA, calculoTaxasDiversas);

        marcaFogo.setSituacao(SituacaoMarcaFogo.PROCESSADO);

        salvar(marcaFogo);
    }

    private static void adicionarDebitoMarcaFogo(MarcaFogo marcaFogo,
                                                 TipoEmissaoMarcaFogo tipoEmissao,
                                                 CalculoTaxasDiversas calculoTaxasDiversas) {
        DebitoMarcaFogo debitoMarcaFogo = new DebitoMarcaFogo();
        debitoMarcaFogo.setMarcaFogo(marcaFogo);
        debitoMarcaFogo.setTipoEmissao(tipoEmissao);
        debitoMarcaFogo.setCalculoTaxasDiversas(calculoTaxasDiversas);
        marcaFogo.getDebitos().add(debitoMarcaFogo);
    }

    public CalculoTaxasDiversas criarCalculoTaxasDiversas(UsuarioSistema usuarioSistema,
                                                          MarcaFogo marcaFogo,
                                                          List<TaxaMarcaFogo> taxas) throws Exception {
        ProcessoCalcTaxasDiversas processo = new ProcessoCalcTaxasDiversas();
        CalculoTaxasDiversas calculoTaxasDiversas = new CalculoTaxasDiversas();
        calculoTaxasDiversas.setProcessoCalcTaxasDiversas(processo);
        calculoTaxasDiversas.setDataHoraLancamento(new Date());
        calculoTaxasDiversas.setDataCalculo(calculoTaxasDiversas.getDataHoraLancamento());
        calculoTaxasDiversas.setSituacao(SituacaoCalculo.ABERTO);
        calculoTaxasDiversas.setVencimento(calculoTaxasDiversasFacade.calcularVencimento(calculoTaxasDiversas.getDataCalculo()));
        calculoTaxasDiversas.setExercicio(calculoTaxasDiversasFacade.getExercicioFacade().getExercicioCorrente());
        calculoTaxasDiversas.setUsuarioLancamento(usuarioSistema);
        calculoTaxasDiversas.setTipoCadastroTributario(marcaFogo.getTipoCadastroTributario());
        calculoTaxasDiversas.setCadastro(marcaFogo.getCadastro());
        calculoTaxasDiversas.setContribuinte(marcaFogo.getPessoa());
        atribuirPessoasCalculoTaxasDiversas(calculoTaxasDiversas);
        atribuirItensCalculoTaxasDiversas(taxas, calculoTaxasDiversas);
        processo = calculoTaxasDiversasFacade.processarCalculoAndGerarDebito(calculoTaxasDiversas);
        return processo.getCalculos().get(0);
    }

    private void atribuirItensCalculoTaxasDiversas(List<TaxaMarcaFogo> taxasMarcaFogo,
                                                   CalculoTaxasDiversas calculoTaxasDiversas) throws UFMException {
        calculoTaxasDiversas.setItens(Lists.newArrayList());
        calculoTaxasDiversas.setValorEfetivo(BigDecimal.ZERO);
        for (TaxaMarcaFogo taxaMarcaFogo : taxasMarcaFogo) {
            ItemCalculoTaxasDiversas itemCalculoTaxasDiversas = new ItemCalculoTaxasDiversas();
            itemCalculoTaxasDiversas.setCalculoTaxasDiversas(calculoTaxasDiversas);
            itemCalculoTaxasDiversas.setTributoTaxaDividasDiversas(taxaMarcaFogo.getTributo());
            itemCalculoTaxasDiversas.setQuantidadeTributoTaxas(1);
            itemCalculoTaxasDiversas.setValorUFM(taxaMarcaFogo.getTributo().getValor());
            itemCalculoTaxasDiversas.setValorReal(moedaFacade.converterToReal(itemCalculoTaxasDiversas.getValorUFM()));
            calculoTaxasDiversas.getItens().add(itemCalculoTaxasDiversas);
            calculoTaxasDiversas.setValorEfetivo(calculoTaxasDiversas.getValorEfetivo().add(itemCalculoTaxasDiversas.getValorReal()));
        }
        calculoTaxasDiversas.setValorReal(calculoTaxasDiversas.getValorEfetivo());
    }

    private void atribuirPessoasCalculoTaxasDiversas(CalculoTaxasDiversas calculoTaxasDiversas) {
        calculoTaxasDiversas.setPessoas(Lists.newArrayList());
        List<Pessoa> listaPessoa = new ArrayList<>();
        if (calculoTaxasDiversas.getCadastro() != null) {
            listaPessoa = calculoTaxasDiversasFacade.getPessoaFacade()
                .recuperaPessoasDoCadastro(calculoTaxasDiversas.getCadastro());
        } else {
            listaPessoa.add(calculoTaxasDiversas.getContribuinte());
        }
        for (Pessoa p : listaPessoa) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculoTaxasDiversas);
            calculoPessoa.setPessoa(p);
            calculoTaxasDiversas.getPessoas().add(calculoPessoa);
        }
    }

    public void gerarSegundaVia(UsuarioSistema usuarioSistema, MarcaFogo marcaFogo) throws Exception {
        ParametroMarcaFogo parametro = buscarParametroDoExercicio(marcaFogo.getExercicio());

        List<TaxaMarcaFogo> taxasSegundaVia = parametro.getTaxasPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA);
        if (taxasSegundaVia == null || taxasSegundaVia.isEmpty()) {
            throw new ValidacaoException("Nenhuma taxa configurada no parâmetro de marca a fogo para emissão de segunda via.");
        }

        CalculoTaxasDiversas calculoTaxasDiversas = criarCalculoTaxasDiversas(usuarioSistema,
            marcaFogo, taxasSegundaVia);

        adicionarDebitoMarcaFogo(marcaFogo, TipoEmissaoMarcaFogo.SEGUNDA_VIA, calculoTaxasDiversas);

        salvar(marcaFogo);
    }

    private ParametroMarcaFogo buscarParametroDoExercicio(Exercicio exercicio) {
        ParametroMarcaFogo parametro = parametroMarcaFogoFacade.buscarParametroPeloExercicio(exercicio);
        if (parametro == null) {
            throw new ValidacaoException("Nenhum parâmentro encontrado com o exercício " + exercicio.getAno() + ".");
        }
        return parametro;
    }

    public DebitoMarcaFogo verificarDebitoEmAberto(MarcaFogo marcaFogo) {
        if (marcaFogo.hasDebitoEmAberto()) {
            throw new ValidacaoException("A emissão da certidão somente será liberada após o pagamento das taxas.");
        }
        List<DebitoMarcaFogo> debitosMarcaFogo = marcaFogo.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA);
        if (debitosMarcaFogo.isEmpty()) {
            debitosMarcaFogo = marcaFogo.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.PRIMEIRA_VIA);
        }
        return debitosMarcaFogo.isEmpty() ? null : debitosMarcaFogo.get(0);
    }

    public MarcaFogo gerarDocumentoOficial(MarcaFogo marcaFogo) throws AtributosNulosException, UFMException {
        marcaFogo = recuperar(marcaFogo.getId());
        DebitoMarcaFogo debitoMarcaFogo = verificarDebitoEmAberto(marcaFogo);
        ParametroMarcaFogo parametroMarcaFogo = buscarParametroDoExercicio(marcaFogo.getExercicio());
        TipoEmissaoMarcaFogo tipoEmissao = debitoMarcaFogo.getTipoEmissao();
        Optional<CertidaoMarcaFogo> certidaoMarcaFogo = parametroMarcaFogo.getCertidoes()
            .stream()
            .filter(c -> c.getTipoEmissao().equals(tipoEmissao))
            .findFirst();
        if (!certidaoMarcaFogo.isPresent()) {
            throw new ValidacaoException("Não foi configurado o Tipo de Documento Oficial para emissão da certidão de " +
                debitoMarcaFogo.getTipoEmissao().getDescricao());
        }
        DocumentoOficial documentoOficial = documentoOficialFacade.gerarDocumento(marcaFogo,
            debitoMarcaFogo.getDocumentoOficial(), marcaFogo.getCadastro(),
            marcaFogo.getPessoa(), certidaoMarcaFogo.get().getTipoDoctoOficial());
        debitoMarcaFogo.setDocumentoOficial(documentoOficial);
        for (DebitoMarcaFogo debito : marcaFogo.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.SEGUNDA_VIA)) {
            if (!debito.isImprimiuDocumento()) debito.setImprimiuDocumento(true);
        }
        for (DebitoMarcaFogo debito : marcaFogo.getDebitoPorTipoEmissao(TipoEmissaoMarcaFogo.PRIMEIRA_VIA)) {
            if (!debito.isImprimiuDocumento()) debito.setImprimiuDocumento(true);
        }
        return em.merge(marcaFogo);
    }

    public void emitirDocumentoOficial(DocumentoOficial documentoOficial) {
        documentoOficialFacade.emiteDocumentoOficial(documentoOficial);
    }
}
