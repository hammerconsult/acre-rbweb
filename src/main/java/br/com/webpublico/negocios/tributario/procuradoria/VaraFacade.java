package br.com.webpublico.negocios.tributario.procuradoria;

import br.com.webpublico.entidades.tributario.procuradoria.ParametroProcuradoria;
import br.com.webpublico.entidades.tributario.procuradoria.Vara;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class VaraFacade extends AbstractFacade<Vara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public VaraFacade() {
        super(Vara.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Vara recuperar(Object id) {
        return em.find(Vara.class, id);
    }


}
