package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Desmembramento;
import br.com.webpublico.entidades.ProgramacaoCobranca;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 15/08/13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class DesmembramentoFacade extends AbstractFacade<Desmembramento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesmembramentoFacade() {
        super(Desmembramento.class);
    }

    @Override
    public Desmembramento recuperar(Object id) {
        Desmembramento d = em.find(Desmembramento.class, id);
        d.getItens().size();
        return d;
    }
}
