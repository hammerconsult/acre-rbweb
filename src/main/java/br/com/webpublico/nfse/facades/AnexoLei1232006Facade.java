package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.AnexoLei1232006;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AnexoLei1232006Facade extends AbstractFacade<AnexoLei1232006> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnexoLei1232006Facade() {
        super(AnexoLei1232006.class);
    }

    @Override
    public AnexoLei1232006 recuperar(Object id) {
        AnexoLei1232006 recuperar = super.recuperar(id);
        Hibernate.initialize(recuperar.getFaixas());
        return recuperar;
    }
}
