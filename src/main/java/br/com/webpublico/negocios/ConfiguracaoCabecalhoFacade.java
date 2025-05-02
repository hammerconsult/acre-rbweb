package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoCabecalho;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mga on 24/07/2017.
 */
@Stateless
public class ConfiguracaoCabecalhoFacade extends AbstractFacade<ConfiguracaoCabecalho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public ConfiguracaoCabecalhoFacade() {
        super(ConfiguracaoCabecalho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoCabecalho buscarConfiguracaoCabecalhoPorUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        ConfiguracaoCabecalho configuracao = buscarConfiguracaoCabecalho(unidadeOrganizacional);
        if (configuracao != null) {
            return configuracao;
        }
        return buscarConfiguracaoCabecalhoRecursivo(unidadeOrganizacional);
    }

    private ConfiguracaoCabecalho buscarConfiguracaoCabecalho(UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select c.* from configuracaocabecalho c where c.unidadeorganizacional_id = :idUnidade";
        Query q = em.createNativeQuery(sql, ConfiguracaoCabecalho.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            ConfiguracaoCabecalho configuracao = (ConfiguracaoCabecalho) q.getResultList().get(0);
            if (configuracao.getDetentorArquivoComposicao() != null
                && configuracao.getDetentorArquivoComposicao().getArquivoComposicao() != null
                && configuracao.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo() != null) {
                configuracao.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo().getPartes().size();
                return configuracao;
            }
        }
        return null;
    }

    private ConfiguracaoCabecalho buscarConfiguracaoCabecalhoRecursivo(UnidadeOrganizacional unidadeOrganizacional) {
        ConfiguracaoCabecalho config = buscarConfiguracaoNaHierarquiaOrcamentaria(unidadeOrganizacional);
        if (config == null) {
            config = buscarConfiguracaoNaHierarquiaAdministrativa(unidadeOrganizacional);
        }
        return config;
    }

    private ConfiguracaoCabecalho buscarConfiguracaoNaHierarquiaAdministrativa(UnidadeOrganizacional unidadeOrganizacional) {
        List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.buscarHierarquiasPorTipoAndUnidadeAndVigencia(TipoHierarquiaOrganizacional.ADMINISTRATIVA, unidadeOrganizacional, sistemaFacade.getDataOperacao());
        ordenar(hierarquias);
        for (HierarquiaOrganizacional ho : hierarquias) {
            ConfiguracaoCabecalho configuracaoCabecalho = buscarConfiguracaoCabecalho(ho.getSubordinada());
            if (configuracaoCabecalho != null) {
                return configuracaoCabecalho;
            }
        }
        throw new ExcecaoNegocioGenerica("Configuração de cabeçalho não encontrada para unidade: " + unidadeOrganizacional);
    }

    private ConfiguracaoCabecalho buscarConfiguracaoNaHierarquiaOrcamentaria(UnidadeOrganizacional unidadeOrganizacional) {
        List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.buscarHierarquiasPorTipoAndUnidadeAndVigencia(TipoHierarquiaOrganizacional.ORCAMENTARIA, unidadeOrganizacional, sistemaFacade.getDataOperacao());
        ordenar(hierarquias);
        for (HierarquiaOrganizacional ho : hierarquias) {
            ConfiguracaoCabecalho configuracaoCabecalho = buscarConfiguracaoCabecalho(ho.getSubordinada());
            if (configuracaoCabecalho != null) {
                return configuracaoCabecalho;
            }
        }
        throw new ExcecaoNegocioGenerica("Configuração de cabeçalho não encontrada para unidade: " + unidadeOrganizacional);
    }

    private void ordenar(List<HierarquiaOrganizacional> hierarquiasOrcamentarias) {
        Collections.sort(hierarquiasOrcamentarias, new Comparator<HierarquiaOrganizacional>() {
            @Override
            public int compare(HierarquiaOrganizacional o1, HierarquiaOrganizacional o2) {
                return o2.getCodigo().compareTo(o1.getCodigo());
            }
        });
    }

    public HierarquiaOrganizacional getHierarquiaUnidade(UnidadeOrganizacional unidadeOrganizacional, TipoHierarquiaOrganizacional tipoHierarquia) {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(tipoHierarquia.name(), unidadeOrganizacional, sistemaFacade.getDataOperacao());
    }

    public Boolean verificarSeExisteConfiguracaoIgual(ConfiguracaoCabecalho cabecalho) {
        String sql = " select c.* from configuracaocabecalho c where c.unidadeorganizacional_id = :idUnidade";
        if (cabecalho != null && cabecalho.getId() != null) {
            sql += "   and c.id <> :idSelecinado ";
        }
        Query q = em.createNativeQuery(sql);
        if (cabecalho != null && cabecalho.getId() != null) {
            q.setParameter("idSelecinado", cabecalho.getId());
        }
        q.setParameter("idUnidade", cabecalho.getUnidadeOrganizacional().getId());
        return !q.getResultList().isEmpty();
    }

    public boolean hasPrincipal(ConfiguracaoCabecalho cabecalho) {
        String sql = " select c.* from configuracaocabecalho c where c.principal = 1 ";
        if (cabecalho != null && cabecalho.getId() != null) {
            sql += "   and c.id <> :idSelecinado ";
        }
        Query q = em.createNativeQuery(sql);
        if (cabecalho != null && cabecalho.getId() != null) {
            q.setParameter("idSelecinado", cabecalho.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
