package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.XmlWsNfse;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class XmlWsNfseFacade extends AbstractFacade<XmlWsNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public XmlWsNfseFacade() {
        super(XmlWsNfse.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public XmlWsNfse recuperar(Object id) {
        XmlWsNfse recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar);
        return recuperar;
    }
}
