/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRendasPatrimoniais;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ContratoRendasPatrimoniaisFacade extends AbstractFacade<ContratoRendasPatrimoniais> {

    Map<ContratoRendasPatrimoniais, ContratoRendasPatrimoniais> contratosRenovados;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaRendasPatrimoniais geraDebito;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ParametroRendasPatrimoniaisFacade parametroRendasPatrimoniaisFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private PontoComercialFacade pontoComercialFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroRendasPatrimoniaisFacade parametroFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private LocalizacaoFacade localizacaoFacade;
    @EJB
    private AtividadePontoFacade atividadeFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private CNAEFacade cNAEFacade;
    private Divida dividaRendasPatrimoniais;
    private AssistenteRendasPatrimoniais assistente;

    public ContratoRendasPatrimoniaisFacade() {
        super(ContratoRendasPatrimoniais.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public ParametroRendasPatrimoniaisFacade getParametroRendasPatrimoniaisFacade() {
        return parametroRendasPatrimoniaisFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public PontoComercialFacade getPontoComercialFacade() {
        return pontoComercialFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ParametroRendasPatrimoniaisFacade getParametroFacade() {
        return parametroFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public LocalizacaoFacade getLocalizacaoFacade() {
        return localizacaoFacade;
    }

    public AtividadePontoFacade getAtividadeFacade() {
        return atividadeFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public LotacaoVistoriadoraFacade getLotacaoVistoriadoraFacade() {
        return lotacaoVistoriadoraFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public CNAEFacade getcNAEFacade() {
        return cNAEFacade;
    }

    @Override
    public void salvarNovo(ContratoRendasPatrimoniais entity) {
        try {
            ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), entity.getLotacaoVistoriadora());
            Integer ano = p.getExercicio().getAno();
            String numeroContrato = ano + getTotalContrato(entity);
            entity.setNumeroContrato(numeroContrato);

            salvar(entity);
        } catch (Exception e) {
            FacesUtil.addError("Ocorreu um erro", e.getMessage());
        }
    }

    public String getSequenciaContratoPorLocatario(ContratoRendasPatrimoniais entity) {
        String sql = "select MAX(sequenciaContrato) from ContratoRendasPatrimoniais where locatario_id = :locatario";
        Query q = em.createNativeQuery(sql);
        q.setParameter("locatario", entity.getLocatario().getId());
        Integer numero = 0;
        if (!q.getResultList().isEmpty()) {
            numero = ((BigDecimal) q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : numero).intValue();
            if (q.getResultList().get(0) != null) {
                numero++;
            }
        }
        String numeroString = numero.toString();
        if (numero <= 9) {
            numeroString = "0" + numeroString;
        }
        if (numero <= 99) {
            numeroString = "0" + numeroString;
        }
        return numeroString;
    }

    public void gerarDebitoContrato(ProcessoCalculoRendas processoCalculoRendas) throws Exception {
        geraDebito.geraDebito(processoCalculoRendas);
    }

    @Override
    public void salvar(ContratoRendasPatrimoniais entity) {
        try {

            if (entity.getId() == null) {
                super.salvarNovo(entity);
            } else {
                entity = em.merge(entity);
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar: {}", e);
        }
    }

    public ContratoRendasPatrimoniais salvarContrato(ContratoRendasPatrimoniais contrato) {
        if (contrato.getNumeroContrato() == null) {
            ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), contrato.getLotacaoVistoriadora());
            Integer ano = p.getExercicio().getAno();
            String numeroContrato = ano + getTotalContrato(contrato);
            contrato.setNumeroContrato(numeroContrato);
        }
        return em.merge(contrato);
    }

    public void gerarDebito(ContratoRendasPatrimoniais entity, Exercicio exercicioCorrente, BigDecimal valorDoContrato, Date primeiroVencimento) {
        try {
            geraDebito.setPrimeiroVencimento(primeiroVencimento);
            ProcessoCalculoRendas processoCalculoRendas = geraProcessoCalculo(entity, exercicioCorrente, valorDoContrato);
            processoCalculoRendas = em.merge(processoCalculoRendas);
            geraDebito.geraDebito(processoCalculoRendas);
        } catch (Exception e) {
            logger.error("Erro ao gerar os débitos: {}", e);
        }
    }

    public String getTotalContrato(ContratoRendasPatrimoniais entity) {
        String sql = "select COUNT(id) from ContratoRendasPatrimoniais where originario_id is null";
        Query q = em.createNativeQuery(sql);
        Integer numero = 0;
        if (!q.getResultList().isEmpty()) {
            numero = ((BigDecimal) q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : numero).intValue();
            if (q.getResultList().get(0) != null) {
                numero++;
            }
        }
        String numeroString = numero.toString();
        if (numero <= 9) {
            numeroString = "0" + numeroString;
        }
        if (numero <= 99) {
            numeroString = "0" + numeroString;
        }
        if (numero <= 999) {
            numeroString = "0" + numeroString;
        }
        if (numero <= 9999) {
            numeroString = "0" + numeroString;
        }
        if (numero <= 99999) {
            numeroString = "0" + numeroString;
        }
        return numeroString;
    }

    public String getSequenciaRenovacaoContrato(ContratoRendasPatrimoniais entity) {
        try {
            String[] numeroContrato = entity.getNumeroContrato().split("-");
            String sql = "select max(numerocontrato) from contratorendaspatrimoniais where numerocontrato like :numeroContrato";
            Query q = em.createNativeQuery(sql);
            q.setParameter("numeroContrato", numeroContrato[0] + "%");

            String numeroDoContrato = "";
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                if (resultList.get(0) != null) {
                    numeroDoContrato = ((String) resultList.get(0));
                }
            }
            String[] numeroRenovacao = numeroDoContrato.split("-");
            if (numeroRenovacao.length <= 1) {
                return "001";
            }
            Integer numero = Integer.parseInt(numeroRenovacao[1]);
            numero = numero + 1;
            String numeroString = numero.toString();
            if (numero <= 9) {
                numeroString = "0" + numeroString;
            }
            if (numero <= 99) {
                numeroString = "0" + numeroString;
            }
            return numeroString;
        } catch (Exception ex) {
            logger.error("Erro ao buscar a sequencia de renovação do contrato: ", ex);
        }
        return "001";
    }

    private ProcessoCalculoRendas geraProcessoCalculo(ContratoRendasPatrimoniais crp, Exercicio exercicioCorrente, BigDecimal valorDoContrato) {
        CalculoRendas cr = new CalculoRendas();
        criaCalculo(cr, crp, valorDoContrato);
        ProcessoCalculoRendas pcr = new ProcessoCalculoRendas();
        cr.setProcessoCalculoRendas(pcr);
        criaProcesso(pcr, cr, crp, exercicioCorrente);
        return pcr;
    }

    public ValorDivida retornaValorDividaDoContrato(ContratoRendasPatrimoniais contrato) {

        Query q = em.createQuery("select vd from ValorDivida vd join vd.calculo calculo where calculo in (select c from CalculoRendas c where c.contrato = :contrato)");
        q.setParameter("contrato", contrato);
        List<ValorDivida> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            ValorDivida vd = resultado.get(0);
            vd.getParcelaValorDividas().size();
            return vd;
        }
    }

    public Boolean existemParcelasEmAberto(ContratoRendasPatrimoniais contrato) {
        Query q = em.createNativeQuery("select parcela.id from parcelavalordivida parcela "
            + " inner join situacaoparcelavalordivida situacao on situacao.id = parcela.situacaoatual_id "
            + " inner join valordivida on parcela.valordivida_id = valordivida.id "
            + " inner join calculorendas on valordivida.calculo_id = calculorendas.id "
            + " inner join contratorendaspatrimoniais on calculorendas.contrato_id = contratorendaspatrimoniais.id "
            + " where situacao.situacaoparcela = 'EM_ABERTO'"
            + " and contratorendaspatrimoniais.id = :idContrato");
        q.setParameter("idContrato", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean hasPontosEmOutroContratoVigenteRendasPatrimoniais(PontoComercial ponto, ContratoRendasPatrimoniais contratoAtual) {
        String sql = "select pt.id "
            + " from PTOCOMERCIALCONTRATORENDAS ptsrendas "
            + " inner join pontocomercial pt on ptsrendas.pontocomercial_id = pt.id "
            + " inner join localizacao on pt.localizacao_id = localizacao.id "
            + " inner join contratorendaspatrimoniais contrato on ptsrendas.contratorendaspatrimoniais_id = contrato.id "
            + " where pt.id = :idPonto and contrato.situacaocontrato = 'ATIVO' ";
        if (contratoAtual != null && contratoAtual.getId() != null) {
            sql += " and contrato.id <> :idContratoAtual";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPonto", ponto.getId());
        if (contratoAtual != null && contratoAtual.getId() != null) {
            q.setParameter("idContratoAtual", contratoAtual.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public boolean hasPontosEmOutroContratoRendasPatrimoniais(PontoComercial ponto) {
        String sql = "select pt.id "
            + " from PTOCOMERCIALCONTRATORENDAS ptsrendas "
            + " inner join pontocomercial pt on ptsrendas.pontocomercial_id = pt.id "
            + " inner join localizacao on pt.localizacao_id = localizacao.id "
            + " inner join contratorendaspatrimoniais contrato on ptsrendas.contratorendaspatrimoniais_id = contrato.id "
            + " where pt.id = :idPonto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPonto", ponto.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean existePontosEmOutroContratoVigenteCEASA(PontoComercial ponto) {
        Query q = em.createNativeQuery("select pt.id "
            + " from PTOCOMERCIALCONTRATOCEASA ptsceasa "
            + " inner join pontocomercial pt on ptsceasa.pontocomercial_id = pt.id "
            + " inner join localizacao on pt.localizacao_id = localizacao.id "
            + " inner join contratoceasa contrato on ptsceasa.contratoceasa_id = contrato.id "
            + " where pt.id = TO_NUMBER(:idPonto) and contrato.situacaocontrato = 'ATIVO' ");
        q.setParameter("idPonto", ponto.getId());
        return !q.getResultList().isEmpty();
    }

    public boolean existePontosEmOutroContratoCEASA(PontoComercial ponto) {
        Query q = em.createNativeQuery("select pt.id "
            + " from PTOCOMERCIALCONTRATOCEASA ptsceasa "
            + " inner join pontocomercial pt on ptsceasa.pontocomercial_id = pt.id "
            + " inner join localizacao on pt.localizacao_id = localizacao.id "
            + " inner join contratoceasa contrato on ptsceasa.contratoceasa_id = contrato.id "
            + " where pt.id = :idPonto");
        q.setParameter("idPonto", ponto.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean existeContratoVigenteDoLocatario(Pessoa pessoa) {
        Query q = em.createNativeQuery("select contrato.id "
            + " from contratorendaspatrimoniais contrato "
            + " where contrato.situacaocontrato = 'ATIVO'"
            + " and contrato.locatario_id = :pessoa ");
        q.setParameter("pessoa", pessoa.getId());
        return !q.getResultList().isEmpty();
    }

    private void criaCalculo(CalculoRendas cr, ContratoRendasPatrimoniais crp, BigDecimal valorDoContrato) {
        cr.setContrato(crp);
        cr.setCadastro(null);
        cr.setDataCalculo(crp.getDataInicio());
        cr.setSimulacao(Boolean.FALSE);
        BigDecimal valor = BigDecimal.ZERO;

        if (valorDoContrato != null && valorDoContrato.compareTo(BigDecimal.ZERO) > 0) {
            valor = valorDoContrato;
        } else {
            for (PontoComercialContratoRendasPatrimoniais p : crp.getPontoComercialContratoRendasPatrimoniais()) {
                if (p.getPontoComercial() != null && p.getPontoComercial().getArea() != null
                    && p.getPontoComercial().getValorMetroQuadrado() != null) {
                    valor = valor.add(p.getValorTotalContrato());
                }
            }
            valor = valor.multiply(moedaFacade.recuperaValorUFMParaData(crp.getDataInicio()));
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
        cr.setValorEfetivo(valor);
        cr.setValorReal(cr.getValorEfetivo());

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(crp.getLocatario());
        calculoPessoa.setCalculo(cr);

        if (cr.getPessoas() == null) {
            cr.setPessoas(new ArrayList<CalculoPessoa>());
        }
        cr.getPessoas().add(calculoPessoa);

    }

    private void criaProcesso(ProcessoCalculoRendas pcr, CalculoRendas cr, ContratoRendasPatrimoniais crp, Exercicio exercicioCorrente) {
        dividaRendasPatrimoniais = configuracaoTributarioFacade.retornaUltimo().getDividaRendasPatrimoniais();
        pcr.getCalculos().add(cr);
        pcr.setCompleto(Boolean.TRUE);
        pcr.setDataLancamento(crp.getDataInicio());
        pcr.setDivida(dividaRendasPatrimoniais);
        pcr.setExercicio(exercicioCorrente);
    }

    private String adicionaValor(String texto, BigDecimal valor, String simbolo, boolean insereNoFinal) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < 22) {
            sb.append(" ");
        }
        //espaço para completar a coluna com 44 caracteres
        int tamanhoEspaco = 9;
        for (int i = 0; i < (tamanhoEspaco - simbolo.length()); i++) {
            sb.append(" ");
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        String valorString = df.format(valor);
        int espacos = 14 - valorString.length();
        if (espacos > 0) {
            for (int i = 0; i < espacos; i++) {
                valorString = " " + valorString;
            }
        }
        if (insereNoFinal) {
            sb.append(valorString + simbolo);
        } else {
            sb.append(simbolo + valorString);
        }
        //sb.append(valorString);
        sb.append(" ");
        return sb.toString();
    }

    public String montaDescricao(ValorDivida valorDivida, boolean isLonga) throws UFMException {
        CalculoRendas calculo = recuperaCalculo(valorDivida);
        ContratoRendasPatrimoniais contrato = calculo.getContrato();

        PontoComercial ponto = new PontoComercial();
        Localizacao localizacao = new Localizacao();
        if (!contrato.getPontoComercialContratoRendasPatrimoniais().isEmpty()) {
            ponto = contrato.getPontoComercialContratoRendasPatrimoniais().get(0).getPontoComercial();
            localizacao = ponto.getLocalizacao();
        }
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat mesAno = new SimpleDateFormat("MMM/yyyy");
        StringBuilder sb = new StringBuilder("");
        BigDecimal valorTotal = BigDecimal.ZERO;
        ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), contrato.getLotacaoVistoriadora());
        if (localizacao.getLogradouro() == null) {
            sb.append("ENDEREÇO: ");
        } else {
            sb.append("ENDEREÇO: ").append(localizacao.getLogradouro().getNome().toUpperCase()).append(localizacao.getNumero() == null ? " " : ", " + localizacao.getNumero()).append(localizacao.getComplemento() != null ? " " : ", " + localizacao.getNumero());
        }
        sb.append("\n");
        if (localizacao.getBairro() == null) {
            sb.append("BAIRRO: ");
        } else {
            sb.append("BAIRRO: ").append(localizacao.getBairro().getDescricao().toUpperCase());
        }
        sb.append("\n");
        sb.append("CONTRATO TIPO: ").append(localizacao.getTipoOcupacaoLocalizacao().getDescricao().toUpperCase());
        sb.append("\n");
        sb.append("LOCALIZAÇÃO: ").append(ponto.getDescricaoCompleta().toUpperCase());
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("NÚMERO DO CONTRATO: ").append(contrato.getNumeroContrato());
        sb.append("\n");
        if (p == null) {
            sb.append("EXERCÍCIO DO CONTRATO:");
        } else {
            sb.append("EXERCÍCIO DO CONTRATO: " + p.getExercicio().getAno().toString());
        }
        sb.append("\n");
        sb.append("DATA DO CONTRATO: ").append(sdf.format(contrato.getDataInicio()));
        sb.append("\n");
        sb.append("VIGÊNCIA: ").append(contrato.getPeriodoVigencia().toString()).append(" MESES");
        sb.append("\n");
        sb.append("DIA VENCIMENTO: ").append(contrato.getDiaVencimento().toString());
        sb.append("\n");
        Calendar cal = Calendar.getInstance();
        cal.setTime(contrato.getDataInicio());
        cal.add(Calendar.MONTH, 1);
        sb.append("REF. MÊS: ").append(mesAno.format(cal.getTime()).toUpperCase());
        if (isLonga) {
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append(adicionaValor("VALOR ALUGUEL", calculo.getValorReal(), "R$", false));
            valorTotal = valorTotal.add(calculo.getValorEfetivo());
            sb.append("\n");
            sb.append(adicionaValor("TAXA DE EXPEDIENTE", BigDecimal.ZERO, "R$", false));
            sb.append("\n");
            sb.append(adicionaValor("CORREÇÃO MONETÁRIA", BigDecimal.ZERO, "R$", false));
            sb.append("\n");
            sb.append(adicionaValor("JUROS DE MORA", BigDecimal.ZERO, "R$", false));
            sb.append("\n");
            sb.append(adicionaValor("MULTA", BigDecimal.ZERO, "R$", false));
            sb.append("\n");
            sb.append("_________________________________________________________________________________________");
            sb.append("\n");
            sb.append(adicionaValor("VALOR TOTAL", valorTotal, "R$", false));
        }
        return sb.toString();
    }

    @Override
    public List<ContratoRendasPatrimoniais> lista() {
        String hql = "from ContratoRendasPatrimoniais order by to_number(numeroContrato) desc";
        Query q = getEntityManager().createQuery(hql);
        try {
            return q.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<ContratoRendasPatrimoniais>();
        }
    }

    private CalculoRendas recuperaCalculo(ValorDivida valorDivida) {
        Query consulta = em.createQuery("select vd.calculo from ValorDivida vd where vd = :valorDivida").setParameter("valorDivida", valorDivida);
        return (CalculoRendas) consulta.getResultList().get(0);
    }

    public ValorDivida recuperaValordivida(ValorDivida valorDividaContrato) {
        ValorDivida ValorDividaRetorno = this.em.find(ValorDivida.class, valorDividaContrato.getId());
        ValorDividaRetorno.getParcelaValorDividas().size();
        return ValorDividaRetorno;
    }

    @Override
    public ContratoRendasPatrimoniais recuperar(Object id) {
        ContratoRendasPatrimoniais contrato = em.find(ContratoRendasPatrimoniais.class, id);
        Hibernate.initialize(contrato.getPontoComercialContratoRendasPatrimoniais());
        Hibernate.initialize(contrato.getContratoRendaCNAEs());
        return contrato;
    }

    public DocumentoOficial geraDocumento(ContratoRendasPatrimoniais contratoRendasPatrimoniais, SistemaControlador sistemaControlador) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        TipoDoctoOficial tipo = null;

        if (contratoRendasPatrimoniais.getLocatario() instanceof PessoaFisica) {
            tipo = configuracaoTributario.getRendasTipoDoctoOficialPF();
        } else {
            tipo = configuracaoTributario.getRendasTipoDoctoOficialPJ();
        }

        try {
            return documentoOficialFacade.geraDocumentoContratoRendasPatrimoniais(contratoRendasPatrimoniais, null, tipo, contratoRendasPatrimoniais.getLocatario(), sistemaControlador);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o documento: {}", ex);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PontoComercialContratoRendasPatrimoniais> recuperarPontoDoContrato(ContratoRendasPatrimoniais contrato) {
        try {
            String sql = ("select ponto.* from PTOCOMERCIALCONTRATORENDAS ponto " +
                " inner join contratorendaspatrimoniais contrato on ponto.contratorendaspatrimoniais_id = contrato.id " +
                " where contrato.id = :idContrato");
            Query q = em.createNativeQuery(sql, PontoComercialContratoRendasPatrimoniais.class);
            q.setParameter("idContrato", contrato.getId());
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (List<PontoComercialContratoRendasPatrimoniais>) resultList;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Erro ao recuperar o ponto comercial do contrato: {}", e);
        }
        return new ArrayList<>();
    }

    public List<ContratoRendaCNAE> recuperarCnaeContrato(ContratoRendasPatrimoniais contrato) {
        String sql = ("select cnae.* from ContratoRendaCNAE cnae " +
            " inner join contratorendaspatrimoniais contrato on cnae.contratorendaspatrimoniais_id = contrato.id " +
            " where contrato.id = :idContrato");
        Query q = em.createNativeQuery(sql, ContratoRendaCNAE.class);
        q.setParameter("idContrato", contrato.getId());
        if (!q.getResultList().isEmpty()) {
            return (List<ContratoRendaCNAE>) q.getResultList();
        }
        return new ArrayList<>();
    }

    public CalculoRendas recuperaCalculoRendas(ContratoRendasPatrimoniais contrato) {
        Query q = em.createQuery(" select calc from CalculoRendas calc where calc.contrato = :contrato");
        q.setParameter("contrato", contrato);
        if (!q.getResultList().isEmpty()) {
            return (CalculoRendas) q.getResultList().get(0);
        }
        return null;
    }

    public ParcelaValorDivida recuperaParcelaValorDivida(Long id) {
        String hql = "select pvd from ParcelaValorDivida pvd where pvd.id = :id";
        Query q = em.createQuery(hql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        ParcelaValorDivida pvd = (ParcelaValorDivida) q.getResultList().get(0);
        pvd.getItensParcelaValorDivida().size();
        pvd.getSituacoes().size();
        return pvd;
    }

    public void cancelarParcela(ParcelaValorDivida pvd, BigDecimal valorParcela) {
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);
        situacao.setDataLancamento(new Date());
        situacao.setParcela(pvd);
        situacao.setSaldo(valorParcela);
        pvd.getSituacoes().add(situacao);
        em.merge(pvd);
    }

    public List<DAM> recuperaDAM(Long id) {
        String hql = "select dam from ItemDAM item " +
            "  join item.DAM dam " +
            " where item.parcela.id = :id " +
            "   and dam.situacao <> :damCancelado " +
            "   and dam.tipo = :tipoDam " +
            " order by dam.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("id", id);
        q.setParameter("damCancelado", DAM.Situacao.CANCELADO);
        q.setParameter("tipoDam", DAM.Tipo.UNICO);
        return q.getResultList();
    }

    public List<ContratoRendasPatrimoniais> recuperarContratosQuePodemSerRenovados(Long idLocalizacao, Date dataOperacao) {
        String sql = " select contrato.* " +
            " from contratorendaspatrimoniais contrato " +
            "         inner join PTOCOMERCIALCONTRATORENDAS ptrend on ptrend.contratorendaspatrimoniais_id = contrato.id " +
            "         inner join pontocomercial pc on ptrend.PONTOCOMERCIAL_ID = pc.ID " +
            "         inner join localizacao l on pc.LOCALIZACAO_ID = l.ID " +
            " where contrato.situacaocontrato = :situacaoContrato " +
            "  and l.ID = :idLocalizacao " +
            "  and trunc(contrato.dataFim) < trunc(:dataOperacao) ";
        Query q = em.createNativeQuery(sql, ContratoRendasPatrimoniais.class);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("situacaoContrato", SituacaoContratoRendasPatrimoniais.ATIVO.name());
        q.setParameter("idLocalizacao", idLocalizacao);
        List<ContratoRendasPatrimoniais> result = q.getResultList();
        for (ContratoRendasPatrimoniais contrato : result) {
            contrato = recuperar(contrato.getId());
        }
        return result;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<AssistenteRendasPatrimoniais> lerListaDeContratosParaRenovacao(AssistenteRendasPatrimoniais assistenteRendasPatrimoniais) {
        try {
            this.assistente = assistenteRendasPatrimoniais;
            contratosRenovados = Maps.newHashMap();
            RenovadorContradoRendas renovadorContradoRendas = new RenovadorContradoRendas(this, assistenteRendasPatrimoniais.getDataAtual(), assistenteRendasPatrimoniais.getUsuarioSistema(), assistenteRendasPatrimoniais.getParametroRendas(), assistenteRendasPatrimoniais.getExercicio());
            for (ContratoRendasPatrimoniais contrato : assistenteRendasPatrimoniais.getContratoRendasPatrimoniais()) {
                try {
                    contrato.setMotivoOperacao(assistenteRendasPatrimoniais.getMotivo());
                    ContratoRendasPatrimoniais renovado = renovadorContradoRendas.criarRenovacaoAutomaticaDosContratos(contrato);
                    renovado.setOriginario(contrato);
                    contratosRenovados.put(contrato, renovado);

                    if (contratosRenovados.size() >= 100) {
                        assistenteRendasPatrimoniais.getProcessos().addAll(salvarContratosRenovados());
                        contratosRenovados.clear();
                    }
                } catch (Exception e) {
                    assistenteRendasPatrimoniais.setExecutando(false);
                    logger.error("Erro no meio da renovação: {}", e);
                } finally {
                    assistenteRendasPatrimoniais.conta();
                }
            }
            if (!contratosRenovados.isEmpty()) {
                assistenteRendasPatrimoniais.getProcessos().addAll(salvarContratosRenovados());
            }
        } catch (Exception e) {
            logger.error("Erro ao ler a lista de contratos para renovação: {}", e);
        }
        return new AsyncResult<>(assistenteRendasPatrimoniais);
    }

    public List<ProcessoCalculoRendas> salvarContratosRenovados() {
        List<ProcessoCalculoRendas> processos = Lists.newArrayList();
        for (Map.Entry<ContratoRendasPatrimoniais, ContratoRendasPatrimoniais> contratos : contratosRenovados.entrySet()) {
            em.merge(contratos.getKey());
            ContratoRendasPatrimoniais renovado = em.merge(contratos.getValue());
            processos.add(em.merge(geraProcessoCalculo(renovado, assistente.getExercicio(), BigDecimal.ZERO)));
        }
        return processos;
    }

    public Integer mesVigente(Date dataOperacao, ParametroRendas parametroRendas) {
        int i = 0;
        if (parametroRendas != null) {
            int diaVencimento = parametroRendas.getDiaVencimentoParcelas().intValue();

            Calendar vencimento = Calendar.getInstance();
            vencimento.setTime(dataOperacao);
            int diaAtual = vencimento.get(Calendar.DAY_OF_MONTH);
            vencimento.set(Calendar.DAY_OF_MONTH, diaVencimento);

            if (diaAtual > diaVencimento) {
                vencimento.add(Calendar.MONTH, 1);
            }

            Calendar operacao = Calendar.getInstance();
            operacao.setTime(dataOperacao);

            Calendar fimVigencia = Calendar.getInstance();
            fimVigencia.setTime(parametroRendas.getDataFimContrato());

            while (vencimento.get(Calendar.YEAR) == operacao.get(Calendar.YEAR) && vencimento.compareTo(fimVigencia) < 0) {
                vencimento.setTime(DataUtil.adicionarMeses(vencimento.getTime(), 1));
                i++;
            }
        }
        return i;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ContratoRendasPatrimoniais salvarRenovacaoContratoAutomatico(ContratoRendasPatrimoniais contrato, ContratoRendasPatrimoniais contratoRenovado) {
        contrato = em.merge(contrato);
        contratoRenovado.setOriginario(contrato);
        em.flush();
        em.clear();
        return em.merge(contratoRenovado);
    }

    public Date ultimoDiaAnoAtual(Exercicio exercicio) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, exercicio.getAno());
        return cal.getTime();
    }

    public List<ContratoRendasPatrimoniais> recuperarContratosPorLocalizacao(Long codigoInicial, Long codigoFinal) {
        if ((codigoInicial == null || codigoInicial == 0) && (codigoFinal == null || codigoFinal == 0)) {
            return Lists.newArrayList();
        }
        String sql = "select contrato.* \n" +
            "   from contratorendaspatrimoniais contrato\n" +
            "  inner join ptocomercialcontratorendas rel on rel.contratorendaspatrimoniais_id = contrato.id\n" +
            "  inner join pontocomercial ponto on rel.pontocomercial_id = ponto.id\n " +
            "  inner join localizacao loc on ponto.localizacao_id = loc.id\n";
        String juncao = " where ";
        if (codigoInicial != null && codigoInicial > 0) {
            sql += juncao + " loc.codigo >= :codigoInicial ";
            juncao = " and ";
        }
        if (codigoFinal != null && codigoFinal > 0) {
            sql += juncao + " loc.codigo <= :codigoFinal ";
            juncao = " and ";
        }
        Query q = em.createNativeQuery(sql, ContratoRendasPatrimoniais.class);
        if (codigoInicial != null && codigoInicial > 0) {
            q.setParameter("codigoInicial", codigoInicial);
        }
        if (codigoFinal != null && codigoFinal > 0) {
            q.setParameter("codigoFinal", codigoFinal);
        }
        return q.getResultList();
    }

    public List<ContratoRendasPatrimoniais> recuperarContratosPorBox(String numeroBoxInicial, String numeroBoxFinal) {
        if ((numeroBoxInicial == null || numeroBoxInicial.trim().isEmpty()) && ((numeroBoxFinal == null || numeroBoxFinal.trim().isEmpty()))) {
            return Lists.newArrayList();
        }
        String sql = "select contrato.* \n" +
            "   from contratorendaspatrimoniais contrato\n" +
            "  inner join ptocomercialcontratorendas rel on rel.contratorendaspatrimoniais_id = contrato.id\n" +
            "  inner join pontocomercial ponto on rel.pontocomercial_id = ponto.id\n ";
        String juncao = " where ";
        if (numeroBoxInicial != null && !numeroBoxInicial.trim().isEmpty()) {
            sql += juncao + " ponto.numeroBox >= :numeroBoxInicial ";
            juncao = " and ";
        }
        if (numeroBoxFinal != null && !numeroBoxFinal.trim().isEmpty()) {
            sql += juncao + " ponto.numeroBox <= :numeroBoxFinal ";
            juncao = " and ";
        }
        Query q = em.createNativeQuery(sql, ContratoRendasPatrimoniais.class);
        if (numeroBoxInicial != null && !numeroBoxInicial.trim().isEmpty()) {
            q.setParameter("numeroBoxInicial", numeroBoxInicial);
        }
        if (numeroBoxFinal != null && !numeroBoxFinal.trim().isEmpty()) {
            q.setParameter("numeroBoxFinal", numeroBoxFinal);
        }
        return q.getResultList();
    }

    public int getQuantidadeParcelasPagas(ContratoRendasPatrimoniais contratoRendasPatrimoniais) {
        int qtde = 0;
        if (contratoRendasPatrimoniais.getOriginario() != null
            && contratoRendasPatrimoniais.getOriginario().getSituacaoContrato().equals(SituacaoContratoRendasPatrimoniais.ALTERADO)) {
            qtde += getQuantidadeParcelasPagas(contratoRendasPatrimoniais.getOriginario());
        }
        CalculoRendas calculo = recuperaCalculoRendas(contratoRendasPatrimoniais);
        if (calculo != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            List<ResultadoParcela> parcelas = consultaParcela
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId())
                .executaConsulta().getResultados();
            qtde += new Long(parcelas.stream().filter(ResultadoParcela::isPago).count()).intValue();
        }
        return qtde;
    }
}


