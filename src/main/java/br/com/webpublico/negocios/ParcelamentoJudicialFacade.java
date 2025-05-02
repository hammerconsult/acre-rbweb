package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteParcelamento;
import br.com.webpublico.entidadesauxiliares.AssistenteSimulacaoParcelamento;
import br.com.webpublico.entidadesauxiliares.CDAResultadoParcela;
import br.com.webpublico.entidadesauxiliares.CadastroDebito;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoParcelamento;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.SituacaoParcelaDTO;
import br.com.webpublico.util.AnexoEmailDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class ParcelamentoJudicialFacade extends AbstractFacade<ParcelamentoJudicial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoJudicialFacade processoJudicialFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private ParametroParcelamentoFacade parametroParcelamentoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParcelamentoJudicialFacade() {
        super(ParcelamentoJudicial.class);
    }

    public ProcessoJudicialFacade getProcessoJudicialFacade() {
        return processoJudicialFacade;
    }

    public ProcessoParcelamentoFacade getProcessoParcelamentoFacade() {
        return processoParcelamentoFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ItemParcelamentoJudicial> criarItensParcelamentoJudicial(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                         List<CDAResultadoParcela> cdaResultadoParcelas) {
        List<ItemParcelamentoJudicial> itens = Lists.newArrayList();
        for (CDAResultadoParcela cdaResultadoParcela : cdaResultadoParcelas) {
            ItemParcelamentoJudicial itemParcelamentoJudicial = getItemParcelamentoJudicial(itens,
                cdaResultadoParcela.getResultadoParcela().getIdCadastro(),
                cdaResultadoParcela.getResultadoParcela().getIdPessoa(),
                cdaResultadoParcela.getResultadoParcela().getIdDivida());
            if (itemParcelamentoJudicial == null) {
                itemParcelamentoJudicial = new ItemParcelamentoJudicial();
                itemParcelamentoJudicial.setTipoCadastroTributario(TipoCadastroTributario
                    .valueOf(cdaResultadoParcela.getResultadoParcela().getTipoCadastroTributarioEnumValue().name()));
                itemParcelamentoJudicial.setCadastro(cdaResultadoParcela.getResultadoParcela().getIdCadastro() != null ?
                    em.find(Cadastro.class, cdaResultadoParcela.getResultadoParcela().getIdCadastro()) : null);
                itemParcelamentoJudicial.setPessoa(cdaResultadoParcela.getResultadoParcela().getIdPessoa() != null ?
                    em.find(Pessoa.class, cdaResultadoParcela.getResultadoParcela().getIdPessoa()) : null);
                itemParcelamentoJudicial.setDivida(em.find(Divida.class,
                    cdaResultadoParcela.getResultadoParcela().getIdDivida()));
                itens.add(itemParcelamentoJudicial);
            }
            DebitoParcelamentoJudicial debitoParcelamentoJudicial = new DebitoParcelamentoJudicial();
            debitoParcelamentoJudicial.setItemParcelamentoJudicial(itemParcelamentoJudicial);
            debitoParcelamentoJudicial.setCertidaoDividaAtiva(cdaResultadoParcela.getCertidaoDividaAtiva());
            debitoParcelamentoJudicial.setParcelaValorDivida(em.find(ParcelaValorDivida.class,
                cdaResultadoParcela.getResultadoParcela().getIdParcela()));
            debitoParcelamentoJudicial.setResultadoParcela(cdaResultadoParcela.getResultadoParcela());
            itemParcelamentoJudicial.getDebitos().add(debitoParcelamentoJudicial);
        }
        gerarParcelamentos(assistenteBarraProgresso, itens);
        return itens;
    }

    public void gerarParcelamentos(AssistenteBarraProgresso assistenteBarraProgresso,
                                   List<ItemParcelamentoJudicial> itens) {
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : itens) {
            List<ResultadoParcela> parcelas = itemParcelamentoJudicial.getDebitos()
                .stream()
                .map(DebitoParcelamentoJudicial::getResultadoParcela)
                .collect(Collectors.toList());
            itemParcelamentoJudicial.setParametros(parametroParcelamentoFacade.buscarParametrosPorParcelas(parcelas));
            ParamParcelamento paramParcelamento = !itemParcelamentoJudicial.getParametros().isEmpty() ?
                itemParcelamentoJudicial.getParametros().get(0) : null;
            if (paramParcelamento != null) {
                paramParcelamento = parametroParcelamentoFacade.recuperar(paramParcelamento.getId());
            }
            ProcessoParcelamento processoParcelamento = criarProcessoParcelamento(assistenteBarraProgresso.getExercicio(),
                itemParcelamentoJudicial, paramParcelamento);
            itemParcelamentoJudicial.setProcessoParcelamento(processoParcelamento);

            recalcularParcelamento(assistenteBarraProgresso.getUsuarioSistema(), itemParcelamentoJudicial);
        }
    }

    public void recalcularParcelamento(UsuarioSistema usuarioSistema,
                                       ItemParcelamentoJudicial itemParcelamentoJudicial) {
        ProcessoParcelamento processoParcelamento = itemParcelamentoJudicial.getProcessoParcelamento();
        if (processoParcelamento.getParamParcelamento() != null) {
            processoParcelamento.setParamParcelamento(parametroParcelamentoFacade
                .recuperar(processoParcelamento.getParamParcelamento().getId()));
            processoParcelamento.setPercentualEntrada(processoParcelamento.getParamParcelamento().getValorPercentualEntrada());
            ConfiguracaoAcrescimos configuracaoAcrescimos = processoParcelamentoFacade
                .buscarConfiguracaoDeAcrescimosVigente(itemParcelamentoJudicial.getProcessoParcelamento()
                    .getParamParcelamento().getDividaParcelamento());
            AssistenteSimulacaoParcelamento assistenteSimulacaoParcelamento =
                new AssistenteSimulacaoParcelamento(processoParcelamentoFacade, usuarioSistema,
                    itemParcelamentoJudicial.getProcessoParcelamento(), configuracaoAcrescimos, new Date());
            try {
                assistenteSimulacaoParcelamento.setValorMinimoParcela(getProcessoParcelamentoFacade().getMoedaFacade()
                    .converterToReal(processoParcelamento.getParamParcelamento().getValorMinimoParcelaUfm()));
            } catch (UFMException e) {
                logger.error(e.getMessage());
            }
            assistenteSimulacaoParcelamento.setValorMinimoEntrada(assistenteSimulacaoParcelamento.getValorMinimoParcela());
            assistenteSimulacaoParcelamento.setParcelas(itemParcelamentoJudicial.getDebitos().stream()
                .map(DebitoParcelamentoJudicial::getResultadoParcela)
                .collect(Collectors.toList()));
            itemParcelamentoJudicial.setAssistenteSimulacaoParcelamento(assistenteSimulacaoParcelamento);
            assistenteSimulacaoParcelamento.atribuirPrimeiroVencimento();
            assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
            assistenteSimulacaoParcelamento.ajustarParcelasAposBuscar();
        }
    }

    private ItemParcelamentoJudicial getItemParcelamentoJudicial(List<ItemParcelamentoJudicial> itens,
                                                                 Long idCadastro,
                                                                 Long idPessoa,
                                                                 Long idDivida) {
        return itens.stream()
            .filter(i -> (idCadastro != null ?
                i.getCadastro().getId().equals(idCadastro) :
                i.getPessoa().getId().equals(idPessoa)) &&
                idDivida.equals(i.getDivida().getId()))
            .findFirst()
            .orElse(null);
    }

    public boolean hasCDAUtilizadaParcelamentoJudicial(CertidaoDividaAtiva certidaoDividaAtiva) {
        return !em.createQuery("from DebitoParcelamentoJudicial deb " +
            " where deb.certidaoDividaAtiva = :certidao ")
            .setParameter("certidao", certidaoDividaAtiva)
            .getResultList().isEmpty();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CadastroDebito> buscarDebitosProcessoJudicial(String numeroProcessoJudicial) {
        List<CDAResultadoParcela> debitos = Lists.newArrayList();
        List<ProcessoJudicialCDA> processosJudicialCDA = processoJudicialFacade.buscarCDAsPorNumeroProcessoForum(numeroProcessoJudicial);
        for (ProcessoJudicialCDA processoJudicialCDA : processosJudicialCDA) {
            if (hasCDAUtilizadaParcelamentoJudicial(processoJudicialCDA.getCertidaoDividaAtiva())) continue;
            CertidaoDividaAtiva certidaoDividaAtiva = certidaoDividaAtivaFacade.recuperar(processoJudicialCDA.getCertidaoDividaAtiva().getId());
            List<ResultadoParcela> parcelasCertidaoDividaAtiva = Lists.newArrayList();
            for (ItemCertidaoDividaAtiva itemCertidaoDividaAtiva : certidaoDividaAtiva.getItensCertidaoDividaAtiva()) {
                List<ResultadoParcela> parcelas = certidaoDividaAtivaFacade
                    .buscarParcelasDoCalculo(itemCertidaoDividaAtiva.getItemInscricaoDividaAtiva().getId());
                parcelas = parcelas.stream()
                    .filter(p -> SituacaoParcelaDTO.EM_ABERTO.equals(p.getSituacaoEnumValue()))
                    .collect(Collectors.toList());
                if (!parcelas.isEmpty()) {
                    parcelasCertidaoDividaAtiva.addAll(parcelas);
                }
            }
            if (!parcelasCertidaoDividaAtiva.isEmpty()) {
                for (ResultadoParcela resultadoParcela : parcelasCertidaoDividaAtiva) {
                    CDAResultadoParcela cdaResultadoParcela = new CDAResultadoParcela();
                    cdaResultadoParcela.setCertidaoDividaAtiva(certidaoDividaAtiva);
                    cdaResultadoParcela.setResultadoParcela(resultadoParcela);
                    debitos.add(cdaResultadoParcela);
                }
            }
        }
        return agruparDebitosPorCadastro(debitos);
    }

    private List<CadastroDebito> agruparDebitosPorCadastro(List<CDAResultadoParcela> debitos) {
        List<CadastroDebito> cadastrosDebitos = Lists.newArrayList();
        for (CDAResultadoParcela debito : debitos) {
            String cadastro = debito.getResultadoParcela().getTipoCadastro()
                + " - " + debito.getResultadoParcela().getCadastro();
            CadastroDebito cadastroDebito = cadastrosDebitos
                .stream()
                .filter(cd -> cd.getCadastro().equals(cadastro))
                .findFirst().orElse(null);
            if (cadastroDebito == null) {
                cadastroDebito = new CadastroDebito();
                cadastroDebito.setCadastro(cadastro);
                cadastrosDebitos.add(cadastroDebito);
            }
            cadastroDebito.getDebitos().add(debito);
        }
        return cadastrosDebitos;
    }

    @Override
    public void preSave(ParcelamentoJudicial entidade) {
        entidade.realizarValidacoes();
        if (entidade.getCodigo() == null) {
            entidade.setCodigo(singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ParcelamentoJudicial.class,
                "codigo", "exercicio_id", entidade.getExercicio()));
        }
    }

    @Override
    public ParcelamentoJudicial recuperar(Object id) {
        ParcelamentoJudicial parcelamentoJudicial = super.recuperar(id);
        if (parcelamentoJudicial != null) {
            Hibernate.initialize(parcelamentoJudicial.getItens());
            for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
                Hibernate.initialize(itemParcelamentoJudicial.getDebitos());
                for (DebitoParcelamentoJudicial debitoParcelamentoJudicial : itemParcelamentoJudicial.getDebitos()) {
                    debitoParcelamentoJudicial.setResultadoParcela(recuperarResultadoParcela(debitoParcelamentoJudicial.getParcelaValorDivida()));
                }
            }
        }
        return parcelamentoJudicial;
    }

    private ResultadoParcela recuperarResultadoParcela(ParcelaValorDivida parcelaValorDivida) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL,
            parcelaValorDivida.getId());
        List<ResultadoParcela> resultados = consultaParcela.executaConsulta().getResultados();
        return !resultados.isEmpty() ? resultados.get(0) : null;
    }

    private ProcessoParcelamento criarProcessoParcelamento(Exercicio exercicio,
                                                           ItemParcelamentoJudicial itemParcelamentoJudicial,
                                                           ParamParcelamento paramParcelamento) {
        ProcessoParcelamento processoParcelamento = new ProcessoParcelamento();
        processoParcelamento.setExercicio(exercicio);
        processoParcelamento.setPessoa(itemParcelamentoJudicial.getPessoa());
        processoParcelamento.setCadastro(itemParcelamentoJudicial.getCadastro());
        processoParcelamento.setSituacaoParcelamento(SituacaoParcelamento.ABERTO);
        processoParcelamento.setParamParcelamento(paramParcelamento);
        processoParcelamento.setDataProcessoParcelamento(new Date());
        processoParcelamento.setItensProcessoParcelamento(new ArrayList<>());
        for (DebitoParcelamentoJudicial debito : itemParcelamentoJudicial.getDebitos()) {
            ItemProcessoParcelamento itemProcessoParcelamento = new ItemProcessoParcelamento();
            itemProcessoParcelamento.setProcessoParcelamento(processoParcelamento);
            itemProcessoParcelamento.setSituacaoAnterior(Enum.valueOf(SituacaoParcela.class,
                debito.getResultadoParcela().getSituacaoNameEnum()));
            itemProcessoParcelamento.setParcelaValorDivida(debito.getParcelaValorDivida());
            itemProcessoParcelamento.setImposto(debito.getResultadoParcela().getValorImposto());
            itemProcessoParcelamento.setTaxa(debito.getResultadoParcela().getValorTaxa());
            itemProcessoParcelamento.setMulta(debito.getResultadoParcela().getValorMulta());
            itemProcessoParcelamento.setCorrecao(debito.getResultadoParcela().getValorCorrecao());
            itemProcessoParcelamento.setJuros(debito.getResultadoParcela().getValorJuros());
            itemProcessoParcelamento.setHonorarios(debito.getResultadoParcela().getValorHonorarios());
            processoParcelamento.getItensProcessoParcelamento().add(itemProcessoParcelamento);
        }
        return processoParcelamento;
    }

    public void gerarParcelasParcelamentos(AssistenteBarraProgresso assistenteBarraProgresso,
                                           ParcelamentoJudicial parcelamentoJudicial,
                                           HashMap<String, AssistenteSimulacaoParcelamento> mapAssistenteSimulacao) {
        parcelamentoJudicial = recuperar(parcelamentoJudicial.getId());
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
            ProcessoParcelamento processoParcelamento = processoParcelamentoFacade
                .recuperar(itemParcelamentoJudicial.getProcessoParcelamento().getId());

            AssistenteSimulacaoParcelamento assistenteSimulacaoParcelamento = mapAssistenteSimulacao
                .get(itemParcelamentoJudicial.getTituloAgrupamento());

            AssistenteParcelamento assistenteParcelamento = new AssistenteParcelamento();
            assistenteParcelamento.setSelecionado(processoParcelamento);
            assistenteParcelamento.setParcelas(assistenteSimulacaoParcelamento.getParcelasGeradas());
            assistenteParcelamento.setExercicio(assistenteBarraProgresso.getExercicio());
            assistenteParcelamento.setUsuarioSistema(assistenteBarraProgresso.getUsuarioSistema());
            assistenteParcelamento.setIpUsuario(assistenteBarraProgresso.getIp());
            assistenteParcelamento.setExecutando(true);

            processoParcelamentoFacade.gerarParcelasParcelamento(new AssistenteBarraProgresso(), assistenteParcelamento);
        }
    }

    public ParcelamentoJudicial alterarSituacao(ParcelamentoJudicial parcelamentoJudicial,
                                                ParcelamentoJudicial.Situacao situacao) {
        parcelamentoJudicial.setSituacao(situacao);
        return salvarRetornando(parcelamentoJudicial);
    }

    public ParcelamentoJudicial recuperarParcelasParcelamentos(ParcelamentoJudicial parcelamentoJudicial) {
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
            if (itemParcelamentoJudicial.getProcessoParcelamento() != null) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CALCULO_ID,
                    br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL,
                    itemParcelamentoJudicial.getProcessoParcelamento().getId());
                itemParcelamentoJudicial.setParcelasParcelamento(consultaParcela.executaConsulta().getResultados());
            }
        }
        return parcelamentoJudicial;
    }

    public byte[] gerarBytesDemonstrativoParcelamento(ProcessoParcelamento processoParcelamento) throws JRException, IOException {
        return processoParcelamentoFacade.gerarBytesDemonstrativo(processoParcelamento);
    }

    public void enviarPorEmail(ParcelamentoJudicial parcelamentoJudicial, String email) throws IOException, JRException {
        if (Strings.isNullOrEmpty(email)) {
            throw new ValidacaoException("O e-mail deve ser informado.");
        }
        List<AnexoEmailDTO> anexos = gerarAnexosEnvioEmail(parcelamentoJudicial);
        String titulo = "PMRB - Documentos da Solicitação de Parcelamento Judicial: " +
            parcelamentoJudicial.getNumeroProcessoForum();
        EmailService.getInstance().enviarEmail(email, titulo, titulo, anexos);
    }

    private List<AnexoEmailDTO> gerarAnexosEnvioEmail(ParcelamentoJudicial parcelamentoJudicial) throws IOException, JRException {
        List<AnexoEmailDTO> anexos = Lists.newArrayList();
        byte[] bytesDam = gerarBytesDamAgrupado(parcelamentoJudicial);
        if (bytesDam != null && bytesDam.length > 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(bytesDam);
            anexos.add(new AnexoEmailDTO(outputStream, "dam", "application/pdf", ".pdf"));
        }

        for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
            if (itemParcelamentoJudicial.getProcessoParcelamento() != null) {
                byte[] bytesDemonstrativoParcelamento = gerarBytesDemonstrativoParcelamento(itemParcelamentoJudicial.getProcessoParcelamento());
                if (bytesDemonstrativoParcelamento != null && bytesDemonstrativoParcelamento.length > 0) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(bytesDemonstrativoParcelamento);
                    anexos.add(new AnexoEmailDTO(outputStream, "demonstrativoParcelamento" +
                        itemParcelamentoJudicial.getProcessoParcelamento().getNumeroComposto(), "application/pdf", ".pdf"));
                }
            }
        }

        return anexos;
    }

    private byte[] gerarBytesDamAgrupado(ParcelamentoJudicial parcelamentoJudicial) {
        List<ResultadoParcela> parcelasEmAberto = Lists.newArrayList();
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
            for (ResultadoParcela resultadoParcela : itemParcelamentoJudicial.getParcelasParcelamento()) {
                if (resultadoParcela.isEmAberto()) {
                    parcelasEmAberto.add(resultadoParcela);
                }
            }
        }
        if (!parcelasEmAberto.isEmpty()) {
            DAM dam = gerarDamAgrupado(parcelasEmAberto);
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            return imprimeDAM.gerarBytesImpressaoDamCompostoViaApi(dam);
        }
        return null;
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> parcelasEmAberto) {
        LocalDate localDate = LocalDate.now();
        Calendar c = DataUtil.ultimoDiaUtil(DataUtil.ultimoDiaMes(localDate.toDate()), feriadoFacade);
        DAM dam = damFacade.gerarDamAgrupado(parcelasEmAberto, c.getTime(), sistemaFacade.getUsuarioCorrente());
        return dam;
    }

    public void calcularValoresAposAlteracaoQuantidadeParcelas(ItemParcelamentoJudicial itemParcelamentoJudicial) {
        ParamParcelamento paramParcelamento = parametroParcelamentoFacade
            .recuperar(itemParcelamentoJudicial.getProcessoParcelamento().getParamParcelamento().getId());
        itemParcelamentoJudicial.getProcessoParcelamento().setParamParcelamento(paramParcelamento);
        itemParcelamentoJudicial.getAssistenteSimulacaoParcelamento().calcularValoresAposAlteracaoQuantidadeParcelas();
    }

    public void salvarParcelamentos(ParcelamentoJudicial parcelamentoJudicial) {
        for (ItemParcelamentoJudicial itemParcelamentoJudicial : parcelamentoJudicial.getItens()) {
            ProcessoParcelamento processoParcelamento = itemParcelamentoJudicial.getProcessoParcelamento();
            processoParcelamento.setContato(parcelamentoJudicial.getContato());
            processoParcelamento.setProcurador(parcelamentoJudicial.getProcurador());
            processoParcelamento = processoParcelamentoFacade.salvarParcelamento(processoParcelamento);
            itemParcelamentoJudicial.setProcessoParcelamento(processoParcelamento);
        }
    }

    public void gerarTermoUnificado(ParcelamentoJudicial selecionado, boolean novo) throws AtributosNulosException, UFMException {
        selecionado.setTermoUnificado(processoParcelamentoFacade.gerarTermoParcelamento(null, selecionado, novo ? null : selecionado.getTermoUnificado()));
        salvar(selecionado);
    }
}
