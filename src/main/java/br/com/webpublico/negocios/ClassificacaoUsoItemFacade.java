package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClassificacaoUsoItem;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ClassificacaoUsoItemFacade extends AbstractFacade<ClassificacaoUsoItem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ClassificacaoUsoItemFacade() {
        super(ClassificacaoUsoItem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
