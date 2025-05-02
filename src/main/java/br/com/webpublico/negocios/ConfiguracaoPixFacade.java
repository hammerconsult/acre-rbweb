package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoDAM;
import br.com.webpublico.entidades.ConfiguracaoPix;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoPixFacade extends AbstractFacade<ConfiguracaoPix> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoPixFacade() {
        super(ConfiguracaoPix.class);
    }

    @Override
    public ConfiguracaoPix recuperar(Object id) {
        ConfiguracaoPix configuracaoPix = em.find(ConfiguracaoPix.class, id);
        Hibernate.initialize(configuracaoPix.getConfiguracoesPorDam());
        return configuracaoPix;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ConfiguracaoDAM> buscarConfiguracoesDam(String parte) {
        String sql = " select distinct cd.* from configuracaodam cd " +
            " where (lower(cd.descricao) like :parte or cd.codigofebraban like :parte) " +
            " order by descricao desc ";

        Query q = em.createNativeQuery(sql, ConfiguracaoDAM.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");

        List<ConfiguracaoDAM> configuracoes = q.getResultList();

        return configuracoes != null ? configuracoes : Lists.<ConfiguracaoDAM>newArrayList();
    }

    public boolean hasConfiguracaoBase(ConfiguracaoPix configuracaoPix) {
        String sql = " select cp.id from configuracaopix cp " +
            " where cp.base = :base ";
        sql += (configuracaoPix.getId() != null ? " and cp.id <> :idConfiguracaoPix " : "");

        Query q = em.createNativeQuery(sql);
        q.setParameter("base", configuracaoPix.getBase().trim().toLowerCase());

        if (configuracaoPix.getId() != null) {
            q.setParameter("idConfiguracaoPix", configuracaoPix.getId());
        }

        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoPix buscarConfiguracaoPix() {
        String sql = " select cp.* from configuracaopix cp " +
            " where cp.base = :base ";

        Query q = em.createNativeQuery(sql, ConfiguracaoPix.class);
        q.setParameter("base", sistemaFacade.getUsuarioBancoDeDados());

        List<ConfiguracaoPix> configuracoes = q.getResultList();

        if (configuracoes != null && !configuracoes.isEmpty()) {
            ConfiguracaoPix configuracaoPix = configuracoes.get(0);
            Hibernate.initialize(configuracaoPix.getConfiguracoesPorDam());

            return configuracaoPix;
        }
        return null;
    }
}
