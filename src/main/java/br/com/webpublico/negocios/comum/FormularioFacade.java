package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FormularioFacade extends AbstractFacade<Formulario> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FormularioFacade() {
        super(Formulario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Formulario recuperar(Object id) {
        Formulario formulario = super.recuperar(id);
        Hibernate.initialize(formulario.getFormularioCampoList());
        for (FormularioCampo campo : formulario.getFormularioCampoList()) {
            Hibernate.initialize(campo.getFormularioCampoOpcaoList());
        }
        return formulario;
    }
}
