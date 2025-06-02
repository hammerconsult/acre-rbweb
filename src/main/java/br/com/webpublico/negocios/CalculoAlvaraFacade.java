package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.ImprimeAlvara;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.AtributoParamAmbiental;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.ParametroDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.WSAlvara;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class CalculoAlvaraFacade extends CalculoExecutorDepoisDePagar<ProcessoCalculoAlvara> {

    public static final int PRIMEIRA_PARCELA = 1;
    public final String SEM_ASSINATURA = "SEM ASSINATURA NA ORIGEM";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private FaixaAlvaraFacade faixaAlvaraFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private ParametrosCalcAmbientalFacade parametrosCalcAmbientalFacade;
    @EJB
    private AlvaraFacade alvaraFacade;
    @EJB
    private GeraValorDividaAlvara geraValorDividaAlvara;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private VistoriaFacade vistoriaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AlteracaoCmcFacade alteracaoCmcFacade;
    @EJB
    private IntegracaoRedeSimFacade redeSimFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private GeraValorDividaAlvaraSanitario geraValorDividaAlvaraSanitario;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private ProcessoSuspensaoCassacaoAlvaraFacade processoSuspensaoCassacaoAlvaraFacade;

    public CalculoAlvaraFacade() {
        super(ProcessoCalculoAlvara.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ProcessoSuspensaoCassacaoAlvaraFacade getProcessoSuspensaoCassacaoAlvaraFacade() {
        return processoSuspensaoCassacaoAlvaraFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public GeraValorDividaAlvaraSanitario getGeraValorDividaAlvaraSanitario() {
        return geraValorDividaAlvaraSanitario;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    @Override
    public ProcessoCalculoAlvara recuperar(Object id) {
        ProcessoCalculoAlvara processoCalculoAlvara = em.find(ProcessoCalculoAlvara.class, id);
        Hibernate.initialize(processoCalculoAlvara.getCnaes());
        Hibernate.initialize(processoCalculoAlvara.getCalculosAlvara());
        Hibernate.initialize(processoCalculoAlvara.getHistoricosAlvara());
        Hibernate.initialize(processoCalculoAlvara.getEnderecosAlvara());
        Hibernate.initialize(processoCalculoAlvara.getCaracteristicasAlvara());
        Hibernate.initialize(processoCalculoAlvara.getHorariosAlvara());
        Hibernate.initialize(processoCalculoAlvara.getCadastroEconomico().getListaBCECaracFuncionamento());
        Hibernate.initialize(processoCalculoAlvara.getCadastroEconomico().getEnquadramentosAmbientais());
        Hibernate.initialize(processoCalculoAlvara.getCadastroEconomico().getEconomicoCNAE());
        Hibernate.initialize(processoCalculoAlvara.getCadastroEconomico().getListaEnderecoCadastroEconomico());
        for (CalculoAlvara calculoAlvara : processoCalculoAlvara.getCalculosAlvara()) {
            Hibernate.initialize(calculoAlvara.getPessoas());
            Hibernate.initialize(calculoAlvara.getItensAlvara());
            for (ItemCalculoAlvara itemCalculoAlvara : calculoAlvara.getItensAlvara()) {
                Hibernate.initialize(itemCalculoAlvara.getItensAmbientais());
            }
        }
        Hibernate.initialize(processoCalculoAlvara.getLogsRedeSim());
        for (LogAlvaraRedeSim log : processoCalculoAlvara.getLogsRedeSim()) {
            Hibernate.initialize(log.getArquivo().getDetentorArquivoComposicao());
        }
        if (processoCalculoAlvara.getAlvara() != null) {
            Hibernate.initialize(processoCalculoAlvara.getAlvara().getHorariosDeFuncionamento());
            Hibernate.initialize(processoCalculoAlvara.getAlvara().getCnaeAlvaras());
            Hibernate.initialize(processoCalculoAlvara.getAlvara().getEnderecosAlvara());
        }
        return processoCalculoAlvara;
    }

    public ProcessoCalculoAlvara recuperarParaEnvioRedeSim(Long id) {
        ProcessoCalculoAlvara processoCalculoAlvara = recuperar(id);
        Hibernate.initialize(processoCalculoAlvara.getCadastroEconomico().getEconomicoCNAE());
        return processoCalculoAlvara;
    }

    public CalculoAlvara recuperarCalculoAlvara(Long id) {
        return em.find(CalculoAlvara.class, id);
    }

    public void salvarHistorico(HistoricoImpressaoAlvara historico) {
        em.merge(historico);
    }

    private Exercicio recuperarExercicioPeloAno(Integer ano) {
        return exercicioFacade.recuperarExercicioPeloAno(ano);
    }

    public Long montarProximoCodigoPorExercicio(Exercicio exercicio) {
        return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProcessoCalculoAlvara.class,
            "codigo", "exercicio", exercicio);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EconomicoCNAE> buscarCnaesVigentesCMC(Long idCmc) {
        String sql = " select ecnae.* from economicocnae ecnae " +
            " inner join cnae on ecnae.cnae_id = cnae.id " +
            " where ecnae.cadastroeconomico_id = :idCmc " +
            " and cnae.situacao = :situacaoCnae " +
            " and trunc(coalesce(ecnae.fim, to_date(:dataAtual, 'dd/MM/yyyy'))) >= to_date(:dataAtual, 'dd/MM/yyyy')";

        Query q = em.createNativeQuery(sql, EconomicoCNAE.class);
        q.setParameter("idCmc", idCmc);
        q.setParameter("situacaoCnae", CNAE.Situacao.ATIVO.name());
        q.setParameter("dataAtual", DataUtil.getDataFormatada(new Date()));
        List<EconomicoCNAE> cnaes = q.getResultList();

        return (cnaes != null && !cnaes.isEmpty()) ? cnaes : Lists.<EconomicoCNAE>newArrayList();
    }

    public DAM buscarDAMPeloIdParcela(Long idParcela, boolean isAtual) {
        String sql = " select distinct dam.* from dam " +
            " inner join itemdam item on dam.id = item.dam_id" +
            " where item.parcela_id = :idParcela ";
        if (isAtual) {
            sql += " and dam.situacao <> :cancelado ";
        }
        sql += " order by dam.vencimento ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", idParcela);

        if (isAtual) {
            q.setParameter("cancelado", DAM.Situacao.CANCELADO.name());
        }

        List<DAM> dans = q.getResultList();

        if (dans != null && !dans.isEmpty()) {
            DAM dam = em.find(DAM.class, dans.get(0).getId());
            Hibernate.initialize(dam.getItens());
            return dam;
        }

        return null;
    }

    public boolean hasAlvaraEfetivadoNoExercicio(Long idCmc, Long idExercicio) {
        String sql = "select proc.* from processocalculoalvara proc " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and proc.exercicio_id = :idExercicio " +
            " and proc.situacaocalculoalvara in :situacoes ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));

        return !q.getResultList().isEmpty();
    }

    public List<ProcessoCalculoAlvara> buscarProcessosEfetivadosNoExercicio(Long idCmc, Long idExercicio) {
        String sql = "select pc.id from processocalculoalvara proc " +
            " inner join processocalculo pc on pc.id = proc.id " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and pc.exercicio_id = :idExercicio " +
            " and proc.situacaocalculoalvara in :situacoes " +
            " order by proc.codigo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("idExercicio", idExercicio);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));
        List<BigDecimal> result = q.getResultList();
        List<ProcessoCalculoAlvara> retorno = Lists.newArrayList();
        for (BigDecimal idProcesso : result) {
            retorno.add(recuperar(idProcesso.longValue()));
        }
        return retorno;
    }

    public ProcessoCalculoAlvara buscarProcessoCalculoPeloIdParcela(Long idParcela) {
        String sql = "select processo.*, proc.* from processocalculoalvara proc " +
            "inner join processocalculo processo on processo.id = proc.id " +
            "inner join calculoalvara calcalvara on proc.id = calcalvara.processocalculoalvara_id " +
            "inner join calculo cal on cal.id = calcalvara.id " +
            "inner join valordivida vd on cal.id = vd.calculo_id " +
            "inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            "where pvd.id = :idParcela ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoAlvara.class);
        q.setParameter("idParcela", idParcela);

        List<ProcessoCalculoAlvara> processos = q.getResultList();

        if (processos != null && !processos.isEmpty()) {
            return processos.get(0);
        }
        return null;
    }

    public CadastroEconomico recuperarCmc(Long idCmc) {
        return em.find(CadastroEconomico.class, idCmc);
    }

    public Alvara buscarAlvaraPorId(Long idAlvara) {
        if (idAlvara != null) {
            String sql = " select alvara.* from alvara " +
                " where alvara.id = :idAlvara ";

            Query q = em.createNativeQuery(sql, Alvara.class);
            q.setParameter("idAlvara", idAlvara);

            List<Alvara> alvaras = q.getResultList();

            if (alvaras != null && !alvaras.isEmpty()) {
                Alvara alvara = alvaras.get(0);
                Hibernate.initialize(alvara.getRecibosImpressaoAlvara());
                Hibernate.initialize(alvara.getCnaeAlvaras());
                return alvara;
            }
        }
        return null;
    }

    public List<ReciboImpressaoAlvara> buscarRecibosDoAlvaraPorId(Long idAlvara) {
        String sql = "select recibo.* from reciboimpressaoalvara recibo " +
            " inner join alvara on recibo.alvara_id = alvara.id " +
            " where alvara.id = :idAlvara ";

        Query q = em.createNativeQuery(sql, ReciboImpressaoAlvara.class);
        q.setParameter("idAlvara", idAlvara);

        List<ReciboImpressaoAlvara> recibos = q.getResultList();
        return recibos != null ? recibos : Lists.<ReciboImpressaoAlvara>newArrayList();
    }

    public List<VOAlvara> definirEmissaoAlvara(CadastroEconomico cmc, TipoAlvara tipoAlvara, ConfiguracaoTributario config, ValidacaoException ve) {
        List<VOAlvara> alvarasDoCmc = buscarAlvarasPeloIdDoCmc(cmc.getId(), tipoAlvara);
        for (VOAlvara alvara : alvarasDoCmc) {
            alvara.setExercicio(recuperarExercicioPeloAno(alvara.getAno()));
            definirPermissaoImpressao(alvara, config, ve, cmc);
        }
        return alvarasDoCmc;
    }

    public HistoricoImpressaoAlvara adicionarProcessoOrCalculoHistorico(VOAlvara voAlvara) {
        HistoricoImpressaoAlvara historico = new HistoricoImpressaoAlvara();
        Object obj = Util.buscarEntidade(voAlvara.getTipoAlvara().getEntidade(), voAlvara.getId(), em);

        if (obj instanceof ProcessoCalculoAlvara) {
            historico.setProcessoCalculoAlvara((ProcessoCalculoAlvara) obj);
        } else {
            historico.setCalculo((Calculo) obj);
        }
        return historico;
    }

    public void definirPermissaoImpressao(VOAlvara voAlvara, ConfiguracaoTributario configuracao, ValidacaoException ve, CadastroEconomico cmc) {
        boolean preencheu = false;
        boolean dispensado = true;
        boolean somenteRiscoI = true;
        for (VOAlvaraCnaes cnae : voAlvara.getCnaes()) {
            if (!hasCnaeDispensado(cnae.getIdCnae())) {
                dispensado = false;
            }
            if (hasCnaeDiferenteRiscoI(cnae.getIdCnae())) {
                somenteRiscoI = false;
            }
        }
        if (dispensado && (!somenteRiscoI || TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(voAlvara.getTipoAlvara()))) {
            voAlvara.setEmitir(false);
            preencheu = true;
            if (ve != null && cmc != null) {
                if (ve.getMensagens().isEmpty()) {
                    ve.adicionarMensagemInfo(SummaryMessages.OPERACAO_REALIZADA, montarMensagemDispensa(cmc));
                }
            }
        }
        if (!preencheu) {
            if (VOAlvara.TipoVoAlvara.PROCESSO_CALCULO.equals(voAlvara.getTipoVoAlvara())) {
                if (vistoriaFacade.hasVistoriaNaoFinalizadaProcessoCalcAlvara(voAlvara.getId(), true)) {
                    voAlvara.setEmitir(false);
                    preencheu = true;
                }
            } else {
                if (vistoriaFacade.temVistoriaNaoFinalizada(voAlvara.getId(), true)) {
                    voAlvara.setEmitir(false);
                    preencheu = true;
                }
            }
        }
        if (!preencheu) {
            if (!configuracao.getTipoVerificacaoDebitoAlvara().equals(TipoVerificacaoDebitoAlvara.PERMITIR)) {
                voAlvara.setEmitir(permitirEmissaoAlvara(voAlvara, configuracao.getTipoVerificacaoDebitoAlvara()));
            } else {
                voAlvara.setEmitir(true);
            }
        }
    }

    private boolean hasCnaeDispensado(Long idCnae) {
        String sql = " select * from cnae " +
            " where cnae.id = :idCnae " +
            " and cnae.dispensado = :dispensado ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCnae", idCnae);
        q.setParameter("dispensado", true);

        return !q.getResultList().isEmpty();
    }

    private boolean hasCnaeDiferenteRiscoI(Long idCnae) {
        String sql = " select * from cnae " +
            " where cnae.id = :idCnae " +
            " and cnae.grauderisco <> :riscoI ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCnae", idCnae);
        q.setParameter("riscoI", GrauDeRiscoDTO.BAIXO_RISCO_A.name());

        return !q.getResultList().isEmpty();
    }

    private String montarMensagemDispensa(CadastroEconomico cmc) {
        return "O CMC " + cmc.getCmcNomeCpfCnpj() + " encontra-se dispensado do cartaz de alvará " +
            "pois seus CNAE(s) estão marcados como dispensados.";
    }

    private boolean permitirEmissaoAlvara(VOAlvara voAlvara, TipoVerificacaoDebitoAlvara tipoVerificacao) {
        Calendar calendar = Calendar.getInstance();
        if (voAlvara.getIdAlvara() != null && calendar.get(Calendar.YEAR) == voAlvara.getAno() && !TipoVerificacaoDebitoAlvara.
            NAO_PERMITIR_DEBITO_ALVARA.equals(tipoVerificacao)) {

            if (!permitirEmitirAlvaraPorTaxasDiversas(voAlvara.getIdCadastro()))
                return false;
            if (!permitirEmitirAlvaraPorDividaDiversa(voAlvara.getIdCadastro()))
                return false;
            if (!permitirEmitirAlvaraPorDebitos(voAlvara.getIdCadastro()))
                return false;
            return permitirEmitirAlvaraPelaArquivoDoParcelamento(voAlvara);
        }
        return permitirEmitirAlvaraPeloDebitoDoAlvara(voAlvara);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraPeloDebitoDoAlvara(VOAlvara voAlvara) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();

        for (VOAlvaraItens item : voAlvara.getItens()) {
            adicionarParcelasConsulta(parcelas, item.getId());
        }

        for (ResultadoParcela parcela : parcelas) {
            if (parcela.isEmAberto()) {
                return false;
            } else if (parcela.isInscrito()) {
                return permitirEmitirAlvaraDividaAtivaDaParcelaInscrita(parcela);
            }
        }
        return parcelas.isEmpty();
    }

    private void adicionarParcelasConsulta(List<ResultadoParcela> parcelas, Long idCalculo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.
            EM_ABERTO, SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA));

        parcelas.addAll(consultaParcela.executaConsulta().getResultados());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraDividaAtivaDaParcelaInscrita(ResultadoParcela parcela) {
        String sql = "select pvd.id, pvd.vencimento, spvd.situacaoParcela from ParcelaValorDivida pvd" +
            " inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            " inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id " +
            " inner join ItemInscricaoDividaAtiva item on item.id = vd.calculo_id " +
            " inner join InscricaoDividaParcela parc on parc.itemInscricaoDividaAtiva_id = item.id " +
            " where parc.parcelaValorDivida_id = :idParcela";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", parcela.getIdParcela());
        List<VOParcelaValorDivida> parcelas = Lists.newArrayList();
        List<Object[]> lista = q.getResultList();
        for (Object[] obj : lista) {
            VOParcelaValorDivida voParcela = new VOParcelaValorDivida();
            voParcela.setIdParcela(((BigDecimal) obj[0]).longValue());
            voParcela.setVencimento((Date) obj[1]);
            voParcela.setSituacaoParcela(SituacaoParcela.valueOf((String) obj[2]));
            parcelas.add(voParcela);
        }
        for (VOParcelaValorDivida parcelaValorDivida : parcelas) {
            if (SituacaoParcela.EM_ABERTO.equals(parcelaValorDivida.getSituacaoParcela())) {
                return false;
            } else if (SituacaoParcela.PARCELADO.equals(parcelaValorDivida.getSituacaoParcela())) {
                return permitirEmitirAlvaraDividaAtivaParcelada(parcelaValorDivida.getIdParcela());
            }
        }
        return true;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraDividaAtivaParcelada(Long idParcela) {
        List<Long> idParcelamentos = processoParcelamentoFacade.recuperarIDDoParcelamentoDaParcelaOriginal(idParcela);
        List<ResultadoParcela> resultadoParcelas = Lists.newArrayList();
        for (Long idParcelamento : idParcelamentos) {
            resultadoParcelas.addAll(processoParcelamentoFacade.recuperaParcelasOriginadasParcelamento(idParcelamento));
        }

        boolean esteValorDividaTemParcelaPaga = false;
        boolean esteValorDividaTemParcelaVencida = false;
        for (ResultadoParcela resultadoParcela : resultadoParcelas) {
            if (resultadoParcela.isVencidoEEmAberto(new Date())) {
                esteValorDividaTemParcelaVencida = true;
            } else if (resultadoParcela.isPago() && resultadoParcela.getSequenciaParcela() == 1) {
                esteValorDividaTemParcelaPaga = true;
            } else if (resultadoParcela.isParcelado()) {
                boolean permitir = permitirEmitirAlvaraDividaAtivaParcelada(resultadoParcela.getIdParcela());
                if (!permitir) {
                    return false;
                }
            }
        }
        return esteValorDividaTemParcelaPaga && !esteValorDividaTemParcelaVencida;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraPorTaxasDiversas(Long idCadastro) {
        List<ResultadoParcela> parcelaTaxasDiversas = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.TAXAS_DIVERSAS)
            .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCadastro)
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
            .addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, sistemaFacade.getDataOperacao())
            .executaConsulta().getResultados();
        for (ResultadoParcela parcela : parcelaTaxasDiversas) {
            if (tributoTaxasDividasDiversasFacade.parcelaComAlvaraBloqueada(parcela.getIdParcela()) && parcela.isEmAberto()) {
                return false;
            }
        }
        return true;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraPorDividaDiversa(Long idCmc) {
        List<ResultadoParcela> parcelaDividasDiversas = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.DIVIDA_DIVERSA)
            .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCmc)
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
            .addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, sistemaFacade.getDataOperacao())
            .executaConsulta().getResultados();
        List<Long> idsValorDivida = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelaDividasDiversas) {
            if (tributoTaxasDividasDiversasFacade.parcelaComAlvaraBloqueada(parcela.getIdParcela()) && parcela.isEmAberto()) {
                if (!idsValorDivida.contains(parcela.getIdValorDivida())) {
                    idsValorDivida.add(parcela.getIdValorDivida());
                }
            }
        }
        for (Long idValorDivida : idsValorDivida) {
            boolean podeEmitir = permitirEmitirAlvaraParcelado(idValorDivida);
            if (!podeEmitir) {
                return false;
            }
        }
        return true;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraPorDebitos(Long idCadastro) {
        List<Calculo.TipoCalculo> tiposCalculoNaoPesquisados = Lists.newArrayList();
        tiposCalculoNaoPesquisados.add(Calculo.TipoCalculo.DIVIDA_DIVERSA);
        tiposCalculoNaoPesquisados.add(Calculo.TipoCalculo.TAXAS_DIVERSAS);

        List<ResultadoParcela> parcelas = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.NOT_IN, tiposCalculoNaoPesquisados)
            .addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCadastro)
            .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO)
            .executaConsulta().getResultados();

        for (ResultadoParcela parcela : parcelas) {
            if (Calculo.TipoCalculo.PARCELAMENTO.equals(parcela.getTipoCalculoEnumValue())) {
                return permitirEmitirAlvaraParcelado(parcela.getIdValorDivida());
            } else {
                if (parcela.isVencido(sistemaFacade.getDataOperacao())) {
                    return false;
                }
            }
        }
        return true;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraParcelado(Long idValorDivida) {
        List<ResultadoParcela> parcelasDoValorDivida = new ConsultaParcela()
            .addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.IGUAL, idValorDivida)
            .executaConsulta().getResultados();
        boolean esteValorDividaTemParcelaPaga = false;
        boolean esteValorDividaTemParcelaVencida = false;
        for (ResultadoParcela resultadoParcela : parcelasDoValorDivida) {
            if (resultadoParcela.isVencidoEEmAberto(new Date())) {
                esteValorDividaTemParcelaVencida = true;
            }
            if (resultadoParcela.isPago() && resultadoParcela.getSequenciaParcela() == PRIMEIRA_PARCELA) {
                esteValorDividaTemParcelaPaga = true;
            }
        }
        return esteValorDividaTemParcelaPaga && !esteValorDividaTemParcelaVencida;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private boolean permitirEmitirAlvaraPelaArquivoDoParcelamento(VOAlvara voAlvara) {
        List<ResultadoParcela> resultadoParcelas = Lists.newArrayList();

        for (VOAlvaraItens item : voAlvara.getItens()) {
            resultadoParcelas.addAll(processoParcelamentoFacade.buscarArvoreParcelamentoPartindoDoCalculo(item.getId()));
        }

        if (resultadoParcelas.isEmpty()) {
            return true;
        }

        if (resultadoParcelas.size() > 1) {
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if (resultadoParcela.isEmAberto()) {
                    if (Calculo.TipoCalculo.PARCELAMENTO.equals(resultadoParcela.getTipoCalculoEnumValue())) {
                        return permitirEmitirAlvaraParcelado(resultadoParcela.getIdValorDivida());
                    } else {
                        return false;
                    }
                }
            }
        }
        return resultadoParcelas.get(0).isPago();
    }

    public VOAlvara preencherVOAlvaraPorIdAlvara(Long idAlvara) {
        List<VOAlvara> voAlvaras = buscarAlvarasPeloIdAlvara(idAlvara);
        if (!voAlvaras.isEmpty()) {
            return voAlvaras.get(0);
        }
        return null;
    }

    private List<VOAlvara> buscarAlvarasPeloIdDoCmc(Long idCmc, TipoAlvara tipoAlvara) {
        String where = " where (pca.situacaocalculoalvara in (:situacoes) " +
            "            or coalesce(cal.situacaocalculoalvara, caf.situacaocalculoalvara, cas.situacaocalculoalvara) = :calculado) " +
            "     and al.cadastroeconomico_id = :idCmc ";
        if (tipoAlvara != null) {
            where += " and al.tipoalvara = :tipoAlvara ";
        }

        String sql = montarSqlAlvaras(where);

        Query q = em.createNativeQuery(sql);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));
        q.setParameter("calculado", SituacaoCalculoAlvara.CALCULADO.name());
        q.setParameter("idCmc", idCmc);
        q.setParameter("grauRiscoBaixo", GrauDeRiscoDTO.BAIXO_RISCO_A.name());

        if (tipoAlvara != null) {
            q.setParameter("tipoAlvara", tipoAlvara.name());
        }

        return preencherCamposAlvara(idCmc, q);
    }

    private List<VOAlvara> buscarAlvarasPeloIdAlvara(Long idAlvara) {
        String where = " where al.id = :idAlvara ";
        String sql = montarSqlAlvaras(where);

        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlvara", idAlvara);
        q.setParameter("grauRiscoBaixo", GrauDeRiscoDTO.BAIXO_RISCO_A.name());
        return preencherCamposAlvara(idAlvara, q);
    }

    private List<VOAlvara> preencherCamposAlvara(Long idCmc, Query q) {
        List<Object[]> alvarasConsulta = q.getResultList();
        List<VOAlvara> alvaras = Lists.newArrayList();

        for (Object[] alvaraConsulta : alvarasConsulta) {
            TipoAlvara tipoAlvara = Util.traduzirEnum(TipoAlvara.class, alvaraConsulta[5] != null ? (String) alvaraConsulta[5] : null);
            VOAlvara.TipoVoAlvara tipoVoAlvara = TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(tipoAlvara)
                ? VOAlvara.TipoVoAlvara.PROCESSO_CALCULO : VOAlvara.TipoVoAlvara.CALCULO;

            VOAlvara alvara = criarVOAlvara(alvaraConsulta, tipoVoAlvara);
            alvara.setCnaes(buscarCnaesVOAlvara(idCmc));

            if (TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(tipoAlvara)) {
                alvara.setItens(buscarItensProcCalcVO(alvara.getId()));
            } else {
                VOAlvaraItens item = new VOAlvaraItens();
                item.setValor((BigDecimal) alvaraConsulta[8]);
                item.setVencimento((Date) alvaraConsulta[9]);
                item.setIsento(alvaraConsulta[10].equals(BigDecimal.valueOf(1)));
                item.setId(alvara.getId());

                alvara.setItens(Lists.newArrayList(item));
            }
            alvaras.add(alvara);
        }
        return alvaras;
    }

    private VOAlvara criarVOAlvara(Object[] objeto, VOAlvara.TipoVoAlvara tipoVoAlvara) {
        VOAlvara alvara = new VOAlvara();
        alvara.setId(objeto[0] != null ? ((BigDecimal) objeto[0]).longValue() : null);
        alvara.setIdAlvara(objeto[1] != null ? ((BigDecimal) objeto[1]).longValue() : null);
        alvara.setIdCadastro(objeto[2] != null ? ((BigDecimal) objeto[2]).longValue() : null);
        alvara.setAno(objeto[3] != null ? ((BigDecimal) objeto[3]).intValue() : null);
        alvara.setSituacaoCalculoAlvara(objeto[4] != null ? SituacaoCalculoAlvara.valueOf((String) objeto[4]) : null);
        alvara.setTipoAlvara(objeto[5] != null ? TipoAlvara.valueOf((String) objeto[5]) : null);
        alvara.setDataCalculo((Date) objeto[6]);
        alvara.setVencimentoAlvara((Date) objeto[7]);
        alvara.setPessoaJuridica(objeto[11].equals(BigDecimal.valueOf(1)));
        alvara.setTipoVoAlvara(tipoVoAlvara);
        alvara.setDispensaLicenciamento(((Number) objeto[12]).intValue() == 0);
        return alvara;
    }

    private String montarSqlAlvaras(String where) {
        String sql = " select coalesce(pca.id, calculo.id) as calculo_id, " +
            "                 al.id as alvara_id, " +
            "                 cmc.id, " +
            "                 ex.ano," +
            "                 coalesce(pca.situacaocalculoalvara, cal.situacaocalculoalvara, caf.situacaocalculoalvara, " +
            "                          cas.situacaocalculoalvara) as situacao, " +
            "                 al.tipoalvara, " +
            "                 coalesce(calculo.dataCalculo,proc.datalancamento) as lancamento, " +
            "                 al.vencimento," +
            "                 calculo.valorefetivo, " +
            "                 coalesce(cal.datavencimento, caf.vencimento, cas.datavencimento) as calculo_vencimento, " +
            "                 calculo.isento, (case when pj.id is not null then 1 else 0 end) as tipo_pessoa," +
            "                 (select count(1) " +
            "                    from cnaealvara ca " +
            "                   inner join cnae c on c.id = ca.cnae_id" +
            "                  where ca.alvara_id = al.id" +
            "                    and c.grauDeRisco != :grauRiscoBaixo) " +
            " from alvara al " +
            " inner join cadastroeconomico cmc on al.cadastroeconomico_id = cmc.id" +
            " inner join pessoa pes on cmc.pessoa_id = pes.id" +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " left join processocalculoalvara pca on al.id = pca.alvara_id " +
            " left join calculoalvaralocalizacao cal on al.id = cal.alvara_id " +
            " left join calculoalvarafunc caf on al.id = caf.alvara_id " +
            " left join calculoalvarasanitario cas on al.id = cas.alvara_id" +
            " left join calculo on calculo.id = coalesce(cal.id, caf.id, cas.id) " +
            " left join processocalculo proc on proc.id = coalesce(cal.processocalculoalvaraloc_id, caf.processocalculo_id, " +
            "                                                      cas.processocalculoalvarasan_id, pca.id) " +
            " inner join exercicio ex on ex.id = coalesce(pca.exercicio_id, proc.exercicio_id) ";
        sql += where;
        sql += " order by coalesce(calculo.dataCalculo,proc.datalancamento) desc";

        return sql;
    }

    public List<VOAlvaraItens> buscarItensProcCalcVO(Long idProcesso) {
        String sql = " select calculo.valorefetivo, calc.vencimento, calculo.isento, calc.id " +
            " from calculoalvara calc " +
            " inner join calculo on calculo.id = calc.id " +
            " where calc.processocalculoalvara_id = :idProcesso " +
            " and calc.controleCalculo = :tipoControle ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("tipoControle", TipoControleCalculoAlvara.CALCULO.name());

        List<Object[]> objetos = q.getResultList();
        List<VOAlvaraItens> itens = Lists.newArrayList();

        for (Object[] objeto : objetos) {
            VOAlvaraItens item = new VOAlvaraItens();
            item.setValor((BigDecimal) objeto[0]);
            item.setVencimento((Date) objeto[1]);
            item.setIsento(objeto[2].equals(BigDecimal.valueOf(1)));
            item.setId(objeto[3] != null ? ((BigDecimal) objeto[3]).longValue() : null);

            itens.add(item);
        }
        return itens;
    }

    private List<VOAlvaraCnaes> buscarCnaesVOAlvara(Long idCmc) {
        Alvara alvara = em.find(Alvara.class, idCmc);
        if (alvara != null) {
            idCmc = alvara.getCadastroEconomico().getId();
        }

        String sql = " select cnae.id from cnae " +
            " inner join economicocnae ec on cnae.id = ec.cnae_id " +
            " where ec.cadastroeconomico_id = :idCmc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);

        List<Object> objetos = q.getResultList();
        List<VOAlvaraCnaes> cnaes = Lists.newArrayList();

        for (Object objeto : objetos) {
            VOAlvaraCnaes cnae = new VOAlvaraCnaes();
            cnae.setIdCnae(objeto != null ? ((BigDecimal) objeto).longValue() : null);

            cnaes.add(cnae);
        }
        return cnaes;
    }

    public List<Alvara> buscarAlvaraVigentePorCmc(Long idCmc) {
        String sql = " SELECT A.* " +
            " FROM ALVARA A " +
            " WHERE TRUNC(A.VENCIMENTO) >= TRUNC(SYSDATE) " +
            " AND A.CADASTROECONOMICO_ID = :idCmc ";

        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("idCmc", idCmc);
        List<Alvara> result = q.getResultList();
        List<Alvara> retorno = Lists.newArrayList();
        for (Alvara alvara : result) {
            if (!processoSuspensaoCassacaoAlvaraFacade.alvaraCassado(alvara.getId())) {
                alvara = alvaraFacade.recuperar(alvara.getId());
                retorno.add(alvara);
            }
        }
        return retorno;
    }

    public List<BCECaracFuncionamento> buscarCaracteristicasFuncionamentoDoCadastro(CadastroEconomico cadastroEconomico) {
        return cadastroEconomicoFacade.recuperarCaracteristicasFuncionamento(cadastroEconomico);
    }

    public List<CadastroEconomico> buscarCmcPorRazaoSocialAndCnpj(String parte) {
        return cadastroEconomicoFacade.buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(parte);
    }

    public EnquadramentoFiscal buscarEnquadramentoFiscalVigente(CadastroEconomico cadastroEconomico) {
        return cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(cadastroEconomico);
    }

    public UsuarioSistema recuperarUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Exercicio recuperarExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public Date recuperarDataAtual() {
        return sistemaFacade.getDataOperacao();
    }

    public Pessoa recuperarHorariosDeFuncionamento(Long id) {
        return pessoaFacade.recuperarHorariosDeFuncionamento(id);
    }

    public List<Telefone> buscarTelefonesPessoa(Pessoa pessoa) {
        return pessoaFacade.telefonePorPessoa(pessoa);
    }

    public List<ResultadoParcela> buscarArvoreParcelamentoPartindoDoCalculo(Long idCalculo) {
        return processoParcelamentoFacade.buscarArvoreParcelamentoPartindoDoCalculo(idCalculo);
    }

    public ConfiguracaoTributario buscarConfiguracaoTriutarioVigente() {
        return configuracaoTributarioFacade.retornaUltimo();
    }

    public CadastroEconomico recuperarParaAlvara(Long id) {
        return cadastroEconomicoFacade.recuperarParaAlvara(id);
    }

    public UsuarioSistema recuperarUsuarioPorLogin(String login) {
        return usuarioSistemaFacade.findOneByLogin(login);
    }

    public Alvara adicionaReciboImpressaoAlvara(Alvara alvara, ReciboImpressaoAlvara recibo) {
        return alvaraFacade.adicionaReciboImpressaoAlvara(alvara, recibo);
    }

    public boolean hasAutorizacaoImprimirAlvaraNaoPago() {
        return usuarioSistemaFacade.validaAutorizacaoUsuario(recuperarUsuarioCorrente(), AutorizacaoTributario.IMPRIMIR_ALVARA_FUNCIONAMENTO_NAO_PAGO);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public ProcessoCalculoAlvara calcularAlvara(ProcessoCalculoAlvara processoCalculoAlvara, Exercicio exercicioCorrente, boolean isRecalculo, Boolean gerarTaxaLocalizacao, ConfiguracaoTributario configuracaoTributario) {
        AssistenteCalculoAlvara assistente = inicializarAssistente(processoCalculoAlvara, exercicioCorrente, isRecalculo, gerarTaxaLocalizacao, configuracaoTributario);
        gerarCalculos(assistente);
        atualizarSituacaoProcessoComCnaeDeInteresseDoEstado(assistente);
        assistente.getProcCalculo().setDescricao("Alvará de Localização e Funcionamento");
        return assistente.getProcCalculo();
    }

    private AssistenteCalculoAlvara inicializarAssistente(ProcessoCalculoAlvara processoCalculoAlvara, Exercicio exercicioCorrente, boolean isRecalculo, Boolean gerarTaxaLocalizacao, ConfiguracaoTributario configuracaoTributario) {
        AssistenteCalculoAlvara assistente = new AssistenteCalculoAlvara();
        assistente.setProcCalculo(processoCalculoAlvara);
        assistente.setConfigTributario(configuracaoTributario);
        assistente.setCmc(processoCalculoAlvara.getCadastroEconomico());
        try {
            assistente.setClassificacaoAtividade(assistente.getCmc().getClassificacaoAtividade());
            assistente.setAreaUtil(BigDecimal.valueOf(assistente.getCmc().getAreaUtilizacao()));
            assistente.setMapaDividas(preencherConfiguracoesAlvaraPorDivida(assistente));
            assistente.setExercicio(exercicioCorrente);
            assistente.setRecalculo(isRecalculo);
            assistente.setGerarTaxaLocalizacao(gerarTaxaLocalizacao);
        } catch (Exception ex) {
            logger.error("Erro no inicializarAssistente: {}", ex);
        }
        return assistente;
    }

    private void gerarCalculos(AssistenteCalculoAlvara assistente) {
        try {
            List<ConfiguracaoAlvara> configuracoesAlvara = buscarConfiguracoesDoCalculo(assistente);
            Map<TipoAlvara, ItemFaixaAlvara> mapaItemFaixaAlvara = montarMapaItemFaixaAlvaraPorTipoAlvara(assistente);
            for (ConfiguracaoAlvara configuracaoAlvara : configuracoesAlvara) {
                CalculoAlvara calculoAlvara = criarCalculoAlvara(assistente.getProcCalculo(), assistente.getCmc(),
                    configuracaoAlvara, assistente.isRecalculo());

                ItemFaixaAlvara itemFaixa = mapaItemFaixaAlvara.get(configuracaoAlvara.getTipoAlvara());
                BigDecimal valorTotalAlvara = itemFaixa != null && itemFaixa.getValorTaxaUFMAno() != null ?
                    itemFaixa.getValorTaxaUFMAno() : BigDecimal.ZERO;

                if (configuracaoAlvara.getTipoAlvara().isSanitario()) {
                    valorTotalAlvara = buscarValorItemFaixaSanitario(assistente);
                }

                AssistenteTipoCalculoAlvara assistenteTipoCalculoAlvara = calcularValorAlvara(assistente,
                    configuracaoAlvara, valorTotalAlvara, TipoPagamento.ANUAL, null, null);

                calculoAlvara.setTipoCalculoAlvara(assistenteTipoCalculoAlvara.getTipoCalculo());
                calculoAlvara.setIsento(isCalculoAlvaraIsento(assistente.getCmc()));

                valorTotalAlvara = assistenteTipoCalculoAlvara.getValorTotalAlvara();

                if (valorTotalAlvara != null && valorTotalAlvara.compareTo(BigDecimal.ZERO) > 0) {
                    calculoAlvara.getItensAlvara().add(criarItemCalcAlvara(calculoAlvara,
                        configuracaoAlvara.getTributo(), valorTotalAlvara, null, null));
                }

                if (configuracaoAlvara.getTipoAlvara().isAmbiental()) {
                    valorTotalAlvara = calcularLicencaAmbiental(assistente, valorTotalAlvara,
                        configuracaoAlvara, calculoAlvara);
                }

                if (valorTotalAlvara != null && valorTotalAlvara.compareTo(BigDecimal.ZERO) > 0
                    || TipoAlvara.AMBIENTAL.equals(calculoAlvara.getTipoAlvara())) {
                    calculoAlvara.setValorReal(valorTotalAlvara);
                    calculoAlvara.setValorEfetivo(valorTotalAlvara);
                    adicionarPessoaAoCalculo(calculoAlvara, assistente.getCmc().getPessoa());

                    if (!TipoAlvara.LOCALIZACAO.equals(calculoAlvara.getTipoAlvara())) {
                        if (hasDiferencaNoValorDoNovoCalculo(assistente, calculoAlvara)) {
                            continue;
                        }
                    }

                    List<CNAE> cnaes = buscarCnaesComInteresseAmbiental(assistente.getProcCalculo().getCnaes());
                    if (calculoAlvara.getValorEfetivo().compareTo(BigDecimal.ZERO) > 0 || (TipoAlvara.AMBIENTAL.equals(calculoAlvara.getTipoAlvara()) && !cnaes.isEmpty())) {
                        removerItensCalculo(calculoAlvara);
                        assistente.getProcCalculo().getCalculosAlvara().add(calculoAlvara);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro no gerarCalculos: {}", ex);
        }
    }

    public Boolean isCalculoAlvaraIsento(CadastroEconomico cadastroEconomico) {
        EnquadramentoFiscal enquadramentoFiscal = cadastroEconomicoFacade.buscarEnquadramentoFiscalVigente(cadastroEconomico);
        if (enquadramentoFiscal != null && enquadramentoFiscal.isMei()) {
            return true;
        }
        List<EconomicoCNAE> listaEconomicoCNAE = cadastroEconomicoFacade.buscarListaEconomicoCNAE(cadastroEconomico);
        if (listaEconomicoCNAE != null &&
            listaEconomicoCNAE
                .stream()
                .allMatch(ec -> GrauDeRiscoDTO.BAIXO_RISCO_A.equals(ec.getCnae().getGrauDeRisco()))) {
            return true;
        }
        return false;
    }

    public List<ConfiguracaoAlvara> buscarConfiguracoesDoCalculo(AssistenteCalculoAlvara assistente) {
        List<ConfiguracaoAlvara> configuracoes = Lists.newArrayList();
        for (List<ConfiguracaoAlvara> configuracoesAlvara : Lists.newArrayList(assistente.getMapaDividas().values())) {
            for (ConfiguracaoAlvara configuracaoAlvara : configuracoesAlvara) {
                if (configuracaoAlvara.getTipoAlvara().isFuncionamentoOrAmbiental()) {
                    configuracoes.add(configuracaoAlvara);
                } else if (configuracaoAlvara.getTipoAlvara().isSanitario() && isInteresseSanitarioSemInteresseDoEstado(assistente.getProcCalculo().getCnaes())) {
                    configuracoes.add(configuracaoAlvara);
                } else if (configuracaoAlvara.getTipoAlvara().isLocalizacao() && canLancarAlvaraLocalizacao(assistente)) {
                    configuracoes.add(configuracaoAlvara);
                }
            }
        }
        return configuracoes;
    }

    private boolean canLancarAlvaraLocalizacao(AssistenteCalculoAlvara assistente) {
        return (assistente.gerarTaxaLocalizacao() || ((canCalcularAlvaraLocalizacao(assistente.getCmc(), assistente.getProcCalculo()) &&
            !assistente.naoGerarTaxaLocalizacao())));
    }

    public Map<TipoAlvara, ItemFaixaAlvara> montarMapaItemFaixaAlvaraPorTipoAlvara(AssistenteCalculoAlvara assistente) {
        Map<TipoAlvara, ItemFaixaAlvara> mapaItemFaixaAlvara = Maps.newHashMap();

        for (TipoAlvara tipoAlvara : TipoAlvara.values()) {
            if (!mapaItemFaixaAlvara.containsKey(tipoAlvara)) {
                ItemFaixaAlvara itemFaixaAlvara = faixaAlvaraFacade.recuperarPorClassificacaoArea(assistente.getClassificacaoAtividade(), tipoAlvara,
                    assistente.getExercicio(), assistente.getAreaUtil());
                mapaItemFaixaAlvara.put(tipoAlvara, itemFaixaAlvara);
            }
        }
        return mapaItemFaixaAlvara;
    }

    private void removerItensCalculo(CalculoAlvara calculoAlvara) {
        Iterator<ItemCalculoAlvara> iteratorItens = calculoAlvara.getItensAlvara().iterator();
        while (iteratorItens.hasNext()) {
            ItemCalculoAlvara item = iteratorItens.next();
            BigDecimal valorEfetivo = item.getValorEfetivo();
            if (valorEfetivo == null || valorEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
                iteratorItens.remove();
            }
        }
    }

    public boolean canCalcularAlvaraLocalizacao(CadastroEconomico cmc, ProcessoCalculoAlvara processo) {
        if (isPrimeiroCalculo(cmc.getId(), true)) {
            return true;
        }
        return alteracaoCmcFacade.hasAlteracaoCadastro(cmc.getId());
    }

    private boolean hasAlvaraLocalizacaoLancado(Long idCmc) {
        String sql = " select proc.* from processocalculoalvara proc " +
            " inner join calculoalvara calc on proc.id = calc.processocalculoalvara_id " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and proc.situacaocalculoalvara in :situacoes " +
            " and calc.tipoalvara = :tipoAlvara ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));
        q.setParameter("tipoAlvara", TipoAlvara.LOCALIZACAO.name());

        return !q.getResultList().isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean hasDiferencaNoValorDoNovoCalculo(AssistenteCalculoAlvara assistente, CalculoAlvara calculoAlvara) {
        try {
            boolean alterouItem = false;
            boolean hasParcelaFuncionamentoOrSanitariaAberta = false;
            if (assistente.isRecalculo() && !calculoAlvara.getTipoAlvara().isAmbiental()) {
                List<CalculoAlvara> calculos = buscarCalculosDoProcesso(calculoAlvara);
                if (calculos != null && !calculos.isEmpty()) {
                    for (CalculoAlvara calculoAnterior : calculos) {
                        if (calculoAlvara.getDivida().getId().equals(calculoAnterior.getDivida().getId())) {
                            if (calculoAnterior.getControleCalculo().isAguardandoCancelamento()) {
                                alterouItem = true;
                                calculoAlvara.setVencimento(calculoAnterior.getVencimento());
                                continue;
                            }
                            if (calculoAlvara.getTipoAlvara().isFuncionamentoOrSanitaria()) {
                                hasParcelaFuncionamentoOrSanitariaAberta = !buscarParcelasEmAbertoDosCalculos(
                                    Lists.newArrayList(calculoAnterior.getId())).isEmpty();
                            }
                            for (ItemCalculoAlvara itemNovo : calculoAlvara.getItensAlvara()) {
                                for (ItemCalculoAlvara itemAnterior : calculoAnterior.getItensAlvara()) {

                                    if (itemNovo.getTributo() != null && itemAnterior.getTributo() != null &&
                                        itemNovo.getTributo().equals(itemAnterior.getTributo())) {

                                        if ((TipoAlvara.FUNCIONAMENTO.equals(calculoAlvara.getTipoAlvara()) &&
                                            itemNovo.getCaracFuncionamento() != null && itemAnterior.getCaracFuncionamento() != null &&
                                            !itemNovo.getCaracFuncionamento().getId().equals(itemAnterior.getCaracFuncionamento().getId()))) {
                                            continue;
                                        }

                                        if (!calculoAlvara.getTipoAlvara().isFuncionamentoOrSanitaria() || (calculoAlvara.getTipoAlvara().isFuncionamentoOrSanitaria() && !hasParcelaFuncionamentoOrSanitariaAberta)) {
                                            if (itemNovo.getValorEfetivo().compareTo(itemAnterior.getValorEfetivo()) > 0) {
                                                BigDecimal diferenca = itemNovo.getValorEfetivo().subtract(itemAnterior.getValorEfetivo());
                                                calculoAlvara.setValorEfetivo(calculoAlvara.getValorEfetivo().subtract(itemNovo.getValorEfetivo().subtract(diferenca)));
                                                itemNovo.setValorEfetivo(diferenca);
                                            } else {
                                                calculoAlvara.setValorEfetivo(calculoAlvara.getValorEfetivo().subtract(itemNovo.getValorEfetivo()));
                                                calculoAlvara.setValorReal(calculoAlvara.getValorReal().subtract(itemNovo.getValorReal()));
                                                itemNovo.setValorEfetivo(BigDecimal.ZERO);
                                            }
                                        }
                                        alterouItem = true;
                                    }
                                }
                            }
                            if (calculoAlvara.getTipoAlvara().isFuncionamentoOrSanitaria() && hasParcelaFuncionamentoOrSanitariaAberta) {
                                if (calculoAlvara.getValorEfetivo().compareTo(calculoAnterior.getValorEfetivo()) != 0 ||
                                    !calculoAlvara.getItensAlvara().equals(calculoAnterior.getItensAlvara())) {

                                    calculoAlvara.setVencimento(calculoAnterior.getVencimento());

                                    for (CalculoAlvara calculo : assistente.getProcCalculo().getCalculosAlvara()) {
                                        if (calculo.getId() != null && calculo.getId().equals(calculoAnterior.getId())) {
                                            calculo.setControleCalculo(TipoControleCalculoAlvara.AGUARDANDO_CANCELAMENTO);
                                        }
                                    }
                                } else {
                                    calculoAlvara.setValorEfetivo(BigDecimal.ZERO);
                                }
                            }
                        }
                    }
                    return !alterouItem;
                }
            }
        } catch (Exception ex) {
            logger.error("Erro em hasDiferencaNoValorDoNovoCalculo: {} ", ex);
        }
        return false;
    }

    private void validarConfiguracao(ValidacaoException ve, ConfiguracaoAlvara configuracaoAlvara, TipoAlvara tipoAlvara, Long idCmc) {
        if (ve != null) {
            if (configuracaoAlvara.getDivida() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            }
            if (configuracaoAlvara.getTributo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tributo das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            }
            if (configuracaoAlvara.getTipoAlvara() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Alvará das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            }
            if (configuracaoAlvara.getTipoCalculoAlvara() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cálculo das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            } else {
                if (TipoCalculoAlvara.FIXO_UFMRB.equals(configuracaoAlvara.getTipoCalculoAlvara()) && configuracaoAlvara.getValorUFMFixoCalculoAlvara() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo em UFM do Cálculo de Alvará das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
                }
            }
            if (configuracaoAlvara.getTipoCalculoRenovacaoAlvara() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cálculo de Renovação das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            } else {
                if (TipoCalculoAlvara.FIXO_UFMRB.equals(configuracaoAlvara.getTipoCalculoRenovacaoAlvara()) && configuracaoAlvara.getValorUFMFixoCalcRenovAlvara() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo em UFM do Cálculo de Renovação do Alvará das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
                }
            }
            if (isPrimeiroCalculo(idCmc, false)) {
                if (configuracaoAlvara.getTipoPrimeiroCalculo() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Primeiro Cálculo das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
                } else {
                    if (TipoCalculoAlvara.FIXO_UFMRB.equals(configuracaoAlvara.getTipoPrimeiroCalculo()) && configuracaoAlvara.getValorUFMFixoPrimeiroCalculo() == null) {
                        ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Fixo em UFM do Primeiro Cálculo das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
                    }
                }
            }
            if (configuracaoAlvara.getDiasVencDebito() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Dias de Vencimento do Débito das Configurações do Alvará do tipo " + tipoAlvara.getDescricaoSimples() + " deve ser preenchido.");
            }
        }
    }

    public void validarCalculo(ProcessoCalculoAlvara processo, ValidacaoException ve, boolean lancarExeption) {
        validarCnaesInativos(processo, ve);
        validarExercicioAlvara(processo, ve);

        if (processo.getCadastroEconomico().getClassificacaoAtividade() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Classificação da Atividade informada para o Cadastro Econômico.");
        } else if (processo.getCadastroEconomico().getAreaUtilizacao() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Área de Utilização do CMC: " + processo.getCadastroEconomico().getCmcNomeCpfCnpj() + " está vazia ou é" +
                " menor/igual a zero. Portanto o cálculo não pode ser realizado.");
        } else {
            validarAlvaraFuncionamento(processo, ve, processo.getCadastroEconomico().getClassificacaoAtividade());
            validarAlvaraLocalizacao(processo, ve, processo.getCadastroEconomico().getClassificacaoAtividade());
            validarAlvaraSanitario(processo, ve, processo.getCadastroEconomico().getClassificacaoAtividade());
            validarAlvaraAmbiental(processo, ve);
        }

        if (lancarExeption) {
            ve.lancarException();
        }
    }

    private void validarAlvaraAmbiental(ProcessoCalculoAlvara processo, ValidacaoException ve) {
        List<CNAE> cnaes = buscarCnaesComInteresseAmbiental(processo.getCnaes());
        if (!cnaes.isEmpty()) {

            ConfiguracaoAlvara configAmbiental = buscarConfiguracaoAlvaraPorTipos(TipoAlvara.AMBIENTAL);
            if (configAmbiental == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem dívidas Ambientais configuradas nas Configurações do Tributário");
            } else {
                validarConfiguracao(ve, configAmbiental, TipoAlvara.SANITARIO, processo.getCadastroEconomico().getId());
            }

            CadastroEconomico cmc = em.find(CadastroEconomico.class, processo.getCadastroEconomico().getId());
            Hibernate.initialize(cmc.getEnquadramentosAmbientais());
            Hibernate.initialize(cmc.getListaEnderecoCadastroEconomico());

            if (cmc.getEnquadramentosAmbientais().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Enquadramento Ambiental configurado para o CMC: " +
                    processo.getCadastroEconomico().getCmcNomeCpfCnpj());
            }
            for (CNAE cnae : cnaes) {
                List<ParametrosCalcAmbiental> parametros = buscarParametrosAmbientais(cnae.getId());
                if (parametros.isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Parâmetro de Cálculo Ambiental configurado para o CNAE: " +
                        cnae.getCodigoCnae() + " - " + cnae.getDescricaoReduzida());
                } else {
                    for (ParametrosCalcAmbiental parametro : parametros) {
                        if (parametro.getLicencaAmbiental() == null) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("O Campo Tipo de Licença Ambiental do Parâmetro do Cálculo Ambiental referente ao CNAE " +
                                cnae.getCodigoCnae() + " - " + cnae.getDescricaoReduzida() + ", deve ser informado.");
                        } else if (!buscarParametroValorAmbiental(parametro.getLicencaAmbiental())) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem Valores configurados para o Tipo de Licença Ambiental: " +
                                parametro.getLicencaAmbiental().getDescricao() + " nas Configurações do Tributário.");
                        } else {
                            validarEnquadramentoAmbientalDoCMC(ve, cmc, parametro);
                        }
                    }
                }
            }
        }
    }

    private void validarAlvaraSanitario(ProcessoCalculoAlvara processo, ValidacaoException ve, ClassificacaoAtividade classificacaoAtividade) {
        boolean interesseSanitario = isInteresseSanitarioSemInteresseDoEstado(processo.getCnaes());
        if (interesseSanitario) {
            ConfiguracaoAlvara configSanitaria = buscarConfiguracaoAlvaraPorTipos(TipoAlvara.SANITARIO);
            if (configSanitaria == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem dívidas Sanitárias configuradas nas Configurações do Tributário");
            } else {
                validarItemFaixaAlvara(processo, ve, classificacaoAtividade, configSanitaria);
                validarConfiguracao(ve, configSanitaria, TipoAlvara.SANITARIO, processo.getCadastroEconomico().getId());
            }
        }
    }

    private void validarAlvaraLocalizacao(ProcessoCalculoAlvara processo, ValidacaoException ve, ClassificacaoAtividade classificacaoAtividade) {
        Boolean alvaraLancado = canCalcularAlvaraLocalizacao(processo.getCadastroEconomico(), processo);
        if (alvaraLancado == null || !alvaraLancado) {
            ConfiguracaoAlvara configLocalizacao = buscarConfiguracaoAlvaraPorTipos(TipoAlvara.LOCALIZACAO);
            if (configLocalizacao == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem dívidas de Localização configuradas nas Configurações do Tributário.");
            } else {
                validarItemFaixaAlvara(processo, ve, classificacaoAtividade, configLocalizacao);

                validarConfiguracao(ve, configLocalizacao, TipoAlvara.FUNCIONAMENTO, processo.getCadastroEconomico().getId());
            }
        }
    }

    private void validarAlvaraFuncionamento(ProcessoCalculoAlvara processo, ValidacaoException ve, ClassificacaoAtividade classificacaoAtividade) {
        ConfiguracaoAlvara configFuncionamento = buscarConfiguracaoAlvaraPorTipos(TipoAlvara.FUNCIONAMENTO);
        if (configFuncionamento == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem dívidas de Funcionamento configuradas nas Configurações do Tributário");
        } else {
            validarItemFaixaAlvara(processo, ve, classificacaoAtividade, configFuncionamento);
            validarConfiguracao(ve, configFuncionamento, TipoAlvara.FUNCIONAMENTO, processo.getCadastroEconomico().getId());
        }
    }

    private void validarItemFaixaAlvara(ProcessoCalculoAlvara processo, ValidacaoException ve, ClassificacaoAtividade classificacaoAtividade, ConfiguracaoAlvara configuracaoAlvara) {
        ItemFaixaAlvara itemFaixaAlvara = faixaAlvaraFacade.recuperarPorClassificacaoArea(classificacaoAtividade,
            configuracaoAlvara.getTipoAlvara(), processo.getExercicio(), BigDecimal.valueOf(processo.getCadastroEconomico().getAreaUtilizacao()));

        if (itemFaixaAlvara == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existe Item de Faixa para o Alvará de " + configuracaoAlvara.getTipoAlvara().getDescricaoSimples());
        }
    }

    private void validarExercicioAlvara(ProcessoCalculoAlvara processo, ValidacaoException ve) {
        Calendar dataAbertura = Calendar.getInstance();
        dataAbertura.setTime(processo.getCadastroEconomico().getAbertura());
        if (dataAbertura.get(Calendar.YEAR) > processo.getExercicio().getAno()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício do Alvará não pode ser menor que o ano de abertura da empresa.");
        }
    }

    private void validarCnaesInativos(ProcessoCalculoAlvara processo, ValidacaoException ve) {
        for (CNAEProcessoCalculoAlvara cnae : processo.getCnaes()) {
            if (CNAE.Situacao.INATIVO.equals(cnae.getCnae().getSituacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível calcular o alvará para um C.M.C. que possua um CNAE Inativo.");
                break;
            }
        }
    }

    private boolean isInteresseSanitarioSemInteresseDoEstado(List<CNAEProcessoCalculoAlvara> cnaes) {
        if (cnaes.stream().anyMatch(cp -> cp.getExercidaNoLocal() && cp.getCnae().getInteresseDoEstado())) {
            return false;
        }
        return cnaes.stream().anyMatch(cp -> cp.getExercidaNoLocal() && cp.getCnae().getFiscalizacaoSanitaria());
    }

    private void validarEnquadramentoAmbientalDoCMC(ValidacaoException ve, CadastroEconomico cmc, ParametrosCalcAmbiental parametro) {
        if (!cmc.getEnquadramentosAmbientais().isEmpty() && parametro != null) {
            for (EnquadramentoAmbiental enquadramento : cmc.getEnquadramentosAmbientais()) {
                for (AtributosCalculoAmbiental atributoCalculo : parametro.getAtributosCalculo()) {
                    Object valor = Util.buscarValorPorClasseAndSuperClasse(EnquadramentoAmbiental.class, enquadramento, atributoCalculo.getAtributo().
                        getNomeCampoEnquadramento());

                    if (valor == null) {
                        adicionarMensagemEnqAmbiental(ve, cmc, atributoCalculo, false);
                    } else {
                        if (valor instanceof BigDecimal && ((BigDecimal) valor).compareTo(BigDecimal.ZERO) <= 0) {
                            adicionarMensagemEnqAmbiental(ve, cmc, atributoCalculo, true);
                        }
                    }
                }
            }
        }
    }

    private void adicionarMensagemEnqAmbiental(ValidacaoException ve, CadastroEconomico cmc, AtributosCalculoAmbiental atributoCalc, boolean isCampoValor) {
        ve.adicionarMensagemDeOperacaoNaoPermitida("O campo " + atributoCalc.getAtributo().getDescricao() +
            " do Enquadramento Ambiental do CMC: " + cmc.getCmcNomeCpfCnpj() + " não pode ser vazio" + (isCampoValor ?
            " e deve ser maior que zero(0)." : "."));
    }

    private ConfiguracaoAlvara buscarConfiguracaoAlvaraPorTipos(TipoAlvara tipoAlvara) {
        String sql = " select distinct config_alvara.* from configuracaoalvara config_alvara " +
            " where config_alvara.tipoalvara = :tipoAlvara ";

        Query q = em.createNativeQuery(sql, ConfiguracaoAlvara.class);
        q.setParameter("tipoAlvara", tipoAlvara.name());
        List<ConfiguracaoAlvara> configuracoes = q.getResultList();
        return (configuracoes != null && !configuracoes.isEmpty()) ? configuracoes.get(0) : null;
    }

    public List<CNAE> buscarCnaesComInteresseAmbiental(List<CNAEProcessoCalculoAlvara> cnaesProcesso) {
        List<CNAE> cnaesRetorno = Lists.newArrayList();
        for (CNAEProcessoCalculoAlvara cnaeProcesso : cnaesProcesso) {
            if (cnaeProcesso.getCnae().getMeioAmbiente()) {
                cnaesRetorno.add(cnaeProcesso.getCnae());
            }
        }
        return cnaesRetorno;
    }

    public List<ParametrosCalcAmbiental> buscarParametrosAmbientais(Long idCnae) {
        String sql = " select param.* from parametroscalcambiental param " +
            " where param.cnae_id = :idCnae ";

        Query q = em.createNativeQuery(sql, ParametrosCalcAmbiental.class);
        q.setParameter("idCnae", idCnae);

        List<ParametrosCalcAmbiental> parametros = q.getResultList();
        return (parametros != null) ? parametros : Lists.<ParametrosCalcAmbiental>newArrayList();
    }

    public boolean buscarParametroValorAmbiental(TipoLicencaAmbiental tipoLicenca) {
        String sql = " select param_valor.* from parametrovaloralvaraamb param_valor " +
            " where param_valor.tipolicencaambiental = :tipoLocenca ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoLocenca", tipoLicenca.name());
        return !q.getResultList().isEmpty();
    }

    private BigDecimal calcularLicencaAmbiental(AssistenteCalculoAlvara assistente, BigDecimal valorTotalAlvara, ConfiguracaoAlvara configuracaoAlvara, CalculoAlvara calculoAlvara) {
        EnquadramentoAmbiental enquadramentoAmbiental = null;
        List<EnquadramentoAmbiental> ambientais = cadastroEconomicoFacade.buscarEnquadramentosAmbientais(assistente.getCmc());
        if (!ambientais.isEmpty()) {
            for (EnquadramentoAmbiental enqAmbiental : ambientais) {
                if (enqAmbiental.isVigente()) {
                    enquadramentoAmbiental = (EnquadramentoAmbiental) Util.clonarObjeto(enqAmbiental);
                }

                Map<AtributoParamAmbiental, Object> atributos = Maps.newHashMap();
                if (enquadramentoAmbiental != null) {
                    preencherMapaAtributosAmbientais(enquadramentoAmbiental, atributos);
                }

                List<ItemCalculoAmbiental> itensAmbientais = Lists.newArrayList();
                valorTotalAlvara = adicionarValorPorCnaeAmbiental(assistente, valorTotalAlvara, atributos, itensAmbientais);
                valorTotalAlvara = calcularValorAlvara(assistente, configuracaoAlvara, valorTotalAlvara, TipoPagamento.ANUAL, itensAmbientais, null).getValorTotalAlvara();
                calculoAlvara.getItensAlvara().add(criarItemCalcAlvara(calculoAlvara, configuracaoAlvara.getTributo(), valorTotalAlvara, itensAmbientais, null));
            }
        }
        return valorTotalAlvara;
    }

    private void preencherMapaAtributosAmbientais(EnquadramentoAmbiental enquadramentoAmbiental, Map<AtributoParamAmbiental, Object> atributos) {
        atributos.put(AtributoParamAmbiental.AREA_CONSTRUIDA, enquadramentoAmbiental.getAreaConstruida());
        atributos.put(AtributoParamAmbiental.CAPACIDADE_INSTALADA, enquadramentoAmbiental.getCapacidadeInstalada());
        atributos.put(AtributoParamAmbiental.MATERIA_PRIMA, enquadramentoAmbiental.getTipoMateriaPrima());
        atributos.put(AtributoParamAmbiental.LOCALIZACAO, enquadramentoAmbiental.getTipoLocalizacao());
        atributos.put(AtributoParamAmbiental.GERACAO_RUIDOS_VIBRACAO, enquadramentoAmbiental.getGeracaoRuidosEVibracao());
        atributos.put(AtributoParamAmbiental.AREA_DESTINADA_CACAMBAS, enquadramentoAmbiental.getCobertaImpermeavel());
        atributos.put(AtributoParamAmbiental.ESPECIES_PROTEGIDAS_POR_LEI, enquadramentoAmbiental.getEspeciesProtegidaPorLei());
        atributos.put(AtributoParamAmbiental.QUANTIDADE_ESPECIES_SUPRIMIDAS, enquadramentoAmbiental.getQuantidadeEspeciesSuprimidas());
        atributos.put(AtributoParamAmbiental.AREA_TERRENO_PARTICULAR, enquadramentoAmbiental.getAreaTerreno());
    }

    private BigDecimal adicionarValorPorCnaeAmbiental(AssistenteCalculoAlvara assistente, BigDecimal valorTotalAlvara, Map<AtributoParamAmbiental, Object> atributos, List<ItemCalculoAmbiental> itensAmbientais) {
        for (CNAEProcessoCalculoAlvara cnaeProcesso : assistente.getProcCalculo().getCnaes()) {
            if (isCnaeInteresseAmbiental(cnaeProcesso)) {

                CNAE cnae = cnaeProcesso.getCnae();
                List<ParametrosCalcAmbiental> parametros = parametrosCalcAmbientalFacade.recuperarParametrosPorCnae(cnae.getId());

                for (ParametrosCalcAmbiental parametro : parametros) {
                    BigDecimal variacaoClasse = BigDecimal.ZERO;
                    if (!atributos.isEmpty()) {
                        BigDecimal valoracao = BigDecimal.ZERO;
                        int numValoracoes = 0;
                        for (AtributosCalculoAmbiental atributoCalculo : parametro.getAtributosCalculo()) {
                            if (atributos.get(atributoCalculo.getAtributo()) instanceof BigDecimal) {
                                if (((BigDecimal) atributos.get(atributoCalculo.getAtributo())).compareTo(atributoCalculo.getValorInicial()) >= 0 &&
                                    ((BigDecimal) atributos.get(atributoCalculo.getAtributo())).compareTo(atributoCalculo.getValorFinal()) <= 0) {

                                    if (atributoCalculo.getValoracao() != null) {
                                        valoracao = valoracao.add(atributoCalculo.getValoracao());
                                        numValoracoes++;
                                    }
                                }
                            }
                            if (atributos.get(atributoCalculo.getAtributo()) instanceof Enum) {
                                if (atributoCalculo.getValoracao() != null) {
                                    valoracao = valoracao.add(atributoCalculo.getValoracao());
                                    numValoracoes++;
                                }
                            }
                        }
                        BigDecimal media = valoracao.divide(BigDecimal.valueOf(numValoracoes), 2, RoundingMode.HALF_UP);
                        variacaoClasse = valoracao.multiply(media);
                    }
                    valorTotalAlvara = valorTotalAlvara.add(atribuirValorClasseUFM(assistente.getConfigTributario(), itensAmbientais, cnae, parametro, variacaoClasse));
                }
            }
        }
        return valorTotalAlvara;
    }

    private Map<Divida, List<ConfiguracaoAlvara>> preencherConfiguracoesAlvaraPorDivida(AssistenteCalculoAlvara assistente) {
        Map<Divida, List<ConfiguracaoAlvara>> dividas = Maps.newHashMap();
        for (ConfiguracaoAlvara configuracaoAlvara : assistente.getConfigTributario().getConfiguracaoAlvara()) {
            Divida divida = configuracaoAlvara.getDivida();
            if (!dividas.containsKey(divida)) {
                dividas.put(divida, Lists.newArrayList(configuracaoAlvara));
            } else {
                dividas.get(divida).add(configuracaoAlvara);
            }
        }
        return dividas;
    }

    private CalculoAlvara criarCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara,
                                             CadastroEconomico cmc,
                                             ConfiguracaoAlvara configuracaoAlvara,
                                             boolean isRecalculo) {
        CalculoAlvara calculoAlvara = new CalculoAlvara();
        calculoAlvara.setIsento(isCalculoAlvaraIsento(cmc));
        calculoAlvara.setCadastro(cmc);
        calculoAlvara.setTipoAlvara(configuracaoAlvara.getTipoAlvara());
        calculoAlvara.setDivida(configuracaoAlvara.getDivida());
        calculoAlvara.setControleCalculo(isRecalculo ? TipoControleCalculoAlvara.RECALCULO : TipoControleCalculoAlvara.CALCULO);
        calculoAlvara.setProcessoCalculoAlvara(processoCalculoAlvara);
        calculoAlvara.setDataCalculo(new Date());
        calculoAlvara.setSubDivida(buscarSdParcela(calculoAlvara, processoCalculoAlvara.getExercicio()));
        calculoAlvara.setVencimento(definirVencimentoParcela(processoCalculoAlvara.getExercicio(), configuracaoAlvara.getDiasVencDebito()));
        return calculoAlvara;
    }

    private AssistenteTipoCalculoAlvara calcularValorAlvara(AssistenteCalculoAlvara assistente, ConfiguracaoAlvara configuracao, BigDecimal taxaUFM, TipoPagamento tipoPagamento,
                                                            List<ItemCalculoAmbiental> itensAmbientais, Boolean isCalculoCaracFunc) {
        BigDecimal valorUFM = buscarValorUFM(assistente.getExercicio());
        AssistenteTipoCalculoAlvara assistenteTipoCalculoAlvara = definirTipoCalculoAlvara(valorUFM, assistente, configuracao);

        BigDecimal valor = BigDecimal.ZERO;
        if (assistenteTipoCalculoAlvara.getTipoCalculo() != null && assistenteTipoCalculoAlvara.getIdentificacao() != null) {
            if ((isCalculoCaracFunc != null && isCalculoCaracFunc) || assistenteTipoCalculoAlvara.getTipoCalculo().isProporcional()) {
                if (TipoPagamento.ANUAL.equals(tipoPagamento)) {
                    taxaUFM = taxaUFM.divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(12).subtract(
                        BigDecimal.valueOf(buscarMesSubtracaoCalculoProporcional(assistente))));
                } else if (TipoPagamento.MENSAL.equals(tipoPagamento)) {
                    taxaUFM = taxaUFM.multiply(BigDecimal.valueOf(12 - (Calendar.getInstance().get(Calendar.MONTH))));
                }
            } else if (assistenteTipoCalculoAlvara.getTipoCalculo().isFixo()) {
                if (assistenteTipoCalculoAlvara.getIdentificacao().isPrimeiroCalculo()) {
                    taxaUFM = configuracao.getValorUFMFixoPrimeiroCalculo();
                } else if (assistenteTipoCalculoAlvara.getIdentificacao().isCalculo()) {
                    taxaUFM = configuracao.getValorUFMFixoCalculoAlvara();
                } else if (assistenteTipoCalculoAlvara.getIdentificacao().isRenovacao()) {
                    taxaUFM = configuracao.getValorUFMFixoCalcRenovAlvara();
                }
            }
            corrigirValoresUfmDosItensAmbientais(taxaUFM, itensAmbientais, valorUFM);
            valor = taxaUFM.multiply(valorUFM).setScale(2, RoundingMode.HALF_UP);
        }
        assistenteTipoCalculoAlvara.setValorTotalAlvara(valor);
        return assistenteTipoCalculoAlvara;
    }

    private BigDecimal buscarValorUFM(Exercicio exercicio) {
        Moeda ufm = moedaFacade.getMoedaPorIndiceAno(indiceEconomicoFacade.recuperaPorDescricao("UFM"), exercicio.getAno());
        return ufm != null ? ufm.getValor() : null;
    }

    private AssistenteTipoCalculoAlvara definirTipoCalculoAlvara(BigDecimal valorUFM, AssistenteCalculoAlvara assistente, ConfiguracaoAlvara configuracao) {
        AssistenteTipoCalculoAlvara assistenteTipoCalculoAlvara = new AssistenteTipoCalculoAlvara();
        TipoIdentificaoCalculoAlvara identificacao = definirIdentificacaoCalculoAlvara(assistente);
        if (valorUFM != null) {
            if (identificacao.isPrimeiroCalculo()) {
                assistenteTipoCalculoAlvara.setTipoCalculo(configuracao.getTipoPrimeiroCalculo());
            } else if (identificacao.isCalculo()) {
                assistenteTipoCalculoAlvara.setTipoCalculo(configuracao.getTipoCalculoAlvara());
            } else {
                assistenteTipoCalculoAlvara.setTipoCalculo(configuracao.getTipoCalculoRenovacaoAlvara());
            }
            assistenteTipoCalculoAlvara.setIdentificacao(identificacao);
        }
        assistente.getProcCalculo().setRenovacao(identificacao != null && identificacao.isRenovacao());
        return assistenteTipoCalculoAlvara;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TipoIdentificaoCalculoAlvara definirIdentificacaoCalculoAlvara(AssistenteCalculoAlvara assistente) {
        if (isPrimeiroCalculo(assistente)) {
            return TipoIdentificaoCalculoAlvara.PRIMEIRO_CALCULO;
        }
        Alvara alvara = buscarAlvaraVigenteDoCmc(assistente.getCmc().getId());
        if (alvara != null && alvara.getVencimento() != null && alvara.getVencimento().compareTo(new Date()) < 0) {
            return TipoIdentificaoCalculoAlvara.CALCULO;
        }
        return TipoIdentificaoCalculoAlvara.RENOVACAO;
    }

    private int buscarMesSubtracaoCalculoProporcional(AssistenteCalculoAlvara assistente) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(isPrimeiroCalculo(assistente) ? assistente.getCmc().getAbertura() : new Date());
        return calendar.get(Calendar.MONTH);
    }

    private void corrigirValoresUfmDosItensAmbientais(BigDecimal taxaUFM, List<ItemCalculoAmbiental> itensAmbientais, BigDecimal valorUFM) {
        if (itensAmbientais != null && valorUFM.compareTo(BigDecimal.ZERO) > 0) {
            for (ItemCalculoAmbiental item : itensAmbientais) {
                String classeValor = item.getClasseValorUFM();
                String[] split = classeValor.split(":");
                BigDecimal valorItem;
                try {
                    classeValor = classeValor.replace(",", ".");
                    valorItem = new BigDecimal(split[1].trim());
                } catch (Exception e) {
                    valorItem = BigDecimal.ONE;
                }

                BigDecimal proporcao = valorItem.multiply(new BigDecimal(100)).divide(valorUFM, 8, RoundingMode.HALF_UP);
                BigDecimal valorEmUfm = taxaUFM.multiply(proporcao).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

                classeValor = classeValor.replace(split[1], " " + String.valueOf(valorEmUfm).replace(",", "."));
                item.setClasseValorUFM(classeValor);
                item.setValor(valorEmUfm.multiply(valorUFM).setScale(2, RoundingMode.HALF_UP));
            }
        }
    }

    private boolean isCnaeInteresseAmbiental(CNAEProcessoCalculoAlvara cnaeProcesso) {
        return cnaeProcesso.getCnae().getMeioAmbiente();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal buscarValorItemFaixaSanitario(AssistenteCalculoAlvara assistente) {
        BigDecimal valorAlvara = BigDecimal.ZERO;
        List<CNAEProcessoCalculoAlvara> cnaes = assistente.getProcCalculo().getCnaes()
            .stream()
            .filter(pc -> pc.getExercidaNoLocal() && pc.getCnae().getFiscalizacaoSanitaria())
            .collect(Collectors.toList());
        for (CNAEProcessoCalculoAlvara cnae : cnaes) {
            ItemFaixaAlvara itemFaixa = faixaAlvaraFacade.recuperarPorClassificacaoArea(assistente.getClassificacaoAtividade(), TipoAlvara.SANITARIO,
                cnae.getCnae().getGrauDeRisco(), assistente.getExercicio(), assistente.getAreaUtil());
            if (itemFaixa != null && valorAlvara.compareTo(itemFaixa.getValorTaxaUFMAno()) < 0) {
                valorAlvara = itemFaixa.getValorTaxaUFMAno();
            }
        }
        return valorAlvara;
    }

    private ItemCalculoAlvara criarItemCalcAlvara(CalculoAlvara calculoAlvara, Tributo tributo, BigDecimal valorReal, List<ItemCalculoAmbiental> itensAmbientais, CaracFuncionamento caracFuncionamento) {
        ItemCalculoAlvara itemCalculoAlvara = new ItemCalculoAlvara();
        itemCalculoAlvara.setCalculoAlvara(calculoAlvara);
        itemCalculoAlvara.setTributo(tributo);
        itemCalculoAlvara.setValorReal(valorReal);
        itemCalculoAlvara.setValorEfetivo(valorReal);

        if (TipoAlvara.FUNCIONAMENTO.equals(calculoAlvara.getTipoAlvara()) && caracFuncionamento != null) {
            itemCalculoAlvara.setCaracFuncionamento(caracFuncionamento);
        }

        if (itensAmbientais != null && !itensAmbientais.isEmpty()) {
            for (ItemCalculoAmbiental item : itensAmbientais) {
                item.setItemCalculoAlvara(itemCalculoAlvara);
                itemCalculoAlvara.getItensAmbientais().add(item);
            }
        }

        return itemCalculoAlvara;
    }

    private ItemCalculoAmbiental criarItemAmbiental(ParametrosCalcAmbiental parametro, String classeUFM, CNAE cnae, BigDecimal variacao) {
        ItemCalculoAmbiental itemCalculoAmbiental = new ItemCalculoAmbiental();
        itemCalculoAmbiental.setCnaeAmbiental(cnae);
        itemCalculoAmbiental.setLicencaAmbiental(parametro.getLicencaAmbiental());
        itemCalculoAmbiental.setDispensaLicenca(parametro.getDispensaLicenca());
        itemCalculoAmbiental.setClasseValorUFM(classeUFM);
        itemCalculoAmbiental.setVariacaoClasse(variacao);

        return itemCalculoAmbiental;
    }

    private void adicionarPessoaAoCalculo(CalculoAlvara calculo, Pessoa pessoa) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        calculoPessoa.setPessoa(pessoa);
        if (calculo.getPessoas() == null) {
            calculo.setPessoas(new ArrayList<CalculoPessoa>());
        }
        calculo.getPessoas().add(calculoPessoa);
    }

    private Date definirVencimentoParcela(Exercicio exercicio, Integer diasVencDebito) {
        Calendar vencimento = Calendar.getInstance();
        vencimento.set(Calendar.YEAR, exercicio.getAno());
        vencimento.setTime(DataUtil.adicionaDias(vencimento.getTime(), diasVencDebito));

        while (DataUtil.ehDiaNaoUtil(vencimento.getTime(), feriadoFacade)) {
            vencimento.add(Calendar.DAY_OF_MONTH, 1);
        }

        return vencimento.getTime();
    }

    private BigDecimal atribuirValorClasseUFM(ConfiguracaoTributario configuracaoTributario, List<ItemCalculoAmbiental> itensAmbientais, CNAE cnae, ParametrosCalcAmbiental parametro, BigDecimal variacaoClasse) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ParametroValorAlvaraAmbiental parametroValorAlvara : configuracaoTributario.getParametrosValorAlvaraAmbiental()) {
            if (parametro.getLicencaAmbiental().equals(parametroValorAlvara.getTipoLicencaAmbiental())) {
                String classeUFM;
                BigDecimal variacao;
                if (variacaoClasse.compareTo(BigDecimal.valueOf(4)) <= 0 && variacaoClasse.compareTo(BigDecimal.ZERO) > 0) {
                    valor = valor.add(parametroValorAlvara.getValorUFMClasseI());
                    classeUFM = "Classe I - Valor(UFM): " + parametroValorAlvara.getValorUFMClasseI();
                    variacao = variacaoClasse;
                } else if (variacaoClasse.compareTo(BigDecimal.valueOf(4)) > 0 && variacaoClasse.compareTo(BigDecimal.valueOf(8)) <= 0) {
                    valor = valor.add(parametroValorAlvara.getValorUFMClasseII());
                    classeUFM = "Classe II - Valor(UFM): " + parametroValorAlvara.getValorUFMClasseII();
                    variacao = variacaoClasse;
                } else {
                    valor = valor.add(parametroValorAlvara.getValorUFMClasseIII());
                    classeUFM = "Classe III - Valor(UFM): " + parametroValorAlvara.getValorUFMClasseIII();
                    variacao = variacaoClasse;
                }
                itensAmbientais.add(criarItemAmbiental(parametro, classeUFM, cnae, variacao));
            }
        }
        return (parametro.getDispensaLicenca() != null && parametro.getDispensaLicenca()) ? BigDecimal.ZERO : valor;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteEfetivarProcesso> efetivarCalculo(AssistenteEfetivarProcesso assistente) throws Exception {
        ProcessoCalculoAlvara processoCalculoAlvara = inicializarAssistente(assistente);

        adicionarEnderecosCadastroEconomico(processoCalculoAlvara);
        processoCalculoAlvara = salvarRetornando(processoCalculoAlvara);

        Alvara alvara = criarOrAtualizarAlvara(processoCalculoAlvara);

        contar(assistente);

        processoCalculoAlvara.setAlvara(alvara);
        processoCalculoAlvara.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.CALCULADO.equals(processoCalculoAlvara.getSituacaoCalculoAlvara()) ? SituacaoCalculoAlvara.EFETIVADO :
            SituacaoCalculoAlvara.RECALCULADO);
        processoCalculoAlvara = salvarRetornando(processoCalculoAlvara);
        contar(assistente);

        processoCalculoAlvara = gerarDebito(processoCalculoAlvara, assistente);
        contar(assistente);

        atualizarControleCalculo(processoCalculoAlvara);
        processoCalculoAlvara = salvarRetornando(processoCalculoAlvara);

        estornarParcelasDoProcesso(processoCalculoAlvara);
        assistente.setProcessoCalculoAlvara(processoCalculoAlvara);

        assistente.getBarraProgressoItens().finaliza();

        return new AsyncResult<>(assistente);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteEfetivarProcesso> aposEfetivarCalculo(AssistenteEfetivarProcesso assistente) {
        assistente.getBarraProgressoItens().inicializa();

        assistente.getBarraProgressoItens().setTotal(1);
        assistente.getBarraProgressoItens().setMensagens("Enviando documentos para RedeSim...");
        verificarAndEnviarDispensaLicenciamento(assistente.getProcessoCalculoAlvara());
        contar(assistente);

        assistente.getBarraProgressoItens().setMensagens("Finalizando...");
        assistente.getBarraProgressoItens().finaliza();

        return new AsyncResult<>(assistente);
    }

    public void atualizarControleCalculo(ProcessoCalculoAlvara processo) {
        for (CalculoAlvara calculo : processo.buscarCalculosAtuais()) {
            if (calculo != null && calculo.getId() != null) {
                if (calculo.getControleCalculo().isCalculo()) {
                    List<ResultadoParcela> parcelas = buscarArvoreParcelamentoPartindoDoCalculo(calculo.getId());
                    if (parcelas.isEmpty() && processo.hasRecalculo()) {
                        calculo.setControleCalculo(TipoControleCalculoAlvara.CANCELADO);
                    }
                } else if (calculo.getControleCalculo().isRecalculo()) {
                    calculo.setControleCalculo(TipoControleCalculoAlvara.CALCULO);
                }
            }
        }
    }

    public ProcessoCalculoAlvara efetivarCalculo(ProcessoCalculoAlvara processo, Exercicio exercicio) throws Exception {
        removerLicencaOperacaoRenovacao(processo);

        processo.setAlvara(criarOrAtualizarAlvara(processo));
        processo.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EFETIVADO);
        processo = salvarRetornando(processo);

        processo = gerarDebito(processo, null);

        return processo;
    }

    private void removerLicencaOperacaoRenovacao(ProcessoCalculoAlvara processo) {
        if (!hasAlvaraEfetivadoNoExercicio(processo.getCadastroEconomico().getId(), processo.getExercicio().getId())) {
            List<ItemCalculoAmbiental> itensRemover = Lists.newArrayList();
            for (CalculoAlvara calculoAlvara : processo.getCalculosAlvara()) {
                if (TipoAlvara.AMBIENTAL.equals(calculoAlvara.getTipoAlvara())) {
                    for (ItemCalculoAlvara itemCalculoAlvara : calculoAlvara.getItensAlvara()) {
                        for (ItemCalculoAmbiental itemAmbiental : itemCalculoAlvara.getItensAmbientais()) {
                            if (TipoLicencaAmbiental.LICENCA_DE_OPERACAO.equals(itemAmbiental.getLicencaAmbiental())) {
                                itensRemover.add(itemAmbiental);
                            }
                        }
                        itemCalculoAlvara.getItensAmbientais().removeAll(itensRemover);
                    }
                }
            }
        }
    }

    public Alvara criarOrAtualizarAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        Alvara alvara = buscarAlvaraVigenteDoCmc(processoCalculoAlvara.getCadastroEconomico().getId());
        if (alvara != null) {
            Date vencimento = definirVencimentoAlvara(DateUtils.dataSemHorario(alvara.getInicioVigencia()), processoCalculoAlvara);
            if (vencimento.compareTo(alvara.getVencimento()) != 0) {
                if (vencimento.before(new Date())) {
                    alvara = null;
                } else {
                    alvara.setVencimento(vencimento);
                    alvara.setFinalVigencia(vencimento);
                }
            }
        }
        if (alvara == null) {
            alvara = new Alvara();
            alvara.setEmissao(processoCalculoAlvara.getDataLancamento());
            Calendar hojeNoExercicio = Calendar.getInstance();
            alvara.setExercicio(processoCalculoAlvara.getExercicio());
            hojeNoExercicio.set(Calendar.YEAR, alvara.getExercicio().getAno());
            alvara.setInicioVigencia(hojeNoExercicio.getTime());
            alvara.setVencimento(definirVencimentoAlvara(processoCalculoAlvara.getDataLancamento(), processoCalculoAlvara));
            alvara.setFinalVigencia(alvara.getVencimento());
        }
        CadastroEconomico cmc = em.find(CadastroEconomico.class, processoCalculoAlvara.getCadastroEconomico().getId());
        alvara.setCadastroEconomico(cmc);
        alvara.setNomeRazaoSocial(cmc.getPessoa() != null ? cmc.getPessoa().getNome() : "");
        alvara.setTipoAlvara(TipoAlvara.LOCALIZACAO_FUNCIONAMENTO);
        alvara.setAssinaturadigital(geraAssinaturaDigital(alvara));
        alvara.setAreaOcupada(BigDecimal.valueOf(cmc.getAreaUtilizacao()));
        alvara.setLicencaEspecial(Boolean.FALSE);
        alvara.setObservacao("");
        alvara.setDataAlteracao(new Date());

        criarCnaesAlvara(processoCalculoAlvara, alvara);
        adicionarEnderecosAndHorariosDeFuncionamento(processoCalculoAlvara, alvara);

        processoCalculoAlvara.setAlvara(alvara);
        return em.merge(alvara);
    }

    private void adicionarEnderecosAndHorariosDeFuncionamento(ProcessoCalculoAlvara processoCalculoAlvara, Alvara alvara) {
        adicionarEnderecosProcessoAlvara(processoCalculoAlvara, alvara);
        adicionarHorariosDeFuncionamentoDoCadastroEconomico(processoCalculoAlvara.getHorariosAlvara(), alvara);
    }

    private void adicionarEnderecosProcessoAlvara(ProcessoCalculoAlvara processoCalculoAlvara, Alvara alvara) {
        if (alvara.getEnderecosAlvara() != null) {
            alvara.getEnderecosAlvara().clear();
        }
        for (EnderecoCalculoAlvara enderecoCalculoAlvara : processoCalculoAlvara.getEnderecosAlvara()) {
            EnderecoAlvara enderecoAlvara = new EnderecoAlvara();
            enderecoAlvara.setAlvara(alvara);
            enderecoAlvara.setSequencial(enderecoCalculoAlvara.getSequencial());
            enderecoAlvara.setTipoEndereco(enderecoCalculoAlvara.getTipoEndereco());
            enderecoAlvara.setBairro(enderecoCalculoAlvara.getBairro());
            enderecoAlvara.setLogradouro(enderecoCalculoAlvara.getLogradouro());
            enderecoAlvara.setNumero(enderecoCalculoAlvara.getNumero());
            enderecoAlvara.setComplemento(enderecoCalculoAlvara.getComplemento());
            enderecoAlvara.setCep(enderecoCalculoAlvara.getCep());
            enderecoAlvara.setAreaUtilizacao(enderecoCalculoAlvara.getAreaUtilizacao());
            alvara.getEnderecosAlvara().add(enderecoAlvara);
        }
    }

    public void adicionarHorariosDeFuncionamentoDoCadastroEconomico(List<HorarioFuncCalculoAlvara> horarios, Alvara alvara) {
        if (horarios != null && alvara != null && alvara.getHorariosDeFuncionamento() != null) {
            alvara.getHorariosDeFuncionamento().clear();
            for (HorarioFuncCalculoAlvara horario : horarios) {
                AlvaraHorarioFuncionamento horarioFuncionamento = new AlvaraHorarioFuncionamento();
                horarioFuncionamento.setHorarioFuncionamento(horario.getHorarioFuncionamento());
                horarioFuncionamento.setAlvara(alvara);
                Util.adicionarObjetoEmLista(alvara.getHorariosDeFuncionamento(), horarioFuncionamento);
            }
        }
    }

    public Date definirVencimentoAlvara(Date dataInicio, ProcessoCalculoAlvara processoCalculoAlvara) {
        Date dataVencimento = null;
        if (!processoCalculoAlvara.isDispensaLicenciamento()) {
            ConfiguracaoTributario configuracaoTributario = buscarConfiguracaoTriutarioVigente();
            if (containsGrauDeRisco(processoCalculoAlvara.getCnaes(), GrauDeRiscoDTO.ALTO)) {
                dataVencimento = DataUtil.adicionaAnos(dataInicio, configuracaoTributario.getAnoCnaeAltoRisco());
            } else if (containsGrauDeRisco(processoCalculoAlvara.getCnaes(), GrauDeRiscoDTO.BAIXO_RISCO_B)) {
                dataVencimento = DataUtil.adicionaAnos(dataInicio, configuracaoTributario.getAnoCnaeBaixoRiscoB());
            }
        }
        return dataVencimento;
    }

    private boolean containsGrauDeRisco(List<CNAEProcessoCalculoAlvara> cnaes, GrauDeRiscoDTO grauDeRisco) {
        boolean contem = false;
        for (CNAEProcessoCalculoAlvara cnae : cnaes) {
            if (grauDeRisco.equals(cnae.getCnae().getGrauDeRisco())) {
                contem = true;
                break;
            }
        }
        return contem;
    }

    private void criarCnaesAlvara(ProcessoCalculoAlvara processoCalculoAlvara, Alvara alvara) {
        if (alvara.getCnaeAlvaras() != null) {
            alvara.getCnaeAlvaras().clear();
        }
        for (CNAEProcessoCalculoAlvara cnae : processoCalculoAlvara.getCnaes()) {
            CNAEAlvara cnaeAlvara = new CNAEAlvara();
            cnaeAlvara.setCnae(cnae.getCnae());
            cnaeAlvara.setAlvara(alvara);
            cnaeAlvara.setHorarioFuncionamento(cnae.getHorarioFuncionamento());
            cnaeAlvara.setExercidaNoLocal(cnae.getExercidaNoLocal());
            alvara.getCnaeAlvaras().add(cnaeAlvara);
        }
    }

    private void contar(AssistenteEfetivarProcesso assistente) {
        if (assistente != null) {
            assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
        }
    }

    private ProcessoCalculoAlvara inicializarAssistente(AssistenteEfetivarProcesso assistente) {
        assistente.setBarraProgressoItens(new BarraProgressoItens());
        assistente.getBarraProgressoItens().inicializa();
        ProcessoCalculoAlvara processoCalculoAlvara = assistente.getProcessoCalculoAlvara();

        assistente.getBarraProgressoItens().setTotal(4 + (processoCalculoAlvara.getCalculosAlvara().size()));
        assistente.getBarraProgressoItens().setProcessados(0);
        assistente.getBarraProgressoItens().setMensagens("Gerando Alvará de Localização e Funcionamento...");
        return processoCalculoAlvara;
    }

    public String geraAssinaturaDigital(Alvara alvara) {
        if (alvara.getCadastroEconomico() != null && alvara.getEmissao() != null) {
            String assinaturaDigital = alvara.getEmissao().toString() + alvara.getCadastroEconomico().getInscricaoCadastral();
            assinaturaDigital = GeradorChaveAutenticacao.geraChaveDeAutenticacao(assinaturaDigital, GeradorChaveAutenticacao.TipoAutenticacao.ALVARA);
            return assinaturaDigital;
        }
        return "SEM ASSINATURA NA ORIGEM";
    }

    public ProcessoCalculoAlvara gerarDebito(ProcessoCalculoAlvara processoCalculo, AssistenteEfetivarProcesso assistente) throws Exception {
        if (assistente != null) {
            assistente.getBarraProgressoItens().setMensagens("Gerando Débitos...");
        }

        List<CalculoAlvara> calculosEfetivos = agruparDebitosCalculo(processoCalculo);

        for (CalculoAlvara calculoAlvara : calculosEfetivos) {
            processoCalculo.setDivida(calculoAlvara.getDivida());
            if (calculoAlvara.getId() != null && buscarValorDividaPorIdCalculo(calculoAlvara.getId()) == null) {
                List<OpcaoPagamentoDivida> opcoes = geraValorDividaAlvara.validaOpcoesPagamento(calculoAlvara.getDivida(), new Date());
                geraValorDividaAlvara.geraValorDivida(calculoAlvara, opcoes);
            }
            if (assistente != null) {
                contar(assistente);
            }
        }

        return salvarRetornando(processoCalculo);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long buscarSdParcela(CalculoAlvara calculoAlvara, Exercicio exercicio) {
        if (calculoAlvara.getProcessoCalculoAlvara() != null && calculoAlvara.getProcessoCalculoAlvara().getId() != null) {
            try {
                String sql = " select count(*) from calculoalvara ca " +
                    " inner join processocalculoalvara processo on ca.processocalculoalvara_id = processo.id " +
                    " inner join exercicio ex on processo.exercicio_id = ex.id " +
                    " inner join valordivida vd on ca.id = vd.calculo_id " +
                    " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
                    " where ca.tipoalvara = :tipoAlvara " +
                    " and processo.id = :idProcesso " +
                    " and ex.ano = :ano ";

                Query q = em.createNativeQuery(sql);
                q.setParameter("tipoAlvara", calculoAlvara.getTipoAlvara().name());
                q.setParameter("idProcesso", calculoAlvara.getProcessoCalculoAlvara().getId());
                q.setParameter("ano", exercicio.getAno());

                List<BigDecimal> quantidade = q.getResultList();
                return (quantidade != null && !quantidade.isEmpty()) ? (quantidade.get(0).longValue() + 1) : 1L;
            } catch (Exception e) {
                return 1L;
            }
        }
        return 1L;
    }

    private List<CalculoAlvara> agruparDebitosCalculo(ProcessoCalculoAlvara processoCalculo) {
        List<CalculoAlvara> calculosEfetivos = Lists.newArrayList();
        Map<Divida, CalculoAlvara> mapaCalculosEfetivos = Maps.newHashMap();
        List<CalculoAlvara> recalculos = processoCalculo.buscarRecalculos();
        List<CalculoAlvara> calculosAlvara = !recalculos.isEmpty() ? recalculos : processoCalculo.buscarCalculos();

        for (CalculoAlvara calculoAtual : calculosAlvara) {
            CalculoAlvara calculoAux = (CalculoAlvara) Util.clonarObjeto(calculoAtual);
            if (calculoAux != null) {
                if (!mapaCalculosEfetivos.containsKey(calculoAux.getDivida())) {
                    mapaCalculosEfetivos.put(calculoAux.getDivida(), calculoAux);
                    calculosEfetivos.add(mapaCalculosEfetivos.get(calculoAux.getDivida()));
                } else {
                    for (ItemCalculoAlvara itemCalculo : calculoAux.getItensAlvara()) {
                        ItemCalculoAlvara item = new ItemCalculoAlvara();
                        item.setValorEfetivo(itemCalculo.getValorEfetivo());
                        item.setValorReal(itemCalculo.getValorReal());
                        item.setTributo(itemCalculo.getTributo());

                        mapaCalculosEfetivos.get(calculoAux.getDivida()).setValorEfetivo(mapaCalculosEfetivos.get(calculoAux.getDivida()).getValorEfetivo().add(item.getValorEfetivo()));
                        mapaCalculosEfetivos.get(calculoAux.getDivida()).setValorReal(mapaCalculosEfetivos.get(calculoAux.getDivida()).getValorReal().add(item.getValorReal()));

                        mapaCalculosEfetivos.get(calculoAux.getDivida()).getItensAlvara().add(item);
                    }
                    mapaCalculosEfetivos.get(calculoAux.getDivida()).getCalculosAgrupados().add(
                        new CalculoAlvaraEfetivo(mapaCalculosEfetivos.get(calculoAux.getDivida()), calculoAux.getId()));
                }
            }
        }
        return calculosEfetivos;
    }

    public DAM gerarDam(List<ResultadoParcela> parcelas, Date vencimento) {
        if (parcelas != null && !parcelas.isEmpty()) {
            if (parcelas.size() == 1) {
                return damFacade.gerarDAMUnicoViaApi(Util.recuperarUsuarioCorrente(), parcelas.get(0));
            } else {
                return damFacade.gerarDAMCompostoViaApi(Util.recuperarUsuarioCorrente(), parcelas, vencimento);
            }
        }
        return null;
    }

    public void enviarDamPorEmail(Long idProcesso, String mensagem, String[] email, List<ResultadoParcela> parcelas) {
        DAM dam = buscarDAMAgrupadoDoProcesso(idProcesso);
        if (dam != null) {
            consultaDebitoFacade.enviarEmailsDAMs(Lists.newArrayList(dam), email, true, mensagem, parcelas);
        }
    }

    public DAM buscarDAMAgrupadoDoProcesso(Long idProcesso) {
        String sql = "select dam.* from dam " +
            "inner join itemdam item on dam.id = item.dam_id " +
            "inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            "inner join valordivida vd on pvd.valordivida_id = vd.id " +
            "inner join calculo calc on vd.calculo_id = calc.id " +
            "inner join calculoalvara calc_alvara on calc_alvara.id = calc.id " +
            "inner join processocalculoalvara proc on calc_alvara.processocalculoalvara_id = proc.id " +
            "where proc.id = :idProc ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idProc", idProcesso);

        List<DAM> dans = q.getResultList();
        return (dans != null && !dans.isEmpty()) ? dans.get(0) : null;
    }

    public ProcessoCalculoAlvara buscarProcessoCalculoAlvaraPorExercicio(Long idCmc, Integer exercicio) {
        String sql = " select proc.*, processo.* from processocalculoalvara proc " +
            " inner join processocalculo processo on processo.id = proc.id " +
            " inner join exercicio ex on processo.exercicio_id = ex.id " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and proc.situacaocalculoalvara <> :estornado";
        if (exercicio != null) {
            sql += " and ex.ano = :ano ";
        }

        Query q = em.createNativeQuery(sql, ProcessoCalculoAlvara.class);
        q.setParameter("idCmc", idCmc);
        if (exercicio != null) {
            q.setParameter("ano", exercicio);
        }
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<ProcessoCalculoAlvara> processos = q.getResultList();
        return (processos != null && !processos.isEmpty()) ? processos.get(0) : null;
    }

    public List<ProcessoCalculoAlvara> buscarProcessosCalculoAlvaraPorExercicio(Long idCmc, Integer exercicio) {
        String sql = " select proc.*, processo.* from processocalculoalvara proc " +
            " inner join processocalculo processo on processo.id = proc.id " +
            " inner join exercicio ex on processo.exercicio_id = ex.id " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and proc.situacaocalculoalvara <> :estornado " +
            " and ex.ano = :ano ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoAlvara.class);
        q.setParameter("idCmc", idCmc);
        q.setParameter("ano", exercicio);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public ProcessoCalculoAlvara estornarCalculosAlvara(ProcessoCalculoAlvara processoCalculo) {
        List<ValorDivida> valores = Lists.newArrayList();
        for (CalculoAlvara calculoAlvara : processoCalculo.getCalculosAlvara()) {
            valores.addAll(recuperarAndAdicionarValorDivida(calculoAlvara));
        }
        alterarSituacaoParcela(valores, SituacaoParcela.CANCELAMENTO);
        return em.merge(processoCalculo);
    }

    private List<ValorDivida> recuperarAndAdicionarValorDivida(CalculoAlvara calculoAlvara) {
        List<ValorDivida> valoresDivida = Lists.newArrayList();
        ValorDivida valorDivida = buscarValorDividaPorIdCalculo(calculoAlvara.getId());
        if (valorDivida != null) {
            valoresDivida.add(valorDivida);
        }
        return valoresDivida;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarSituacaoParcela(List<ValorDivida> valores, SituacaoParcela situacao) {
        JdbcParcelaValorDividaDAO pvdDao = (JdbcParcelaValorDividaDAO) Util.getSpringBeanPeloNome("jdbcParcelaValorDividaDAO");
        JdbcDamDAO damDAO = (JdbcDamDAO) Util.getSpringBeanPeloNome("damDAO");
        for (ValorDivida vd : valores) {
            for (ParcelaValorDivida pvd : vd.getParcelaValorDividas()) {
                if (pvd.getSituacaoAtual() != null && SituacaoParcela.EM_ABERTO.equals(pvd.getSituacaoAtual().getSituacaoParcela())) {
                    pvdDao.inserirSituacaoParcelaValorDivida(pvd, pvd.getSituacaoAtual(), situacao);

                    List<DAM> dans = Lists.newArrayList();
                    dans.add(buscarDAMPeloIdParcela(pvd.getId(), false));
                    dans.addAll(damFacade.buscarDamsAgrupadosDaParcela(pvd.getId()));

                    for (DAM dam : dans) {
                        if (dam != null && dam.getId() != null) {
                            damDAO.atualizar(dam.getId(), DAM.Situacao.CANCELADO);
                        }
                    }
                }
            }
        }
    }

    public ValorDivida buscarValorDividaPorIdCalculo(Long idCalculo) {
        if (idCalculo != null) {
            String sql = " select vd.* from valordivida vd " +
                " where vd.calculo_id = :idCalculo ";

            Query q = em.createNativeQuery(sql, ValorDivida.class);
            q.setParameter("idCalculo", idCalculo);

            List<ValorDivida> valoresDivida = q.getResultList();

            if (valoresDivida != null && !valoresDivida.isEmpty()) {
                ValorDivida valorDivida = valoresDivida.get(0);
                Hibernate.initialize(valorDivida.getParcelaValorDividas());

                return valorDivida;
            }
        }
        return null;
    }

    public ProcessoCalculoAlvara recuperarProcessoCalculoDoAlvara(Long idAlvara) {
        String sql = "select procAlvara.id " +
            " from processocalculoalvara procAlvara " +
            " inner join processocalculo pc on procAlvara.id = pc.id" +
            " inner join exercicio ex on pc.exercicio_id = ex.id " +
            " where procalvara.alvara_id = :idAlvara " +
            " order by ex.ano desc";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlvara", idAlvara);
        List<Object> processos = q.getResultList();
        ProcessoCalculoAlvara retorno = null;
        if (!processos.isEmpty()) {
            Long idProcesso = ((BigDecimal) processos.get(0)).longValue();
            retorno = recuperar(idProcesso);
        }
        return retorno;
    }

    public ProcessoCalculoAlvaraSan recuperaProcessoCalculoSan(Long idCalculo) {
        String hql = "select p from ProcessoCalculoAlvaraSan p " +
            " join p.calculosAlvaraSanitarios c where c.id = :id";

        Query q = em.createQuery(hql);
        q.setParameter("id", idCalculo);

        List<ProcessoCalculoAlvaraSan> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            ProcessoCalculoAlvaraSan processo = resultado.get(0);
            Hibernate.initialize(processo.getCalculosAlvaraSanitarios());
            return processo;
        }
    }

    public void imprimirAlvara(Alvara alvara, ReciboImpressaoAlvara recibo, VOAlvara voAlvara, boolean isento) throws Exception {
        alvara = adicionaReciboImpressaoAlvara(alvara, recibo);

        if (VOAlvara.TipoVoAlvara.PROCESSO_CALCULO.equals(voAlvara.getTipoVoAlvara())) {
            ProcessoCalculoAlvara proc = em.find(ProcessoCalculoAlvara.class, voAlvara.getId());
            Hibernate.initialize(proc.getCnaes());
            alvaraFacade.imprimirNovoAlvara(proc, isento, false);
        } else {
            alvaraFacade.imprimirAlvara(alvara, alvara.getTipoAlvara(), isento, false);
        }
    }

    public List<ResultadoParcela> buscarParcelas(Object calculo, List<SituacaoParcela> situacoes) {
        try {
            List<Long> calculos;
            Integer ano;
            Long idCmc;

            if (calculo instanceof ProcessoCalculoAlvara) {
                calculos = Util.montarIdsList(((ProcessoCalculoAlvara) calculo).getCalculosAlvara());
                ano = ((ProcessoCalculoAlvara) calculo).getExercicio().getAno();
                idCmc = ((ProcessoCalculoAlvara) calculo).getCadastroEconomico().getId();
            } else {
                calculos = Util.montarIdsList(((VOAlvara) calculo).getItens());
                ano = ((VOAlvara) calculo).getAno();
                idCmc = ((VOAlvara) calculo).getIdCadastro();
            }
            ConsultaParcela consulta = executarConsultaParcela(ano, idCmc, calculos, situacoes);

            return consulta.executaConsulta().getResultados();
        } catch (Exception e) {
            logger.error("Erro ao fazer cast processo calculo alvara. ", e);
            return Lists.newArrayList();
        }
    }

    private ConsultaParcela executarConsultaParcela(Integer ano, Long idCmc, List<Long> calculos, List<SituacaoParcela> situacoes) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCmc);
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, ano);
        if (!calculos.isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, calculos);
        }
        if (situacoes != null && !situacoes.isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);
        }
        return consulta;
    }

    public ProcessoCalculoAlvara enviarPdfDAMsRedeSim(ProcessoCalculoAlvara processo) throws Exception {
        List<ResultadoParcela> parcelas = buscarParcelas(processo,
            Lists.newArrayList(SituacaoParcela.EM_ABERTO));

        if (!parcelas.isEmpty()) {
            Date vencimento = definirVencimentoDAM(parcelas);
            DAM dam = gerarDam(parcelas, vencimento);
            Exercicio exercicioCorrente = exercicioFacade.getExercicioCorrente();
            if (!exercicioCorrente.getAno().equals(dam.getExercicio().getAno())) {
                FacesUtil.addOperacaoNaoPermitida("Só é possivel enviar DAMs do exercício corrente de " + exercicioCorrente.getAno() + " para a Rede SIM.");
                return processo;
            }
            if (processo.getCadastroEconomico().getPessoa() instanceof PessoaJuridica) {
                processo = gerarByteArrayAndEnviarParaRedeSim(processo, parcelas, dam, processo.getUsuario().getNome());
            }
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foram encontrados DAMs para serem enviados.");
        }
        return processo;
    }

    public ProcessoCalculoAlvara gerarByteArrayAndEnviarParaRedeSim(ProcessoCalculoAlvara processo,
                                                                    List<ResultadoParcela> parcelas,
                                                                    DAM dam,
                                                                    String usuario) throws Exception {
        if (dam != null) {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            byte[] bytesDam;
            if (parcelas.size() > 1) {
                bytesDam = imprimeDAM.gerarBytesImpressaoDamCompostoViaApi(dam);
            } else {
                bytesDam = imprimeDAM.gerarByteImpressaoDamUnicoViaApi(dam);
            }
            if (bytesDam != null) {
                processo = montarEventoRedeSimAndConfirmarResposta(processo, dam.getId(),
                    bytesDam, TipoArquivoLogAlvaraRedeSim.DAM);
                if (FacesContext.getCurrentInstance() != null) {
                    FacesUtil.addOperacaoRealizada("Os DAMs de Alvará foram enviados para RedeSim com sucesso.");
                }
            }
        }
        return processo;
    }

    public Date definirVencimentoDAM(List<ResultadoParcela> parcelas) {
        Date dataVencimento = null;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getVencimento().compareTo(new Date()) >= 0) {
                if (dataVencimento == null) {
                    dataVencimento = parcela.getVencimento();
                } else if (dataVencimento.after(parcela.getVencimento())) {
                    dataVencimento = parcela.getVencimento();
                }
            }
        }
        return dataVencimento != null ? dataVencimento : DataUtil.ultimoDiaMes(new Date()).getTime();
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        enviarPdfsRedeSim(calculo, buscarConfiguracaoTriutarioVigente());
    }

    public void enviarPdfsRedeSim(Calculo calculo, ConfiguracaoTributario configuracaoTributario) {
        try {
            ProcessoCalculoAlvara processo = burcarProcessoCalculoPeloIdCalculo(calculo.getId());
            if (processo != null && hasParcelaNaoPagaConfirmarRespostaRedeSim(processo)) {
                enviarAlvara(processo, configuracaoTributario);
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar pdf para rede sim. ", e);
        }
    }

    public void enviarAlvara(ProcessoCalculoAlvara processo, ConfiguracaoTributario configuracaoTributario) throws Exception {
        if (!processo.isDispensaLicenciamento()) {
            Alvara alvara = em.find(Alvara.class, processo.getAlvara().getId());

            if (alvara != null) {
                ImpressaoAlvara impressaoAlvara = alvaraFacade.imprimirNovoAlvara(processo, isCalculoAlvaraIsento(processo.getCadastroEconomico()), true);

                ImprimeAlvara imprimeAlvara = new ImprimeAlvara();
                Connection connection = imprimeAlvara.recuperaConexaoJDBC();
                String separator = System.getProperty("file.separator");
                String caminho = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
                String caminhoImg = Util.getApplicationPath("/img/") + separator;

                String rodape = configuracaoTributario.getMensagemRodapeAlvara();

                UsuarioSistema usuarioLogado = Util.recuperarUsuarioCorrente();
                String login = (usuarioLogado != null && usuarioLogado.getLogin() != null) ? usuarioLogado.getLogin() : "";

                ByteArrayOutputStream outputStream = imprimeAlvara.imprimirAlvaraRetornando(alvara, impressaoAlvara, login, rodape, caminho,
                    caminhoImg, connection);

                if (outputStream != null) {
                    montarEventoRedeSimAndConfirmarResposta(processo, null, outputStream.toByteArray(), TipoArquivoLogAlvaraRedeSim.ALVARA);
                }
            }
        }
    }

    public ProcessoCalculoAlvara buscarProcessoPorExercicioAndCadastroEconomico(Exercicio exercicio,
                                                                                CadastroEconomico cadastroEconomico) {
        List resultList = em.createQuery(" select pca from ProcessoCalculoAlvara pca " +
                " join pca.exercicio e " +
                " join pca.cadastroEconomico ce " +
                " where e = :exercicio " +
                "  and ce = :cadastroEconomico ")
            .setParameter("exercicio", exercicio)
            .setParameter("cadastroEconomico", cadastroEconomico)
            .setMaxResults(1)
            .getResultList();
        return !resultList.isEmpty() ? (ProcessoCalculoAlvara) resultList.get(0) : null;
    }

    public void recuperarCnaes(ProcessoCalculoAlvara processoCalculoAlvara) {
        List resultList = em.createQuery(" from CNAEProcessoCalculoAlvara cp " +
                " where cp.processoCalculoAlvara = :processoCalculoAlvara ")
            .setParameter("processoCalculoAlvara", processoCalculoAlvara)
            .getResultList();
        processoCalculoAlvara.setCnaes(!resultList.isEmpty() ?
            (List<CNAEProcessoCalculoAlvara>) resultList : Lists.newArrayList());
    }

    public void enviarDispensaLicenciamento(Exercicio exercicio,
                                            CadastroEconomico cadastroEconomico) {
        ProcessoCalculoAlvara processoCalculoAlvara =
            buscarProcessoPorExercicioAndCadastroEconomico(exercicio, cadastroEconomico);
        if (processoCalculoAlvara != null) {
            processoCalculoAlvara = recuperar(processoCalculoAlvara.getId());
        }
        if (processoCalculoAlvara == null || (processoCalculoAlvara.isEstornado())) {
            throw new ValidacaoException("Nenhum cálculo de alvará encontrado para o exercício " + exercicio.getAno() + ".");
        } else {
            if (processoCalculoAlvara.isEmAberto() || processoCalculoAlvara.isCalculado()) {
                throw new ValidacaoException("Nenhum cálculo de alvará para o exercício " + exercicio.getAno() + ", ainda não foi efetivado.");
            }
            if (!processoCalculoAlvara.isDispensaLicenciamento()) {
                throw new ValidacaoException("O cadastro econômico " + cadastroEconomico.getCmcNomeCpfCnpj() +
                    " não é dispensado de licenciamento. ");
            }
            enviarDispensaLicenciamento(processoCalculoAlvara);
        }
    }

    public void verificarAndEnviarDispensaLicenciamento(ProcessoCalculoAlvara processoCalculoAlvara) {
        try {
            if (processoCalculoAlvara.isDispensaLicenciamento()) {
                enviarDispensaLicenciamento(processoCalculoAlvara);
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar a dispensa de licenciamento após efetivação. {}", e.getMessage());
            logger.debug("Detalhes do erro ao enviar a dispensa de licenciamento após efetivação.", e);
        }
    }

    public void enviarDispensaLicenciamento(ProcessoCalculoAlvara processo) {
        byte[] pdf = gerarDeclaracaoDispensaLicenciamento(processo);
        montarEventoRedeSimAndConfirmarRespostaBCM(pdf, processo.getCadastroEconomico(),
            TipoArquivoLogAlvaraRedeSim.DISPENSA_LICENCIAMENTO);
    }

    private boolean hasParcelaNaoPagaConfirmarRespostaRedeSim(ProcessoCalculo processo) {
        String sql = " select calculo.*, calc.* from calculoalvara calculo " +
            " inner join calculo calc on calc.id = calculo.id " +
            " inner join processocalculoalvara processo on calculo.processocalculoalvara_id = processo.id " +
            " inner join valordivida vd on vd.calculo_id = calculo.id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where processo.id = :idProcessoCalculo " +
            " and spvd.situacaoparcela not in :situacoes " +
            " and processo.situacaoCalculoAlvara <> :estornado " +
            " and calculo.controlecalculo = :tipoControle ";

        Query q = em.createNativeQuery(sql, CalculoAlvara.class);
        q.setParameter("idProcessoCalculo", processo.getId());
        q.setParameter("situacoes", SituacaoParcela.getsituacoesPago());
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());
        q.setParameter("tipoControle", TipoControleCalculoAlvara.CALCULO.name());

        return q.getResultList().isEmpty();
    }

    public void montarEventoRedeSimAndConfirmarRespostaBCM(byte[] bytesPdf,
                                                           CadastroEconomico cadastroEconomico,
                                                           TipoArquivoLogAlvaraRedeSim tipoArquivoLogAlvaraRedeSim) {
        confirmarRespostaPdfRedeSim(bytesPdf, cadastroEconomico);
        ProcessoCalculoAlvara processoCalculoAlvara = buscarProcessoCalculoAlvaraPorExercicio(cadastroEconomico.getId(),
            Calendar.getInstance().get(Calendar.YEAR));

        if (processoCalculoAlvara != null) {
            gerarLog(processoCalculoAlvara, null, bytesPdf, tipoArquivoLogAlvaraRedeSim);
        }
    }

    private ProcessoCalculoAlvara montarEventoRedeSimAndConfirmarResposta(ProcessoCalculoAlvara processo, Long idDam,
                                                                          byte[] bytesPdf,
                                                                          TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        byte[] bytes = confirmarRespostaPdfRedeSim(bytesPdf, processo.getCadastroEconomico());
        return gerarLog(processo, idDam, bytes, tipoArquivo);
    }

    private byte[] confirmarRespostaPdfRedeSim(byte[] bytesPdf, CadastroEconomico cadastroEconomico) {
        EventoRedeSimDTO eventoRedeSimDTO = new EventoRedeSimDTO();
        eventoRedeSimDTO.setPdfResultado(bytesPdf);
        if (cadastroEconomico.getPessoa() instanceof PessoaJuridica) {
            redeSimFacade.confirmarResposta(eventoRedeSimDTO, (PessoaJuridica) cadastroEconomico.getPessoa(), cadastroEconomico);
        }
        return bytesPdf;
    }

    private ProcessoCalculoAlvara gerarLog(ProcessoCalculoAlvara processo, Long idDam, byte[] bytes, TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        LogAlvaraRedeSim ultimoLog = buscarUltimoLog(processo);

        if (isLancarLog(ultimoLog, tipoArquivo, idDam)) {
            LogAlvaraRedeSim log = criarLogDeEnviosRedeSim(processo, idDam, bytes, tipoArquivo);
            if (log != null) {
                em.merge(log);
            }
        }
        return processo;
    }

    private boolean isLancarLog(LogAlvaraRedeSim ultimoLog, TipoArquivoLogAlvaraRedeSim tipoArquivo, Long idDam) {
        boolean lancarLog = true;
        if (ultimoLog != null && ultimoLog.getArquivo() != null && tipoArquivo.equals(ultimoLog.getArquivo().getTipoArquivo())) {
            Calendar dataAtual = Calendar.getInstance();
            dataAtual.setTime(new Date());
            dataAtual.add(Calendar.MINUTE, -5);

            if ((TipoArquivoLogAlvaraRedeSim.DAM.equals(tipoArquivo) && ultimoLog.getIdDam() != null && ultimoLog.getIdDam().equals(idDam)) ||
                !TipoArquivoLogAlvaraRedeSim.DAM.equals(tipoArquivo) && dataAtual.getTime().before(ultimoLog.getData())) {
                lancarLog = false;
            }
        }
        return lancarLog;
    }

    private LogAlvaraRedeSim criarLogDeEnviosRedeSim(ProcessoCalculoAlvara processo, Long idDam, byte[] bytes, TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        try {
            LogAlvaraRedeSim log = new LogAlvaraRedeSim();
            log.setProcessoCalculoAlvara(processo);
            log.setUsuarioSistema(Util.recuperarUsuarioCorrente());
            log.setData(new Date());
            log.setIpMaquina(Util.obterIpUsuario());
            log.setIdAlvara(processo.getAlvara() != null ? processo.getAlvara().getId() : null);
            log.setIdDam(idDam);

            ArquivoLogAlvaraRedeSim arquivoLog = criarArquivoLog(tipoArquivo);
            Arquivo arquivo = criarArquivo(arquivoLog.getTipoArquivo().getDescricao() + "_" + DataUtil.getDataFormatada(log.getData()) + ".pdf");

            ArquivoComposicao arquivoComposicao = criarArquivoComposicao(bytes, arquivoLog.getDetentorArquivoComposicao(), arquivo);
            arquivoLog.getDetentorArquivoComposicao().setArquivoComposicao(arquivoComposicao);

            arquivoLog = salvarArquivo(arquivoLog);

            log.setArquivo(arquivoLog);

            return log;
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo de log. ", e);
        }
        return null;
    }

    private ArquivoLogAlvaraRedeSim criarArquivoLog(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        ArquivoLogAlvaraRedeSim arquivoLog = new ArquivoLogAlvaraRedeSim();
        arquivoLog.setTipoArquivo(tipoArquivo);
        return arquivoLog;
    }

    private Arquivo criarArquivo(String nomeArquivo) {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(nomeArquivo);
        arquivo.setMimeType("application/pdf");
        arquivo.setNome(nomeArquivo);
        return arquivo;
    }

    public ArquivoComposicao criarArquivoComposicao(byte[] bytes, DetentorArquivoComposicao detentor, Arquivo arquivo) throws Exception {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes)));
        arquivoComposicao.setFile(Util.criarArquivoUpload(arquivo.getNome(), arquivo.getMimeType(), bytes));
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(detentor);
        return arquivoComposicao;
    }

    public ArquivoLogAlvaraRedeSim salvarArquivo(ArquivoLogAlvaraRedeSim arquivo) {
        return em.merge(arquivo);
    }

    public LogAlvaraRedeSim buscarUltimoLog(ProcessoCalculoAlvara processo) {
        String sql = "select log.* from logalvararedesim log " +
            " inner join processocalculoalvara proc on log.processocalculoalvara_id = proc.id " +
            " where proc.id = :idProcesso " +
            " order by log.data desc ";

        Query q = em.createNativeQuery(sql, LogAlvaraRedeSim.class);
        q.setParameter("idProcesso", processo.getId());

        List<LogAlvaraRedeSim> logs = q.getResultList();
        return (logs != null && !logs.isEmpty()) ? logs.get(0) : null;
    }

    private ProcessoCalculoAlvara burcarProcessoCalculoPeloIdCalculo(Long idCalculo) {
        String sql = "select proc.*, processo.* from processocalculoalvara proc " +
            " inner join processocalculo processo on processo.id = proc.id " +
            " inner join calculoalvara calcalvara on proc.id = calcalvara.processocalculoalvara_id " +
            " inner join calculo cal on cal.id = calcalvara.id " +
            " where cal.id = :idCalculo ";

        Query q = em.createNativeQuery(sql, ProcessoCalculoAlvara.class);
        q.setParameter("idCalculo", idCalculo);

        List<ProcessoCalculoAlvara> processos = q.getResultList();

        if (processos != null && !processos.isEmpty()) {
            return processos.get(0);
        }
        return null;
    }

    public String buscarProtocoloRedeSIM(String cnpj) {
        String sql = " select evento.identificador from eventoredesim evento " +
            " where evento.dataoperacao = (select max(e.dataoperacao) from eventoredesim e " +
            " where (replace(replace(replace(e.cnpj,'.',''),'-',''),'/','')) = :cnpj) " +
            " and (replace(replace(replace(evento.cnpj,'.',''),'-',''),'/','')) = :cnpj and rownum = 1" +
            " and evento.identificador <> :identificacao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cnpj", StringUtil.retornaApenasNumeros(cnpj));
        q.setParameter("identificacao", "Sincronização Manual");

        List<String> retorno = q.getResultList();

        return (retorno != null && !retorno.isEmpty()) ? retorno.get(0) : "";

    }

    public ProcessoCalculoAlvara adicionarInformacoesCmc(ProcessoCalculoAlvara selecionado) {
        if (selecionado != null && selecionado.getCadastroEconomico() != null) {
            adicionarCaracteristicasDeFuncionamento(selecionado);
            adicionarHorariosDeFuncionamento(selecionado);
        }
        return selecionado;
    }

    private void adicionarHorariosDeFuncionamento(ProcessoCalculoAlvara selecionado) {
        if (selecionado.getCadastroEconomico().getPessoa() != null) {
            Pessoa pessoaCadastro = recuperarHorariosDeFuncionamento(selecionado.getCadastroEconomico().getPessoa().getId());
            selecionado.setAlterouFuncionamento(verificarAlteracaoHorarioFuncionamento(selecionado, pessoaCadastro.getHorariosFuncionamentoAtivos()));
            selecionado.getHorariosAlvara().clear();
            for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pessoaCadastro.getHorariosFuncionamentoAtivos()) {
                HorarioFuncCalculoAlvara horarioFuncCalculoAlvara = new HorarioFuncCalculoAlvara();
                horarioFuncCalculoAlvara.setProcessoCalculoAlvara(selecionado);
                horarioFuncCalculoAlvara.setHorarioFuncionamento(pessoaHorarioFuncionamento.getHorarioFuncionamento());

                selecionado.getHorariosAlvara().add(horarioFuncCalculoAlvara);
            }
        }
    }

    private boolean verificarAlteracaoHorarioFuncionamento(ProcessoCalculoAlvara selecionado, List<PessoaHorarioFuncionamento> horariosAtivos) {
        boolean alterou = false;
        if (selecionado.getHorariosAlvara().size() != horariosAtivos.size()) {
            alterou = true;
        } else {
            for (PessoaHorarioFuncionamento horarioAtivo : horariosAtivos) {
                if (!containsHorarioFuncionamento(selecionado.getHorariosAlvara(), horarioAtivo.getHorarioFuncionamento())) {
                    alterou = true;
                    break;
                }
            }
        }
        return alterou;
    }

    private boolean containsHorarioFuncionamento(List<HorarioFuncCalculoAlvara> horariosAlvara, HorarioFuncionamento horarioFuncionamento) {
        if (horarioFuncionamento != null && horarioFuncionamento.getId() != null) {
            for (HorarioFuncCalculoAlvara horarioAlvara : horariosAlvara) {
                if (horarioAlvara.getHorarioFuncionamento() != null && horarioAlvara.getHorarioFuncionamento().getId() != null &&
                    horarioAlvara.getHorarioFuncionamento().getId().equals(horarioFuncionamento.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void adicionarCaracteristicasDeFuncionamento(ProcessoCalculoAlvara processo) {
        List<BCECaracFuncionamento> caracteristicas = buscarCaracteristicasFuncionamentoDoCadastro(processo.getCadastroEconomico());
        processo.getCaracteristicasAlvara().clear();
        for (BCECaracFuncionamento caracteristica : caracteristicas) {
            CaracteristicaFuncCalculoAlvara caracteristicaFuncCalculoAlvara = new CaracteristicaFuncCalculoAlvara();
            caracteristicaFuncCalculoAlvara.setProcessoCalculoAlvara(processo);
            caracteristicaFuncCalculoAlvara.setCaracFuncionamento(caracteristica.getCaracFuncionamento());
            caracteristicaFuncCalculoAlvara.setFormaPagamento(caracteristica.getFormaPagamento());
            caracteristicaFuncCalculoAlvara.setQuantidade(caracteristica.getQuantidade());

            processo.getCaracteristicasAlvara().add(caracteristicaFuncCalculoAlvara);
        }
    }

    public void adicionarEnderecosCadastroEconomico(ProcessoCalculoAlvara processo) {
        processo.getEnderecosAlvara().clear();
        for (EnderecoCadastroEconomico enderecoCadastroEconomico : processo.getCadastroEconomico().getListaEnderecoCadastroEconomico()) {
            EnderecoCalculoAlvara enderecoCalculoAlvara = new EnderecoCalculoAlvara();
            enderecoCalculoAlvara.setProcessoCalculoAlvara(processo);
            enderecoCalculoAlvara.setSequencial(enderecoCadastroEconomico.getSequencial());
            enderecoCalculoAlvara.setTipoEndereco(enderecoCadastroEconomico.getTipoEndereco());
            enderecoCalculoAlvara.setBairro(enderecoCadastroEconomico.getBairro());
            enderecoCalculoAlvara.setLogradouro(enderecoCadastroEconomico.getLogradouro());
            enderecoCalculoAlvara.setNumero(enderecoCadastroEconomico.getNumero());
            enderecoCalculoAlvara.setComplemento(enderecoCadastroEconomico.getComplemento());
            enderecoCalculoAlvara.setCep(enderecoCadastroEconomico.getCep());
            enderecoCalculoAlvara.setAreaUtilizacao(enderecoCadastroEconomico.getAreaUtilizacao());
            processo.getEnderecosAlvara().add(enderecoCalculoAlvara);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ProcessoCalculoAlvara atualizarDadosCnae(ProcessoCalculoAlvara processoCalculoAlvara) {
        for (CNAEProcessoCalculoAlvara cnae : processoCalculoAlvara.getCnaes()) {
            Object[] informacoes = buscarInformacoesAtualizadasEconomicoCNAE(cnae);
            if (informacoes != null) {
                EconomicoCNAE.TipoCnae tipoCnae;
                try {
                    tipoCnae = EconomicoCNAE.TipoCnae.valueOf((String) informacoes[0]);
                } catch (Exception e) {
                    tipoCnae = null;
                }
                if (!cnae.getTipoCnae().equals(tipoCnae)) {
                    cnae.setTipoCnae(tipoCnae);
                    cnae = em.merge(cnae);
                }
                cnae.setInicioAtividade(informacoes[2] != null ? (Date) informacoes[2] : null);
            }
        }
        return processoCalculoAlvara;
    }

    private Object[] buscarInformacoesAtualizadasEconomicoCNAE(CNAEProcessoCalculoAlvara cnaeProcessoCalculoAlvara) {
        String sql = " select ec.tipo, ec.exercidanolocal, ec.inicio from economicocnae ec " +
            " inner join cnae c on ec.cnae_id = c.id " +
            " where c.codigocnae = :codigo " +
            " and c.situacao = :situacao " +
            " and c.grauderisco = :grauDeRisco " +
            " and ec.cadastroeconomico_id = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", cnaeProcessoCalculoAlvara.getCnae().getCodigoCnae());
        q.setParameter("situacao", CNAE.Situacao.ATIVO.name());
        q.setParameter("grauDeRisco", cnaeProcessoCalculoAlvara.getCnae().getGrauDeRisco().name());
        q.setParameter("idCadastro", cnaeProcessoCalculoAlvara.getProcessoCalculoAlvara().getCadastroEconomico().getId());

        List<Object[]> objects = q.getResultList();

        if (objects != null && !objects.isEmpty()) {
            return objects.get(0);
        }
        return null;
    }

    public void imprimirArquivoRedeSim(ArquivoComposicao arquivoComposicao) {
        AbstractReport abstractReport = AbstractReport.getAbstractReport();
        abstractReport.setGeraNoDialog(Boolean.TRUE);

        byte[] bytes = arquivoFacade.montarArquivoParaDownload(arquivoComposicao.getArquivo());
        AbstractReport.poeRelatorioNaSessao(bytes, arquivoComposicao.getArquivo().getNome());
    }

    public Date buscarDataPgtoParcela(Long idProcesso) {
        try {
            String sql = " select (select max(revisao.datahora) from lotebaixa_aud lb_aud " +
                "                  inner join revisaoauditoria revisao on revisao.id = lb_aud.rev " +
                "                  where lb_aud.id = lb.id) as data " +
                " from processocalculoalvara proc " +
                " inner join calculoalvara calc on proc.id = calc.processocalculoalvara_id " +
                " inner join calculo c on calc.id = c.id " +
                " inner join valordivida vd on c.id = vd.calculo_id " +
                " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
                " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
                " inner join itemdam on pvd.id = itemdam.parcela_id " +
                " inner join dam on itemdam.dam_id = dam.id " +
                " inner join itemlotebaixa ilb on ilb.dam_id = dam.id " +
                " inner join lotebaixa lb on ilb.lotebaixa_id = lb.id " +
                " where proc.id = :idProcesso " +
                " and spvd.situacaoparcela in :situacoesPago " +
                " and lb.situacaolotebaixa in :situacoesLote " +
                " order by data desc ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idProcesso", idProcesso);
            q.setParameter("situacoesPago", SituacaoParcela.getsituacoesPago());
            q.setParameter("situacoesLote", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO.name(), SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()));

            List<Date> datas = q.getResultList();
            return (datas != null && !datas.isEmpty()) ? datas.get(0) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CalculoAlvara> buscarCalculosDoProcesso(CalculoAlvara calculoAlvara) {
        try {
            if (calculoAlvara.getProcessoCalculoAlvara().getId() == null) {
                return Lists.newArrayList();
            }

            String sql = " select calc.*, c.* from calculoalvara calc " +
                " inner join calculo c on calc.id = c.id " +
                " inner join valordivida vd on c.id = vd.calculo_id " +
                " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
                " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
                " where calc.processocalculoalvara_id = :idProcessoCalculo " +
                " and calc.tipoalvara = :tipoAlvara " +
                " and spvd.situacaoparcela <> :cancelamento " +
                " order by spvd.datalancamento desc ";

            Query q = em.createNativeQuery(sql, CalculoAlvara.class);
            q.setParameter("idProcessoCalculo", calculoAlvara.getProcessoCalculo().getId());
            q.setParameter("tipoAlvara", calculoAlvara.getTipoAlvara().name());
            q.setParameter("cancelamento", SituacaoParcela.CANCELAMENTO.name());

            List<CalculoAlvara> calculos = q.getResultList();
            if (calculos != null && !calculos.isEmpty()) {
                for (CalculoAlvara calculo : calculos) {
                    Hibernate.initialize(calculo.getItensAlvara());
                }
            }
            return calculos;
        } catch (Exception ex) {
            logger.error("Erro no buscarCalculosDoProcesso: {}", ex);
        }
        return Lists.newArrayList();
    }

    public CalculoAlvara buscarUltimoCalculoLocalizacao(Long idCmc) {
        String sql = " select calc.*, c.* from calculoalvara calc " +
            " inner join calculo c on calc.id = c.id " +
            " inner join processocalculoalvara proc on calc.processocalculoalvara_id = proc.id " +
            " inner join valordivida vd on c.id = vd.calculo_id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where proc.cadastroeconomico_id = :idCmc " +
            " and calc.tipoalvara = :tipoAlvara " +
            " and proc.situacaocalculoalvara <> :estornado " +
            " order by spvd.datalancamento desc ";

        Query q = em.createNativeQuery(sql, CalculoAlvara.class);
        q.setParameter("tipoAlvara", TipoAlvara.LOCALIZACAO.name());
        q.setParameter("idCmc", idCmc);
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<CalculoAlvara> calculos = q.getResultList();
        return (calculos != null && !calculos.isEmpty()) ? calculos.get(0) : null;
    }

    private boolean isAberturaNoExercicioCorrente(Long idCmc) {
        return isAberturaNoExercicioCorrente(idCmc, exercicioFacade.getExercicioCorrente().getAno());
    }

    private boolean isAberturaNoExercicioCorrente(Long idCmc, Integer ano) {
        String sql = " select cmc.id from cadastroeconomico cmc " +
            " where cmc.id = :idCmc " +
            " and extract(year from cmc.abertura) = :ano ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("ano", ano);

        return !q.getResultList().isEmpty();
    }

    private boolean isPrimeiroCalculo(Long idCmc, boolean validarAlvaraLancao) {
        return isAberturaNoExercicioCorrente(idCmc) && (!validarAlvaraLancao || !hasAlvaraLocalizacaoLancado(idCmc));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isPrimeiroCalculo(AssistenteCalculoAlvara assistente) {
        return isAberturaNoExercicioCorrente(assistente.getCmc().getId(), assistente.getExercicio().getAno());
    }

    private Alvara buscarAlvaraVigenteDoCmc(Long idCmc) {
        String sql = " select * from alvara al                                             " +
            " inner join processocalculoalvara proc on al.id = proc.alvara_id              " +
            " where al.cadastroeconomico_id = :idCmc                                       " +
            " and to_date(:dataParametro, 'dd/MM/yyyy') between trunc(al.iniciovigencia)   " +
            " and trunc(coalesce(al.finalvigencia, to_date(:dataParametro, 'dd/MM/yyyy'))) " +
            " and proc.situacaocalculoalvara <> :estornado " +
            " order by al.id desc fetch first 1 rows only ";

        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("idCmc", idCmc);
        q.setParameter("dataParametro", DataUtil.getDataFormatada(new Date()));
        q.setParameter("estornado", SituacaoCalculoAlvara.ESTORNADO.name());

        List<Alvara> alvaras = q.getResultList();
        Alvara a = null;
        for (Alvara alvara : alvaras) {
            if (!processoSuspensaoCassacaoAlvaraFacade.alvaraCassado(alvara.getId())) {
                a = alvara;
                break;
            }
        }
        return a;
    }

    public ProcessoCalculoAlvara atualizarHorariosDeFuncionamento(ProcessoCalculoAlvara proc) {
        if (proc.getAlvara() != null && (hasAlteracaoHorarioFuncionamento(proc) || hasAlteracaoEndereco(proc.getCadastroEconomico().getId()))) {
            adicionarEnderecosAndHorariosDeFuncionamento(proc, proc.getAlvara());
        }
        return proc;
    }

    public boolean hasAlteracaoCaracFuncionamento(CadastroEconomico cadastroEconomico, Long idProcessoCalculo) {
        List<CaracteristicaFuncCalculoAlvara> caracteristicasDoProcesso = buscarCaracteristicasFuncDoProcesso(idProcessoCalculo);

        if (caracteristicasDoProcesso.size() != cadastroEconomico.getListaBCECaracFuncionamento().size()) {
            return true;
        }
        List<CaracFuncionamento> caracteristicasProcesso = montarListaCaracteristicaFuncionamento(caracteristicasDoProcesso);
        List<CaracFuncionamento> caracteristicasCmc = montarListaCaracteristicaFuncionamento(cadastroEconomico.getListaBCECaracFuncionamento());
        return !caracteristicasProcesso.containsAll(caracteristicasCmc);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CaracteristicaFuncCalculoAlvara> buscarCaracteristicasFuncDoProcesso(Long idProcessoAlvara) {
        String sql = "select caracPro.* from CARACFUNCCALCALVARA caracPro " +
            " where caracPro.processoCalculoAlvara_id = :idProcesso";
        Query q = em.createNativeQuery(sql, CaracteristicaFuncCalculoAlvara.class);
        q.setParameter("idProcesso", idProcessoAlvara);
        return q.getResultList();
    }

    public <T> List<CaracFuncionamento> montarListaCaracteristicaFuncionamento(List<T> caracteristicas) {
        try {
            return Lists.newArrayList(Lists.transform(caracteristicas, new Function<T, CaracFuncionamento>() {
                @Override
                public CaracFuncionamento apply(T caracteristica) {
                    if (caracteristica != null) {
                        return caracteristica
                            instanceof CaracteristicaFuncCalculoAlvara ? ((CaracteristicaFuncCalculoAlvara) caracteristica).getCaracFuncionamento() :
                            ((BCECaracFuncionamento) caracteristica).getCaracFuncionamento();
                    }
                    return null;
                }
            }));
        } catch (Exception e) {
            logger.error("Erro ao converter lista de horarios. ", e);
        }
        return Lists.newArrayList();
    }

    public boolean hasAlteracaoHorarioFuncionamento(ProcessoCalculoAlvara proc) {
        if (proc.getAlvara() != null) {
            List<AlvaraHorarioFuncionamento> horarios = buscarHorariosFuncionamentoDoAlvara(proc.getAlvara());
            if (proc.getHorariosAlvara().size() != horarios.size()) {
                return true;
            } else {
                List<HorarioFuncionamento> horariosCalculo = montarListaHorarioFuncionamento(proc.getHorariosAlvara());
                List<HorarioFuncionamento> horariosAlvara = montarListaHorarioFuncionamento(horarios);
                return !horariosCalculo.containsAll(horariosAlvara);
            }
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<AlvaraHorarioFuncionamento> buscarHorariosFuncionamentoDoAlvara(Alvara alvara) {
        String sql = "select * from AlvaraHorarioFuncionamento where alvara_id = :idAlvara";
        Query q = em.createNativeQuery(sql, AlvaraHorarioFuncionamento.class);
        q.setParameter("idAlvara", alvara.getId());
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Boolean isPrimeiroCalculo(Long idCmc) {
        String sql = "select procalvara.id " +
            "                     from PROCESSOCALCULOALVARA procalvara " +
            "                     where procalvara.SITUACAOCALCULOALVARA <> :situacaoProc " +
            "                     and procalvara.CADASTROECONOMICO_ID = :idCmc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCmc", idCmc);
        q.setParameter("situacaoProc", SituacaoCalculoAlvara.ESTORNADO.name());
        return q.getResultList().isEmpty();
    }

    public <T> List<HorarioFuncionamento> montarListaHorarioFuncionamento(List<T> horarios) {
        try {
            return Lists.newArrayList(Lists.transform(horarios, new Function<T, HorarioFuncionamento>() {
                @Override
                public HorarioFuncionamento apply(T horarioFuncionamento) {
                    if (horarioFuncionamento != null) {
                        return horarioFuncionamento
                            instanceof HorarioFuncCalculoAlvara ? ((HorarioFuncCalculoAlvara) horarioFuncionamento).getHorarioFuncionamento() :
                            ((AlvaraHorarioFuncionamento) horarioFuncionamento).getHorarioFuncionamento();
                    }
                    return null;
                }
            }));
        } catch (Exception e) {
            logger.error("Erro ao converter lista de horarios. ", e);
        }
        return Lists.newArrayList();
    }

    public BigDecimal converterToUFMParaExercicio(BigDecimal valor, Exercicio exercicio) throws UFMException {
        return moedaFacade.converterToUFMParaExercicio(valor, exercicio);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public VOConfirmaTaxaDeLocalizacao preencherInfoConfirmacaoDeTaxaDeLocalizacao(ProcessoCalculoAlvara processoCalculoAlvara) {
        Alvara alvara = recuperarUltimoAlvaraDoCmc(processoCalculoAlvara.getCadastroEconomico().getId(), "");
        if (alvara == null) return null;
        VOConfirmaTaxaDeLocalizacao confirmaTaxaDeLocalizacao = new VOConfirmaTaxaDeLocalizacao();
        confirmaTaxaDeLocalizacao.setHasAlteracaoEndereco(hasAlteracaoEndereco(processoCalculoAlvara.getCadastroEconomico().getId()));
        if (confirmaTaxaDeLocalizacao.getHasAlteracaoEndereco()) {
            confirmaTaxaDeLocalizacao.setEnderecoAlvara(alvara.getEnderecoPrincipal());
            confirmaTaxaDeLocalizacao.setEnderecoCadastroEconomico(cadastroEconomicoFacade.getLocalizacao(processoCalculoAlvara.getCadastroEconomico()));
        }
        confirmaTaxaDeLocalizacao.setHasAlteracaoCnae(hasAlteracaoCnae(processoCalculoAlvara.getCadastroEconomico().getId()));
        if (confirmaTaxaDeLocalizacao.getHasAlteracaoCnae()) {
            List<CNAE> cnaesAlvara = alvara.getCnaeAlvaras().stream().map(CNAEAlvara::getCnae)
                .collect(Collectors.toList());
            List<CNAE> cnaesCmc = buscarCnaesVigentesCMC(processoCalculoAlvara.getCadastroEconomico().getId())
                .stream().map(EconomicoCNAE::getCnae).collect(Collectors.toList());
            List<CNAE> cnaesDiferentes = Lists.newArrayList();
            List<CNAE> cnaesRemovidos = separarCnaesDiferentes(cnaesAlvara, cnaesCmc);
            cnaesDiferentes.addAll(cnaesRemovidos);
            if (!cnaesRemovidos.isEmpty()) {
                confirmaTaxaDeLocalizacao.getCnaesDiferentes().addAll(cnaesRemovidos
                    .stream()
                    .map((cnae -> new VOCnae(cnae, Boolean.FALSE)))
                    .collect(Collectors.toList()));
            }
            List<CNAE> cnaesAdicionados = separarCnaesDiferentes(cnaesCmc, cnaesAlvara);
            cnaesDiferentes.addAll(cnaesAdicionados);
            if (!cnaesAdicionados.isEmpty()) {
                confirmaTaxaDeLocalizacao.getCnaesDiferentes().addAll(cnaesAdicionados
                    .stream()
                    .map((cnae -> new VOCnae(cnae, Boolean.TRUE)))
                    .collect(Collectors.toList()));
            }

            boolean alterou = false;
            boolean alterouSoCodigo = false;
            for (CNAE cnaeDiferente : cnaesDiferentes) {
                if (!contemCnaeMesmoCodigo(cnaesAlvara, cnaeDiferente) || !contemCnaeMesmoCodigo(cnaesCmc, cnaeDiferente)) {
                    alterou = true;
                } else if (contemCnaeMesmoCodigo(cnaesAlvara, cnaeDiferente) || contemCnaeMesmoCodigo(cnaesCmc, cnaeDiferente)) {
                    alterouSoCodigo = true;
                }
            }

            if (!alterou && alterouSoCodigo) {
                Map<String, List<CNAE>> mapaCnaesMesmoCodigo = montarMapaCnaesMesmoCodigo(cnaesAlvara, cnaesCmc);
                for (Map.Entry<String, List<CNAE>> entryCnae : mapaCnaesMesmoCodigo.entrySet()) {
                    if (entryCnae.getValue().size() > 1) {
                        for (CNAE cnae : entryCnae.getValue()) {
                            confirmaTaxaDeLocalizacao.getCnaesComMesmoCodigo().add(new VOCnae(cnae, Boolean.FALSE));
                        }
                    }
                }
            }
        }
        return confirmaTaxaDeLocalizacao.hasAlteracao() ? confirmaTaxaDeLocalizacao : null;
    }

    public boolean hasAlteracaoCnae(Long idCmc) {
        return alteracaoCmcFacade.hasAlteracaoCnae(idCmc);
    }

    public boolean hasAlteracaoEndereco(Long idCmc) {
        return alteracaoCmcFacade.hasAlteracaoEndereco(idCmc);
    }

    private List<CNAE> separarCnaesDiferentes(List<CNAE> cnaes1, List<CNAE> cnaes2) {
        List<CNAE> cnaesDiferentes = Lists.newArrayList();
        if (cnaes1 != null) {
            for (CNAE cnae : cnaes1) {
                if (!cnaes2.contains(cnae)) {
                    cnaesDiferentes.add(cnae);
                }
            }
        }
        return cnaesDiferentes;
    }

    private boolean contemCnaeMesmoCodigo(List<CNAE> cnaes, CNAE cnae) {
        boolean contem = false;
        for (CNAE cnaeDiferente : cnaes) {
            if (cnaeDiferente.getCodigoCnae().equals(cnae.getCodigoCnae())) {
                contem = true;
                break;
            }
        }
        return contem;
    }

    private Map<String, List<CNAE>> montarMapaCnaesMesmoCodigo(List<CNAE> cnaesAlvara, List<CNAE> cnaesCmc) {
        Map<String, List<CNAE>> mapaCnaesMesmoCodigo = preencherMapaCnaes(cnaesAlvara);
        mapaCnaesMesmoCodigo.putAll(preencherMapaCnaes(cnaesCmc));
        return mapaCnaesMesmoCodigo;
    }

    private Map<String, List<CNAE>> preencherMapaCnaes(List<CNAE> cnaes) {
        Map<String, List<CNAE>> mapaCnaesMesmoCodigo = Maps.newHashMap();
        for (CNAE cnae : cnaes) {
            if (!mapaCnaesMesmoCodigo.containsKey(cnae.getCodigoCnae())) {
                mapaCnaesMesmoCodigo.put(cnae.getCodigoCnae(), Lists.newArrayList(cnae));
            } else {
                List<CNAE> cnaesCodigo = mapaCnaesMesmoCodigo.get(cnae.getCodigoCnae());
                if (!cnaesCodigo.contains(cnae)) {
                    mapaCnaesMesmoCodigo.get(cnae.getCodigoCnae()).add(cnae);
                }
            }
        }
        return mapaCnaesMesmoCodigo;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public List<ProcessoCalculoAlvara> gerarTaxasAlvarasEmLote(AssistenteBarraProgresso assistente,
                                                               Exercicio exercicio,
                                                               List<CadastroEconomico> cadastros) throws Exception {
        assistente.setDescricaoProcesso("Gerando taxa de alvará para o exercício de " + exercicio.getAno());
        assistente.setTotal(cadastros.size());
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        List<ProcessoCalculoAlvara> processos = Lists.newArrayList();
        for (CadastroEconomico cadastro : cadastros) {
            if (buscarProcessoCalculoAlvaraPorExercicio(cadastro.getId(), exercicio.getAno()) != null) {
                assistente.conta();
                continue;
            }
            cadastro = cadastroEconomicoFacade.recuperar(cadastro.getId());
            ProcessoCalculoAlvara processoCalculoAlvara = criarProcessoCalculoAlvara(exercicio, assistente.getUsuarioSistema(),
                cadastro, SituacaoCalculoAlvara.EFETIVADO);
            processoCalculoAlvara = calcularAlvara(processoCalculoAlvara, exercicio, false, false, configuracaoTributario);
            if (processoCalculoAlvara.getCalculosAlvara() == null || processoCalculoAlvara.getCalculosAlvara().isEmpty()) {
                assistente.conta();
                continue;
            }
            processoCalculoAlvara = em.merge(processoCalculoAlvara);

            AssistenteEfetivarProcesso assistenteEfetivarProcesso = new AssistenteEfetivarProcesso();
            assistenteEfetivarProcesso.setProcessoCalculoAlvara(processoCalculoAlvara);
            assistenteEfetivarProcesso.setExercicioCorrente(exercicio);
            inicializarAssistente(assistenteEfetivarProcesso);
            gerarDebito(processoCalculoAlvara, assistenteEfetivarProcesso);

            processoCalculoAlvara.setAlvara(criarOrAtualizarAlvara(processoCalculoAlvara));
            em.merge(processoCalculoAlvara);

            processos.add(processoCalculoAlvara);
            assistente.conta();
        }
        return processos;
    }

    @Asynchronous
    public Future enviarPdfDAMsRedeSim(AssistenteBarraProgresso assistenteBarraProgresso,
                                       List<ProcessoCalculoAlvara> processos) throws Exception {
        if (processos != null) {
            assistenteBarraProgresso.setDescricaoProcesso("Enviando documentos de arrecadação municipal para REDESIM.");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(processos.size());
            for (ProcessoCalculoAlvara processo : processos) {
                if (!processo.isDispensaLicenciamento()) {
                    enviarPdfDAMsRedeSim(processo);
                } else {
                    enviarDispensaLicenciamento(processo);
                }
                assistenteBarraProgresso.conta();
            }
        }
        return new AsyncResult(null);
    }

    private ProcessoCalculoAlvara criarProcessoCalculoAlvara(Exercicio exercicio,
                                                             UsuarioSistema usuarioSistema,
                                                             CadastroEconomico cadastro,
                                                             SituacaoCalculoAlvara situacaoCalculoAlvara) {
        ProcessoCalculoAlvara processoCalculoAlvara = new ProcessoCalculoAlvara();
        processoCalculoAlvara.setDataLancamento(new Date());
        processoCalculoAlvara.setUsuario(usuarioSistema);
        processoCalculoAlvara.setSituacaoCalculoAlvara(situacaoCalculoAlvara);
        processoCalculoAlvara.setExercicio(exercicio);
        processoCalculoAlvara.setCadastroEconomico(cadastro);
        processoCalculoAlvara.setCodigo(montarProximoCodigoPorExercicio(processoCalculoAlvara.getExercicio()));
        criarCNAESProcessoCalculoAlvara(processoCalculoAlvara);
        adicionarEnderecosCadastroEconomico(processoCalculoAlvara);
        adicionarInformacoesCmc(processoCalculoAlvara);
        return processoCalculoAlvara;
    }

    private void criarCNAESProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        List<EconomicoCNAE> cnaesVigentesCMC = buscarCnaesVigentesCMC(processoCalculoAlvara.getCadastroEconomico().getId());
        List<EconomicoCNAE> cnaesNaoRepetidos = Lists.newArrayList();
        for (EconomicoCNAE economicoCNAE : cnaesVigentesCMC) {
            boolean adicionado = false;
            for (EconomicoCNAE economicoCNAENaoRepetido : cnaesNaoRepetidos) {
                if (economicoCNAENaoRepetido.getCnae().equals(economicoCNAE.getCnae())) {
                    adicionado = true;
                    break;
                }
            }
            if (!adicionado) {
                cnaesNaoRepetidos.add(economicoCNAE);
            }
        }
        for (EconomicoCNAE economicoCNAE : cnaesNaoRepetidos) {
            CNAEProcessoCalculoAlvara cnaeProcessoCalculoAlvara = new CNAEProcessoCalculoAlvara();
            cnaeProcessoCalculoAlvara.setProcessoCalculoAlvara(processoCalculoAlvara);
            cnaeProcessoCalculoAlvara.setCnae(economicoCNAE.getCnae());
            cnaeProcessoCalculoAlvara.setTipoCnae(economicoCNAE.getTipo());
            cnaeProcessoCalculoAlvara.setHorarioFuncionamento(economicoCNAE.getHorarioFuncionamento());
            cnaeProcessoCalculoAlvara.setExercidaNoLocal(economicoCNAE.getExercidaNoLocal());
            if (processoCalculoAlvara.getCnaes() == null) {
                processoCalculoAlvara.setCnaes(Lists.newArrayList());
            }
            processoCalculoAlvara.getCnaes().add(cnaeProcessoCalculoAlvara);
        }
    }

    private void atualizarSituacaoProcessoComCnaeDeInteresseDoEstado(AssistenteCalculoAlvara assistente) {
        try {
            if (assistente.getProcCalculo().getId() != null) {
                if (hasCnaeInteresseDoEstadoComParcelaAberta(assistente.getProcCalculo().getId(), null)) {
                    ProcessoCalculoAlvara processo = assistente.getProcCalculo();
                    processo.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.RECALCULADO);
                    assistente.setProcCalculo(processo);
                }
            }
        } catch (Exception ex) {
            logger.error("Erro no atualizarSituacaoProcessoComCnaeDeInteresseDoEstado: {}", ex);
        }
    }

    public boolean hasCnaeInteresseDoEstadoComParcelaAberta(Long idProcessoCalculo, Long idCalculo) {
        String sql = " select cnae.id from processocalculoalvara pca " +
            " inner join calculoalvara ca on pca.id = ca.processocalculoalvara_id " +
            " inner join cnaeprocessocalculoalvara cpca on pca.id = cpca.processocalculoalvara_id " +
            " inner join cnae cnae on cpca.cnae_id = cnae.id " +
            " inner join calculo c on ca.id = c.id " +
            " inner join valordivida vd on c.id = vd.calculo_id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where pca.id = :idProcessoCalculo " +
            " and coalesce(cpca.exercidanolocal, 0) = :exercidaNoLocal " +
            " and coalesce(cnae.interessedoestado, 0) = :interesse " +
            " and spvd.situacaoparcela = :aberto " +
            " and ca.controlecalculo <> :cancelado " +
            " and ca.tipoalvara = :sanitario ";
        if (idCalculo != null) {
            sql += "and ca.id = :idCalculo ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcessoCalculo", idProcessoCalculo);
        q.setParameter("exercidaNoLocal", 1);
        q.setParameter("interesse", 1);
        q.setParameter("aberto", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("cancelado", TipoControleCalculoAlvara.CANCELADO.name());
        q.setParameter("sanitario", TipoAlvara.SANITARIO.name());
        if (idCalculo != null) {
            q.setParameter("idCalculo", idCalculo);
        }

        return !q.getResultList().isEmpty();
    }

    private void estornarParcelasDoProcesso(ProcessoCalculoAlvara processoCalculo) {
        estornarParcelasEmAbertoParaCnaesComInteresseDoEstado(processoCalculo);
        estornarParcelasAguardandoCancelamento(processoCalculo);
    }

    private void estornarParcelasAguardandoCancelamento(ProcessoCalculoAlvara processoCalculo) {
        List<CalculoAlvara> calculoAlvaras = Lists.newArrayList();
        for (CalculoAlvara calculoAlvara : processoCalculo.getCalculosAlvara()) {
            if (calculoAlvara.getControleCalculo().isAguardandoCancelamento()) {
                calculoAlvaras.add(calculoAlvara);
            }
        }
        List<ValorDivida> valores = Lists.newArrayList();
        for (CalculoAlvara calculoAlvara : calculoAlvaras) {
            valores.addAll(recuperarAndAdicionarValorDivida(calculoAlvara));
            calculoAlvara.setControleCalculo(TipoControleCalculoAlvara.CANCELADO);
            em.merge(calculoAlvara);
        }
        alterarSituacaoParcela(valores, SituacaoParcela.CANCELAMENTO);
    }

    private void estornarParcelasEmAbertoParaCnaesComInteresseDoEstado(ProcessoCalculoAlvara processoCalculo) {
        String sql = " select ca.*, c.* " +
            " from calculoalvara ca " +
            " inner join calculo c on ca.id = c.id " +
            " inner join valordivida vd on c.id = vd.calculo_id " +
            " inner join parcelavalordivida pvd on vd.id = pvd.valordivida_id " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " inner join cnaeprocessocalculoalvara cpca on c.processocalculo_id = cpca.processocalculoalvara_id " +
            " inner join cnae on cpca.cnae_id = cnae.id " +
            " where ca.processocalculoalvara_id = :idProcesso " +
            " and spvd.situacaoparcela = :aberto " +
            " and coalesce(cpca.exercidanolocal, 0) = :exercidaNoLocal " +
            " and coalesce(cnae.interessedoestado, 0) = :interesse " +
            " and ca.tipoalvara = :sanitario ";

        Query q = em.createNativeQuery(sql, CalculoAlvara.class);
        q.setParameter("idProcesso", processoCalculo.getId());
        q.setParameter("exercidaNoLocal", 1);
        q.setParameter("interesse", 1);
        q.setParameter("aberto", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("sanitario", TipoAlvara.SANITARIO.name());

        List<CalculoAlvara> calculos = q.getResultList();

        if (calculos != null) {
            List<ValorDivida> valores = Lists.newArrayList();
            for (CalculoAlvara calculoAlvara : calculos) {
                valores.addAll(recuperarAndAdicionarValorDivida(calculoAlvara));
            }
            alterarSituacaoParcela(valores, SituacaoParcela.CANCELAMENTO);
        }
    }

    public AgrupadorVORevisaoCalculoLocalizacao preencherCnaesAndEnderecoPelaRevisao(Long idCmc, Long idCalculo) {
        return alteracaoCmcFacade.buscarRevisoesDosCalculoDeLocalizacaoDoCmc(idCmc, idCalculo);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public ProcessoCalculoAlvara corrigirCalculosDeLocalizacaoLancadosEmDuplicidade(ProcessoCalculoAlvara processoCalculoAlvara) {
        try {
            if (processoCalculoAlvara != null && processoCalculoAlvara.getId() != null) {
                List<CalculoAlvara> calculos = processoCalculoAlvara.buscarCalculosAtuais();
                Map<Date, List<CalculoAlvara>> calculosLocalizacaoPorData = Maps.newHashMap();

                for (CalculoAlvara calculo : calculos) {
                    if (calculo.getTipoAlvara().isLocalizacao()) {
                        Date chave = calculo.getDataCalculo();
                        if (!calculosLocalizacaoPorData.containsKey(chave)) {
                            calculosLocalizacaoPorData.put(chave, Lists.newArrayList(calculo));
                        } else {
                            calculosLocalizacaoPorData.get(chave).add(calculo);
                        }
                    }
                }

                List<ValorDivida> valores = Lists.newArrayList();
                for (Map.Entry<Date, List<CalculoAlvara>> calculoPorData : calculosLocalizacaoPorData.entrySet()) {
                    if (calculoPorData.getValue().size() > 1) {
                        int sizeCalculos = calculoPorData.getValue().size();
                        for (int i = 0; i < sizeCalculos; i++) {
                            CalculoAlvara calculoAlvara = calculoPorData.getValue().get(i);
                            valores.addAll(recuperarAndAdicionarValorDivida(calculoAlvara));
                            calculoAlvara.setControleCalculo(TipoControleCalculoAlvara.CANCELADO);
                            if (i == (sizeCalculos - 1)) {
                                gerarCalculoCorrigido(processoCalculoAlvara, calculoAlvara);
                            }
                        }
                    }
                }
                alterarSituacaoParcela(valores, SituacaoParcela.CANCELAMENTO);
            }
        } catch (Exception ex) {
            logger.error("Erro no corrigirCalculosDeLocalizacaoLancadosEmDuplicidade: {}", ex);
        }
        return processoCalculoAlvara;
    }

    public ProcessoCalculoAlvara corrigirCalculosDeMei(ProcessoCalculoAlvara processoCalculoAlvara) {
        List<CalculoAlvara> calculos = processoCalculoAlvara.buscarCalculosAtuais();
        List<ValorDivida> valores = Lists.newArrayList();
        for (CalculoAlvara calculo : calculos) {
            if (TipoControleCalculoAlvara.CALCULO.equals(calculo.getControleCalculo())) {
                valores.addAll(recuperarAndAdicionarValorDivida(calculo));
                calculo.setControleCalculo(TipoControleCalculoAlvara.CANCELADO);
                calculo.setIsento(true);
            }
        }
        alterarSituacaoParcela(valores, SituacaoParcela.CANCELAMENTO);
        return processoCalculoAlvara;
    }

    private void gerarCalculoCorrigido(ProcessoCalculoAlvara processoCalculoAlvara, CalculoAlvara calculoAlvara) {
        CalculoAlvara calculoCorrigido = new CalculoAlvara();
        calculoCorrigido.setProcessoCalculoAlvara(processoCalculoAlvara);
        calculoCorrigido.setDivida(calculoAlvara.getDivida());
        calculoCorrigido.setTipoAlvara(TipoAlvara.LOCALIZACAO);
        calculoCorrigido.setControleCalculo(TipoControleCalculoAlvara.RECALCULO);
        calculoCorrigido.setTipoCalculoAlvara(calculoAlvara.getTipoCalculoAlvara());
        calculoCorrigido.setTipoCalculo(calculoAlvara.getTipoCalculo());
        calculoCorrigido.setDataCalculo(calculoAlvara.getDataCalculo());
        calculoCorrigido.setValorEfetivo(calculoAlvara.getValorEfetivo());
        calculoCorrigido.setValorReal(calculoAlvara.getValorReal());
        calculoCorrigido.setIsento(calculoAlvara.getIsento());
        calculoCorrigido.setCadastro(calculoAlvara.getCadastro());
        calculoCorrigido.setReferencia(calculoAlvara.getReferencia());
        calculoCorrigido.setProcessoCalculo(calculoAlvara.getProcessoCalculo());
        calculoCorrigido.setObservacao(calculoAlvara.getObservacao());
        calculoCorrigido.setIsentaAcrescimos(calculoAlvara.getIsento());

        calculoCorrigido.setCalculosAgrupados(Lists.<CalculoAlvaraEfetivo>newArrayList());

        calculoCorrigido.setSubDivida(buscarSdParcela(calculoAlvara, processoCalculoAlvara.getExercicio()));
        calculoCorrigido.setVencimento(calculoAlvara.getVencimento());

        adicionarPessoaAoCalculo(calculoCorrigido, processoCalculoAlvara.getCadastroEconomico().getPessoa());

        for (ItemCalculoAlvara itemCalculo : calculoAlvara.getItensAlvara()) {
            ItemCalculoAlvara itemCorrigido = new ItemCalculoAlvara();
            itemCorrigido.setCalculoAlvara(calculoCorrigido);
            itemCorrigido.setTributo(itemCalculo.getTributo());
            itemCorrigido.setValorReal(itemCalculo.getValorReal());
            itemCorrigido.setValorEfetivo(itemCalculo.getValorEfetivo());
            itemCorrigido.setCaracFuncionamento(itemCalculo.getCaracFuncionamento());
            itemCorrigido.setItensAmbientais(Lists.<ItemCalculoAmbiental>newArrayList());

            calculoCorrigido.getItensAlvara().add(itemCorrigido);
        }
        processoCalculoAlvara.getCalculosAlvara().add(calculoCorrigido);
    }

    public Alvara recuperarUltimoAlvaraDoCmc(Long idCmc, String complementoWhere) {
        return alteracaoCmcFacade.recuperarUltimoAlvaraDoCmc(idCmc, complementoWhere);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ResultadoParcela> buscarParcelasEmAbertoDosCalculos(List<Long> idsCalculo) {
        if (idsCalculo != null && !idsCalculo.isEmpty()) {
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculo);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO.name());
            return consulta.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ProcessoCalculoAlvara atualizarValorTotalCalculado(ProcessoCalculoAlvara processoCalculoAlvara) {
        try {
            BigDecimal valorCalculado = BigDecimal.ZERO;
            for (CalculoAlvara buscarCalculo : processoCalculoAlvara.buscarCalculos()) {
                valorCalculado = valorCalculado.add(buscarCalculo.getValorEfetivo());
            }
            processoCalculoAlvara.setValorTotalCalculado(valorCalculado);
            processoCalculoAlvara = salvarRetornando(processoCalculoAlvara);
            processoCalculoAlvara = recuperar(processoCalculoAlvara.getId());
        } catch (Exception ex) {
            logger.error("Erro ao atualizarValorTotalCalculado: ", ex);
        }
        return processoCalculoAlvara;
    }

    public ProcessoCalculoAlvara finalizarRecalculos(ProcessoCalculoAlvara processoCalculoAlvara) {
        for (CalculoAlvara recalculo : processoCalculoAlvara.buscarRecalculos()) {
            recalculo.setControleCalculo(TipoControleCalculoAlvara.CANCELADO);
        }
        return processoCalculoAlvara;
    }

    public byte[] gerarDeclaracaoDispensaLicenciamento(ProcessoCalculoAlvara processoCalculoAlvara) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setApi("tributario/dispensa-licenciamento/");
        dto.setNomeRelatorio("Declaração de Dispensa de Licenciamento (" + processoCalculoAlvara.getId() + ")");
        dto.getParametroDTO().add(new ParametroDTO("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO"));
        dto.getParametroDTO().add(new ParametroDTO("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS"));
        dto.getParametroDTO().add(new ParametroDTO("ID_PROCESSO", processoCalculoAlvara.getId()));
        return ReportService.getInstance().gerarRelatorioSincrono(dto);
    }

    public boolean autenticouAlvara(AutenticaAlvara autenticaAlvara) {
        return buscarAlvaraAutenticado(autenticaAlvara) != null;
    }

    public WSAlvara buscarAlvaraAutenticado(AutenticaAlvara autenticaAlvara) {
        String sql = " select a.* from alvara a " +
            " inner join cadastroeconomico ce on a.cadastroeconomico_id = ce.id " +
            " inner join " + autenticaAlvara.getTipoAlvara().getTabelaSql() + " ca on ca.alvara_id = a.id" +
            " where trunc(a.emissao) = to_date(:emissao, 'dd/MM/yyyy')" +
            " and ce.inscricaocadastral = :inscricao" +
            " and a.assinaturadigital = :assinatura" +
            " and a.tipoalvara = :tipoAlvara " +
            " and ca.situacaocalculoalvara in (:situacao) ";

        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("emissao", DataUtil.getDataFormatada(autenticaAlvara.getDataEmissao()));
        q.setParameter("inscricao", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaAlvara.getIncricaoCadastral()).trim());
        q.setParameter("assinatura", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaAlvara.getAssinaturaParaAutenticar()).trim());
        q.setParameter("tipoAlvara", autenticaAlvara.getTipoAlvara().name());
        if (TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(autenticaAlvara.getTipoAlvara())) {
            q.setParameter("situacao", Lists.newArrayList(SituacaoCalculoAlvara.EFETIVADO.name(), SituacaoCalculoAlvara.RECALCULADO.name()));
        } else {
            q.setParameter("situacao", Lists.newArrayList(SituacaoCalculoAlvara.CALCULADO.name()));
        }

        List<Alvara> alvaras = q.getResultList();
        if (alvaras != null && !alvaras.isEmpty()) {
            return new WSAlvara(alvaras.get(0), "", true);
        }

        return null;
    }

    public Alvara buscarAlvaraLocalizacaoPago(CadastroEconomico cadastroEconomico, Exercicio exercicio, Alvara alvara) {
        String sql = " select al.* " +
            " from CalculoAlvaraLocalizacao cl " +
            " inner join Calculo cal on cal.id = cl.id " +
            " inner join Alvara al on al.id = cl.alvara_id " +
            " inner join ValorDivida vd on vd.calculo_id = cl.id " +
            " inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id " +
            " inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id " +
            " inner join Exercicio ex on ex.id =  al.exercicio_id " +
            " where cl.cadastroEconomico_id = :id_cadastroEconomico " +
            " and spvd.situacaoParcela in (:situacoesPago) " +
            " and al.tipoAlvara = :tipo " +
            " and cal.dataCalculo <= :vencimento " +
            " and ex.ano = :exercicio " +
            " order by cal.dataCalculo desc, al.id desc ";
        Query q = em.createNativeQuery(sql, Alvara.class);
        q.setParameter("id_cadastroEconomico", cadastroEconomico.getId());
        q.setParameter("situacoesPago", SituacaoParcela.getsituacoesPago());
        q.setParameter("tipo", TipoAlvara.LOCALIZACAO.name());
        q.setParameter("vencimento", alvara.getVencimento());
        q.setParameter("exercicio", exercicio.getAno());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            Alvara alvaraRecuperado = (Alvara) q.getSingleResult();
            alvaraRecuperado.getRecibosImpressaoAlvara().size();
            alvaraRecuperado.getCnaeAlvaras().size();
            return alvaraRecuperado;
        }
        return null;
    }

    public void salvarAlvara(Alvara alvara) {
        em.merge(alvara);
    }
}
