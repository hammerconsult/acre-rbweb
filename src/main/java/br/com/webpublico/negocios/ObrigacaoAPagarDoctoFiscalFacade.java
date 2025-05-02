package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mga on 23/06/2017.
 */
@Stateless
public class ObrigacaoAPagarDoctoFiscalFacade extends AbstractFacade<ObrigacaoAPagarDoctoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ObrigacaoAPagarDoctoFiscalFacade() {
        super(ObrigacaoAPagarDoctoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


}
