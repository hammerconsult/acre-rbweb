package br.com.webpublico.negocios.contabil;

import br.com.webpublico.entidades.ConfiguracaoAnexosRREO;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoAnexosRREOFacade extends AbstractFacade<ConfiguracaoAnexosRREO> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoAnexosRREOFacade() {
        super(ConfiguracaoAnexosRREO.class);
    }

    public ConfiguracaoAnexosRREO buscarConfiguracaoPorExercicio(Exercicio exercicio) {
        String sql = " select cfg.* from ConfiguracaoAnexosRREO cfg " +
            " where cfg.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, ConfiguracaoAnexosRREO.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        List<ConfiguracaoAnexosRREO> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public boolean hasConfiguracaoExistente(ConfiguracaoAnexosRREO selecionado) {
        String sql = " select cfg.* from ConfiguracaoAnexosRREO cfg " +
            " where cfg.exercicio_id = :exercicio ";
        if (selecionado.getId() != null) {
            sql += " and cfg.id <> :id ";
        }
        Query q = em.createNativeQuery(sql, ConfiguracaoAnexosRREO.class);
        q.setParameter("exercicio", selecionado.getExercicio().getId());
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
