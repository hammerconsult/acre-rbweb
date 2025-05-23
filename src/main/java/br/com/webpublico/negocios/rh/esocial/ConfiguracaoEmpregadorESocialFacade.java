package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.contabil.reinf.RegistroEventoRetencaoReinf;
import br.com.webpublico.entidades.contabil.reinf.RegistroReinf;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.*;
import br.com.webpublico.entidades.rh.saudeservidor.CAT;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.rh.esocial.FiltroEventosEsocial;
import br.com.webpublico.entidadesauxiliares.rh.esocial.ReativacaoBeneficio;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VWContratoFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EmpregadorESocialDTO;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.dao.JdbcRegistroESocialDAO;
import br.com.webpublico.negocios.rh.saudeservidor.CATFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.*;
import br.com.webpublico.reinf.service.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.esocial.UtilEsocial;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 05/06/2018.
 */
@Stateless
public class ConfiguracaoEmpregadorESocialFacade extends AbstractFacade<ConfiguracaoEmpregadorESocial> {


    protected static final Logger logger = LoggerFactory.getLogger(ConfiguracaoEmpregadorESocialFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CodigoAliquotaFPAEsocialFacade codigoAliquotaFPAEsocialFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    @EJB
    private ProcessoAdministrativoJudicialFacade processoAdministrativoJudicialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ASOFacade asoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private LogErroEnvioEventoFacade logErroEnvioEventoFacade;
    @EJB
    private CATFacade catFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;

    private ESocialService eSocialService;
    private S1000Service s1000Service;
    private S1005Service s1005Service;
    private S1010Service s1010Service;
    private S1280Service s1280Service;
    private S1020Service s1020Service;
    private S1030Service s1030Service;
    private S3000Service s3000Service;
    private S1200Service s1200Service;
    private S1202Service s1202Service;
    private S1207Service s1207Service;
    private S1299Service s1299Service;
    private S1298Service s1298Service;
    private S1210Service s1210Service;
    private S2299Service s2299Service;
    private S2418Service s2418Service;
    private S2240Service s2240Service;
    private S2400Service s2400Service;
    private EmpregadorService empregadorService;

    private RegistroESocialService registroESocialService;

    private R1000Service r1000Service;
    private R1070Service r1070Service;
    private R2010Service r2010Service;
    private R2020Service r2020Service;
    private R2098Service r2098Service;
    private R2099Service r2099Service;
    private R4020Service r4020Service;
    private R4099Service r4099Service;
    private R9000Service r9000Service;
    private RegistroReinfService registroReinfService;

    private JdbcRegistroESocialDAO jdbcRegistroESocialDAO;
    private ReinfService reinfService;

    public ConfiguracaoEmpregadorESocialFacade() {
        super(ConfiguracaoEmpregadorESocial.class);
    }

    @PostConstruct
    public void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        reinfService = (ReinfService) Util.getSpringBeanPeloNome("reinfService");
        s1000Service = (S1000Service) Util.getSpringBeanPeloNome("s1000Service");
        s1005Service = (S1005Service) Util.getSpringBeanPeloNome("s1005Service");
        s1010Service = (S1010Service) Util.getSpringBeanPeloNome("s1010Service");
        s1020Service = (S1020Service) Util.getSpringBeanPeloNome("s1020Service");
        s1030Service = (S1030Service) Util.getSpringBeanPeloNome("s1030Service");
        s1280Service = (S1280Service) Util.getSpringBeanPeloNome("s1280Service");
        s3000Service = (S3000Service) Util.getSpringBeanPeloNome("s3000Service");
        s1200Service = (S1200Service) Util.getSpringBeanPeloNome("s1200Service");
        s1202Service = (S1202Service) Util.getSpringBeanPeloNome("s1202Service");
        s1207Service = (S1207Service) Util.getSpringBeanPeloNome("s1207Service");
        s1210Service = (S1210Service) Util.getSpringBeanPeloNome("s1210Service");
        s1299Service = (S1299Service) Util.getSpringBeanPeloNome("s1299Service");
        s1298Service = (S1298Service) Util.getSpringBeanPeloNome("s1298Service");
        s2299Service = (S2299Service) Util.getSpringBeanPeloNome("s2299Service");
        s2418Service = (S2418Service) Util.getSpringBeanPeloNome("s2418Service");
        s2240Service = (S2240Service) Util.getSpringBeanPeloNome("s2240Service");
        s2400Service = (S2400Service) Util.getSpringBeanPeloNome("s2400Service");
        empregadorService = (EmpregadorService) Util.getSpringBeanPeloNome("empregadorService");
        registroESocialService = (RegistroESocialService) Util.getSpringBeanPeloNome("registroESocialService");
        r1000Service = (R1000Service) Util.getSpringBeanPeloNome("r1000Service");
        r1070Service = (R1070Service) Util.getSpringBeanPeloNome("r1070Service");
        r2010Service = (R2010Service) Util.getSpringBeanPeloNome("r2010Service");
        r2020Service = (R2020Service) Util.getSpringBeanPeloNome("r2020Service");
        r2098Service = (R2098Service) Util.getSpringBeanPeloNome("r2098Service");
        r2099Service = (R2099Service) Util.getSpringBeanPeloNome("r2099Service");
        r4020Service = (R4020Service) Util.getSpringBeanPeloNome("r4020Service");
        r4099Service = (R4099Service) Util.getSpringBeanPeloNome("r4099Service");
        r9000Service = (R9000Service) Util.getSpringBeanPeloNome("r9000Service");
        registroReinfService = (RegistroReinfService) Util.getSpringBeanPeloNome("registroReinfService");

        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcRegistroESocialDAO = (JdbcRegistroESocialDAO) ap.getBean("registroESocialDAO");


    }

    public JdbcRegistroESocialDAO getJdbcRegistroESocialDAO() {
        return jdbcRegistroESocialDAO;
    }


    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CodigoAliquotaFPAEsocialFacade getCodigoAliquotaFPAEsocialFacade() {
        return codigoAliquotaFPAEsocialFacade;
    }

    public RegistroESocialFacade getRegistroESocialFacade() {
        return registroESocialFacade;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfiguracaoEmpregadorESocial recuperar(Object id) {
        ConfiguracaoEmpregadorESocial configuracao = super.recuperar(id);
        Hibernate.initialize(configuracao.getItemConfiguracaoEmpregadorESocial());
        Hibernate.initialize(configuracao.getItemIndicativoContribuicao());
        if (configuracao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(configuracao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return configuracao;
    }

    public Long recuperarIdFichaPorOrgaoEIdentificadorWP(Long idUnidade, String idWP) {
        if (StringUtils.isBlank(idWP)) {
            return null;
        }

        String primeiroIdFicha = idWP.split("/")[0].trim();

        String sql = "select vinculo.id from FichaFinanceiraFP ficha " +
            "inner join vinculofp vinculo on ficha.vinculofp_id = vinculo.id " +
            "where ficha.id = :idWP ";

        if (idUnidade != null) {
            sql += "and vinculo.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(primeiroIdFicha));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdVinculoFPPorUnidadeEId(Long idUnidade, String idWP) {
        if (StringUtils.isBlank(idWP)) {
            return null;
        }

        String sql = "select vinculo.id from vinculofp vinculo " +
            "where vinculo.id = :idWP ";

        if (idUnidade != null) {
            sql += "and vinculo.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdRegistroPorUnidadeEId(Long idUnidade, String idWP, String objetoContratoFP) {
        if (StringUtils.isBlank(idWP) || StringUtils.isBlank(objetoContratoFP)) {
            return null;
        }

        String sql = "select vinculo.id from vinculofp vinculo " +
            "inner join " + objetoContratoFP + " obj on obj.contratofp_id = vinculo.id " +
            "where obj.id = :idWP ";

        if (idUnidade != null) {
            sql += "and obj.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdContratoFPPorUnidadeEId(Long idUnidade, String idWP, String objetoContratoFP) {
        if (StringUtils.isBlank(idWP) || StringUtils.isBlank(objetoContratoFP)) {
            return null;
        }

        String sql = "select vinculo.id from vinculofp vinculo " +
            "inner join " + objetoContratoFP + " obj on obj.contratofp_id = vinculo.id " +
            "where obj.id = :idWP ";

        if (idUnidade != null) {
            sql += "and vinculo.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdVinculoFPAssociadoPorUnidadeEId(Long idUnidade, String idWP, String objetoVinculoFP) {
        if (StringUtils.isBlank(idWP) || StringUtils.isBlank(objetoVinculoFP)) {
            return null;
        }

        String sql = "select vinculo.id from vinculofp vinculo " +
            "inner join " + objetoVinculoFP + " obj on obj.vinculofp_id = vinculo.id " +
            "where obj.id = :idWP ";

        if (idUnidade != null) {
            sql += "and vinculo.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdPrestadorPorUnidadeEId(Long idUnidade, String idWP) {
        if (StringUtils.isBlank(idWP)) {
            return null;
        }

        String sql = "select prestador.id from prestadorservicos prestador " +
            "where prestador.id = :idWP ";

        if (idUnidade != null) {
            sql += "and prestador.lotacao_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }

        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long recuperarIdPessoaPorUnidadeEId(Long idUnidade, String idWP) {
        if (StringUtils.isBlank(idWP)) {
            return null;
        }

        String sql = "select distinct p.id from vinculofp vinculo " +
            "inner join matriculafp mat on vinculo.matriculafp_id = mat.id " +
            "inner join pessoafisica p on mat.pessoa_id = p.id " +
            "inner join cat cat on cat.colaborador_id = p.id " +
            "WHERE cat.id = :idWP ";

        if (idUnidade != null) {
            sql += "AND vinculo.unidadeorganizacional_id = :idUnidade ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("idWP", Long.parseLong(idWP));

        if (idUnidade != null) {
            q.setParameter("idUnidade", idUnidade);
        }
        try {
            Object result = q.getSingleResult();
            return (result != null) ? ((Number) result).longValue() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean hasConfiguracaoParaEntidade(ConfiguracaoEmpregadorESocial configuracao) {
        String sql = "select * from CONFIGEMPREGADORESOCIAL configuracao" +
            " where configuracao.ENTIDADE_ID = :entidade";
        if (configuracao.getId() != null) {
            sql += " and configuracao.id <> :configuracao";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("entidade", configuracao.getEntidade().getId());
        if (configuracao.getId() != null) {
            q.setParameter("configuracao", configuracao.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoEmpregadorESocial recuperarPorEntidade(Entidade entidade) {
        String sql = "select config.* from CONFIGEMPREGADORESOCIAL config where ENTIDADE_ID = :idEntidade";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idEntidade", entidade.getId());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }

    public List<ConfiguracaoEmpregadorESocial> recuperarConfiguracaoEmpregadorVigente() {
        String sql = " select config.* " +
            "from CONFIGEMPREGADORESOCIAL config " +
            "where sysdate between config.INICIOVIGENCIA and coalesce (config.FINALVIGENCIA, sysdate)" +
            "and config.CERTIFICADO_ID is not null";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    @Override
    public void salvar(ConfiguracaoEmpregadorESocial entity) {
        ConfiguracaoEmpregadorESocial merge = em.merge(entity);
        EmpregadorESocialDTO empregadorESocialDTO = empregadorService.montarEmpregadorESocial(merge);
        eSocialService.enviarEmpregador(empregadorESocialDTO);
    }

    @Override
    public void salvarNovo(ConfiguracaoEmpregadorESocial entity) {
        ConfiguracaoEmpregadorESocial merge = em.merge(entity);
        em.flush();//Forçar escrever as mudanças no banco.
        EmpregadorESocialDTO empregadorESocialDTO = empregadorService.montarEmpregadorESocial(entity);
        eSocialService.enviarEmpregador(empregadorESocialDTO);
    }


    public void enviarS1000(ConfiguracaoEmpregadorESocial config) {
        s1000Service.enviarS1000(config);
    }

    public void enviarS1005(ConfiguracaoEmpregadorESocial config) {
        s1005Service.enviarS1005(config);
    }

    public List<EventoESocialDTO> getEventosEmpregador(ConfiguracaoEmpregadorESocial config) {
        return empregadorService.getEventosEmpregador(config.getEntidade());
    }


    public void enviarS1010(ConfiguracaoEmpregadorESocial config, List<EventoFP> eventos) {
        s1010Service.enviarS1010(config, eventos, null);
    }

    public void enviarS1010(ConfiguracaoEmpregadorESocial config, List<EventoFP> eventos, List<DetalheLogErroEnvioEvento> itemLog) {
        s1010Service.enviarS1010(config, eventos, itemLog);
    }

    public void enviarS1280(List<IndicativoContribuicao> indicativos) {
        for (IndicativoContribuicao indicativo : indicativos) {
            s1280Service.enviarS1280(indicativo);
        }
    }

    public List<EventoESocialDTO> getEventosEmpregador(Entidade empregador) {
        return empregadorService.getEventosEmpregador(empregador);
    }

    public List<EventoESocialDTO> getEventosEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoESocial tipoArquivoESocial, int page, int pageSize, SituacaoESocial situacao) {
        try {
            return empregadorService.getEventosEmpregadorAndTipoArquivo(empregador, tipoArquivoESocial, situacao, page, pageSize);
        } catch (Exception e) {
            logger.error("Problema para encontrar eventos do empregador: " + empregador);
        }
        return null;
    }

    public Integer getQuantidadeEventosPorEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao) {
        return empregadorService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregador, tipoArquivoESocial, situacao);
    }


    public void enviarS1020(ConfiguracaoEmpregadorESocial empregadorESocial) throws ValidacaoException {
        Map<Integer, Entidade> entidades = Maps.newHashMap();
        empregadorESocial = recuperar(empregadorESocial.getId());
        for (ItemConfiguracaoEmpregadorESocial item : empregadorESocial.getItemConfiguracaoEmpregadorESocial()) {
            if (possuiFPAS(item)) {
                logger.debug("Entidade: [{}]", item.getUnidadeOrganizacional().getEntidade());
                logger.debug("FPAS: [{}]", item.getUnidadeOrganizacional().getEntidade().getCodigoFpas());
                entidades.put(item.getUnidadeOrganizacional().getEntidade().getCodigoFpas(), item.getUnidadeOrganizacional().getEntidade());
            }
        }
        logger.debug("Entidade: [{}]", empregadorESocial.getEntidade());
        logger.debug("FPAS: [{}]", empregadorESocial.getEntidade().getCodigoFpas());
        entidades.put(empregadorESocial.getEntidade().getCodigoFpas(), empregadorESocial.getEntidade());

        for (Map.Entry<Integer, Entidade> entry : entidades.entrySet()) {
            enviarS1020(empregadorESocial, entry.getValue());
        }


    }

    private boolean possuiFPAS(ItemConfiguracaoEmpregadorESocial item) {
        return item.getUnidadeOrganizacional() != null && item.getUnidadeOrganizacional().getEntidade() != null && item.getUnidadeOrganizacional().getEntidade().getCodigoFpas() != null;
    }


    public void enviarS1020(ConfiguracaoEmpregadorESocial empregador, Entidade entidade) throws ValidacaoException {
        s1020Service.enviarS1020(empregador, entidade);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS2205(ConfiguracaoEmpregadorESocial empregador, List<VWContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso, List<DetalheLogErroEnvioEvento> itemLogErro) throws ValidacaoException {
        int quantidadeErros = 0;
        for (VWContratoFP contrato : contratos) {
            try {
                if (contrato.getId() != null) {
                    contratoFPFacade.enviarS2205ESocial(empregador, contratoFPFacade.recuperar(contrato.getId()));
                }
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ContratoFP", contrato.getId(), empregador, TipoArquivoESocial.S2205, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
            }
        }
        if (quantidadeErros > 0) {
            String mensagem = "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + contratos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.";
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public void enviarS2206(ConfiguracaoEmpregadorESocial empregador, Map<ContratoFP, Date> mapContratos, List<DetalheLogErroEnvioEvento> itemLogErro) throws ValidacaoException {
        int quantidadeErros = 0;
        for (ContratoFP contrato : mapContratos.keySet()) {
            try {
                contratoFPFacade.enviarS2206ESocial(empregador, contratoFPFacade.recuperar(contrato.getId()), sistemaFacade.getDataOperacao(), mapContratos.get(contrato));
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ContratoFP", contrato.getId(), empregador, TipoArquivoESocial.S2206, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + mapContratos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        } else {
            FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
        }
    }

    public void enviarS2220(ConfiguracaoEmpregadorESocial empregador, List<ASO> asos, List<DetalheLogErroEnvioEvento> itemErroLog) throws ValidacaoException {
        int quantidadeErros = 0;
        for (ASO aso : asos) {
            try {
                asoFacade.enviarS2220ESocial(empregador, aso);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ASO", aso.getId(), empregador, TipoArquivoESocial.S2220, ve);
                itemErroLog.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + asos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void enviarS2299(ConfiguracaoEmpregadorESocial selecionado, List<ExoneracaoRescisao> exoneracoes,
                            List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        for (ExoneracaoRescisao exoneracao : exoneracoes) {
            try {
                s2299Service.enviarS2299(selecionado, exoneracao);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ExoneracaoRescisao", exoneracao.getId(), selecionado, TipoArquivoESocial.S2299, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + exoneracoes.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS1200(RegistroEventoEsocial registroESocial,
                                                        List<VinculoFPEventoEsocial> vinculos,
                                                        Entidade entidade, ConfiguracaoEmpregadorESocial config,
                                                        AssistenteBarraProgresso assistenteBarraProgresso,
                                                        ConfiguracaoRH configuracaoRH, AtomicInteger quantidadeErros,
                                                        List<DetalheLogErroEnvioEvento> itemLogErro) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(entidade.getPessoaJuridica().getCnpj()));

        vinculos.stream().forEach(vinculoEvento -> {
            try {
                registroESocial.setEntidade(entidade);

                if (vinculoEvento.getIdVinculo() != null) {
                    VinculoFP vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
                    vinculoEvento.setVinculoFP(vinculo);
                }
                if (vinculoEvento.getIdPessoa() != null && TipoApuracaoFolha.MENSAL.equals(registroESocial.getTipoApuracaoFolha())) {
                    PrestadorServicos prestadorServicos = prestadorServicosFacade.buscarPrestadorServicosPorIdPessoa(vinculoEvento.getIdPessoa());
                    vinculoEvento.setPrestadorServico(prestadorServicos != null ? (PessoaFisica) prestadorServicos.getPrestador() : null);
                }
                s1200Service.enviarS1200(registroESocial, empregador, config, vinculoEvento.getIdsFichas(), null,
                    vinculoEvento, configuracaoRH);
            } catch (ValidacaoException ve) {
                quantidadeErros.incrementAndGet();
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1200, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
                quantidadeErros.incrementAndGet();
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1200, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
            assistenteBarraProgresso.conta();
        });

        if (quantidadeErros.get() > 0) {
            assistenteBarraProgresso.setQuantidadeErros(quantidadeErros.get());
        }

        return new AsyncResult<>(assistenteBarraProgresso);
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void criarHistoricoEventosPagamento(RegistroEventoEsocial registroESocial,
                                               ConfiguracaoEmpregadorESocial config,
                                               TipoClasseESocial tipoClasseESocial,
                                               Date dataEnvio, UsuarioSistema usuario, ClasseWP classeWP) {
        HistoricoEnvioEsocial historicoEnvioEsocial = UtilEsocial.criarHistoricoEsocial(config, dataEnvio, usuario,
            tipoClasseESocial, registroESocial.getMes(), registroESocial.getExercicio(), null);
        List<ItemHistoricoEnvioEsocial> itens = Lists.newArrayList();

        List<VinculoFPEventoEsocial> vinculos = Lists.newArrayList();

        vinculos = definirVinculo(registroESocial, vinculos);

        for (VinculoFPEventoEsocial vinculoEvento : vinculos) {
            recuperarVinculoOuPrestador(vinculoEvento);
            VinculoFP vinculo = vinculoEvento.getVinculoFP();
            PessoaFisica pf = vinculoEvento.getPrestadorServico();
            List<Long> idsFichaFinanceira = Lists.newArrayList();

            idsFichaFinanceira = definirIdFichaFinanceira(vinculoEvento, idsFichaFinanceira);

            criarItemHistoricoEventoPagamento(classeWP, historicoEnvioEsocial, vinculo, pf, idsFichaFinanceira, itens);
        }
        historicoEnvioEsocial.getItemHistoricoEnvios().addAll(itens);
        em.merge(historicoEnvioEsocial);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void criarHistoricoEsocial(List<? extends IHistoricoEsocial> itemInterface, ConfiguracaoEmpregadorESocial config,
                                      TipoClasseESocial tipoClasseESocial,
                                      Date dataEnvio, UsuarioSistema usuario, ClasseWP classeWP, Mes mes, Exercicio exercicio) {
        HistoricoEnvioEsocial historicoEnvioEsocial = UtilEsocial.criarHistoricoEsocial(config, dataEnvio, usuario,
            tipoClasseESocial, mes, exercicio, null);
        List<ItemHistoricoEnvioEsocial> itens = Lists.newArrayList();

        for (IHistoricoEsocial item : itemInterface) {
            ItemHistoricoEnvioEsocial itemHistorico = UtilEsocial.criarItemHistoricoEnvioEsocial(historicoEnvioEsocial,
                item.getDescricaoCompleta(),
                item.getIdentificador(), classeWP);
            itens.add(itemHistorico);
        }
        historicoEnvioEsocial.getItemHistoricoEnvios().addAll(itens);
        em.merge(historicoEnvioEsocial);
    }

    private void criarItemHistoricoEventoPagamento(ClasseWP classeWP, HistoricoEnvioEsocial historicoEnvioEsocial, VinculoFP vinculo, PessoaFisica pf, List<Long> idsFichaFinanceira, List<ItemHistoricoEnvioEsocial> itens) {
        ItemHistoricoEnvioEsocial item = UtilEsocial.criarItemHistoricoEnvioEsocial(historicoEnvioEsocial,
            vinculo != null ? vinculo.toString() : pf.toString(), getIdFichas(idsFichaFinanceira), classeWP);
        itens.add(item);
    }

    private List<Long> definirIdFichaFinanceira(VinculoFPEventoEsocial vinculoEvento, List<Long> idsFichaFinanceira) {
        if (vinculoEvento.getIdsFichas() != null && !vinculoEvento.getIdsFichas().isEmpty()) {
            idsFichaFinanceira = vinculoEvento.getIdsFichas();
        } else if (vinculoEvento.getIdFichaFinanceira() != null) {
            idsFichaFinanceira = Lists.newArrayList(vinculoEvento.getIdFichaFinanceira());
        }
        return idsFichaFinanceira;
    }

    private void recuperarVinculoOuPrestador(VinculoFPEventoEsocial vinculoEvento) {
        if (vinculoEvento.getIdVinculo() != null) {
            VinculoFP vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
            vinculoEvento.setVinculoFP(vinculo);
        } else if (vinculoEvento.getIdPessoa() != null) {
            PrestadorServicos prestadorServicos = prestadorServicosFacade.buscarPrestadorServicosPorIdPessoa(vinculoEvento.getIdPessoa());
            vinculoEvento.setPrestadorServico(prestadorServicos != null ? (PessoaFisica) prestadorServicos.getPrestador() : null);
        }
    }

    private List<VinculoFPEventoEsocial> definirVinculo(RegistroEventoEsocial registroESocial, List<VinculoFPEventoEsocial> vinculos) {
        if (registroESocial.getItemVinculoFPSelecionados() != null && registroESocial.getItemVinculoFPSelecionados().length > 0) {
            vinculos = Lists.newArrayList(registroESocial.getItemVinculoFPSelecionados());
        } else if (registroESocial.getItemVinculoFP() != null && !registroESocial.getItemVinculoFP().isEmpty()) {
            vinculos = registroESocial.getItemVinculoFP();
        }
        return vinculos;
    }

    private String getIdFichas(List<Long> ids) {
        String retorno = "";
        for (Long id : ids) {
            retorno = retorno.concat(id.toString()).concat(",");
        }
        return retorno.substring(0, retorno.length() - 2);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS1202(RegistroEventoEsocial registroESocial,
                                                        List<VinculoFPEventoEsocial> vinculos,
                                                        Entidade entidade, ConfiguracaoEmpregadorESocial config,
                                                        AssistenteBarraProgresso assistenteBarraProgresso, AtomicInteger quantidadeErros,
                                                        List<DetalheLogErroEnvioEvento> itemLogErro) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(entidade.getPessoaJuridica().getCnpj()));
        vinculos.forEach(vinculoEvento -> {
            try {
                registroESocial.setEntidade(entidade);
                VinculoFP vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
                vinculoEvento.setVinculoFP(vinculo);
                PrestadorServicos prestadorServicos = prestadorServicosFacade
                    .buscarPrestadorServicosPorIdPessoa(
                        Optional.ofNullable(vinculo)
                            .map(VinculoFP::getContratoFP)
                            .map(ContratoFP::getMatriculaFP)
                            .map(MatriculaFP::getPessoa)
                            .map(Pessoa::getId)
                            .orElse(null)
                    );
                s1202Service.enviarEventoS1202(registroESocial, empregador, config,
                    vinculo, vinculoEvento.getIdsFichas(), prestadorServicos);
            } catch (ValidacaoException ve) {
                quantidadeErros.incrementAndGet();
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP",
                    !vinculoEvento.getIdsFichas().isEmpty() ? vinculoEvento.getIdsFichas().get(0) : null, config,
                    TipoArquivoESocial.S1202, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
                quantidadeErros.incrementAndGet();
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP",
                    !vinculoEvento.getIdsFichas().isEmpty() ? vinculoEvento.getIdsFichas().get(0) : null, config,
                    TipoArquivoESocial.S1202, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
            if (quantidadeErros.get() > 0) {
                assistenteBarraProgresso.setQuantidadeErros(quantidadeErros.get());
            }
            assistenteBarraProgresso.conta();
        });
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS1207(RegistroEventoEsocial registroESocial, Entidade entidade,
                                                        ConfiguracaoEmpregadorESocial config,
                                                        AssistenteBarraProgresso assistenteBarraProgresso,
                                                        List<DetalheLogErroEnvioEvento> itemLogErro) {

        int quantidadeErros = 0;
        for (VinculoFPEventoEsocial vinculoEvento : registroESocial.getItemVinculoFP()) {
            try {
                registroESocial.setEntidade(entidade);
                VinculoFP vinculo = null;

                if (vinculoEvento.getIdVinculo() != null) {
                    vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
                }
                vinculoEvento.setVinculoFP(vinculo);

                s1207Service.enviarS1207(registroESocial, vinculo, vinculoEvento.getIdFichaFinanceira());
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1207, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1207, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
            assistenteBarraProgresso.conta();
        }
        if (quantidadeErros > 0) {
            String mensagem = "Atenção! Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + registroESocial.getItemVinculoFP().size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.";
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS1210(RegistroEventoEsocial registroESocial, List<VinculoFPEventoEsocial> vinculos,
                                                        Entidade entidade, ConfiguracaoEmpregadorESocial config,
                                                        AssistenteBarraProgresso assistenteBarraProgresso,
                                                        TipoRegimePrevidenciario tipoRegimePrevidenciario,
                                                        AtomicInteger quantidadeErros, List<DetalheLogErroEnvioEvento> itemLogErro) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(entidade.getPessoaJuridica().getCnpj()));
        vinculos.stream().forEach(vinculoEvento -> {
            try {
                registroESocial.setEntidade(entidade);
                VinculoFP vinculo;
                if (vinculoEvento.getIdVinculo() != null) {
                    vinculo = vinculoFPFacade.recuperarSimples(vinculoEvento.getIdVinculo());
                    vinculoEvento.setVinculoFP(vinculo);
                }
                if (tipoRegimePrevidenciario == null || TipoRegimePrevidenciario.RGPS.equals(tipoRegimePrevidenciario)) {
                    PrestadorServicos prestadorServicos = prestadorServicosFacade.buscarPrestadorServicosPorIdPessoa(vinculoEvento.getIdPessoa());
                    vinculoEvento.setPrestadorServico(prestadorServicos != null ? (PessoaFisica) prestadorServicos.getPrestador() : null);
                }
                s1210Service.enviarEventoS1210(config, empregador, registroESocial, vinculoEvento);
            } catch (ValidacaoException ve) {
                quantidadeErros.incrementAndGet();
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1210, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                quantidadeErros.incrementAndGet();
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("FichaFinanceiraFP", vinculoEvento.getIdFichaFinanceira(), config, TipoArquivoESocial.S1200, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
            assistenteBarraProgresso.conta();
        });
        if (quantidadeErros.get() > 0) {
            assistenteBarraProgresso.setQuantidadeErros(quantidadeErros.get());
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public void enviarS1299(RegistroEventoEsocialS1299 registro) {
        s1299Service.enviarS1299(registro);
    }

    public void enviarS1298(RegistroEventoEsocial registro) {
        s1298Service.enviarS1298(registro);
    }

    public void enviarS3000(ExclusaoEventoEsocial exclusaoEventoEsocial, ConfiguracaoEmpregadorESocial configuracao) {
        for (RegistroExclusaoS3000 registroExclusaoS3000 : exclusaoEventoEsocial.getItemExclusao()) {
            s3000Service.enviarS3000(configuracao, registroExclusaoS3000);
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteBarraProgresso> enviarS2200(ConfiguracaoEmpregadorESocial empregador,
                                                        List<VWContratoFP> contratos, AssistenteBarraProgresso assistenteBarraProgresso, Date dataOperacao, List<DetalheLogErroEnvioEvento> itemErroLog) throws ValidacaoException {
        int quantidadeErros = 0;

        for (VWContratoFP contrato : contratos) {
            try {
                contratoFPFacade.enviarS2200ESocial(empregador, contrato, dataOperacao, assistenteBarraProgresso);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ContratoFP", contrato.getId(), empregador, TipoArquivoESocial.S2200, ve);
                itemErroLog.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
            }
        }
        if (quantidadeErros > 0) {
            String mensagem = "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + contratos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.";
            assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(mensagem));
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }

    public void enviarS2240(ConfiguracaoEmpregadorESocial empregador, List<RiscoOcupacional> riscos) throws ValidacaoException {
        for (RiscoOcupacional risco : riscos) {
            s2240Service.enviarS2240(empregador, risco);
        }
    }

    public void enviarS2418(ConfiguracaoEmpregadorESocial empregador, List<ReativacaoBeneficio> reativacoes,
                            List<DetalheLogErroEnvioEvento> itemErroLog) throws ValidacaoException {
        int quantidadeErros = 0;
        for (ReativacaoBeneficio reativacao : reativacoes) {
            try {
                s2418Service.enviarS2418(empregador, reativacao, sistemaFacade.getDataOperacao());
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ReativacaoBeneficio", reativacao.getId(), empregador, TipoArquivoESocial.S2418, ve);
                itemErroLog.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + reativacoes.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    public void enviarS2230(ConfiguracaoEmpregadorESocial empregador, List<Afastamento> afastamentos,
                            List<DetalheLogErroEnvioEvento> itemLogErro) throws ValidacaoException {
        int quantidadeErros = 0;
        for (Afastamento afastamento : afastamentos) {
            try {
                afastamentoFacade.enviarS2230ESocial(empregador, afastamento);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("Afastamento", afastamento.getId(), empregador, TipoArquivoESocial.S2230, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + afastamentos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    public void enviarS2231(ConfiguracaoEmpregadorESocial empregador, List<CedenciaContratoFP> cedencias,
                            TipoCessao2231 tipoCessao2231, List<DetalheLogErroEnvioEvento> itemLogErro) throws ValidacaoException {
        int quantidadeErros = 0;
        for (CedenciaContratoFP cedencia : cedencias) {
            try {
                cedenciaContratoFPFacade.enviarS2231ESocial(empregador, cedencia, tipoCessao2231);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("CedenciaContratoFP", cedencia.getId(), empregador, TipoArquivoESocial.S2231, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + cedencias.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }


    public void enviarS1050(ConfiguracaoEmpregadorESocial config, List<HorarioDeTrabalho> horarios) {
        List<HorarioDeTrabalho> lista = horarioDeTrabalhoFacade.lista();
        for (HorarioDeTrabalho horarioDeTrabalho : horarios) {
            horarioDeTrabalhoFacade.enviarS1050ESocial(config, horarioDeTrabalho);
        }
    }

    public void enviarS1070(ConfiguracaoEmpregadorESocial config, List<ProcessoAdministrativoJudicial> processos) {
        if (processos != null) {
            for (ProcessoAdministrativoJudicial processo : processos) {
                processoAdministrativoJudicialFacade.enviarProcessoJudicialAdministrativo(config, processo);
            }
        }
    }

    public void enviarCargaInicialS1050(ConfiguracaoEmpregadorESocial selecionado) {
        List<HorarioDeTrabalho> lista = horarioDeTrabalhoFacade.lista();
        for (HorarioDeTrabalho horarioDeTrabalho : lista) {
            horarioDeTrabalhoFacade.enviarS1050ESocial(selecionado, horarioDeTrabalho);
        }

    }

    public void enviarCargaInicialS1070(ConfiguracaoEmpregadorESocial selecionado) {
        List<ProcessoAdministrativoJudicial> processos = processoAdministrativoJudicialFacade.buscarProcessosPorEmpregador(selecionado, TipoIntegracaoEsocial.ESOCIAL);
        if (processos != null) {
            for (ProcessoAdministrativoJudicial processo : processos) {
                processoAdministrativoJudicialFacade.enviarProcessoJudicialAdministrativo(selecionado, processo);
            }
        }
    }

    public void enviarlS2190(ConfiguracaoEmpregadorESocial selecionado, List<VWContratoFP> contratos, List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        if (contratos != null) {
            for (VWContratoFP contrato : contratos) {
                try {
                    contratoFPFacade.enviarS2190ESocial(selecionado, contrato);
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ContratoFP", contrato.getId(), selecionado, TipoArquivoESocial.S2190, ve);
                    itemLogErro.addAll(log.getItemDetalheLog());
                }
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + contratos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    public void enviarS2405(ConfiguracaoEmpregadorESocial selecionado, List<VinculoFP> aposentados,
                            List<DetalheLogErroEnvioEvento> itemErroLog) {
        int quantidadeErros = 0;
        if (aposentados != null) {
            for (VinculoFP contrato : aposentados) {
                try {
                    vinculoFPFacade.enviarS2405ESocial(selecionado, contrato);
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("ContratoFP", contrato.getId(), selecionado, TipoArquivoESocial.S2405, ve);
                    itemErroLog.addAll(log.getItemDetalheLog());
                }
            }
        }

        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + aposentados.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }


    public void enviarS2420(ConfiguracaoEmpregadorESocial selecionado, List<VinculoFP> vinculos, List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        if (vinculos != null) {
            for (VinculoFP vinculo : vinculos) {
                try {
                    vinculoFPFacade.enviarS2420ESocial(selecionado, vinculo, registroDeObitoFacade.existeRegistroDeObitoPorPessoaFisica(vinculo.getMatriculaFP().getPessoa()));
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("VinculoFP", vinculo.getId(), selecionado, TipoArquivoESocial.S2420, ve);
                    itemLogErro.addAll(log.getItemDetalheLog());
                }
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + vinculos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }


    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Future<AssistenteBarraProgresso> enviarCargaInicialS2200(ConfiguracaoEmpregadorESocial selecionado, AssistenteBarraProgresso assistenteBarraProgresso, Date dataOperacao) throws ValidacaoException {
        List<ContratoFP> contratos = contratoFPFacade.buscarContratoFpPorEmpregador(selecionado, dataOperacao);
        if (contratos != null) {
            assistenteBarraProgresso.setTotal(contratos.size());
            for (ContratoFP contrato : contratos) {
                VWContratoFP contratoFPDto = new VWContratoFP();
                contratoFPDto.setId(contrato.getId());
                contratoFPFacade.enviarS2200ESocial(selecionado, contratoFPDto, dataOperacao, assistenteBarraProgresso);
                assistenteBarraProgresso.conta();
            }
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }


    //apenas para teste, o s2205 não tem carga inicial
    public void enviarCargaInicialS2205(ConfiguracaoEmpregadorESocial selecionado) {
        contratoFPFacade.enviarS2205ESocial(selecionado, contratoFPFacade.recuperar(639006473l));
//        ABDEL BARBOSA DERZE
    }

    //apenas para teste, o s2205 não tem carga inicial
    public void enviarCargaInicialS2206(ConfiguracaoEmpregadorESocial selecionado) {
        contratoFPFacade.enviarS2206ESocial(selecionado, contratoFPFacade.recuperar(639006473l), sistemaFacade.getDataOperacao(), sistemaFacade.getDataOperacao());
//        ABDEL BARBOSA DERZE
    }

    public void enviarCargaInicialS2300(ConfiguracaoEmpregadorESocial selecionado) {
        List<PrestadorServicos> prestadores = prestadorServicosFacade.buscarPrestadorServicoPorEmpregador(selecionado, false, null, null, null);
        if (prestadores != null) {
            for (PrestadorServicos prestador : prestadores) {
                prestadorServicosFacade.enviarS2300ESocial(selecionado, prestador);
            }
        }
    }

    public void enviarS2300(ConfiguracaoEmpregadorESocial empregador, List<PrestadorServicos> prestadores,
                            List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        if (prestadores != null) {
            for (PrestadorServicos prestador : prestadores) {
                try {
                    prestadorServicosFacade.enviarS2300ESocial(empregador, prestador);
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("PrestadorServicos", prestador.getId(), empregador, TipoArquivoESocial.S2300, ve);
                    itemLogErro.addAll(log.getItemDetalheLog());
                }
            }
            if (quantidadeErros > 0) {
                FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + prestadores.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
            } else {
                FacesUtil.addAtencao("Evento Enviado com sucesso, aguarde o retorno da validação do evento.");
            }
        }
    }

    public void enviarS2306(ConfiguracaoEmpregadorESocial empregador, List<PrestadorServicos> prestadores,
                            List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        if (prestadores != null) {
            for (PrestadorServicos prestador : prestadores) {
                try {
                    prestadorServicosFacade.enviarS2306ESocial(empregador, prestador);
                } catch (ValidacaoException ve) {
                    quantidadeErros++;
                    LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("PrestadorServicos", prestador.getId(), empregador, TipoArquivoESocial.S2306, ve);
                    itemLogErro.addAll(log.getItemDetalheLog());
                }
            }
            if (quantidadeErros > 0) {
                FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + prestadores.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void enviarS2400(ConfiguracaoEmpregadorESocial selecionado, List<VinculoFP> vinculos, List<DetalheLogErroEnvioEvento> itemLogErro) {
        int quantidadeErros = 0;
        for (VinculoFP vinculo : vinculos) {
            try {
                s2400Service.enviarS2400(selecionado, vinculo);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                LogErroEnvioEvento log = logErroEnvioEventoFacade.criarLogErro("VinculoFP", vinculo.getId(), selecionado, TipoArquivoESocial.S2400, ve);
                itemLogErro.addAll(log.getItemDetalheLog());
            } catch (Exception e) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoRealizada(e.getMessage());
                quantidadeErros++;
                logErroEnvioEventoFacade.criarLogErro("VinculoFP", vinculo.getId(), selecionado, TipoArquivoESocial.S2400, ve);
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + vinculos.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    public void enviarCargaInicialS2405(ConfiguracaoEmpregadorESocial selecionado) {
        int quantidadeErros = 0;
        List<VinculoFP> vinculoFPS = vinculoFPFacade.buscarBeneficiariosParaEnvioEsocial(selecionado, true, new Date(), null, null, TipoArquivoESocial.S2405);
        for (VinculoFP vinculoFP : vinculoFPS) {
            try {
                vinculoFPFacade.enviarS2405ESocial(selecionado, vinculoFP);
            } catch (ValidacaoException ve) {
                quantidadeErros++;
                logErroEnvioEventoFacade.criarLogErro("VinculoFP", vinculoFP.getId(), selecionado, TipoArquivoESocial.S2405, ve);
            }
        }
        if (quantidadeErros > 0) {
            FacesUtil.addWarn("Atenção!", "Ocorreu <b>" + quantidadeErros + "</b> erro(s) de <b>" + vinculoFPS.size() + "</b> evento(s) enviado(s), consulte o Log no painel do e-social para mais detalhes.");
        }
    }

    public void enviarS2210(List<CAT> itemCAT, ConfiguracaoEmpregadorESocial config) {
        itemCAT.forEach(cat -> {
            List<ContratoFP> contratos = contratoFPFacade.listaContratosVigentesPorPessoaFisica(cat.getColaborador());
            catFacade.enviarEventoS2210(config, cat, contratos.get(0));
        });
    }

    public ConfiguracaoEmpregadorESocial buscarEmpregadorPorVinculoFP(VinculoFP vinculoFP) {
        String sql = "  select empregador.* from vinculofp vinculo " +
            " inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.id" +
            " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.SUBORDINADA_ID = unidade.id" +
            " inner join ITEMEMPREGADORESOCIAL item on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " inner join CONFIGEMPREGADORESOCIAL empregador on item.CONFIGEMPREGADORESOCIAL_ID = empregador.id" +
            " where vinculo.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID" +
            " and vinculo.id = :idContrato " +
            " and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idContrato", vinculoFP.getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }

    public ConfiguracaoEmpregadorESocial buscarEmpregadorPorPrestadorServico(PrestadorServicos prestador) {
        String sql = "  select empregador.* from PRESTADORSERVICOS  prestador " +
            " inner join UNIDADEORGANIZACIONAL unidade on prestador.lotacao_id = unidade.id" +
            " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.SUBORDINADA_ID = unidade.id" +
            " inner join ITEMEMPREGADORESOCIAL item on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " inner join CONFIGEMPREGADORESOCIAL empregador on item.CONFIGEMPREGADORESOCIAL_ID = empregador.id" +
            " where prestador.lotacao_id = ho.SUBORDINADA_ID" +
            " and prestador.id = :idPrestador " +
            " and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idPrestador", prestador.getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }

    public List<EventoESocialDTO> getEventosPorXML(int first, int pageSize, FiltroEventosEsocial filtro, TipoArquivoESocial tipoArquivoESocial) {
        String sql = "select REGISTROESOCIAL.*" +
            " from REGISTROESOCIAL " +
            " inner join CONFIGEMPREGADORESOCIAL emp on REGISTROESOCIAL.EMPREGADOR_ID = emp.ID " +
            " where TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
            " and emp.ENTIDADE_ID = :empregador_id ";
        if (filtro.getMes() != null && filtro.getExercicio() != null) {
            sql += "  and xml like  :mesAno ";
        }
        if (filtro.getTipoFolhaDePagamento() != null) {
            sql += "and EXTRACTVALUE(xmltype(XML), '/eSocial/evtRemun/dmDev/ideDmDev', " +
                "  (case ";
            for (NamespacesXmlEsocial n : NamespacesXmlEsocial.values()) {
                sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
            }
            sql += " end))  in (select ficha.id from fichafinanceirafp ficha " +
                "                                                                        inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
                "                                                                        where folha.TIPOFOLHADEPAGAMENTO = :tipoFolha)";
        }

        if (filtro.getVinculoFP() != null) {
            sql += " and  EXTRACTVALUE(xmltype(XML), " +
                "'/eSocial/evtRemun/dmDev/infoPerApur/ideEstabLot/remunPerApur/matricula', " +
                " (case ";
            for (NamespacesXmlEsocial n : NamespacesXmlEsocial.values()) {
                sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
            }
            sql += " end)) like :matricula";
        }
        if (filtro.getSituacaoESocial() != null) {
            sql += " and REGISTROESOCIAL.situacao = :situacaoEvento ";
        }

        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        q.setParameter("empregador_id", filtro.getEmpregador().getId());
        if (filtro.getMes() != null && filtro.getExercicio() != null) {
            String mes = filtro.getMes().getNumeroMes().toString().length() == 1 ? "0" + filtro.getMes().getNumeroMes() : filtro.getMes().getNumeroMes().toString();
            String mesAno = filtro.getExercicio().getAno().toString().concat("-").concat(mes);
            q.setParameter("mesAno", "%<perApur>" + mesAno + "%");
        } else if (filtro.getMes() != null) {
            String mes = filtro.getMes().getNumeroMes().toString().length() == 1 ? "0" + filtro.getMes().getNumeroMes().toString() : filtro.getMes().getNumeroMes().toString();
            q.setParameter("mes", "%" + mes);
        } else if (filtro.getExercicio() != null) {
            q.setParameter("mes", filtro.getExercicio().getAno() + "%");
        }
        if (filtro.getTipoFolhaDePagamento() != null) {
            q.setParameter("tipoFolha", filtro.getTipoFolhaDePagamento().name());
        }
        if (filtro.getVinculoFP() != null) {
            q.setParameter("matricula", filtro.getVinculoFP().getMatriculaFP().getMatricula() + "%");
        }
        if (filtro.getSituacaoESocial() != null) {
            q.setParameter("situacaoEvento", filtro.getSituacaoESocial().name());
        }
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        List resultList = q.getResultList();
        List<EventoESocialDTO> item = Lists.newArrayList();

        for (Object o : resultList) {
            EventoESocialDTO eventoESocialDTO = RegistroESocial.registroESocialToEventoESocialDTO((RegistroESocial) o);
            item.add(eventoESocialDTO);
        }
        return item;
    }

    public int contarEventosPorXML(FiltroEventosEsocial filtro, TipoArquivoESocial tipoArquivoESocial) {
        String sql = "select count(REGISTROESOCIAL.id)" +
            " from REGISTROESOCIAL " +
            " inner join CONFIGEMPREGADORESOCIAL emp on REGISTROESOCIAL.EMPREGADOR_ID = emp.ID " +
            " where TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
            " and emp.ENTIDADE_ID = :empregador_id ";
        if (filtro.getMes() != null && filtro.getExercicio() != null) {
            sql += "  and xml like  :mesAno ";
        }

        if (filtro.getTipoFolhaDePagamento() != null) {
            sql += "and EXTRACTVALUE(xmltype(XML), '/eSocial/evtRemun/dmDev/ideDmDev', " +
                "  (case ";
            for (NamespacesXmlEsocial n : NamespacesXmlEsocial.values()) {
                sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
            }
            sql += " end)) in (select ficha.id from fichafinanceirafp ficha " +
                "                                                                        inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id\n" +
                "                                                                        where folha.TIPOFOLHADEPAGAMENTO = :tipoFolha)";
        }
        if (filtro.getVinculoFP() != null) {
            sql += " and  EXTRACTVALUE(xmltype(XML), " +
                "'/eSocial/evtRemun/dmDev/infoPerApur/ideEstabLot/remunPerApur/matricula', " +
                " (case ";
            for (NamespacesXmlEsocial n : NamespacesXmlEsocial.values()) {
                sql += " when xml like '%" + n.name().substring(6) + "%' then '" + n.getDescricao() + "'";
            }
            sql += " end)) like :matricula";
        }
        if (filtro.getSituacaoESocial() != null) {
            sql += " and REGISTROESOCIAL.situacao = :situacaoEvento ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        q.setParameter("empregador_id", filtro.getEmpregador().getId());
        if (filtro.getMes() != null && filtro.getExercicio() != null) {
            String mes = filtro.getMes().getNumeroMes().toString().length() == 1 ? "0" + filtro.getMes().getNumeroMes() : filtro.getMes().getNumeroMes().toString();
            String mesAno = filtro.getExercicio().getAno().toString().concat("-").concat(mes);
            q.setParameter("mesAno", "%<perApur>" + mesAno + "%");
        }
        if (filtro.getTipoFolhaDePagamento() != null) {
            q.setParameter("tipoFolha", filtro.getTipoFolhaDePagamento().name());
        }
        if (filtro.getVinculoFP() != null) {
            q.setParameter("matricula", filtro.getVinculoFP().getMatriculaFP().getMatricula() + "%");
        }
        if (filtro.getSituacaoESocial() != null) {
            q.setParameter("situacaoEvento", filtro.getSituacaoESocial().name());
        }
        return ((Number) q.getResultList().get(0)).intValue();
    }

    public ConfiguracaoEmpregadorESocial buscarConfiguracaoPorEntidade(Entidade entidade) {
        String sql = "select config.* from CONFIGEMPREGADORESOCIAL config where ENTIDADE_ID = :idEntidade";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idEntidade", entidade.getId());
        List resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) resultado.get(0);
        }
        return null;
    }

    public List<EventoESocialDTO> getEventosFolhaByEmpregador(String cnpj, TipoArquivoESocial tipoArquivoESocial, Integer mes,
                                                              Integer ano, String numeroRecibo, String idVinculo, String idXml,
                                                              String cpfVinculo, List<String> idsExoneracao) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(cnpj));
        List<SituacaoESocial> situacoes = Lists.newArrayList();
        situacoes.add(SituacaoESocial.PROCESSADO_COM_SUCESSO);
        situacoes.add(SituacaoESocial.PROCESSADO_COM_ADVERTENCIA);
        return eSocialService.getEventosFolhaByEmpregador(empregador, tipoArquivoESocial, mes, ano, situacoes, numeroRecibo,
            idVinculo, idXml, cpfVinculo, idsExoneracao);
    }


    @Asynchronous
    public Future<AssistenteSincronizacaoReinf> iniciarSincronizacao(AssistenteSincronizacaoReinf assistente) {
        assistente.setMensagens(com.google.common.collect.Lists.<String>newArrayList());
        assistente.setBarraProgressoItens(new BarraProgressoItens());
        try {
            assistente.iniciarBarraProgresso(0);
            sincronizarReinfPorTipo(assistente);
            assistente.getBarraProgressoItens().finaliza();
            assistente.getBarraProgressoItens().setMensagens("Finalizou a sincronização....");
        } catch (Exception e) {
            e.printStackTrace();
            assistente.setMensagemErro(e.getMessage());
            assistente.finalizar();
            throw e;
        }
        return new AsyncResult<>(assistente);
    }

    private void sincronizarReinfPorTipo(AssistenteSincronizacaoReinf assistente) {
        if (TipoArquivoReinf.R1000.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR1000(assistente);
        }
        if (TipoArquivoReinf.R1070.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR1070(assistente);
        }
        if (TipoArquivoReinf.R2010.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR2010(assistente);
        }
        if (TipoArquivoReinf.R2020.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR2020(assistente);
        }
        if (TipoArquivoReinf.R2098.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR2098(assistente);
        }
        if (TipoArquivoReinf.R2099.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR2099(assistente);
        }
        if (TipoArquivoReinf.R4020.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR4020(assistente);
        }
        if (TipoArquivoReinf.R4099.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR4099(assistente);
        }
        if (TipoArquivoReinf.R9000.equals(assistente.getTipoArquivo())) {
            iniciarSincronizacaoR9000(assistente);
        }
    }

    private void iniciarSincronizacaoR1070(AssistenteSincronizacaoReinf assistente) {
        assistente.getBarraProgressoItens().setMensagens("Iniciando a sincronização dos eventos R1070....");
        if (assistente.getEventoR1070s() != null) {
            assistente.getEventoR1070s().clear();
        }
        assistente.getBarraProgressoItens().setMensagens("Montando arquivo do evento....");
        List<EventoR1070> eventos = r1070Service.criarEventosR1070(assistente, assistente.getSelecionado().getItemEmpregadorR1070());
        if (eventos != null) {
            assistente.getBarraProgressoItens().setMensagens("Enviando " + eventos.size() + " eventos.");
            assistente.setEventoR1070s(eventos);
            assistente.iniciarBarraProgresso(assistente.getEventoR1070s().size());
            for (EventoR1070 evento : assistente.getEventoR1070s()) {
                try {
                    assistente.getBarraProgressoItens().setMensagens("Enviando o evento R1070" + evento.getId());
                    r1070Service.enviarR1070(assistente, evento);
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                } catch (Exception e) {
                    assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                }
            }
        }
    }

    private void iniciarSincronizacaoR2098(AssistenteSincronizacaoReinf assistente) {
        if (assistente.getSelecionado().getEventoR2098() != null) {
            assistente.iniciarBarraProgresso(1);
            try {
                assistente.getBarraProgressoItens().setMensagens("Enviando o evento R2098 " + assistente.getSelecionado().getEventoR2098().getId());
                r2098Service.enviar(assistente, assistente.getSelecionado().getEventoR2098());
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            } catch (Exception e) {
                assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            }
        }
    }

    private void iniciarSincronizacaoR9000(AssistenteSincronizacaoReinf assistente) {
        if (assistente.getSelecionado().getEventoR9000() != null) {
            assistente.iniciarBarraProgresso(1);
            try {
                assistente.getBarraProgressoItens().setMensagens("Enviando o evento R9000 " + assistente.getSelecionado().getEventoR9000().getId());
                EventoR9000 eventoR9000 = buscarR9000(assistente, assistente.getConfiguracaoEmpregadorESocial(), assistente.getSelecionado());
                if (eventoR9000 != null) {
                    r9000Service.enviar(assistente, eventoR9000);
                }
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            } catch (Exception e) {
                e.printStackTrace();
                assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            }
        }
    }

    private void iniciarSincronizacaoR2099(AssistenteSincronizacaoReinf assistente) {
        if (assistente.getSelecionado().getEventoR2099() != null) {
            assistente.iniciarBarraProgresso(1);
            try {
                assistente.getBarraProgressoItens().setMensagens("Enviando o evento R2099 " + assistente.getSelecionado().getEventoR2099().getId());
                r2099Service.enviar(assistente);
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            } catch (Exception e) {
                assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            }
        }
    }

    private void iniciarSincronizacaoR2020(AssistenteSincronizacaoReinf assistente) {
        assistente.getBarraProgressoItens().setMensagens("Iniciando a sincronização dos eventos R2010....");
        if (assistente.getEventoR2020s() != null) {
            assistente.getEventoR2020s().clear();
        }
        assistente.getBarraProgressoItens().setMensagens("Montando arquivo do evento....");
        List<EventoR2020> eventos = r2020Service.criarEventosR2020(assistente, assistente.getSelecionado().getItemEmpregadorR2020());
        if (eventos != null) {
            assistente.getBarraProgressoItens().setMensagens("Enviando " + eventos.size() + " eventos.");
            assistente.setEventoR2020s(eventos);
            assistente.iniciarBarraProgresso(assistente.getEventoR2020s().size());
            for (EventoR2020 evento : assistente.getEventoR2020s()) {
                try {
                    assistente.getBarraProgressoItens().setMensagens("Enviando o evento R2020 " + evento.getId());
                    r2020Service.enviar(assistente, evento);
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                } catch (Exception e) {
                    assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                }
            }
        }
    }

    private void iniciarSincronizacaoR2010(AssistenteSincronizacaoReinf assistente) {
        assistente.getBarraProgressoItens().setMensagens("Iniciando a sincronização dos eventos R1070....");
        if (assistente.getEventoR2010s() != null) {
            assistente.getEventoR2010s().clear();
        }
        assistente.getBarraProgressoItens().setMensagens("Montando arquivo do evento....");
        List<EventoR2010> eventos = r2010Service.criarEventosR2010(assistente, assistente.getSelecionado().getItemEmpregadorR2010());
        if (eventos != null) {
            assistente.getBarraProgressoItens().setMensagens("Enviando " + eventos.size() + " eventos.");
            assistente.setEventoR2010s(eventos);
            assistente.iniciarBarraProgresso(assistente.getEventoR2010s().size());
            for (EventoR2010 evento : assistente.getEventoR2010s()) {
                try {
                    assistente.getBarraProgressoItens().setMensagens("Enviando o evento R2010 " + evento.getId());
                    r2010Service.enviar(assistente, evento);
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                } catch (Exception e) {
                    assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                }
            }
        }
    }

    private void iniciarSincronizacaoR1000(AssistenteSincronizacaoReinf assistente) {
        assistente.getBarraProgressoItens().setMensagens("Iniciando a sincronização dos eventos R1000....");
        if (assistente.getEventoR1000s() != null) {
            assistente.getEventoR1000s().clear();
        }
        assistente.getBarraProgressoItens().setMensagens("Montando arquivo do evento....");
        List<EventoR1000> eventoR1000s = r1000Service.criarEventosR1000(assistente);
        if (eventoR1000s != null) {
            assistente.getBarraProgressoItens().setMensagens("Enviando " + eventoR1000s.size() + " eventos.");
            assistente.setEventoR1000s(eventoR1000s);
            assistente.iniciarBarraProgresso(assistente.getEventoR1000s().size());
            for (EventoR1000 eventoR1000 : assistente.getEventoR1000s()) {
                try {
                    assistente.getBarraProgressoItens().setMensagens("Enviando o evento R1000" + eventoR1000.getId());
                    r1000Service.enviarR1000(assistente, eventoR1000);
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                } catch (Exception e) {
                    assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                }

            }
        }
    }

    private void iniciarSincronizacaoR4020(AssistenteSincronizacaoReinf assistente) {
        assistente.getBarraProgressoItens().setMensagens("Iniciando a sincronização dos eventos R4020....");
        if (assistente.getEventoR4020s() != null) {
            assistente.getEventoR4020s().clear();
        }
        assistente.getBarraProgressoItens().setMensagens("Montando arquivo do evento....");
        List<EventoR4020> eventos = r4020Service.criarEventosR4020(assistente, assistente.getSelecionado().getItemEmpregadorR4020());
        if (eventos != null) {
            assistente.getBarraProgressoItens().setMensagens("Enviando " + eventos.size() + " eventos.");
            assistente.setEventoR4020s(eventos);
            assistente.iniciarBarraProgresso(assistente.getEventoR4020s().size());
            for (EventoR4020 evento : assistente.getEventoR4020s()) {
                try {
                    assistente.getBarraProgressoItens().setMensagens("Enviando o evento R4020 " + evento.getId());
                    r4020Service.enviar(evento);
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                } catch (Exception e) {
                    assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                    assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
                }
            }
        }
    }

    private void iniciarSincronizacaoR4099(AssistenteSincronizacaoReinf assistente) {
        if (assistente.getSelecionado().getEventoR4099() != null) {
            assistente.iniciarBarraProgresso(1);
            try {
                assistente.getBarraProgressoItens().setMensagens("Enviando o evento R4099 " + assistente.getSelecionado().getEventoR4099().getId());
                r4099Service.enviar(assistente);
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            } catch (Exception e) {
                assistente.getBarraProgressoItens().addMensagem("Erro no evento " + e.getMessage());
                assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
            }
        }
    }

    public List<ProcessoAdministrativoJudicial> buscarR1070(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r1070Service.buscarR1070(config, filtroReinf);
    }

    public List<RegistroEventoRetencaoReinf> buscarR2010(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r2010Service.buscarEvento(config, filtroReinf);
    }

    public List<RegistroEventoRetencaoReinf> buscarR2020(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r2020Service.buscarEvento(config, filtroReinf);
    }

    public EventoR2098 buscarR2098(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r2098Service.criarEventosR2098(assistente, config, filtroReinf);
    }

    public EventoR2099 buscarR2099(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r2099Service.criarEventosR2099(assistente, config, filtroReinf);
    }

    public List<RegistroEventoRetencaoReinf> buscarR4020(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r4020Service.buscarEvento(config, filtroReinf);
    }

    public EventoR4099 buscarR4099(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r4099Service.criarEventosR4099(config, filtroReinf);
    }

    public EventoR9000 buscarR9000(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf) {
        return r9000Service.criarEventosR9000(assistente, config, filtroReinf.getEventoR9000());
    }

    public List<EventoReinfDTO> getEventosReinfEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, SituacaoESocial situacao, int page, int pageSize) {
        try {
            return reinfService.getEventosReinfEmpregadorAndTipoArquivo(empregador, tipoArquivoESocial, situacao, page, pageSize);
        } catch (Exception e) {
            logger.error("Problema para encontrar eventos do empregador: " + empregador);
        }
        return null;
    }

    public List<EventoReinfDTO> getEventosPorEmpregadorAndPeriodo(Entidade empregador, Integer mes, Integer ano) {
        try {
            return reinfService.getEventosPorEmpregadorAndPeriodo(empregador, mes, ano);
        } catch (Exception e) {
            logger.error("Problema para encontrar eventos do empregador e periodo: " + empregador);
        }
        return null;
    }

    public Integer getQuantidadeEventosReinfPorEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, SituacaoESocial situacao) {
        return reinfService.getQuantidadeEventosReinfPorEmpregadorAndTipoArquivo(empregador, tipoArquivoESocial, situacao);
    }

    public RegistroReinf criarEventoReinf(EventoReinfDTO evento, ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        return registroReinfService.criarEventoReinf(evento, configuracaoEmpregadorESocial);
    }

    public EventoReinfDTO getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(String cnpjEmpregadorESocial, TipoArquivoReinf tipoArquivoReinf, Mes mes, Integer ano, String cnpjFornecedor) {
        try {
            return reinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(cnpjEmpregadorESocial.replaceAll("[^0-9]", ""), tipoArquivoReinf, mes, ano, cnpjFornecedor.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            logger.error("Erro para encontrar eventos por CNPJ Empregador/Tipo Arquivo/Período/CNPJ Fornecedor: " + cnpjEmpregadorESocial + "/" + tipoArquivoReinf.getCodigo() + "/" + mes + "/" + ano + "/" + cnpjFornecedor);
        }
        return null;
    }

    public EventoReinfDTO getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(Entidade empregador, TipoArquivoReinf tipoArquivoESocial, Mes mes, Integer ano, String cnpjFornecedor) {
        try {
            return reinfService.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(empregador, tipoArquivoESocial, mes, ano, cnpjFornecedor.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            logger.error("Erro para encontrar eventos por Empregador/Tipo Arquivo/Período/CNPJ Fornecedor: " + empregador);
        }
        return null;
    }

    public List<IndicativoContribuicao> getConfiguracaoEmpregadorS1280(ConfiguracaoEmpregadorESocial config, Exercicio exercicio, Mes mes, Boolean decimoTerceiro, Boolean apenasNaoEnviados) {
        String sql = "select distinct indicativo.* from CONFIGEMPREGADORESOCIAL config " +
            " inner join IndicativoContribuicao indicativo on indicativo.EMPREGADOR_ID = config.id " +
            " where config.id = :idConfig";
        Query q = em.createNativeQuery(sql, IndicativoContribuicao.class);
        q.setParameter("idConfig", config.getId());
        List list = q.getResultList();
        if (list != null) {
            return list;
        }
        return null;
    }

    public List<ConfiguracaoEmpregadorESocial> getEntidadesSemConfiguracao(List<EventoFPEmpregador> itemEventoFP) {
        String sql = "select config.* from CONFIGEMPREGADORESOCIAL config " +
            " inner join entidade on config.ENTIDADE_ID = ENTIDADE.ID" +
            " where entidade.id not in :idsEntidade";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idsEntidade", getIdsEntidade(itemEventoFP));
        return q.getResultList();
    }


    private List<Long> getIdsEntidade(List<EventoFPEmpregador> itemEventoFP) {
        return itemEventoFP.stream().map(e -> e.getEntidade().getId()).collect(Collectors.toList());
    }

    public List<ConfiguracaoEmpregadorESocial> buscarConfiguracoesVigentesPeloCodigoDaHierarquiaDoItem(String codigo, Date dataOperacao) {
        String sql = "select conf.* " +
            " from ITEMEMPREGADORESOCIAL item " +
            "         inner join CONFIGEMPREGADORESOCIAL conf on item.CONFIGEMPREGADORESOCIAL_ID = conf.ID " +
            "         inner join VWHIERARQUIAADMINISTRATIVA ho on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " where :dataOperacao between conf.INICIOVIGENCIA and coalesce(conf.FINALVIGENCIA, :dataOperacao) " +
            " and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) " +
            "       and trim(ho.CODIGO) like :codigo";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("codigo", codigo.trim());
        q.setParameter("dataOperacao", dataOperacao);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }


    public ConfiguracaoEmpregadorESocial buscarConfiguracaoEmpregadorPorCNPJ(String cnpj) {
        String sql = "select * from CONFIGEMPREGADORESOCIAL config " +
            "inner join entidade on config.ENTIDADE_ID = entidade.ID " +
            "inner join pessoajuridica pj on entidade.PESSOAJURIDICA_ID = pj.ID " +
            "where REGEXP_REPLACE(pj.cnpj, '[^0-9]', '')  = :cnpj";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("cnpj", cnpj);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) resultList.get(0);
        }
        return null;
    }
}
