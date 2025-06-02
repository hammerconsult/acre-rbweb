/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.service.S2299Service;
import br.com.webpublico.esocial.service.S2399Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.negocios.rh.previdencia.PrevidenciaComplementarFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import org.hibernate.NonUniqueResultException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class ExoneracaoRescisaoFacade extends AbstractFacade<ExoneracaoRescisao> {

    private static final Logger logger = LoggerFactory.getLogger(ExoneracaoRescisaoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private PrevidenciaComplementarFacade previdenciaComplementarFacade;

    private S2299Service s2299Service;

    private S2399Service s2399Service;

    public ExoneracaoRescisaoFacade() {
        super(ExoneracaoRescisao.class);
    }

    @PostConstruct
    public void init() {
        s2299Service = (S2299Service) Util.getSpringBeanPeloNome("s2299Service");
        s2399Service = (S2399Service) Util.getSpringBeanPeloNome("s2399Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public PeriodoAquisitivoFLFacade getPeriodoAquisitivoFLFacade() {
        return periodoAquisitivoFLFacade;
    }

    public RegistroESocialFacade getRegistroESocialFacade() {
        return registroESocialFacade;
    }

    public ReintegracaoFacade getReintegracaoFacade() {
        return reintegracaoFacade;
    }

    @Override
    public void salvar(ExoneracaoRescisao entity) {
        getEntityManager().merge(entity);
        List<PeriodoAquisitivoFL> list;
        if (isContratoFP(entity.getVinculoFP().getId())) {
            try {
                if (entity.getVinculoFP() instanceof Estagiario) {
                    em.merge(entity);
                } else {
                    periodoAquisitivoFLFacade.gerarPeriodosAquisitivos((ContratoFP) entity.getVinculoFP(), sistemaFacade.getDataOperacao(), null);
                }
            } catch (SemBasePeriodoAquisitivoException se) {
                throw new SemBasePeriodoAquisitivoException(se.getMessage());
            }
        }
        try {
            gerarFolhaRescisao(entity);
        } catch (JRException ex) {
            logger.error("Erro:", ex);
        } catch (IOException ex) {
            logger.error("Erro:", ex);
        }
        excluirFichaFinanceira(buscarFichaFinanceiraParaExclusao(entity.getVinculoFP(), entity.getDataRescisao()));
    }

    public Date controleDataFinalRecisao(ExoneracaoRescisao entity) {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(sistemaFacade.getDataOperacao());
        if (configuracaoRH.getConfiguracaoRescisao().getControleVigenciaFinalViculoFP()) {
            return DataUtil.adicionaDias(entity.getDataRescisao(), -1);
        } else {
            return entity.getDataRescisao();
        }
    }

    public List<Afastamento> hasAfastamentoDataExoneracao(VinculoFP vinculo, Date dataExoneracao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from AFASTAMENTO ");
        sql.append(" where CONTRATOFP_ID = :idContrato and :dataExoneracao < coalesce(termino, :dataExoneracao) ");
        Query q = em.createNativeQuery(sql.toString(), Afastamento.class);
        q.setParameter("idContrato", vinculo.getId());
        q.setParameter("dataExoneracao", dataExoneracao);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public List<ConcessaoFeriasLicenca> hasConcessaoDataExoneracao(VinculoFP vinculo, Date dataExoneracao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ConcessaoFeriasLicenca concessao ");
        sql.append(" inner join PERIODOAQUISITIVOFL pa on concessao.periodoaquisitivofl_id = pa.id ");
        sql.append(" where pa.CONTRATOFP_ID = :idContrato and :dataExoneracao <= coalesce(concessao.DATAFINAL, :dataExoneracao) ");
        Query q = em.createNativeQuery(sql.toString(), ConcessaoFeriasLicenca.class);
        q.setParameter("idContrato", vinculo.getId());
        q.setParameter("dataExoneracao", dataExoneracao);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public ConcessaoFeriasLicenca temConcessaoNaDataExoneracao(VinculoFP vinculo, Date dataExoneracao) {
        String sql = "select * from ConcessaoFeriasLicenca concessao " +
             " inner join periodoaquisitivofl pa ON concessao.periodoaquisitivofl_id = pa.id " +
             " where pa.contratofp_id = :idContrato " +
             " and concessao.datainicial <= :dataExoneracao " +
             " and :dataExoneracao < COALESCE(concessao.datafinal, :dataExoneracao)";

        Query query = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        query.setParameter("idContrato", vinculo.getId());
        query.setParameter("dataExoneracao", dataExoneracao);

        try {
            return (ConcessaoFeriasLicenca) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new RuntimeException("Mais de uma concessão encontrada para a data de exoneração", e);
        }
    }


    public List<ConcessaoFeriasLicenca> temConcessaoAposDataExoneraca(VinculoFP vinculo, Date dataExoneracao) {
        String sql = "select concessao.* from concessaoferiaslicenca concessao " +
            "inner join periodoaquisitivofl pa on concessao.periodoaquisitivofl_id = pa.id " +
            "where pa.CONTRATOFP_ID = :idContrato " +
            "and concessao.datainicial > :dataExoneracao";
        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("idContrato", vinculo.getId());
        q.setParameter("dataExoneracao", dataExoneracao);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public void salvarNovo(ExoneracaoRescisao entity, Date dataFinalVigenciaDoContratoFP) {
        bloquearUsuario(entity);
        if (isContratoFP(entity.getVinculoFP().getId())) {
            realizarRecisaoContratoPF(entity, dataFinalVigenciaDoContratoFP);
        }
        if (isAposentado(entity.getVinculoFP().getId())) {
            realizarRecisaoAposentadoria(entity);
        }
        if (isPensionista(entity.getVinculoFP().getId())) {
            realizarRecisaoPensionista(entity);
        }
        try {
            gerarFolhaRescisao(entity);
        } catch (JRException | IOException ex) {
            logger.error("Erro:", ex);
        }
        excluirFichaFinanceira(buscarFichaFinanceiraParaExclusao(entity.getVinculoFP(), entity.getDataRescisao()));
    }

    private void bloquearUsuario(ExoneracaoRescisao exoneracao) {
        UsuarioSistema usuario = usuarioSistemaFacade.usuarioSistemaDaPessoa(exoneracao.getVinculoFP().getMatriculaFP().getPessoa());
        if (usuario != null && !usuario.getExpira()) {
            List<ContratoFP> contratos = contratoFPFacade.listaContratosVigentesPorPessoaFisica(exoneracao.getVinculoFP().getMatriculaFP().getPessoa());
            boolean contratoComMultiploVinculoAtivo = false;
            for (ContratoFP contrato : contratos) {
                if (!exoneracao.getVinculoFP().getId().equals(contrato.getId())) {
                    contratoComMultiploVinculoAtivo = true;
                    break;
                }
            }
            if (!contratoComMultiploVinculoAtivo && DataUtil.dateToLocalDate(sistemaFacade.getDataOperacao()).isAfter(DataUtil.dateToLocalDate(exoneracao.getDataRescisao()))) {
                usuarioSistemaFacade.inativaUsuario(usuario);
            }
        }
    }

    public FichaFinanceiraFP buscarFichaFinanceiraParaExclusao(VinculoFP vinculoFP, Date dataRescisao) {
        int mes = DataUtil.getMes(dataRescisao);
        int ano = DataUtil.getAno(dataRescisao);
        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(vinculoFP, mes, ano, TipoFolhaDePagamento.NORMAL);
        if (ficha != null && !ficha.getFolhaDePagamento().folhaEfetivada() && !ficha.getCreditoSalarioPago() && !StatusCompetencia.EFETIVADA.equals(ficha.getFolhaDePagamento().getCompetenciaFP().getStatusCompetencia())) {
            return ficha;
        }
        return null;
    }

    public void excluirFichaFinanceira(FichaFinanceiraFP ficha) {
        try {
            if (ficha != null) {
                fichaFinanceiraFPFacade.remover(ficha);
            }
        } catch (Exception ex) {
            logger.error("Erro ao excluir a ficha financeira {}", ex);
        }
    }

    private boolean pessoaTemUsuarioSistema(ExoneracaoRescisao exoneracao) {
        return usuarioSistemaFacade.usuarioSistemaDaPessoa(exoneracao.getVinculoFP().getMatriculaFP().getPessoa()) != null;
    }

    public void criarNovoProvimentoFPEstornoExoneracao(ExoneracaoRescisao entity) {
        ContratoFP contratoFP = entity.getVinculoFP().getContratoFP();
        ProvimentoFP provimentoFPEstornoExoneracao = getNovoProvimentoFP(entity, contratoFP);
        provimentoFPEstornoExoneracao.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(getCodigoTipoProvimentoEstornoExoneracao(contratoFP)));
        em.merge(provimentoFPEstornoExoneracao);
    }

    private int getCodigoTipoProvimentoEstornoExoneracao(ContratoFP contratoFP) {
        if (contratoFP.isModalidadeContratoFPConcursados() || contratoFP.isModalidadeContratoFPTrabalhoTempoIndeterminado()) {
            return TipoProvimento.ESTORNO_EXONERAÇÃO_CARREIRA;
        } else {
            return TipoProvimento.ESTORNO_EXONERAÇÃO_COMISSÃO;
        }
    }

    private int getCodigoTipoProvimentoExoneracao(ContratoFP contratoFP) {
        if (contratoFP.isModalidadeContratoFPConcursados() || contratoFP.isModalidadeContratoFPTrabalhoTempoIndeterminado()) {
            return TipoProvimento.EXONERACAORESCISAO_CARREIRA;
        } else {
            return TipoProvimento.EXONERACAORESCISAO_COMISSAO;
        }
    }

    private void realizarRecisaoPensionista(ExoneracaoRescisao entity) {
        VinculoFP v = entity.getVinculoFP();
        DateTime dataRescisao = new DateTime(entity.getDataRescisao());
        v.setFinalVigencia(dataRescisao.minusDays(1).toDate());
        em.persist(v);
        em.persist(entity);
    }

    private void realizarRecisaoPrevidenciaComplemenatar(ExoneracaoRescisao entity) {
        ItemPrevidenciaComplementar item = previdenciaComplementarFacade.buscarItemPrevidenciaComplementarPorContratoSemFinalVigencia((ContratoFP) entity.getVinculoFP(), entity.getDataRescisao());
        if (item != null) {
            item.setFinalVigencia(entity.getDataRescisao());
            em.persist(item);
        }
    }

    public void reabrirVigenciaPrevidenciaComplementar(ExoneracaoRescisao entity) {
        ItemPrevidenciaComplementar item = previdenciaComplementarFacade.buscarItemPrevidenciaComplementarPorContratoComFinalVigenciaIgualDataReferencia((ContratoFP) entity.getVinculoFP(), entity.getDataRescisao());
        if (item != null) {
            item.setFinalVigencia(null);
            em.persist(item);
        }
    }

    private void realizarRecisaoAposentadoria(ExoneracaoRescisao entity) {
        VinculoFP v = entity.getVinculoFP();
        DateTime dataRescisao = new DateTime(entity.getDataRescisao());
        v.setFinalVigencia(dataRescisao.minusDays(1).toDate());
        em.persist(v);
        em.persist(entity);
    }

    public boolean isAposentado(Object id) {
        Aposentadoria ap = em.find(Aposentadoria.class, id);
        if (ap == null) return false;
        return true;
    }

    public boolean isContratoFP(Object id) {
        ContratoFP con = em.find(ContratoFP.class, id);
        if (con == null) return false;
        return true;
    }

    public boolean isPensionista(Object id) {
        Pensionista pen = em.find(Pensionista.class, id);
        if (pen == null) return false;
        return true;
    }

    public void realizarRecisaoContratoPF(ExoneracaoRescisao entity, Date controleRescisao) {
        if (entity.getVinculoFP() instanceof Estagiario) {
            entity = em.merge(entity);
        } else {
            ContratoFP contratoFP = entity.getVinculoFP().getContratoFP();
            contratoFPFacade.processarHistoricoAndEncerrarVigenciasPorContratoFP(contratoFP, controleRescisao);
            gerarPeriodoAquisitivo(entity);
            entity = em.merge(entity);
            criarNovaSituacaoContratoFP(contratoFP, controleRescisao);
            criarNovoProvimentoExoneracao(entity, contratoFP);
            em.merge(entity);
            realizarRecisaoPrevidenciaComplemenatar(entity);
        }
    }

    private void criarNovoProvimentoExoneracao(ExoneracaoRescisao entity, ContratoFP contratoFP) {
        ProvimentoFP novoProvimentoFPExoneracao = getNovoProvimentoFP(entity, contratoFP);
        novoProvimentoFPExoneracao.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(getCodigoTipoProvimentoExoneracao(contratoFP)));
        entity.setProvimentoFP(em.merge(novoProvimentoFPExoneracao));
    }

    private void gerarPeriodoAquisitivo(ExoneracaoRescisao entity) {
        try {
            periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(entity.getVinculoFP().getContratoFP(), new Date(), null);
        } catch (SemBasePeriodoAquisitivoException se) {
            throw new SemBasePeriodoAquisitivoException(se.getMessage());
        }
    }

    private void criarNovaSituacaoContratoFP(ContratoFP contratoFP, Date finalVigencia) {
        //pego a data da aposentadoria e aumento 1 dia
        //para ser o inicio da vigencia da nova situação
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(finalVigencia);
        calendario.add(Calendar.DATE, 1);

        //criar a nova situacaoContratoFP vigente
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setInicioVigencia(calendario.getTime());
        situacaoContratoFP.setContratoFP(contratoFP);
        //recuperar a situacao Funcional para aposentados
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.EXONERADO_RESCISO));
        em.persist(situacaoContratoFP);
    }

    private ProvimentoFP getNovoProvimentoFP(ExoneracaoRescisao entity, ContratoFP contratoFP) {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        provimentoFP.setDataProvimento(entity.getDataRescisao());
        provimentoFP.setAtoLegal(entity.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        return provimentoFP;
    }

    public void gerarFolhaRescisao(ExoneracaoRescisao exoneracao) throws JRException, IOException {
//        FolhaDePagamento folhaRescisao = folhaDePagamentoFacade.getFolhaPorTipo(TipoFolhaDePagamento.RESCISAO);
//        //System.out.println(folhaRescisao);
//        if (folhaRescisao == null) {
//            folhaRescisao = new FolhaDePagamento();
//            folhaRescisao.setAno(FolhaDePagamentoFacade.getAno(new Date()));
//            folhaRescisao.setMes(Mes.getMesToInt(FolhaDePagamentoFacade.getMes(new Date())));
//            folhaRescisao.setCalculadaEm(new Date());
//            folhaRescisao.setTipoFolhaDePagamento(TipoFolhaDePagamento.RESCISAO);
//            folhaRescisao.setCompetenciaFP(getCompetenciaAberta(folhaRescisao));
//
//            folhaRescisao.setUnidadeOrganizacional(hierarquiaOrganizacionalFacade.getRaizHierarquia(sistemaFacade.getExercicioCorrente()).getSubordinada());
//            folhaDePagamentoFacade.salvarNovo(folhaRescisao);
//        }
//
//        exoneracao.getVinculoFP().setFolha(folhaRescisao);
//        DetalheProcessamentoFolha detalhe = new DetalheProcessamentoFolha();
//        detalhe.setTipoCalculo(CalculoFolhaDePagamentoControlador.TipoCalculo.INDIVIDUAL);
//        detalhe.setItemCalendarioFP(calendarioFPFacade.recuperarDataCalculoCalendarioFP(folhaRescisao.getMes().getNumeroMes(), folhaRescisao.getAno()));
//        List<VinculoFP> vinculos = new ArrayList<>();
//        vinculos.add(exoneracao.getVinculoFP());
//        folhaDePagamentoFacade.iniciarProcessamento(folhaRescisao, vinculos, detalhe);
    }

    public CompetenciaFP getCompetenciaAberta(FolhaDePagamento folha) {
        CompetenciaFP comp = folhaDePagamentoFacade.verificaCompetenciaAberta(folha);
        if (comp == null) {
            comp = new CompetenciaFP();
            comp.setExercicio(sistemaFacade.getExercicioCorrente());
            comp.setMes(Mes.getMesToInt(FolhaDePagamentoFacade.getMes(new Date())));
            comp.setStatusCompetencia(StatusCompetencia.ABERTA);
            comp.setTipoFolhaDePagamento(TipoFolhaDePagamento.RESCISAO);
            competenciaFPFacade.salvarNovo(comp);
        }
        return comp;
    }

    public boolean existeContratoFP(VinculoFP vinculo) {
        String hql = " from ExoneracaoRescisao e where e.vinculoFP = :parametro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", vinculo);
        return (q.getResultList().isEmpty());
    }

    public Boolean buscaContratosNaoExonerados(VinculoFP contratoFP) {
        Query q = em.createQuery(" select contrato from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " where contrato in("
            + " select vinculoFP from ProvimentoFP exonerado "
            + " inner join exonerado.vinculoFP vinculoFP "
            + " inner join exonerado.tipoProvimento tipo "
            + " where (tipo.codigo = :exoneracaoCarreira or tipo.codigo = :exoneracaoComissao)"
            + " and vinculoFP = contrato and exonerado.sequencia > "
            + " coalesce((select max(reintegrado.sequencia) from ProvimentoFP reintegrado "
            + " inner join reintegrado.vinculoFP vinculoFP "
            + " inner join reintegrado.tipoProvimento tipo "
            + " where tipo.codigo = :provimentoReintegracao and vinculoFP = contrato),0)"
            + " and contrato.tipoRegime.codigo = :codigoEstatutario) "
            + " and contrato.id = :paramentroContratoFP ");
        q.setParameter("codigoEstatutario", 2l);
        q.setParameter("provimentoReintegracao", TipoProvimento.REINTEGRACAO);
        q.setParameter("exoneracaoCarreira", TipoProvimento.EXONERACAORESCISAO_CARREIRA);
        q.setParameter("exoneracaoComissao", TipoProvimento.EXONERACAORESCISAO_COMISSAO);
        q.setParameter("paramentroContratoFP", contratoFP.getId());
        return !q.getResultList().isEmpty();
    }

    public List<ExoneracaoRescisao> listaExoneracaoRescisao(String s, int inicio, int max) {
        StringBuilder sb = new StringBuilder();
        sb.append("select r from ExoneracaoRescisao r inner join r.contratoFP contrato ");
        sb.append("where lower(contrato.matriculaFP.matricula) like :filtro ");
        sb.append("or  lower(contrato.matriculaFP.pessoa.nome) like :filtro ");
        sb.append("or  lower(contrato.numero) like :filtro ");
        sb.append("or  lower(contrato.numero) like :filtro ");
        sb.append("or  lower(r.motivoExoneracaoRescisao.descricao) like :filtro ");
        sb.append("or  lower(r.tipoAvisoPrevio) like :filtro ");
        sb.append("or  (to_char(r.dataRescisao,'dd/MM/yyyy') like :filtro) ");
        Query q = em.createQuery(sb.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<ExoneracaoRescisao> listaExoneracaoRescisaoCompetenciaSEFIP(ContratoFP contratoFP, Integer mes, Integer ano) {
        Query q = em.createQuery(" from ExoneracaoRescisao e "
            + " where e.vinculoFP = :contrato "
            + " and extract(month from e.dataRescisao) = :mes "
            + " and extract(year from e.dataRescisao) = :ano ");
        q.setParameter("contrato", contratoFP);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public List<ExoneracaoRescisao> recuperaExoneracaoRescisaoProvimento(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("  from ExoneracaoRescisao rescisao");
        hql.append(" where rescisao.vinculoFP = :contrato");
        hql.append("   and :dataProvimento >= rescisao.dataRescisao");
        hql.append(" order by rescisao.dataRescisao");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);
        return q.getResultList();
    }

    public ExoneracaoRescisao recuperaExoneracaoRecisao(VinculoFP contratoFP) {
        String hql = "from ExoneracaoRescisao ex where ex.vinculoFP = :contratoFP order by ex.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);

        List<ExoneracaoRescisao> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ExoneracaoRescisao recuperaExoneracaoRecisao2(ContratoFP contratoFP) {
        String hql = "from ExoneracaoRescisao ex where ex.vinculoFP = :contratoFP order by ex.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);

        List<ExoneracaoRescisao> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public ExoneracaoRescisao recuperaExoneracaoRecisaoPorAno(ContratoFP contratoFP, Integer ano) {
        try {
            String hql = "from ExoneracaoRescisao ex " +
                " where ex.vinculoFP = :contratoFP  " +
                " and extract(year from ex.dataRescisao) = :ano " +
                " order by ex.id desc ";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            q.setParameter("ano", ano);
            q.setMaxResults(1);

            return (ExoneracaoRescisao) q.getSingleResult();
        } catch (NoResultException nre) {
            return new ExoneracaoRescisao();
        }
    }

    public ExoneracaoRescisao recuperaContratoExonerado(ContratoFP contratoFP, Mes mes, Integer ano) {
        String hql = "select er from ExoneracaoRescisao er join er.vinculoFP contrato"
            + " where contrato = :contrato and (extract(month from er.dataRescisao) = :mes and extract(year from er.dataRescisao) = :ano) " +
            " order by er.dataRescisao desc ";

        Query q = em.createQuery(hql);
        q.setParameter("contrato", contratoFP);
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (ExoneracaoRescisao) q.getResultList().get(0);
        }
        return null;
    }


    public Integer buscarQuantidadeRescisoesPorData(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from ExoneracaoRescisao_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 0 and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public boolean existeExoneracaoRescisaoPorVinculoFP(VinculoFP vinculoFP) {
        ExoneracaoRescisao exoneracaoRescisaoPorVinculoFP = buscarExoneracaoRescisaoPorVinculoFP(vinculoFP);
        return exoneracaoRescisaoPorVinculoFP != null ? true : false;
    }

    public ExoneracaoRescisao buscarExoneracaoRescisaoPorVinculoFP(VinculoFP vinculoFP) {
        try {
            Query q = em.createQuery("select exoneracao from ExoneracaoRescisao exoneracao where exoneracao.vinculoFP = :vin");
            q.setParameter("vin", vinculoFP);
            q.setMaxResults(1);
            return (ExoneracaoRescisao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        return configuracaoRHFacade;
    }

    public void enviarS2299ESocial(ConfiguracaoEmpregadorESocial empregador, ExoneracaoRescisao exoneracaoRescisao) throws ValidacaoException {
        s2299Service.enviarS2299(empregador, exoneracaoRescisao);
    }

    public void enviarS2399ESocial(ConfiguracaoEmpregadorESocial empregador, ExoneracaoRescisao exoneracaoRescisao) throws ValidacaoException {
        PrestadorServicos prestadorServicos = prestadorServicosFacade.buscarPrestadorServicosPorIdPessoa(
            Optional.ofNullable(exoneracaoRescisao).map(ExoneracaoRescisao::getVinculoFP).map(VinculoFP::getContratoFP).map(ContratoFP::getMatriculaFP).map(MatriculaFP::getPessoa).map(Pessoa::getId).orElse(null));
        s2399Service.enviarS2399(empregador, exoneracaoRescisao, prestadorServicos);
    }

    public ExoneracaoRescisao recuperarExoneracaoRescisaoPorContratoFP(ContratoFP contratoFP) {
        String hql = "from ExoneracaoRescisao exoneracao" +
            " where exoneracao.vinculoFP = :contratoFP " +
            " order by exoneracao.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        List<ExoneracaoRescisao> lista = q.getResultList();

        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public List<ExoneracaoRescisao> recuperarExoneracaoRescisaoPorVinculoFP(Long vinculo) {
        String hql = "from ExoneracaoRescisao exoneracao" +
            " where exoneracao.vinculoFP.id = :vinculo " +
            " order by exoneracao.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("vinculo", vinculo);
        List<ExoneracaoRescisao> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return Lists.newArrayList();
        }
        return lista;
    }

    public List<ExoneracaoRescisao> buscarExoneracaoRescisaoContrato(Long contratoID) {
        String sql = "select e.* " +
            "from exoneracaoRescisao e " +
            "where e.VINCULOFP_ID = :contratoID " +
            "  and e.id not in (select r.EXONERACAORESCISAO_ID from REINTEGRACAO r where r.CONTRATOFP_ID = e.VINCULOFP_ID) " +
            " order by e.DATARESCISAO desc ";
        Query q = em.createNativeQuery(sql, ExoneracaoRescisao.class);
        q.setParameter("contratoID", contratoID);
        return q.getResultList();
    }

    public ExoneracaoRescisao buscarExoneracaoRescisaoContratoNotReintegracao(Long contratoID) {
        String sql = "select e.* " +
            "from exoneracaoRescisao e " +
            "where e.VINCULOFP_ID = :contratoID " +
            "  and e.id not in (select r.EXONERACAORESCISAO_ID from REINTEGRACAO r where r.CONTRATOFP_ID = e.VINCULOFP_ID) " +
            " order by e.DATARESCISAO desc ";
        Query q = em.createNativeQuery(sql, ExoneracaoRescisao.class);
        q.setParameter("contratoID", contratoID);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ExoneracaoRescisao) resultList.get(0);
        }
        return null;
    }

    public List<Long> buscarIdsExoneracoesPorContratoFP(ContratoFP contratoFP) {
        String sql = " select exoneracao.id from ExoneracaoRescisao exoneracao " +
            " where exoneracao.vinculofp_id = :contrato " +
            " order by exoneracao.id desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", contratoFP.getId());
        List<BigDecimal> resultList = q.getResultList();
        List<Long> retorno = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (BigDecimal resultado : resultList) {
                retorno.add(resultado.longValue());
            }
        }
        return retorno;
    }

    public List<String> getIdsEsocialExoneracaoPorEmpregadorFiltrandoPorInicioAndFinalVigenciaAndTipoArquivoEsocial(Date inicio, Date fim, List<Long> idsUnidade, TipoArquivoESocial tipoArquivoESocial) {
        String sql = "select registro.IDENTIFICADORWP " + " from REGISTROESOCIAL registro " + " inner join exoneracaorescisao exoneracao on exoneracao.id = registro.IDENTIFICADORWP" + " inner join vinculofp vinculo on exoneracao.VINCULOFP_ID = vinculo.ID " + " where TIPOARQUIVOESOCIAL = :tipoArquivo  " + " and (SITUACAO = 'PROCESSADO_COM_SUCESSO' or SITUACAO = 'PROCESSADO_COM_ADVERTENCIA' " + " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades))";
        if (inicio != null || fim != null) {
            sql += " and exoneracao.DATARESCISAO between coalesce(:inicio, sysdate) and coalesce(:fim, sysdate)";
        }
        Query q = em.createNativeQuery(sql);
        if (inicio != null || fim != null) {
            q.setParameter("inicio", inicio != null ? inicio : new Date());
            q.setParameter("fim", fim != null ? fim : new Date());
        }
        q.setParameter("unidades", idsUnidade);
        q.setParameter("tipoArquivo", tipoArquivoESocial.name());
        List result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }
}
