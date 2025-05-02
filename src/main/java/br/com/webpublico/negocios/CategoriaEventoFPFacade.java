package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaEventoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 24/02/14
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CategoriaEventoFPFacade extends AbstractFacade<CategoriaEventoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public CategoriaEventoFPFacade() {
        super(CategoriaEventoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaEventoFP existeCategoriaEventoVigente(CategoriaEventoFP selecionado) {
        boolean temfim = selecionado.getFinalVigencia() != null;
        String fim = "";
        if (temfim) {
            fim = " or :data1 between cat.inicioVigencia and coalesce(cat.finalVigencia,:data1) ";
        }

        String sql = "from CategoriaEventoFP  cat where (cat.eventoFP = :evento or cat.categoriaPCS = :categoria) " +
                " and (:data between cat.inicioVigencia and coalesce(cat.finalVigencia,:data) " + fim + " )";
        if (selecionado.getId() != null) {
            sql += " and cat.id <> :id ";
        }
        Query q = em.createQuery(sql);
        q.setParameter("evento", selecionado.getEventoFP());
        q.setParameter("categoria", selecionado.getCategoriaPCS());
        q.setParameter("data", selecionado.getInicioVigencia(), TemporalType.DATE);
        if (temfim) {
            q.setParameter("data1", selecionado.getFinalVigencia(), TemporalType.DATE);
        }
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (CategoriaEventoFP) q.getResultList().get(0);

    }
}
