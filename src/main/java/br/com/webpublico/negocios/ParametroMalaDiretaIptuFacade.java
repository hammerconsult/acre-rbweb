package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametroMalaDiretaIPTU;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Pedro on 19/02/2024.
 */
@Stateless
public class ParametroMalaDiretaIptuFacade extends AbstractFacade<ParametroMalaDiretaIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroMalaDiretaIptuFacade() {
        super(ParametroMalaDiretaIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroMalaDiretaIPTU recuperar(Object id) {
        ParametroMalaDiretaIPTU obj = super.recuperar(id);
        return obj;
    }

    public boolean jaExisteParamatroComExercicio(Long idSelecionado, int ano) {
        String sql = "select param.* from parametromaladiretaiptu param " +
            " inner join exercicio ex on param.exercicio_id = ex.id" +
            " where ex.ano = :ano";
        if (idSelecionado != null) sql += " and param.id <> :id";
        Query q = em.createNativeQuery(sql, ParametroMalaDiretaIPTU.class);
        q.setParameter("ano", ano);
        if (idSelecionado != null) q.setParameter("id", idSelecionado);
        return !q.getResultList().isEmpty();
    }

    public ParametroMalaDiretaIPTU buscarParametroPeloExercicio(Exercicio exercicio) {
        if (exercicio == null) return null;
        String sql = "select param.* from parametromaladiretaiptu param " +
            " where param.exercicio_id = :idExercicio";
        Query q = em.createNativeQuery(sql, ParametroMalaDiretaIPTU.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<ParametroMalaDiretaIPTU> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

}
