package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.VaraCivel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class VaraCivelFacade extends AbstractFacade<VaraCivel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VaraCivelFacade() {
        super(VaraCivel.class);
    }
}
