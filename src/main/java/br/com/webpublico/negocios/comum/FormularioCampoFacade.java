package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FormularioCampoFacade extends AbstractFacade<FormularioCampo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FormularioCampoFacade() {
        super(FormularioCampo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FormularioCampo recuperar(Object id) {
        FormularioCampo formularioCampo = super.recuperar(id);
        Hibernate.initialize(formularioCampo.getFormularioCampoOpcaoList());
        return formularioCampo;
    }
}
