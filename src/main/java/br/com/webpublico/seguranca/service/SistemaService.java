package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.*;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class SistemaService implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(SistemaService.class);

    @PersistenceContext
    protected transient EntityManager em;
    private List<HierarquiaOrganizacionalDTO> orcamentarias;
    private List<HierarquiaOrganizacionalDTO> administrativas;
    private List<HierarquiaOrganizacionalDTO> administrativasOrgao;
    private HierarquiaOrganizacional hierarquiAdministrativaCorrente;
    private HierarquiaOrganizacional hierarquiOrcamentariaCorrente;
    private UsuarioSistema usuarioCorrente;
    private Date dataOperacao;
    private Exercicio exercicioCorrente;
    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;
    @Autowired
    private LeitorMenuFacade leitorMenuFacade;
    private String faseBloqueando;
    private String usuarioAlternativo;
    @Autowired
    private SingletonMetricas singletonMetricas;
    @Autowired
    private NotificacaoService notificacaoService;
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ExercicioFacade exercicioFacade;
    @Autowired
    private ReportService reportService;
    private String ipAlternativo;
    private String appVersion;
    private String appArtifactId;

    @PostConstruct
    public void init() {
        try {
            InitialContext initialContext = new InitialContext();
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacade)
                initialContext.lookup("java:module/HierarquiaOrganizacionalFacade");
            exercicioFacade = (ExercicioFacade)
                initialContext.lookup("java:module/ExercicioFacade");
        } catch (Exception e) {
            logger.error("Não foi possivel fazer lookup do singletonMenu");
        }
    }

    public ReportService getReportService() {
        return reportService;
    }

    public SingletonRecursosSistema getSingletonRecursosSistema() {
        return singletonRecursosSistema;
    }

    public HierarquiaOrganizacional getHierarquiAdministrativaCorrente() {
        if (hierarquiAdministrativaCorrente == null) {
            atualizarHierarquiaAdministrativa();
        }
        return hierarquiAdministrativaCorrente;
    }

    public HierarquiaOrganizacional getHierarquiOrcamentariaCorrente() {
        if (hierarquiOrcamentariaCorrente == null) {
            atualizarHierarquiaOrcamentaria();
        }
        return hierarquiOrcamentariaCorrente;
    }

    public Exercicio getExercicioPorAno(int ano) {
        Query q = em.createQuery("from Exercicio exercicio where exercicio.ano=:ano");
        q.setParameter("ano", ano);
        if (!q.getResultList().isEmpty()) {
            return (Exercicio) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("Nenhum Exercício foi cadastrado para o ano de " + ano);
    }

    public UnidadeOrganizacional getOrcamentariaCorrente() {
        if (usuarioCorrente.getOrcamentariaCorrente() == null) {
            recuperarOrcamentariaDaAdministrativa();
        }
        return usuarioCorrente.getOrcamentariaCorrente();
    }


    public UnidadeOrganizacional getAdministrativaCorrente() {
        return usuarioCorrente.getAdministrativaCorrente();
    }

    private void atualizarHierarquiaAdministrativa() {
        if (usuarioCorrente.getAdministrativaCorrente() != null) {
            hierarquiAdministrativaCorrente = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(usuarioCorrente.getAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA, getDataOperacao());
        }
    }

    private void atualizarHierarquiaOrcamentaria() {
        if (usuarioCorrente.getOrcamentariaCorrente() != null) {
            hierarquiOrcamentariaCorrente = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(usuarioCorrente.getOrcamentariaCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA, getDataOperacao());
        }
    }

    public void atualizarHierarquiaDoUsuario() {
        if (usuarioCorrente.getOrcamentariaCorrente() != null && usuarioCorrente.getAdministrativaCorrente() != null) {
            String sql = "update usuariosistema set orcamentariaCorrente_id = :orc, administrativaCorrente_id = :adm where id = :usuario";
            Query q = em.createNativeQuery(sql);
            q.setParameter("orc", usuarioCorrente.getOrcamentariaCorrente().getId());
            q.setParameter("adm", usuarioCorrente.getAdministrativaCorrente().getId());
            q.setParameter("usuario", usuarioCorrente.getId());
            q.executeUpdate();
        }
    }

    @Transactional
    public void setAdministrativaCorrente(Long id) {
        UnidadeOrganizacional administrativaCorrente = em.find(UnidadeOrganizacional.class, id);
        usuarioCorrente.setAdministrativaCorrente(administrativaCorrente);
        usuarioCorrente.setOrcamentariaCorrente(null);
        hierarquiOrcamentariaCorrente = null;
        atualizarHierarquiaAdministrativa();
        recuperarOrcamentariaDaAdministrativa();
        atualizarHierarquiaDoUsuario();
    }

    @Transactional
    public void setOrcamentariaCorrente(Long id, boolean atualizaAdministativa) {
        UnidadeOrganizacional orcamentariaCorrente = em.find(UnidadeOrganizacional.class, id);
        usuarioCorrente.setOrcamentariaCorrente(orcamentariaCorrente);
        atualizarHierarquiaOrcamentaria();
        if (atualizaAdministativa) {
            usuarioCorrente.setAdministrativaCorrente(null);
            hierarquiAdministrativaCorrente = null;
            recuperarAdministrativaDaOrcamentaria();
        }
        atualizarHierarquiaDoUsuario();
    }


    @Transactional
    public void atribuirUnidadesDoUsuarioCorrente(UnidadeOrganizacional administrativa, UnidadeOrganizacional orcamentaria) {
        usuarioCorrente.setOrcamentariaCorrente(orcamentaria);
        usuarioCorrente.setAdministrativaCorrente(administrativa);
        atualizarHierarquiaOrcamentaria();
        atualizarHierarquiaAdministrativa();
        atualizarHierarquiaDoUsuario();
    }

    public List<HierarquiaOrganizacionalDTO> getOrcamentarias() {
        return orcamentarias;
    }

    public List<HierarquiaOrganizacionalDTO> getAdministrativas() {
        if (administrativas == null) {
            recarregaAdministrativas();
        }
        return administrativas;
    }

    public List<HierarquiaOrganizacionalDTO> getAdministrativasOrgao() {
        if (administrativasOrgao == null) {
            recarregaOrgaoAdministrativoUsuario();
        }
        return administrativasOrgao;
    }

    public void limparUsuarioCorrente() {
        usuarioCorrente = null;
    }

    public UsuarioSistema getUsuarioCorrente() {
        try {
            if (usuarioCorrente == null) {
                usuarioCorrente = Util.recuperarUsuarioCorrente();
            }
            return usuarioCorrente;
        } catch (Exception e) {
            return null;
        }
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public Exercicio getExercicioCorrente() {
        if (exercicioCorrente == null) {
            exercicioCorrente = getExercicioPorAno(Calendar.getInstance().get(Calendar.YEAR));
        }
        return exercicioCorrente;
    }

    public void trocarExercicio(Exercicio exercicioCorrente) {
        if (exercicioCorrente == null || exercicioCorrente.getId() == null || exercicioCorrente.getAno() == null) {
            throw new ExcecaoNegocioGenerica("Exercício Inválido");
        }
        this.exercicioCorrente = exercicioCorrente;
    }

    public Date getDataOperacao() {
        if (dataOperacao == null) {
            dataOperacao = new Date();
        }
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }


    public void recarregaUnidades() {
        recarregaAdministrativas();
        recarregaOrgaoAdministrativoUsuario();
        tentaEncontrarAlgumaOrcParaAsOutrasAdms();
    }

    private void recarregaAdministrativas() {
        administrativas = listaUnidadesAdministrativasDoUsuario(getUsuarioCorrente());
        if (administrativas.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma unidade administrativa encontrada");
        }
        if (usuarioCorrente.getAdministrativaCorrente() == null) {
            for (HierarquiaOrganizacionalDTO administrativa : administrativas) {
                usuarioCorrente.setAdministrativaCorrente(em.find(UnidadeOrganizacional.class, administrativa.getSubordinadaId()));
                if (usuarioCorrente.getOrcamentariaCorrente() == null) {
                    recuperarOrcamentariaDaAdministrativa();
                    break;
                }
            }
        }
    }

    private void recarregaOrgaoAdministrativoUsuario() {
        administrativasOrgao = listaUnidadesAdministrativasOrgaoDoUsuario(getUsuarioCorrente());
    }

    private void tentaEncontrarAlgumaOrcParaAsOutrasAdms() {
        orcamentarias = listaUnidadesOrcamentariasDoUsuario(getUsuarioCorrente(), usuarioCorrente.getAdministrativaCorrente());
        if (orcamentarias.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhuma Unidade Orçamentária encontrada para a Unidade Administrativa " + usuarioCorrente.getAdministrativaCorrente().getDescricao());
        } else {
            recuperarOrcamentariaDaAdministrativa();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void recuperarAdministrativaDaOrcamentaria() {
        for (HierarquiaOrganizacionalDTO orcamentaria : orcamentarias) {
            if (orcamentaria.getSubordinadaId().equals(usuarioCorrente.getOrcamentariaCorrente().getId())) {
                String sql = "select uo.* from HIERARQUIAORGCORRE corr " +
                    "inner join hierarquiaorganizacional hoadm on corr.hierarquiaorgadm_id = hoadm.id " +
                    "inner join unidadeorganizacional uo on hoadm.subordinada_id = uo.id " +
                    "where corr.hierarquiaorgorc_id = :id";
                Query consulta = em.createNativeQuery(sql, UnidadeOrganizacional.class);
                consulta.setParameter("id", orcamentaria.getId());
                consulta.setMaxResults(1);
                usuarioCorrente.setAdministrativaCorrente((UnidadeOrganizacional) consulta.getSingleResult());
                atualizarHierarquiaAdministrativa();
                break;
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void recuperarOrcamentariaDaAdministrativa() {
        if (usuarioCorrente.getOrcamentariaCorrente() == null) {
            for (HierarquiaOrganizacionalDTO administrativa : administrativas) {
                if (administrativa.getSubordinadaId().equals(usuarioCorrente.getAdministrativaCorrente().getId())) {
                    try {
                        String sql = "select uo.* from HIERARQUIAORGRESP resp " +
                            "inner join hierarquiaorganizacional hoorc on resp.hierarquiaorgorc_id = hoorc.id " +
                            "inner join unidadeorganizacional uo on hoorc.subordinada_id = uo.id " +
                            "where resp.hierarquiaorgadm_id = :id";
                        Query consulta = em.createNativeQuery(sql, UnidadeOrganizacional.class);
                        consulta.setParameter("id", administrativa.getId());
                        consulta.setMaxResults(1);
                        if (!consulta.getResultList().isEmpty()) {
                            usuarioCorrente.setOrcamentariaCorrente((UnidadeOrganizacional) consulta.getSingleResult());
                            atualizarHierarquiaOrcamentaria();
                        } else {
                            usuarioCorrente.setOrcamentariaCorrente(null);
                        }
                    } catch (Exception e) {
                        logger.error("Não foi possível encontrar uma unidade orçamentaria correspondente", e);
                    }
                    break;
                }
            }
        }
    }


    public List<HierarquiaOrganizacional> getTodasHierarquiaOrcamentariasDaAdminstrativa() {
        List<HierarquiaOrganizacional> retorno = new ArrayList<HierarquiaOrganizacional>();

        HierarquiaOrganizacional hoAdm = getHierarquiaOrganizacionalPorUnidade(dataOperacao, usuarioCorrente.getAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        hoAdm.getHierarquiaOrganizacionalResponsavels().size();
        for (HierarquiaOrganizacionalResponsavel responsavel : hoAdm.getHierarquiaOrganizacionalResponsavels()) {
            if (dataOperacao.compareTo(responsavel.getDataInicio()) >= 0
                && responsavel.getDataFim() != null ? dataOperacao.compareTo(responsavel.getDataFim()) <= 0 : true) {
                retorno.add(responsavel.getHierarquiaOrganizacionalOrc());
            }
        }
        return retorno;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(Date data, UnidadeOrganizacional unidade, TipoHierarquiaOrganizacional tipo) {
        if (unidade == null) {
            return null;
        }
        String hql = "from  HierarquiaOrganizacional h where "
            + "h.subordinada.id=:unidadeParametro "
            + "and :data between h.inicioVigencia and coalesce(h.fimVigencia, :data) "
            + "and (h.tipoHierarquiaOrganizacional=:tipoHO "
            + "or h.tipoHierarquiaOrganizacional is null) order by h.codigo";

        Query q = em.createQuery(hql);
        q.setParameter("unidadeParametro", unidade.getId());
        q.setParameter("tipoHO", tipo);
        q.setParameter("data", data, TemporalType.DATE);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getSingleResult();
        }
        return null;
    }

    public List<HierarquiaOrganizacionalDTO> listaUnidadesOrcamentariasDoUsuario(UsuarioSistema usuario, UnidadeOrganizacional administrativa) {
        String sql = " select distinct hoorca.id, hoorca.codigo, hoorca.DESCRICAO, hoorca.SUBORDINADA_ID  " +
            " from vwhierarquiaadministrativa v" +
            " inner join hierarquiaorganizacional ho on ho.id = v.id" +
            " inner join unidadeorganizacional uo on v.subordinada_id = uo.id" +
            " inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = uo.id" +
            " inner join usuariosistema u on u.id = uu.usuariosistema_id" +
            " inner join HIERARQUIAORGRESP responsavel on responsavel.HIERARQUIAORGADM_ID = ho.id" +
            " inner join hierarquiaorganizacional hoorca on hoorca.id = responsavel.HIERARQUIAORGORC_ID" +
            " inner join unidadeorganizacional uoorca on hoorca.subordinada_id = uoorca.id" +
            " inner join USUARIOUNIDADEORGANIZACORC uoorcausu on uoorcausu.unidadeOrganizacional_id = uoorca.id and uoorcausu.usuarioSistema_id = u.id" +
            " where to_date(:data,'dd/MM/yyyy') between v.iniciovigencia and coalesce(v.fimvigencia, to_date(:data,'dd/MM/yyyy'))" +
            " and to_date(:data,'dd/MM/yyyy') between hoorca.iniciovigencia and coalesce(hoorca.fimvigencia, to_date(:data,'dd/MM/yyyy'))" +
            " and u.id = :usuario " +
            " order by hoorca.codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(getDataOperacao()));
        q.setParameter("usuario", usuario.getId());
        return popularHierarquiaDTOPorQuery(q);
    }

    private List<HierarquiaOrganizacionalDTO> popularHierarquiaDTOPorQuery(Query q) {
        List<HierarquiaOrganizacionalDTO> hierarquias = Lists.newArrayList();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            HierarquiaOrganizacionalDTO dto = new HierarquiaOrganizacionalDTO();
            dto.setId(((Number) o[0]).longValue());
            dto.setCodigo((String) o[1]);
            dto.setDescricao((String) o[2]);
            dto.setSubordinadaId(((Number) o[3]).longValue());
            hierarquias.add(dto);
        }
        return hierarquias;
    }

    public List<HierarquiaOrganizacionalDTO> listaUnidadesAdministrativasDoUsuario(UsuarioSistema usuario) {
        String sql = "select distinct v.id, v.codigo, v.DESCRICAO, v.SUBORDINADA_ID " +
            " from vwhierarquiaadministrativa v " +
            " inner join hierarquiaorganizacional ho on ho.id = v.id " +
            " inner join unidadeorganizacional uo on v.subordinada_id = uo.id " +
            " inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = uo.id " +
            " inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            " where to_date(:data,'dd/MM/yyyy') between v.iniciovigencia and coalesce(v.fimvigencia, to_date(:data,'dd/MM/yyyy')) " +
            " and u.id = :usuario " +
            " order by v.codigo";

        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(getDataOperacao()));
        q.setParameter("usuario", usuario.getId());
        return popularHierarquiaDTOPorQuery(q);
    }

    public List<HierarquiaOrganizacionalDTO> listaUnidadesAdministrativasOrgaoDoUsuario(UsuarioSistema usuario) {
        String sql = "select distinct v.id, v.codigo, v.DESCRICAO, v.SUBORDINADA_ID " +
            " from vwhierarquiaadministrativa v " +
            " inner join hierarquiaorganizacional ho on ho.id = v.id " +
            " inner join unidadeorganizacional uo on v.subordinada_id = uo.id " +
            " inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = uo.id " +
            " inner join usuariosistema u on u.id = uu.usuariosistema_id " +
            " where to_date(:data,'dd/MM/yyyy') between v.iniciovigencia and coalesce(v.fimvigencia, to_date(:data,'dd/MM/yyyy')) " +
            " and u.id = :usuario " +
            " and ho.NIVELNAENTIDADE = 2 " +
            " order by v.codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(getDataOperacao()));
        q.setParameter("usuario", usuario.getId());
        return popularHierarquiaDTOPorQuery(q);
    }

    public void limpaUnidades() {
        orcamentarias = null;
        administrativas = null;
        administrativasOrgao = null;
    }

    public SingletonMetricas getSingletonMetricas() {
        return singletonMetricas;
    }

    public void atribuirFaseBloqueando(String faseBloqueando) {
        this.faseBloqueando = faseBloqueando;
    }

    public String recuperarFaseBloqueando() {
        return faseBloqueando;
    }

    public String getUsuarioAlternativo() {
        return usuarioCorrente != null ? usuarioCorrente.getLogin() : usuarioAlternativo;
    }

    public void setUsuarioAlternativo(String usuarioAlternativo) {
        this.usuarioAlternativo = usuarioAlternativo;
    }

    public static SistemaService getInstance() {
        return (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
    }

    public void trocarDataSistema(Date data) {
        try {
            dataOperacao = data;
            Calendar c = Calendar.getInstance();
            c.setTime(getDataOperacao());
            Exercicio exercicioPorAno = exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
            if (!exercicioPorAno.getId().equals(getExercicioCorrente().getId())) {
                exercicioCorrente = exercicioPorAno;
            }
            /*recarregaUnidades();*/
        } catch (ExcecaoNegocioGenerica e) {
            setDataOperacao(new Date());
            exercicioCorrente = (null);
        }
    }

    public class AgrupadorNotificacao implements Serializable {
        String titulo;
    }


    public void limparTodasDependenciasDoUsuario(UsuarioSistema usuarioSistema) {
        singletonRecursosSistema.limparUsuario(usuarioSistema);
        leitorMenuFacade.limparMenusUsuario(usuarioSistema);
        recarregaUnidades();
    }

    public String getIpAlternativo() {
        return ipAlternativo;
    }

    public void setIpAlternativo(String ipAlternativo) {
        this.ipAlternativo = ipAlternativo;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppArtifactId() {
        return appArtifactId;
    }

    public void setAppArtifactId(String appArtifactId) {
        this.appArtifactId = appArtifactId;
    }

    @PostConstruct
    private void initVersion() {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));

            appVersion = properties.getProperty("version");
            appArtifactId = properties.getProperty("artifactId");
        } catch (Exception ex) {
            logger.trace("Não foi possível recuperar a versão do projeto.");
        }

    }
}
