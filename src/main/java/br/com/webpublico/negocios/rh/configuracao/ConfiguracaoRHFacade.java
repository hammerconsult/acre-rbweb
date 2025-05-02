package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.rh.configuracao.*;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.SingletonCreditoSalario;
import br.com.webpublico.singletons.CacheRH;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 29/09/2015  11:50.
 */
@Stateless
public class ConfiguracaoRHFacade extends AbstractFacade<ConfiguracaoRH> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonCreditoSalario singletonCreditoSalario;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private CacheRH cacheRH;

    private ESocialService eSocialService;

    public ConfiguracaoRHFacade() {
        super(ConfiguracaoRH.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public ConfiguracaoRH recuperar(Object id) {
        ConfiguracaoRH conf = super.recuperar(id);
        conf.getConfiguracoesFaltasInjustificadas().size();
        conf.getConfiguracaoRelatorio().getConfiguracaoRelatorioRHBaseFPs().size();
        conf.getHoraExtraUnidade().size();
        conf.getConfiguracaoWebServiceRH().size();
        conf.getConfiguracoesClassesCredor().size();
        conf.getItemDirfCodigo().size();
        conf.getItemDirfUsuario().size();
        return conf;
    }

    @Override
    public void salvar(ConfiguracaoRH entity) {
        singletonCreditoSalario.recarregarSequencial();
        cacheRH.setConfiguracaoRH(null);
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ConfiguracaoRH entity) {
        singletonCreditoSalario.recarregarSequencial();
        super.salvarNovo(entity);
    }

    public List<ConfiguracaoRH> buscarConfiguracoesRH(ConfiguracaoRH config) {
        String hql = "select config from ConfiguracaoRH config ";
        if (config.getId() != null) {
            hql += " where config = :config ";
        }
        Query q = em.createQuery(hql);
        if (config.getId() != null) {
            q.setParameter("config", config);
        }
        return q.getResultList();
    }

    public ConfiguracaoHolerite buscarConfiguracaoHoleriteVigente(Date data) throws ExcecaoNegocioGenerica {
        String sql = " select ch.* from CONFIGURACAOHOLERITE ch " +
            "   inner join configuracaorh crh on ch.id = crh.configuracaoholerite_id " +
            "   where crh.iniciovigencia = (select max(iniciovigencia) from configuracaorh " +
            "   where configuracaorh.configuracaoholerite_id is not null) " +
            "   and :data BETWEEN crh.INICIOVIGENCIA and coalesce(crh.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql, ConfiguracaoHolerite.class);
        q.setParameter("data", data);
        try {
            return (ConfiguracaoHolerite) q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração encontrada para data " + DataUtil.getDataFormatada(data));
        }
    }

    public ConfiguracaoRH recuperarConfiguracaoRHVigente(Date data) {
        String sql = " SELECT CONF.* " +
            "   FROM CONFIGURACAORH CONF " +
            " WHERE :data BETWEEN CONF.INICIOVIGENCIA AND COALESCE(CONF.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql, ConfiguracaoRH.class);
        q.setParameter("data", data, TemporalType.DATE);
        try {
            return (ConfiguracaoRH) q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração encontrada para data " + DataUtil.getDataFormatada(data));
        }
    }

    public ConfiguracaoFP buscarConfiguracaoFPVigente(Date data) {
        ConfiguracaoRH configuracaoRH = recuperarConfiguracaoRHVigente(data);
        if (configuracaoRH.getConfiguracaoFP() == null) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração FP encontrada para data " + DataUtil.getDataFormatada(data));
        }

        return configuracaoRH.getConfiguracaoFP();

    }

    public String buscarBaseUrlESocial(ConfiguracaoWebServiceRH config) {

        if (config != null) {
            return config.getUrl();
        }
        return null;
    }

    public ConfiguracaoRH buscarConfiguracaoRH() throws ExcecaoNegocioGenerica {
        if (cacheRH.getConfiguracaoRH() == null) {
            cacheRH.setConfiguracaoRH(recuperarConfiguracaoRHVigente());
        }
        return cacheRH.getConfiguracaoRH();
    }

    public ConfiguracaoRH recuperarConfiguracaoRHVigente() {
        String sql = " SELECT CONF.* " +
            "   FROM CONFIGURACAORH CONF " +
            " WHERE :data BETWEEN CONF.INICIOVIGENCIA AND COALESCE(CONF.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql, ConfiguracaoRH.class);
        q.setParameter("data", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        try {
            return (ConfiguracaoRH) q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração rh encontrada para data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        }
    }

    public Boolean habilitaDadosRHPortal() {
        String sql = " SELECT coalesce(CONF.mostrarDadosRHPortal,0) " +
            "   FROM CONFIGURACAORH CONF " +
            " WHERE :data BETWEEN CONF.INICIOVIGENCIA AND COALESCE(CONF.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        return !q.getResultList().isEmpty() && ((Number) q.getResultList().get(0)).intValue() == 1;
    }

    //usado no e-social, por não ter escopo não é possivel pegar a data de operacao do sistemaFacade
    public ConfiguracaoRH recuperarConfiguracaoRHVigenteDataAtual() {
        String sql = " SELECT CONF.* " +
            "   FROM CONFIGURACAORH CONF " +
            " WHERE :data BETWEEN CONF.INICIOVIGENCIA AND COALESCE(CONF.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql, ConfiguracaoRH.class);
        q.setParameter("data", new Date(), TemporalType.DATE);
        try {
            return (ConfiguracaoRH) q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração rh encontrada para data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        }
    }

    public Integer buscarQuantidadeDiasDocumentacaoPortal() {
        String sql = " SELECT CONF.diasApresentacaoDocPortal " +
            "   FROM CONFIGURACAORH CONF " +
            " WHERE :data BETWEEN CONF.INICIOVIGENCIA AND COALESCE(CONF.FINALVIGENCIA, :data)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        try {
            if (!q.getResultList().isEmpty()) {
                return ((BigDecimal) q.getResultList().get(0)).intValue();
            }
        } catch (NoResultException no) {
            logger.error("Nenhuma configuração RH vigente encontrada em " + sistemaFacade.getDataOperacao() + " retornando default value 0.");
            return 0;
        }
        return 0;
    }

    public ConfiguracaoRelatorio buscarConfiguracaoRelatorio() {
        String sql = " SELECT RELATORIO.* FROM CONFIGURACAORH CONFIGURACAO " +
            " INNER JOIN CONFIGURACAORELATORIO RELATORIO ON CONFIGURACAO.CONFIGURACAORELATORIO_ID = RELATORIO.ID ";
        Query q = em.createNativeQuery(sql, ConfiguracaoRelatorio.class);
        try {
            return (ConfiguracaoRelatorio) q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração de relatório encontrada para data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        }
    }

    public ConfiguracaoCreditoSalario buscarConfiguracaoCreditoDeSalario() {
        String sql = " SELECT config.* FROM CONFIGURACAORH CONFIGURACAO " +
            " INNER JOIN ConfiguracaoCreditoSalario config ON CONFIGURACAO.ConfiguracaoCreditoSalario_ID = config.ID ";
        Query q = em.createNativeQuery(sql, ConfiguracaoCreditoSalario.class);
        try {
            return (ConfiguracaoCreditoSalario) q.getSingleResult();
        } catch (NoResultException no) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração de crédito de salário encontrada para data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        }
    }

    public List<BaseFP> buscarBaseFPsToConfiguracaoRelatorioRH() throws ExcecaoNegocioGenerica {
        String sql = "select base.* from CONFIGURACAORH conf " +
            "inner join CONFIGURACAORELATORIO relatoriorh on conf.CONFIGURACAORELATORIO_ID = relatoriorh.ID " +
            "inner join CONFIGRELATORIORHBASEFP relatorioBaseFP on relatoriorh.ID = relatorioBaseFP.CONFIGURACAORELATORIO_ID " +
            "inner join BASEFP base on relatorioBaseFP.BASEFP_ID = base.ID " +
            "where conf.id = :configuracaoRH ";

        ConfiguracaoRH configuracaoRH = recuperarConfiguracaoRHVigente();

        Query q = em.createNativeQuery(sql, BaseFP.class);
        q.setParameter("configuracaoRH", configuracaoRH.getId());

        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar BaseFPs nas configurações vigentes.");
        }
    }

    public List<ClasseCredor> buscarClassesCredoresDaConfiguracao() {
        String sql = " select classe.* from configuracaorh crh" +
            " inner join ConfiguracaoRHClasseCredor crhcc on crhcc.configuracaorh_id = crh.id  " +
            " inner join classecredor classe on crhcc.classecredor_id = classe.id ";
        Query q = em.createNativeQuery(sql, ClasseCredor.class);
        List<ClasseCredor> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }

    public List<ClasseCredor> buscarClassesCredor(String filtro) {
        return classeCredorFacade.listaFiltrandoPorPessoaPorTipoClasse(filtro, null, TipoClasseCredor.PRESTADOR_SERVICO);
    }

    public String getCodigoDirfPorAno(Integer ano, ConfiguracaoRH configuracaoRH) {
        String sql = " select dirf.CODIGO from CONFIGURACAORH config " +
            " inner join dirfcodigo dirf on dirf.CONFIGURACAORH_ID = config.ID " +
            " inner join exercicio on dirf.EXERCICIO_ID = EXERCICIO.ID " +
            " where EXERCICIO.ano = :ano and config.id = :configuracao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("configuracao", configuracaoRH.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (String) resultList.get(0);
        }
        return "T17BS45";
    }

    public DirfCodigo getDirfCodigoPorAno(Integer ano, ConfiguracaoRH configuracaoRH) {
        String sql = " select dirf.* from CONFIGURACAORH config " +
            " inner join dirfcodigo dirf on dirf.CONFIGURACAORH_ID = config.ID " +
            " inner join exercicio on dirf.EXERCICIO_ID = EXERCICIO.ID " +
            " where EXERCICIO.ano = :ano and config.id = :configuracao";
        Query q = em.createNativeQuery(sql, DirfCodigo.class);
        q.setParameter("ano", ano);
        q.setParameter("configuracao", configuracaoRH.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (DirfCodigo) resultList.get(0);
        }
        return null;
    }
}
