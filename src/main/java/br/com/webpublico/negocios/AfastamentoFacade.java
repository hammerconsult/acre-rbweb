/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.EnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.enums.FinalidadeCedenciaContratoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.esocial.service.S2230Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.integracaoponto.EnvioDadosRBPontoFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static br.com.webpublico.util.DataUtil.localDateToDate;

@Stateless
public class AfastamentoFacade extends AbstractFacade<Afastamento> {

    public static final int TIPO_Q2 = 23;
    public static final int CESSAO_COM_ONUS = 14;
    public static final int CESSAO_SEM_ONUS = 15;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    private S2230Service s2230Service;

    public AfastamentoFacade() {
        super(Afastamento.class);
        s2230Service = (S2230Service) Util.getSpringBeanPeloNome("s2230Service");

    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaltasFacade getFaltasFacade() {
        return faltasFacade;
    }

    @Override
    public Afastamento recuperar(Object id) {
        Afastamento afastamento = super.recuperar(id);
        if (afastamento.getDetentorArquivoComposicao() != null && afastamento.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            for (ArquivoComposicao arquivoComposicao : afastamento.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return afastamento;
    }

    @Override
    public void salvarNovo(Afastamento entity) {

        DateTime refe = new DateTime(entity.getInicio());


        SituacaoContratoFP vigente = situacaoContratoFPFacade.recuperaSituacaoContratoFPVigente(entity.getContratoFP());
        if (vigente != null) {
            vigente.setFinalVigencia(refe.minusDays(1).toDate());
            em.merge(vigente);
        }


        if (entity.isRetornoInformado()) {

            SituacaoContratoFP situacaoRetorno = new SituacaoContratoFP();
            situacaoRetorno.setInicioVigencia(entity.getInicio());
            situacaoRetorno.setFinalVigencia(entity.getTermino());
            situacaoRetorno.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.AFASTADO_LICENCIADO));
            situacaoRetorno.setContratoFP(entity.getContratoFP());
            situacaoRetorno = em.merge(situacaoRetorno);

            DateTime inicioVigencia = new DateTime(situacaoRetorno.getFinalVigencia()).plusDays(1);
            SituacaoContratoFP situacaoVigente = new SituacaoContratoFP();
            situacaoVigente.setInicioVigencia(inicioVigencia.toDate());
            situacaoVigente.setFinalVigencia(null);
            situacaoVigente.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));
            situacaoVigente.setContratoFP(entity.getContratoFP());
            em.persist(situacaoVigente);
        }

        if (!entity.isRetornoInformado()) {

            SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
            situacaoContratoFP.setInicioVigencia(entity.getInicio());
            situacaoContratoFP.setContratoFP(entity.getContratoFP());
            situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.AFASTADO_LICENCIADO));
            em.persist(situacaoContratoFP);

        }
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        if (configuracaoRH.getEnvioAutomaticoDadosPonto() == null || !configuracaoRH.getEnvioAutomaticoDadosPonto()) {
            entity.setTipoEnvioDadosRBPonto(TipoEnvioDadosRBPonto.NAO_ENVIADO);
        }
        entity = getEntityManager().merge(entity);
        processarPA(entity.getContratoFP());

        if (configuracaoRH.getEnvioAutomaticoDadosPonto() != null && configuracaoRH.getEnvioAutomaticoDadosPonto()) {
            EnvioDadosRBPonto envioDadosRBPonto = new EnvioDadosRBPonto();
            envioDadosRBPonto.setDataInicial(entity.getInicio());
            envioDadosRBPonto.setDataFinal(entity.getTermino());
            envioDadosRBPonto.setContratoFP(entity.getContratoFP());
            envioDadosRBPonto.setTipoInformacaoEnvioRBPonto(TipoInformacaoEnvioRBPonto.AFASTAMENTO);
            envioDadosRBPonto.setDataEnvio(SistemaFacade.getDataCorrente());
            envioDadosRBPonto.setUsuario(sistemaFacade.getUsuarioCorrente());

            ItemEnvioDadosRBPonto itemEnvioDadosRBPonto = new ItemEnvioDadosRBPonto();
            itemEnvioDadosRBPonto.setDataInicial(entity.getInicio());
            itemEnvioDadosRBPonto.setDataFinal(entity.getTermino());
            itemEnvioDadosRBPonto.setContratoFP(entity.getContratoFP());
            itemEnvioDadosRBPonto.setEnvioDadosRBPonto(envioDadosRBPonto);
            itemEnvioDadosRBPonto.setIdentificador(entity.getId());
            envioDadosRBPonto.getItensEnvioDadosRBPontos().add(itemEnvioDadosRBPonto);
            em.persist(envioDadosRBPonto);
            envioDadosRBPontoFacade.integracaoPonto(envioDadosRBPonto, TipoEnvioDadosRBPonto.ENVIO_AUTOMATICO);
        }
        boolean criarRegistroEsocial = false; //TODO ainda não começou a fase desse evento então não deve criar notificação
    }

    @Override
    public void salvar(Afastamento entity) {
        if (entity.isRetornoInformado()) {
            SituacaoContratoFP situacaoAtual = situacaoContratoFPFacade.recuperaSituacaoContratoFPVigente(entity.getContratoFP());
            if (situacaoAtual != null) {
                situacaoAtual.setFinalVigencia(entity.getTermino());
                em.merge(situacaoAtual);
            }

            SituacaoContratoFP situacaoVigente = new SituacaoContratoFP();
            DateTime inicioVigencia = new DateTime(entity.getTermino()).plusDays(1);

            situacaoVigente.setInicioVigencia(inicioVigencia.toDate());
            situacaoVigente.setFinalVigencia(null);
            situacaoVigente.setContratoFP(entity.getContratoFP());
            situacaoVigente.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));
            em.persist(situacaoVigente);

        }
        entity = em.merge(entity);
        entity = getEntityManager().merge(entity);
        processarPA(entity.getContratoFP());
    }

    public void processarPA(ContratoFP contratofp) {
        periodoAquisitivoFLFacade.excluirPeriodosAquisitivosParaRegerar(contratofp);
        periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratofp, SistemaFacade.getDataCorrente(), null);
    }


    public void salvarAfastamentoRetorno(Afastamento entity) {
        try {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(entity.getTermino());
            calendario.add(Calendar.DATE, 1);

            SituacaoContratoFP vigente = situacaoContratoFPFacade.recuperaSituacaoContratoFPVigente(entity.getContratoFP());
            if (vigente != null) {
                vigente.setFinalVigencia(entity.getTermino());
            }
            em.merge(vigente);

            //criar a nova situacaoContratoFP vigente
            SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
            situacaoContratoFP.setInicioVigencia(calendario.getTime());
            situacaoContratoFP.setContratoFP(entity.getContratoFP());
            //recuperar a situacao Funcional para aposentados
            situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));
            em.persist(situacaoContratoFP);
            //System.out.println("situação contrato : " + situacaoContratoFP);
            //System.out.println("afastemento : " + entity);
            getEntityManager().merge(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro ao tentar alterar os dados de funcionais " + ex);
        }
    }

    public List<ContratoFP> listaFiltrandoContratoFPVigente(String s) {
        String hql = " select cfp from ContratoFP cfp   "
            + " inner join cfp.matriculaFP mfp    "
            + " inner join mfp.pessoa pessoa "
            + " where cfp.matriculaFP.id = mfp.id "
            + " and mfp.pessoa.id = pessoa.id "
            + " and (lower(pessoa.nome) like :filtro "
            + " or lower(mfp.matricula) like :filtro) "
            + " and :dataVigencia >= cfp.inicioVigencia "
            + " and :dataVigencia <= coalesce(cfp.finalVigencia,:dataVigencia) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Afastamento> listaFiltrandoAtributosAfastamento(String s) {
        String hql = " select af from Afastamento af "
            + " inner join af.contratoFP cfp "
            + " inner join af.tipoAfastamento tipo "
            + " inner join cfp.matriculaFP mfp "
            + " inner join mfp.pessoa pessoa"
            + " inner join cfp.modalidadeContratoFP mcfp "
            + " where af.contratoFP.id = cfp.id "
            + " and af.tipoAfastamento.id = tipo.id "
            + " and cfp.matriculaFP.id = mfp.id "
            + " and mfp.pessoa.id = pessoa.id"
            + " and cfp.modalidadeContratoFP.id = mcfp.id "
            + " and (to_char(af.inicio,'dd/MM/yyyy') like :parametro "
            + " or to_char(af.termino,'dd/MM/yyyy') like :parametro "
            + " or mfp.matricula like :parametro "
            + " or pessoa.nome like :parametro"
            + " or tipo.descricao like :parametro"
            + " or mcfp.descricao like :parametro ) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public Afastamento recuperaAfastamentoVigente(ContratoFP contratoFP) {
        Query q = em.createQuery("from Afastamento af where af.contratoFP = :contratoFP and :dataOperacao between af.inicio and coalesce(af.termino,:dataOperacao)");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataOperacao", UtilRH.getDataOperacao());
        q.setMaxResults(1);
        try {
            return (Afastamento) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public Afastamento recuperaAfastamentoVigentePorPeriodo(Afastamento afastamento) {
        String hql = "from Afastamento af where af.contratoFP = :contratoFP "
            + " and ((:dataInicio between af.inicio and coalesce(af.termino,:dataInicio)) or (:dataFim between af.inicio and coalesce(af.termino,:dataFim))"
            + " or (af.inicio between :dataInicio and :dataFim) or (af.termino between :dataInicio and :dataFim))";
        if (afastamento.getId() != null) {
            hql += " and af.id != :id";
        }
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", afastamento.getContratoFP());
        q.setParameter("dataInicio", afastamento.getInicio());
        q.setParameter("dataFim", afastamento.getTermino());
        if (afastamento.getId() != null) {
            q.setParameter("id", afastamento.getId());
        }
        q.setMaxResults(1);
        try {
            return (Afastamento) q.getSingleResult();
        } catch (NoResultException nr) {
            //System.out.println("Não foram encontrados nenhum resultado para a consulta ");
            return null;
        }
    }

    public void salvarNovo(Afastamento entity, Afastamento afastamentoVigente) {
        if (afastamentoVigente != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(entity.getInicio());
            c.add(Calendar.DATE, -1);
            afastamentoVigente.setTermino(c.getTime());
            afastamentoVigente.setDataCadastroFinalVigencia(entity.getDataCadastro());
            em.merge(afastamentoVigente);
        }
        em.persist(entity);
    }

    public List<Afastamento> listaFiltrandoServidorPorCompetencia(ContratoFP contratoFP, Date inicio, Date fim) {
        String sql = "select a.* from Afastamento a " +
            " WHERE (:inicio BETWEEN a.inicio AND a.termino " +
            " OR (:fim BETWEEN a.inicio AND a.termino) " +
            " or (a.inicio between :inicio and :fim) " +
            " OR (a.termino between :inicio and :fim)) " +
            " AND a.CONTRATOFP_ID = :contrato";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public List<Afastamento> listaFiltrandoX(String s, int inicio, int max) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select afastamento from Afastamento afastamento ");
        sb.append(" left join afastamento.tipoAfastamento tipo ");
        sb.append(" where (lower(afastamento.contratoFP.matriculaFP.matricula) like :filtro) ");
        sb.append(" or (lower(afastamento.contratoFP.matriculaFP.pessoa.nome) like :filtro) ");
        sb.append(" or (lower(afastamento.contratoFP.matriculaFP.pessoa.cpf) like :filtro) ");
        sb.append(" or (to_char(afastamento.inicio,'dd/MM/yyyy') like :filtro) ");
        sb.append(" or (to_char(afastamento.termino,'dd/MM/yyyy') like :filtro) ");
        sb.append(" or (cast(afastamento.carencia as string) like :filtro) ");
        sb.append(" or (lower(tipo.descricao) like :filtro)");
        Query q = em.createQuery(sb.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<Afastamento> listaAfastamentosInicioCompetenciaSEFIP(ContratoFP contratoFP, Integer mes, Integer ano) {
        Query q = em.createQuery(" from Afastamento a "
            + " where extract(month from a.inicio) = :mes "
            + " and extract(year from a.inicio) = :ano "
            + " and a.contratoFP = :contrato "
            + " order by a.inicio desc ");


        q.setParameter("contrato", contratoFP);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);

        return q.getResultList();
    }

    public List<Afastamento> servidorAfastadoEmPeriodoSEFIP(ContratoFP contratoFP, Date inicio, Date fim) {
        String hql = "select a from Afastamento a " +
            " where ((a.inicio <= :fim and a.termino is null)" +
            "    or (a.inicio <= :fim and a.termino >= :inicio))" +
            "   and a.contratoFP = :contrato and a.tipoAfastamento.codigo <> :codigoTipoAfastamento";

        Query q = em.createQuery(hql);
        q.setParameter("contrato", contratoFP);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("codigoTipoAfastamento", TIPO_Q2);
        return q.getResultList();
    }

    public List<Afastamento> listaAfastamentosFinalCompetenciaSEFIP(ContratoFP contratoFP, Integer mes, Integer ano) {
        Query q = em.createQuery(" from Afastamento a "
            + " where extract(month from a.termino) = :mes "
            + " and extract(year from a.termino) = :ano "
            + " and a.contratoFP = :contrato " +
            " order by a.termino desc ");

        q.setParameter("contrato", contratoFP);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);

        return q.getResultList();
    }

    public List<Afastamento> listaAfastamentoVigenteMes(VinculoFP vinculoFP, Integer ano, Mes mes) {
        Calendar iniMes = Calendar.getInstance();
        Calendar fimMes = Calendar.getInstance();
        iniMes.setTime(Util.criaDataPrimeiroDiaDoMesFP(mes.ordinal(), ano));
        fimMes.setTime(Util.recuperaDataUltimoDiaDoMes(iniMes.getTime()));
        return listaAfastamentoVigentePorPeriodo(vinculoFP, iniMes.getTime(), fimMes.getTime());
    }

    public List<Afastamento> listaAfastamentoVigentePorPeriodo(VinculoFP vinculoFP, Date iniMes, Date fimMes) {

        StringBuilder hql = new StringBuilder();
        hql.append("  from Afastamento a");
        hql.append(" where a.contratoFP.id = :vinculo");
        hql.append("   and a.inicio <= :fimMes");
        hql.append("   and a.termino >= :iniMes");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("iniMes", iniMes);
        q.setParameter("fimMes", fimMes);
        List<Afastamento> lista = q.getResultList();
        lista.addAll(listaAfastamentoSemRetorno(vinculoFP));
        List<Afastamento> asf = new ArrayList<>(new HashSet<>(lista));
        return asf;
    }

    public List<Afastamento> buscarAfastamentosVigentesPorPeriodoAndDescontarTempoServicoEfetivo(VinculoFP vinculoFP, LocalDate iniMes, LocalDate fimMes) {

        StringBuilder hql = new StringBuilder();
        hql.append("  from Afastamento a");
        hql.append("  inner join a.tipoAfastamento tipo");
        hql.append(" where a.contratoFP.id = :vinculo");
        hql.append("   and a.inicio <= :fimMes");
        hql.append("   and a.termino >= :iniMes");
        hql.append("   and tipo.descontarTempoServicoEfetivo = true");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("iniMes", localDateToDate(iniMes));
        q.setParameter("fimMes", localDateToDate(fimMes));
        List<Afastamento> lista = q.getResultList();
        lista.addAll(buscarAfastamentosSemRetornoAndDescontarTempoServicoEfetivo(vinculoFP));
        List<Afastamento> asf = new ArrayList<>(new HashSet<>(lista));
        return asf;
    }

    public List<Afastamento> recuperarTodosAfastamentos(VinculoFP vinculoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select afastamento.* ");
        sql.append(" from afastamento");
        sql.append(" inner join tipoafastamento tipo on afastamento.TIPOAFASTAMENTO_ID = tipo.id");
        sql.append(" where CONTRATOFP_ID = :vinculo");
        sql.append(" ORDER BY afastamento.inicio DESC");

        Query q = em.createNativeQuery(sql.toString(), Afastamento.class);
        q.setParameter("vinculo", vinculoFP.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<Afastamento> listaAfastamentoSemRetorno(VinculoFP vinculoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append("  from Afastamento a");
        hql.append(" where a.contratoFP.id = :vinculo and a.retornoInformado is false ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", vinculoFP.getId());
        return q.getResultList();
    }

    public List<Afastamento> buscarAfastamentosSemRetornoAndDescontarTempoServicoEfetivo(VinculoFP vinculoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append("  from Afastamento a");
        hql.append("  inner join a.tipoAfastamento tipo");
        hql.append(" where a.contratoFP.id = :vinculo and a.retornoInformado is false ");
        hql.append("  and tipo.descontarTempoServicoEfetivo = true");

        Query q = em.createQuery(hql.toString());
        q.setParameter("vinculo", vinculoFP.getId());
        return q.getResultList();
    }

    public List<Afastamento> recuperaAfastamentos(ContratoFP contratoFP, Integer ano) {
        String sql = " select af.* from Afastamento af where af.contratoFP_id = :contratoFP " +
            "          and :ano between extract(year from af.inicio) and extract(year from af.termino)  order by af.id desc ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("ano", ano);

        List<Afastamento> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new ArrayList<>();
        }
        return lista;
    }

    public List<Afastamento> buscarAfastamentosPorVinculoFPAndData(VinculoFP vinculo, Date dataMaxima, Boolean descontaTempoServicoEfetivo) {
        String sql = "from Afastamento a where a.contratoFP.id = :contrato and a.tipoAfastamento.classeAfastamento <> :tipo ";
        if (descontaTempoServicoEfetivo != null) {
            sql += " and a.tipoAfastamento.descontarTempoServicoEfetivo = " + descontaTempoServicoEfetivo;
        }
        sql += " and a.inicio <= :maximaData order by a.inicio";
        Query qAfastamento = em.createQuery(sql);
        qAfastamento.setParameter("contrato", vinculo.getId());
        qAfastamento.setParameter("maximaData", dataMaxima);
        qAfastamento.setParameter("tipo", ClasseAfastamento.FALTA_JUSTIFICADA);
        return qAfastamento.getResultList();
    }

    public List<Afastamento> recuperaAfastamentosDescontosTempoEfetivo(ContratoFP contrato) {
        Query q = em.createQuery("from Afastamento a "
            + "inner join a.tipoAfastamento tipo "
            + "where a.contratoFP = :contrato and tipo.descontarTempoServicoEfetivo = true and a.inicio <= current_date");

        q.setParameter("contrato", contrato);
        return q.getResultList();
    }

    public List<Afastamento> recuperaAfastamentosDescontosTempoEfetivoNaVigencia(VinculoFP v, LocalDate inicio, LocalDate fim) {
        Query q = em.createQuery("select a from Afastamento a "
            + "inner join a.tipoAfastamento tipo "
            + "where a.contratoFP.id = :contrato and tipo.descontarTempoServicoEfetivo = true and (a.inicio between :inicio and :fim or a.termino between :inicio and :fim) order by a.inicio");

        q.setParameter("contrato", v.getIdCalculo());
        q.setParameter("inicio", localDateToDate(inicio));
        q.setParameter("fim", localDateToDate(fim));
        return q.getResultList();
    }

    public List<Afastamento> buscarAfastamentosDescontarDiasTrabalhados(VinculoFP v, LocalDate inicio, LocalDate fim) {
        Query q = em.createQuery("select a from Afastamento a "
            + "inner join a.tipoAfastamento tipo "
            + "where a.contratoFP.id = :contrato and tipo.descontarDiasTrabalhados = true "
            + " and (a.inicio between :inicio and :fim "
            + "           or case when a.retornoInformado = true then a.termino else current_date end between :inicio and :fim "
            + "           or :inicio between a.inicio and case when a.retornoInformado = true then a.termino else current_date end "
            + "           or :fim between a.inicio and case when a.retornoInformado = true then a.termino else current_date end) "
            + " order by a.inicio");

        q.setParameter("contrato", v.getIdCalculo());
        q.setParameter("inicio", localDateToDate(inicio));
        q.setParameter("fim", localDateToDate(fim));
        return q.getResultList();
    }

    public List<Afastamento> buscarAfatamentosPorPeriodo(ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        Query q = em.createQuery("select a from Afastamento a where a.contratoFP = :contrato and (a.inicio between :inicio and :fim " +
            " or a.termino between :inicio and :fim" +
            " ) and a.tipoAfastamento.descontarTempoServicoEfetivo is true order by a.inicio asc");
        q.setParameter("contrato", contratoFP);
        q.setParameter("inicio", inicioVigencia);
        q.setParameter("fim", finalVigencia);
        return q.getResultList();
    }

    public Afastamento buscarAfastamentoPorData(ContratoFP contratoFP, Date inicioVigencia) {
        Query q = em.createQuery("select a from Afastamento a where a.contratoFP = :contrato and :inicio between a.inicio and a.termino and a.tipoAfastamento.descontarTempoServicoEfetivo is true order by a.inicio asc");
        q.setParameter("contrato", contratoFP);
        q.setParameter("inicio", inicioVigencia);
        try {
            if (q.getResultList().isEmpty()) {
                return null;
            }
            return (Afastamento) q.getResultList().get(0);
        } catch (NoResultException noResult) {
            logger.trace("nenhum registro encontrado.");
            return null;
        }
    }

    public List<Afastamento> buscarAfastamentos(Afastamento a) {
        List<Afastamento> afastamentos = new LinkedList<>();
        buscarAfastamentosRecurivamente(a.getContratoFP(), new DateTime(a.getTermino()).plusDays(1).toDate(), afastamentos);
        return afastamentos;
    }


    public Afastamento buscarAfastamentosRecurivamente(ContratoFP c, Date data, List<Afastamento> todos) {
        Afastamento a = buscarAfastamentoPorData(c, data);
        if (a != null) {
            todos.add(a);
            buscarAfastamentosRecurivamente(a.getContratoFP(), new DateTime(a.getTermino()).plusDays(1).toDate(), todos);
        }
        return null;
    }

    public List<Afastamento> buscarAfastamentosWithAlongamentoPeriodoAquisitivoByContratoFP(ContratoFP contrato, Date inicio, Date fim) {
        String sql = "   SELECT DISTINCT afastamento.* FROM AFASTAMENTO afastamento " +
            "  INNER JOIN TIPOAFASTAMENTO tipo ON afastamento.TIPOAFASTAMENTO_ID = tipo.ID " +
            "  INNER JOIN CONTRATOFP contrato ON afastamento.CONTRATOFP_ID = contrato.ID " +
            " WHERE contrato.id = :contratoId " +
            " AND  ((afastamento.INICIO between :inicio and :fim ) " +
            " OR  (afastamento.TERMINO between :inicio and :fim )" +
            " OR  (:inicio between afastamento.inicio and afastamento.termino)" +
            " OR  (:fim between afastamento.inicio and afastamento.termino) ) " +
            " AND (tipo.alongarperiodoaquisitivo = 1 or tipo.maximoperdaperiodoaquisitivo > 0 OR tipo.reiniciarContagem = 1 " +
            "       or tipo.alongarperaquisitivolicenca = 1 or tipo.maximoperdaperaquisitlicenca > 0 or tipo.reiniciarperaquisitlicenca = 1) " +
            " order by afastamento.inicio asc ";


        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("contratoId", contrato.getId());


        return q.getResultList().isEmpty() ? new ArrayList<Afastamento>() : q.getResultList();
    }

    public List<Afastamento> buscarTodosAfastamentosPorContrato(VinculoFP vinculoFP) {
        Assert.notNull(vinculoFP);
        Query q = em.createQuery("from Afastamento a where a.contratoFP.id = :id order by a.inicio");
        q.setParameter("id", vinculoFP.getId());
        return q.getResultList();
    }

    public List<Afastamento> buscarAfastamentoVigentePorContratoAndNaoPermitirProgressao(VinculoFP vinculoFP, Date dataReferencia) {
        Assert.notNull(vinculoFP);
        String sql = " select a.* from Afastamento a " +
            " inner join tipoafastamento tp on A.TIPOAFASTAMENTO_ID = tp.id " +
            " where a.contratoFP_id = :id " +
            " and tp.NAOPERMITIRPROGRESSAO = :naoPermitir " +
            " and to_date(:dataReferencia, 'dd/mm/yyyy') between a.inicio and (case when coalesce(A.RETORNOINFORMADO, 0) = 1 then a.termino else to_date(:dataReferencia, 'dd/mm/yyyy') end) ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("id", vinculoFP.getId());
        q.setParameter("naoPermitir", Boolean.TRUE);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        try {
            return (List<Afastamento>) q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public List<Afastamento> buscarAfastamentoVigentePorContratoAndNaoPermitirPromocao(VinculoFP vinculoFP, Date dataReferencia) {
        Assert.notNull(vinculoFP);
        String sql = " select a.* from Afastamento a " +
            " inner join tipoafastamento tp on A.TIPOAFASTAMENTO_ID = tp.id " +
            " where a.contratoFP_id = :id " +
            " and tp.NAOPERMITIRPROMOCAO = :naoPermitir " +
            " and to_date(:dataReferencia, 'dd/mm/yyyy') between a.inicio and (case when coalesce(A.RETORNOINFORMADO, 0) = 1 then a.termino else to_date(:dataReferencia, 'dd/mm/yyyy') end) ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("id", vinculoFP.getId());
        q.setParameter("naoPermitir", Boolean.TRUE);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        try {
            return (List<Afastamento>) q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public Afastamento servidorPossuiAfastamentoNoPeriodo(ContratoFP contrato, Date inicio, Date fim) {
        String hql = " select a from Afastamento a" +
            "       where a.contratoFP = :contrato" +
            "         and ((:inicio between a.inicio and a.termino or :fim between a.inicio and a.termino) or " +
            "         (a.inicio between :inicio and :fim or a.termino between :inicio and :fim)) ";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        if (!q.getResultList().isEmpty()) {
            return (Afastamento) q.getResultList().get(0);
        }
        return null;
    }

    @Override
    public void remover(Afastamento entity) {

        try {
            SituacaoContratoFP situacaoAfastamento = situacaoFuncionalFacade.buscarSituacaoFuncionalExataPorServidorVigenteEm(entity.getContratoFP(), entity.getInicio());
            SituacaoContratoFP situacaoDasFerias = situacaoFuncionalFacade.buscarSituacaoFuncionalPorServidorVigenteEm(entity.getContratoFP(), entity.getInicio());
            SituacaoContratoFP situacaoAnteriorFerias = situacaoFuncionalFacade.buscarSituacaoFuncionalPorServidorAndLimite(entity.getContratoFP(), entity.getInicio(), SituacaoFuncionalFacade.AntesDepoisParametroFerias.ANTES);
            SituacaoContratoFP situacaoPosteriorFerias = situacaoFuncionalFacade.buscarSituacaoFuncionalPorServidorAndLimite(entity.getContratoFP(), entity.getInicio(), SituacaoFuncionalFacade.AntesDepoisParametroFerias.DEPOIS);
            if (situacaoAfastamento != null) {
                if (situacaoPosteriorFerias != null) {
                    situacaoDasFerias.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.ATIVO_PARA_FOLHA));
                    em.merge(situacaoDasFerias);
                } else {
                    if (situacaoDasFerias != null && situacaoAnteriorFerias != null) {
                        situacaoAnteriorFerias.setFinalVigencia(situacaoDasFerias.getFinalVigencia());
                    } else if (situacaoDasFerias != null && situacaoPosteriorFerias != null) {
                        situacaoDasFerias.setFinalVigencia(situacaoPosteriorFerias.getFinalVigencia());
                    }
                    if (situacaoAnteriorFerias != null) {
                        em.merge(situacaoAnteriorFerias);

                    }
                    if (situacaoDasFerias != null) {
                        em.remove(situacaoDasFerias);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar reverter situação funcional ao excluir o afastamento " + entity, e);
        }
        ContratoFP contratoFP = contratoFPFacade.recuperarSomentePeriodosAquisitivos(entity.getContratoFP().getId());
        super.remover(entity);
        processarPA(contratoFP);
    }

    public void enviarS2230ESocial(ConfiguracaoEmpregadorESocial empregador, Afastamento afastamento) throws ValidacaoException {
        PrestadorServicos prestadorServicos = prestadorServicosFacade
            .buscarPrestadorServicosPorIdPessoa(
                Optional.ofNullable(afastamento)
                    .map(Afastamento::getContratoFP)
                    .map(ContratoFP::getMatriculaFP)
                    .map(MatriculaFP::getPessoa)
                    .map(Pessoa::getId)
                    .orElse(null));
        s2230Service.enviarS2230(empregador, afastamento, prestadorServicos);
    }

    public Afastamento recuperaAfastamentoVigente(ContratoFP contratoFP, Date dataOperacao) {
        Query q = em.createQuery("from Afastamento af where af.contratoFP = :contratoFP and :dataOperacao between af.inicio and coalesce(af.termino,:dataOperacao)");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataOperacao", dataOperacao);
        q.setMaxResults(1);
        try {
            return (Afastamento) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public Afastamento recuperarAfastadoLicencaMaternidadePorPeridodo(ContratoFP contratoFP, Date inicio, Date fim) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.* from Afastamento a ")
            .append(" inner join tipoAfastamento tipo on tipo.id = a.tipoAfastamento_id ")
            .append(" where ((a.inicio <= :fim and a.termino is null) ")
            .append("    or (a.inicio <= :fim and a.termino >= :inicio)) ")
            .append("   and tipo.codigo = :codigoAfastamento ")
            .append("   and a.contratoFP_id = :contrato ");
        Query q = em.createNativeQuery(sql.toString(), Afastamento.class);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("codigoAfastamento", 3);
        try {
            return (Afastamento) q.getSingleResult();
        } catch (Exception ne) {
            return null;
        }
    }

    public Afastamento buscarAfastamentoVigenteDataReferencia(ContratoFP contratoFP, Date dataReferencia) {
        Query q = em.createQuery("from Afastamento af where af.contratoFP = :contratoFP and :dataOperacao between af.inicio and coalesce(af.termino,:dataOperacao)");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataOperacao", dataReferencia);
        q.setMaxResults(1);
        try {
            return (Afastamento) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public List<Afastamento> recuperarTodosAfastamentos(Long idVinculo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select afastamento.* ");
        sql.append(" from afastamento");
        sql.append(" inner join tipoafastamento tipo on afastamento.TIPOAFASTAMENTO_ID = tipo.id");
        sql.append(" where CONTRATOFP_ID = :vinculo");
        sql.append(" ORDER BY afastamento.inicio DESC");

        Query q = em.createNativeQuery(sql.toString(), Afastamento.class);
        q.setParameter("vinculo", idVinculo);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public void validarCampos(Afastamento selecionado) {
        ValidacaoException ve = new ValidacaoException();
        Afastamento afastamentoVigente = recuperaAfastamentoVigentePorPeriodo(selecionado);

        if (afastamentoVigente != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + selecionado.getContratoFP() + " já possui um afastamento vigente: " + afastamentoVigente.getTipoAfastamento() + " - " + new SimpleDateFormat("dd/MM/yyyy").format(afastamentoVigente.getInicio()) + " a " + new SimpleDateFormat("dd/MM/yyyy").format(afastamentoVigente.getTermino()) + ", o sistema não permite 2 afastamentos vigentes para o mesmo servidor.");
        }

        List<ConcessaoFeriasLicenca> listaDeFerias = concessaoFeriasLicencaFacade.listaFiltrandoServidorPorCompetencia(selecionado.getContratoFP(), selecionado.getInicio(), selecionado.getTermino());

        VwFalta faltas = getFaltasFacade().buscarFaltasInjustificadasNoPeriodo(selecionado.getContratoFP(), selecionado.getInicio(), selecionado.getTermino());
        if (faltas != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar o lançamento de afastamento, o servidor possui falta entre os dias "
                + DataUtil.getDataFormatada(faltas.getInicio()) + " e " + DataUtil.getDataFormatada(faltas.getFim()));
        }

        if (selecionado.getTipoAfastamento() != null && selecionado.getTipoAfastamento().getTipoAfastamentoESocial() != null) {
            if (TipoAfastamentoESocial.ACIDENTE_DOENCA_TRABALHO.equals(selecionado.getTipoAfastamento().getTipoAfastamentoESocial())) {
                if (selecionado.getCid() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Para o Tipo de Afastamento " + selecionado.getTipoAfastamento().getDescricao() + " deve ser informado o CID");
                }
                if (selecionado.getMedico() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Para o Tipo de Afastamento " + selecionado.getTipoAfastamento().getDescricao() + " deve ser informado o Médico");
                }
            }
        }
        if (selecionado.getTipoAfastamento() != null && selecionado.getTipoAfastamento().getExigeSindicato()) {
            if (selecionado.getSindicato() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Para esse Tipo de Afastamento deve ser informado o Sindicato.");
            }
        } else {
            selecionado.setSindicato(null);
        }

        if (listaDeFerias != null && !listaDeFerias.isEmpty()) {
            for (ConcessaoFeriasLicenca ferias : listaDeFerias) {
                if ((!selecionado.getTipoAfastamento().getProcessaNormal() ||
                    TipoPeriodoAquisitivo.LICENCA.equals(ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo())) &&
                    (selecionado.getInicio().after(ferias.getDataInicial())
                        && selecionado.getInicio().compareTo(ferias.getDataFinal()) <= 0
                        || selecionado.getTermino().compareTo(ferias.getDataInicial()) >= 0
                        && selecionado.getTermino().compareTo(ferias.getDataFinal()) <= 0
                        || ferias.getDataInicial().compareTo(selecionado.getInicio()) >= 0
                        && ferias.getDataInicial().compareTo(selecionado.getTermino()) <= 0
                        || ferias.getDataFinal().compareTo(selecionado.getInicio()) >= 0
                        && ferias.getDataFinal().compareTo(selecionado.getTermino()) <= 0)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + selecionado.getContratoFP() + " está gozando de "
                        + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao()
                        + " no período de " + DataUtil.getDataFormatada(ferias.getDataInicial()) + " a "
                        + DataUtil.getDataFormatada(ferias.getDataFinal())
                        + ", o sistema não permite afastar um servidor no mesmo período de "
                        + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao() + ".");
                }
            }
        }

        CedenciaContratoFP cedencia = cedenciaContratoFPFacade.recuperaCedenciaByServidorAndPeriodo(selecionado.getContratoFP(), selecionado.getInicio(), selecionado.getTermino());
        if (cedencia != null && (CESSAO_COM_ONUS == selecionado.getTipoAfastamento().getCodigo() || CESSAO_SEM_ONUS == selecionado.getTipoAfastamento().getCodigo())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + selecionado.getContratoFP() + " está cedido no período " + new SimpleDateFormat("dd/MM/yyyy").format(cedencia.getDataCessao()) + " a " + (cedencia.getDataRetorno() != null ? new SimpleDateFormat("dd/MM/yyyy").format(cedencia.getDataRetorno()) : " - ") + ", para a unidade " + (cedencia.getFinalidadeCedenciaContratoFP().equals(FinalidadeCedenciaContratoFP.INTERNA) ? cedencia.getUnidadeOrganizacional() : cedencia.getCessionario()));
        }

        if (!isSexoValido(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Tipo de afastamento selecionado contempla apenas servidor do sexo  " + selecionado.getTipoAfastamento().getSexo().getDescricao());
        }

        if (!buscarAfastamentosPorContratoAndDataSemRetornoInformado(selecionado.getContratoFP(), selecionado.getTermino()).isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe afastamento registrado para o servidor cujo campo \"Retorno Informado\" ainda não foi marcado.");
        }

        ve.lancarException();
    }

    private boolean isSexoValido(Afastamento selecionado) {
        return selecionado.getTipoAfastamento().getSexo() == null || selecionado.getContratoFP().getMatriculaFP().getPessoa().getSexo().equals(selecionado.getTipoAfastamento().getSexo());
    }

    public List<Afastamento> buscarAfatamentosPorPeriodoQueNaoPermitemProgressao(ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        Query q = em.createQuery("select a from Afastamento a " +
            " where a.contratoFP = :contrato " +
            " and (a.inicio between :inicio and :fim " +
            "       or a.termino between :inicio and :fim " +
            "       or :inicio between a.inicio and a.termino " +
            "       or :fim between a.inicio and a.termino)" +
            " and a.tipoAfastamento.naoPermitirProgressao is true " +
            " order by a.inicio asc");
        q.setParameter("contrato", contratoFP);
        q.setParameter("inicio", inicioVigencia);
        q.setParameter("fim", finalVigencia);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Afastamento> buscarAfastamentosNoPeriodoPorTipos(ContratoFP contrato, Date inicio, Date fim, Integer... codigos) {
        String hql = " select a from Afastamento a " +
            "       inner join a.tipoAfastamento tipo " +
            "       where a.contratoFP = :contrato " +
            "           and tipo.codigo in (:codigos) " +
            "         and (:inicio between a.inicio and a.termino ";
        if (fim != null) {
            hql += " or :fim between a.inicio and a.termino " +
                "   or a.inicio between :inicio and :fim " +
                "   or a.termino between :inicio and :fim ";
        } else {
            hql += " or :inicio <= a.inicio or  :inicio <= a.termino ";
        }
        hql += ") ";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        q.setParameter("inicio", inicio);
        if (fim != null) {
            q.setParameter("fim", fim);
        }
        q.setParameter("codigos", Arrays.asList(codigos));
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<Afastamento> buscarAfastamentosDescontarDiasTrabalhadosPorCodigo(VinculoFP v, Date inicio, Date fim, List<Integer> afastamentos) {
        String sql = " select a.* from Afastamento a " +
            "  inner join tipoAfastamento tipo on a.tipoafastamento_id = tipo.id " +
            " where a.contratofp_id = :contrato " +
            "  and tipo.codigo in(:codigos) " +
            "  and (a.inicio between :inicio and :fim or a.termino between :inicio and :fim or :inicio between a.inicio and a.termino or :fim between a.inicio and a.termino) order by a.inicio ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("contrato", v.getIdCalculo());
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("codigos", afastamentos);
        return q.getResultList();
    }

    public List<Long> buscarIdsAfastamentosPorContratoFP(ContratoFP contratoFP) {
        String sql = " select afastamento.id from Afastamento afastamento " +
            "where afastamento.contratofp_id = :contrato " +
            "order by afastamento.id desc ";
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

    public Afastamento buscarAfastamentoProrrogacao(Afastamento afastamentoReferencia) {
        String sql = " select afastamento.* " +
            " from Afastamento afastamento " +
            "         inner join tipoafastamento tipo on afastamento.tipoafastamento_id = tipo.id " +
            " where tipo.codigo = :tipoAfastamento " +
            "  and afastamento.contratofp_id = :ep " +
            "  and trunc(afastamento.termino) = to_date(:dataReferencia, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("ep", afastamentoReferencia.getContratoFP().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(DataUtil.dateToLocalDate(afastamentoReferencia.getInicio()).minusDays(1)));
        q.setParameter("tipoAfastamento", afastamentoReferencia.getTipoAfastamento().getCodigo());
        List<Afastamento> afastamentos = q.getResultList();

        if (afastamentos != null && !afastamentos.isEmpty()) {
            return afastamentos.get(0);
        }
        return null;
    }

    public List<Afastamento> buscarAfastamentosPorContratoAndDataSemRetornoInformado(ContratoFP contratoFP, Date dataMaxima) {
        String sql = " select afastamento.* " +
            " from Afastamento afastamento " +
            " where afastamento.contratofp_id = :contratoFP " +
            "  and trunc(afastamento.termino) < to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "  and afastamento.retornoinformado = 0 ";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataMaxima));
        List<Afastamento> afastamentos = q.getResultList();

        if (afastamentos != null) {
            return afastamentos;
        }
        return Lists.newArrayList();
    }

    public List<ContratoFP> buscarContratosAfastadosPorTipoAfastamento(TipoAfastamento tipo, Date dataOperacao) {
        String hql = " select distinct contrato from Afastamento afas " +
            " inner join afas.contratoFP contrato " +
            " inner join afas.tipoAfastamento tipo " +
            " where tipo.codigo = :codigoTipo " +
            "   and :dataOperacao between contrato.inicioVigencia and coalesce(contrato.finalVigencia, :dataOperacao)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoTipo", tipo.getCodigo());
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    public boolean isAfastamentoProrrogacao(Long IdCalculo, Date dataIntervaloSubtraida, Date dataIntervaloSomada, Date inicioAfastamentoReferencia, Date termino, String codigo) {

        String sql = "  select 1 from afastamento  " +
            " inner join tipoAfastamento tipo on afastamento.tipoafastamento_id = tipo.id  " +
            " where afastamento.contratofp_id = :ep " +
            " and tipo.codigo = :codigo" +
            " and (afastamento.termino >= :dataIntervaloSubtraida and afastamento.inicio < :inicioAfastamentoReferencia ";

        if (termino != null && dataIntervaloSomada != null) {
            sql += " or ( afastamento.inicio between :termino and :dataIntervaloSomada";
        }

        sql += " and not exists (select 1 from afastamento afast " +
            " where afast.id = afastamento.id and afast.inicio = :inicioAfastamentoReferencia))";
        if (termino != null && dataIntervaloSomada != null) {
            sql += " ) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("ep", IdCalculo);
        q.setParameter("dataIntervaloSubtraida", dataIntervaloSubtraida);
        q.setParameter("inicioAfastamentoReferencia", inicioAfastamentoReferencia);
        if (termino != null && dataIntervaloSomada != null) {
            q.setParameter("termino", termino);
            q.setParameter("dataIntervaloSomada", dataIntervaloSomada);
        }
        List<?> resultado = q.getResultList();
        return !resultado.isEmpty();
    }
}
