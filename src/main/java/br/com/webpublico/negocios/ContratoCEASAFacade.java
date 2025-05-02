/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteContratoCeasa;
import br.com.webpublico.entidadesauxiliares.RenovadorContratoCeasa;
import br.com.webpublico.enums.SituacaoContratoCEASA;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

@Stateless
public class ContratoCEASAFacade extends AbstractFacade<ContratoCEASA> {

    Map<ContratoCEASA, ContratoCEASA> contratosRenovados;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaCeasa geraDebito;
    @EJB
    private GeraValorDividaLicitacaoCeasa geraDebitoLicitacao;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ParametroRendasPatrimoniaisFacade parametroRendasPatrimoniaisFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private DAMFacade dAMFacade;
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
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private FacadeAutoGerenciado facadeAutoGerenciado;
    @EJB
    private GeraValorDividaCeasaAutomatico geraDebitoAutomatico;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    private AssistenteContratoCeasa assistente;


    public ContratoCEASAFacade() {
        super(ContratoCEASA.class);
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

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public DAMFacade getdAMFacade() {
        return dAMFacade;
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

    public void salvarNovo(ContratoCEASA entity, Exercicio exercicio) {
        try {
            ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), entity.getLotacaoVistoriadora());
            Integer ano = p.getExercicio().getAno();

            String numeroContrato = ano + getTotalContrato(entity);
            entity.setNumeroContrato(numeroContrato);

            salvar(entity, exercicio);
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addError("Ocorreu um erro", e.getMessage());
        }
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public void setTipoDoctoOficialFacade(TipoDoctoOficialFacade tipoDoctoOficialFacade) {
        this.tipoDoctoOficialFacade = tipoDoctoOficialFacade;
    }

    public ContratoCEASA salvarContrato(ContratoCEASA entity) {
        return em.merge(entity);
    }

    public void gerarDebito(ProcessoCalculoCEASA processoCalculoCEASA, ContratoCEASA contratoCEASA) throws Exception {
        geraDebito.geraDebito(processoCalculoCEASA);
        if (contratoCEASA.getValorLicitacao() != null && contratoCEASA.getValorLicitacao().compareTo(BigDecimal.ZERO) > 0) {
            geraDebitoLicitacao.geraDebito(geraProcessoCalculoLicitacao(contratoCEASA));
        }
    }

    public void gerarDebitoContrato(ProcessoCalculoCEASA processoCalculoCEASA) throws Exception {
        geraDebito.geraDebito(processoCalculoCEASA);
    }

    public void salvar(ContratoCEASA entity, Exercicio exercicio) {
        try {

            if (entity.getId() == null) {
                super.salvarNovo(entity);
            } else {
                entity = em.merge(entity);
            }

            gerarDebitoLicitacao(entity);
            geraDebito.geraDebito(gerarProcessoCalculo(entity, BigDecimal.ZERO));

        } catch (Exception e) {
            logger.error("Erro o gerar débitos do processo. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar débitos. Detalhes: " + e.getMessage());
        }
    }

    public void gerarDebitoLicitacao(ContratoCEASA contratoCEASA) {
        try {
            if (contratoCEASA.getValorLicitacao() != null && contratoCEASA.getValorLicitacao().compareTo(BigDecimal.ZERO) > 0) {
                geraDebitoLicitacao.geraDebito(geraProcessoCalculoLicitacao(contratoCEASA));
            }
        } catch (Exception e) {
            logger.error("Erro ao gerar débitos de licitação. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar débitos de licitação. Detalhes: " + e.getMessage());
        }
    }

    public String getTotalContrato(ContratoCEASA entity) {
        String sql = "SELECT COUNT(id) FROM ContratoCEASA WHERE originario_id IS null";
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


    public String getSequenciaRenovacaoContrato(ContratoCEASA entity) {
        String[] numeroContrato = entity.getNumeroContrato().split("-");
        String sql = "select max(numerocontrato) from ContratoCEASA where numerocontrato like :numeroContrato";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numeroContrato", numeroContrato[0] + "%");
//        String numero = "";
        String numeroDoContrato = "";
        if (!q.getResultList().isEmpty()) {
            numeroDoContrato = ((String) q.getResultList().get(0));
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
    }

    public ProcessoCalculoCEASA gerarProcessoCalculo(ContratoCEASA cc, BigDecimal valorContrato) {
        CalculoCEASA calculoCEASA = new CalculoCEASA();
        criarCalculo(calculoCEASA, cc, valorContrato);
        ProcessoCalculoCEASA pcc = new ProcessoCalculoCEASA();
        calculoCEASA.setProcessoCalculoCEASA(pcc);
        criaProcesso(pcc, calculoCEASA, cc);
        return em.merge(pcc);
    }

    private ProcessoCalculoLicitacaoCEASA geraProcessoCalculoLicitacao(ContratoCEASA cc) {
        CalculoLicitacaoCEASA calculoLicitacaoCEASA = new CalculoLicitacaoCEASA();
        ProcessoCalculoLicitacaoCEASA pclc = new ProcessoCalculoLicitacaoCEASA();
        calculoLicitacaoCEASA.setProcessoCalculoLicitacaoCEASA(pclc);
        criaProcessoLicitacao(pclc, calculoLicitacaoCEASA, cc);
        criaCalculoLicitacao(calculoLicitacaoCEASA, cc);

        return em.merge(pclc);
    }

    private void criaProcessoLicitacao(ProcessoCalculoLicitacaoCEASA pclc, CalculoLicitacaoCEASA calculoLicitacaoCEASA, ContratoCEASA cc) {
        pclc.getCalculos().add(calculoLicitacaoCEASA);
        pclc.setCompleto(Boolean.TRUE);
        pclc.setDataLancamento(cc.getDataInicio());
        pclc.setDivida(configuracaoTributarioFacade.retornaUltimo().getCeasaDividaLicitacao());

        ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), cc.getLotacaoVistoriadora());
        pclc.setExercicio(p.getExercicio());
    }


    public ValorDivida retornaValorDividaDoContrato(ContratoCEASA contrato) {
        Query q = em.createQuery("select vd from ValorDivida vd join vd.calculo calculo where calculo in (select c from CalculoCEASA c where c.contrato = :contrato)");
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

    public ValorDivida retornaValorDividaLicitacaoDoContrato(ContratoCEASA contrato) {
        Query q = em.createQuery("select vd from ValorDivida vd "
            + " join vd.calculo calculo "
            + " where calculo in (select c from CalculoLicitacaoCEASA c where c.contrato = :contrato)");
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

    private void criarCalculo(CalculoCEASA calculoCeasa, ContratoCEASA cc, BigDecimal valorContrato) {
        calculoCeasa.setContrato(cc);
        calculoCeasa.setDataCalculo(cc.getDataInicio());
        calculoCeasa.setSimulacao(Boolean.FALSE);
        BigDecimal valorPontos = BigDecimal.ZERO;

        BigDecimal valor;
        if (valorContrato != null && valorContrato.compareTo(BigDecimal.ZERO) > 0) {
            valor = valorContrato;
        } else {
            for (PontoComercialContratoCEASA p : cc.getPontoComercialContratoCEASAs()) {
                if (p.getPontoComercial() != null && p.getPontoComercial().getArea() != null
                    && p.getPontoComercial().getValorMetroQuadrado() != null) {
                    valorPontos = valorPontos.add(p.getValorTotalContrato());
                }
            }
            valorPontos = valorPontos.multiply(moedaFacade.recuperaValorUFMParaData(cc.getDataInicio()));
            valorPontos = valorPontos.setScale(2, RoundingMode.HALF_UP);
            valor = cc.getValorTotalRateio().add(valorPontos);
        }

        valor = valor.setScale(2, RoundingMode.HALF_UP);
        calculoCeasa.setValorEfetivo(valor);
        calculoCeasa.setValorReal(calculoCeasa.getValorEfetivo());

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cc.getLocatario().getPessoa());
        calculoPessoa.setCalculo(calculoCeasa);
        calculoCeasa.setCadastro(cc.getLocatario());
        if (calculoCeasa.getPessoas() == null) {
            calculoCeasa.setPessoas(new ArrayList<CalculoPessoa>());
        }

        criaItemDoCalculo(cc, calculoCeasa);
        calculoCeasa.getPessoas().add(calculoPessoa);

    }

    public void criaItemDoCalculo(ContratoCEASA selecionado, CalculoCEASA calculoCEASA) {
        ItensCalculoCEASA item = new ItensCalculoCEASA();
        ConfiguracaoTributario configuracao = parametroRendasPatrimoniaisFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao() != null) {
            if (selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getCalculaRateio() == null ||
                selecionado.getPontoComercialContratoCEASAs().get(0).getPontoComercial().getLocalizacao().getCalculaRateio()) {
                if (configuracao.getTributoRateio() != null) {
                    item.setValor(selecionado.getValorTotalRateio());
                    item.setTributo(configuracao.getTributoRateio());
                    item.setCalculoCEASA(calculoCEASA);
                    calculoCEASA.getItensCalculoCEASA().add(item);
                    item = new ItensCalculoCEASA();
                }
            }
        }

        if (configuracao.getCeasaTributoContrato() != null) {
            item.setValor(selecionado.getValorTotalContrato());
            item.setTributo(configuracao.getCeasaTributoContrato());
            item.setCalculoCEASA(calculoCEASA);
            calculoCEASA.getItensCalculoCEASA().add(item);
            item = new ItensCalculoCEASA();
        }
    }

    private void criaCalculoLicitacao(CalculoLicitacaoCEASA calculoLicitacaoCeasa, ContratoCEASA cc) {
        calculoLicitacaoCeasa.setContrato(cc);
        calculoLicitacaoCeasa.setDataCalculo(cc.getDataInicio());
        calculoLicitacaoCeasa.setSimulacao(Boolean.FALSE);
        calculoLicitacaoCeasa.setCadastro(cc.getLocatario());
        calculoLicitacaoCeasa.setValorEfetivo(cc.getValorLicitacao());
        calculoLicitacaoCeasa.setValorReal(cc.getValorLicitacao());

        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setPessoa(cc.getLocatario().getPessoa());
        calculoPessoa.setCalculo(calculoLicitacaoCeasa);
        if (calculoLicitacaoCeasa.getPessoas() == null) {
            calculoLicitacaoCeasa.setPessoas(new ArrayList<CalculoPessoa>());
        }
        calculoLicitacaoCeasa.getPessoas().add(calculoPessoa);
    }

    private void criaProcesso(ProcessoCalculoCEASA pcc, CalculoCEASA calculoCEASA, ContratoCEASA cc) {
        pcc.getCalculos().add(calculoCEASA);
        pcc.setCompleto(Boolean.TRUE);
        pcc.setDataLancamento(cc.getDataInicio());
        pcc.setDivida(configuracaoTributarioFacade.retornaUltimo().getCeasaDividaContrato());
        Exercicio exercicio;
        if (assistente != null) {
            exercicio = assistente.getExercicio();
        } else {
            exercicio = sistemaFacade.getExercicioCorrente();
        }
        ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(exercicio, cc.getLotacaoVistoriadora());
        pcc.setExercicio(p.getExercicio());
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
        CalculoCEASA calculo = recuperaCalculo(valorDivida);
        ContratoCEASA contrato = calculo.getContrato();
        PontoComercial ponto = contrato.getPontoComercialContratoCEASAs().get(0).getPontoComercial();
        Localizacao localizacao = ponto.getLocalizacao();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat mesAno = new SimpleDateFormat("MMM/yyyy");
        StringBuilder sb = new StringBuilder("");
        BigDecimal valorTotal = BigDecimal.ZERO;
        ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), contrato.getLotacaoVistoriadora());

        sb.append("ENDEREÇO: ").append(localizacao.getLogradouro().getNome().toUpperCase()).append(localizacao.getNumero() == null ? " " : ", " + localizacao.getNumero()).append(localizacao.getComplemento() != null ? " " : ", " + localizacao.getNumero());
        sb.append("\n");
        sb.append("BAIRRO: ").append(localizacao.getBairro().getDescricao().toUpperCase());
        sb.append("\n");
        sb.append("CONTRATO TIPO: ").append(localizacao.getTipoOcupacaoLocalizacao().getDescricao().toUpperCase());
        sb.append("\n");
        sb.append("LOCALIZAÇÃO: ").append(ponto.getDescricaoCompleta().toUpperCase());
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        if (contrato.getNumeroContrato() == null) {
            sb.append("NÚMERO DO CONTRATO: ");
        } else {
            sb.append("NUMERO DO CONTRATO: ").append(contrato.getNumeroContrato());
        }
        sb.append("\n");
        sb.append("EXERCÍCIO DO CONTRATO: " + p.getExercicio().getAno().toString());
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

    public String montaDescricaoLicitacao(ValorDivida valorDivida, boolean isLonga) throws UFMException {
        CalculoLicitacaoCEASA calculo = recuperaCalculoLicitacaoCEASA(valorDivida);
        ContratoCEASA contrato = calculo.getContrato();

        PontoComercial ponto = contrato.getPontoComercialContratoCEASAs().get(0).getPontoComercial();
        Localizacao localizacao = ponto.getLocalizacao();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat mesAno = new SimpleDateFormat("MMM/yyyy");
        StringBuilder sb = new StringBuilder("");
        BigDecimal valorTotal = BigDecimal.ZERO;
        ParametroRendas p = parametroRendasPatrimoniaisFacade.recuperaParametroRendasPorExercicioLotacaoVistoriadora(sistemaFacade.getExercicioCorrente(), contrato.getLotacaoVistoriadora());
        String atividade = "";
        String numerocnae = "";
        for (EconomicoCNAE cnae : contrato.getLocatario().getEconomicoCNAEVigentes()) {
            atividade = cnae.getCnae().getDescricaoDetalhada();
        }
        for (EconomicoCNAE cnae : contrato.getLocatario().getEconomicoCNAEVigentes()) {
            numerocnae = cnae.getCnae().getCodigoCnae();
        }

        sb.append("\n");
        sb.append("ATIVIDADE: ").append(numerocnae).append(" ").append(atividade);
        sb.append("\n");
        sb.append("LOCALIZAÇÃO: ").append(ponto.getDescricaoCompleta().toUpperCase());
        sb.append("\n");
        sb.append("\n");
        sb.append("DESCRIÇÃO DAS TAXAS: ").append(configuracaoTributarioFacade.retornaUltimo().getCeasaTributoLicitacao().getDescricao());
        sb.append("\n");
        sb.append("NÚMERO DO CONTRATO: ").append(contrato.getNumeroContrato()).append("");
        sb.append("\n");
        sb.append("EXERCÍCIO DO CONTRATO: " + p.getExercicio().getAno().toString());
        sb.append("\n");
        sb.append("DATA DO CONTRATO: ").append(sdf.format(contrato.getDataInicio()));
        sb.append("\n");
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(contrato.getDataInicio());
//        cal.add(Calendar.MONTH, 1);
//        sb.append("REF. MÊS: ").append(mesAno.format(cal.getTime()).toUpperCase());
        if (isLonga) {
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append(adicionaValor("VALOR DO DÉBITO", calculo.getValorReal(), "R$", false));
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
    public List<ContratoCEASA> lista() {
        String hql = "from ContratoCEASA order by to_number(numeroContrato) desc";
        Query q = getEntityManager().createQuery(hql);
        try {
            return q.getResultList();
        } catch (NullPointerException e) {
            return new ArrayList<ContratoCEASA>();
        }
    }

    public Boolean existemParcelasEmAberto(ContratoCEASA contrato) {
        Query q = em.createNativeQuery("select parcela.id from parcelavalordivida parcela "
            + " inner join situacaoparcelavalordivida situacao on situacao.id = parcela.situacaoatual_id "
            + " inner join valordivida on parcela.valordivida_id = valordivida.id "
            + " inner join calculoceasa on valordivida.calculo_id = calculoceasa.id "
            + " inner join contratoceasa on calculoceasa.contrato_id = contratoceasa.id "
            + " where situacao.situacaoparcela = 'EM_ABERTO'"
            + " and contratoceasa.id = :idContrato");
        q.setParameter("idContrato", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean existemParcelasEmAbertoLicitacao(ContratoCEASA contrato) {
        Query q = em.createNativeQuery("select parcela.id from parcelavalordivida parcela "
            + " inner join situacaoparcelavalordivida situacao on situacao.id = parcela.situacaoatual_id "
            + " inner join valordivida on parcela.valordivida_id = valordivida.id "
            + " inner join CalculoLicitacaoCEASA on valordivida.calculo_id = CalculoLicitacaoCEASA.id "
            + " inner join contratoceasa on CalculoLicitacaoCEASA.contrato_id = contratoceasa.id "
            + " where situacao.situacaoparcela = 'EM_ABERTO'"
            + " and contratoceasa.id = :idContrato");
        q.setParameter("idContrato", contrato.getId());
        return !q.getResultList().isEmpty();
    }

    private CalculoCEASA recuperaCalculo(ValorDivida valorDivida) {
        Thread.dumpStack();
        Query consulta = em.createQuery("select vd.calculo from ValorDivida vd where vd = :valorDivida").setParameter("valorDivida", valorDivida);
        return (CalculoCEASA) consulta.getResultList().get(0);
    }

    private CalculoLicitacaoCEASA recuperaCalculoLicitacaoCEASA(ValorDivida valorDivida) {
        Query consulta = em.createQuery("select vd.calculo from ValorDivida vd where vd = :valorDivida").setParameter("valorDivida", valorDivida);
        return (CalculoLicitacaoCEASA) consulta.getResultList().get(0);
    }

    public ValorDivida recuperaValordivida(ValorDivida valorDividaContrato) {
        ValorDivida ValorDividaRetorno = this.em.find(ValorDivida.class, valorDividaContrato.getId());
        ValorDividaRetorno.getParcelaValorDividas().size();
        return ValorDividaRetorno;
    }

    @Override
    public ContratoCEASA recuperar(Object id) {
        ContratoCEASA contrato = em.find(ContratoCEASA.class, id);
        contrato.getPontoComercialContratoCEASAs().size();
        return contrato;
    }

    public DocumentoOficial geraNovoDocumento(ContratoCEASA contratoCEASA, SistemaControlador sistemaControlador) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        TipoDoctoOficial tipo = configuracaoTributario.getCeasaDoctoOficialContrato();

        try {
            return documentoOficialFacade.geraDocumentoContratoCEASA(contratoCEASA, null, tipo, contratoCEASA.getLocatario().getPessoa(), sistemaControlador);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public DocumentoOficial geraDocumentoExistente(ContratoCEASA contratoCEASA, SistemaControlador sistemaControlador) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        TipoDoctoOficial tipo = configuracaoTributario.getCeasaDoctoOficialContrato();

        try {
            return documentoOficialFacade.geraDocumentoContratoCEASA(contratoCEASA, contratoCEASA.getDocumentoOficial(), tipo, contratoCEASA.getLocatario().getPessoa(), sistemaControlador);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ContratoCEASA> contratosVigentes() {
        String sql = "select cd.*, " +
            "            ct.*  " +
            "     from contratoceasa ct " +
            " inner join Cadastro cd on cd.id = ct.id " +
            " inner join lotacaovistoriadora lot on lot.id = ct.lotacaovistoriadora_id " +
            " where ct.situacaocontrato = :situacao " +
            "       and (sysdate between ct.datainicio and add_months(ct.datainicio, ct.periodovigencia)) " +
            "       and lot.descricao = :descricao";
        Query q = em.createNativeQuery(sql, ContratoCEASA.class);
        q.setParameter("situacao", SituacaoContratoCEASA.ATIVO.name());
        q.setParameter("descricao", "CEASA");
        return q.getResultList();
    }

    public CalculoCEASA recuperaCalculoCeasaPorContratoCeasa(ContratoCEASA contrato) {
        try {
            return (CalculoCEASA) em.createQuery("select cl from CalculoCEASA cl where cl.contrato = :contrato").setParameter("contrato", contrato).getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            return null;
        }
    }

    public CalculoLicitacaoCEASA recuperaCalculoLicitacaoCeasaPorContratoCeasa(ContratoCEASA contrato) {
        try {
            return (CalculoLicitacaoCEASA) em.createQuery("select cl from CalculoLicitacaoCEASA cl where cl.contrato = :contrato").setParameter("contrato", contrato).getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            return null;
        }
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PontoComercialContratoCEASA> recuperarPontoDoContrato(ContratoCEASA contrato) {
        try {
            String sql = ("select ponto.* from PTOCOMERCIALCONTRATOCEASA ponto " +
                " inner join contratoceasa contrato on ponto.contratoceasa_id = contrato.id " +
                " where contrato.id = :idContrato");
            Query q = em.createNativeQuery(sql, PontoComercialContratoCEASA.class);
            q.setParameter("idContrato", contrato.getId());
            if (!q.getResultList().isEmpty()) {
                return (List<PontoComercialContratoCEASA>) q.getResultList();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<ContratoCEASA> recuperarContratoCeasa(CadastroEconomico cadastro, Long idContratoAtual) {
        String sql = "SELECT CONTRATO.* FROM CONTRATOCEASA CONTRATO " +
            " WHERE LOCATARIO_ID = :locatario " +
            "   AND CONTRATO.SITUACAOCONTRATO = :situacao";
        if (idContratoAtual != null) {
            sql += "   and contrato.id <> :idContratoAtual ";
        }
        Query q = em.createNativeQuery(sql, ContratoCEASA.class);
        q.setParameter("situacao", SituacaoContratoCEASA.ATIVO.name());
        q.setParameter("locatario", cadastro.getId());
        if (idContratoAtual != null) {
            q.setParameter("idContratoAtual", idContratoAtual);
        }
        if (!q.getResultList().isEmpty()) {
            List<ContratoCEASA> c = q.getResultList();
            for (ContratoCEASA contrato : c) {
                contrato.getPontoComercialContratoCEASAs().size();
            }
            return c;
        }
        return null;
    }

    public List<ContratoCEASA> buscarContratosQuePodemSerRenovados(Date dataOperacao) {
        String sql = " select distinct contrato.* from contratoceasa contrato " +
            " LEFT JOIN PTOCOMERCIALCONTRATOCEASA PT ON PT.CONTRATOCEASA_ID= CONTRATO.ID " +
            " where PT.CONTRATOCEASA_ID is not null and  contrato.situacaocontrato = 'ATIVO' " +
            " and contrato.dataFim < :dataOperacao ";
        Query q = em.createNativeQuery(sql, ContratoCEASA.class);
        q.setParameter("dataOperacao", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    @Asynchronous
    public Future<AssistenteContratoCeasa> lerListaDeContratosParaRenovacao(AssistenteContratoCeasa assistenteCeasa) {
        try {
            this.assistente = assistenteCeasa;
            contratosRenovados = Maps.newHashMap();
            RenovadorContratoCeasa renovadorContratoCeasa = new RenovadorContratoCeasa(this, assistenteCeasa.getDataAtual(),
                assistenteCeasa.getUsuarioSistema(), assistenteCeasa.getParametroRendas(), assistenteCeasa.getExercicio());
            for (ContratoCEASA contrato : assistenteCeasa.getContratos()) {
                try {
                    contrato.setMotivoOperacao(assistenteCeasa.getMotivo());
                    ContratoCEASA renovado = renovadorContratoCeasa.criarRenovacaoAutomaticaDosContratos(contrato);
                    renovado.setOriginario(contrato);
                    contratosRenovados.put(contrato, renovado);

                    if (contratosRenovados.size() >= 100) {
                        assistenteCeasa.getProcessosDeCalculo().addAll(salvarContratosRenovados());
                        contratosRenovados.clear();
                    }
                } catch (Exception e) {
                    logger.error("Erro no meio da renovação: {}", e);
                } finally {
                    assistenteCeasa.conta();
                }
            }
            if (!contratosRenovados.isEmpty()) {
                assistenteCeasa.getProcessosDeCalculo().addAll(salvarContratosRenovados());
            }
        } catch (Exception ex) {
            assistenteCeasa.setExecutando(false);
            logger.error("Erro ao lerListaDeContratosParaRenovacao: {}", ex);
        }
        return new AsyncResult<>(assistenteCeasa);
    }

    public List<ProcessoCalculoCEASA> salvarContratosRenovados() {
        List<ProcessoCalculoCEASA> processos = Lists.newArrayList();
        for (Map.Entry<ContratoCEASA, ContratoCEASA> contratos : contratosRenovados.entrySet()) {
            em.merge(contratos.getKey());
            ContratoCEASA renovado = em.merge(contratos.getValue());
            processos.add(em.merge(gerarProcessoCalculo(renovado, BigDecimal.ZERO)));
        }
        return processos;
    }

    public Integer mesVigente(Date dataOperacao, ParametroRendas parametroRendas) {
        return contratoRendasPatrimoniaisFacade.mesVigente(dataOperacao, parametroRendas);
    }
}
