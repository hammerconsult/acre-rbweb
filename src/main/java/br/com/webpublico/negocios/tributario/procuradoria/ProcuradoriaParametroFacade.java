package br.com.webpublico.negocios.tributario.procuradoria;

import br.com.webpublico.entidades.tributario.procuradoria.ParametroProcuradoria;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProcuradoriaParametroFacade extends AbstractFacade<ParametroProcuradoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProcuradoriaParametroFacade() {
        super(ParametroProcuradoria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroProcuradoria recuperar(Object id) {
        ParametroProcuradoria parametro = em.find(ParametroProcuradoria.class, id);
        parametro.getLinks().size();
        parametro.getDocumentosNecessarios().size();
        return parametro;
    }


    public List<ParametroProcuradoria> recuperarTodosParametros(ParametroProcuradoria parametro) {
        String sql = " SELECT * FROM PARAMETROPROCURADORIA P";
        if (parametro.getId() != null) {
            sql += " where P.ID <> :id";
        }
        Query q = em.createNativeQuery(sql, ParametroProcuradoria.class);
        if (parametro.getId() != null) {
            q.setParameter("id", parametro.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public ParametroProcuradoria recuperarParametroVigente(Date dataParametro) {
        String sql = "SELECT * FROM PARAMETROPROCURADORIA P WHERE trunc(:dataParametro) between trunc(P.DATAINICIAL) AND trunc(P.DATAFINAL)";
        Query q = em.createNativeQuery(sql, ParametroProcuradoria.class);
        q.setParameter("dataParametro", DataUtil.dataSemHorario(dataParametro));
        if (!q.getResultList().isEmpty()) {
            ParametroProcuradoria parametroProcuradoria = (ParametroProcuradoria) q.getResultList().get(0);
            parametroProcuradoria.getDocumentosNecessarios().size();
            parametroProcuradoria.getLinks().size();
            return parametroProcuradoria;
        }
        return null;
    }

}
