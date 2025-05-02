/*
 * Codigo gerado automaticamente em Wed Feb 09 11:02:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroETR;
import br.com.webpublico.entidades.ParametroETRFormulario;
import br.com.webpublico.negocios.comum.FormularioCampoFacade;
import br.com.webpublico.negocios.comum.FormularioFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroETRFacade extends AbstractFacade<ParametroETR> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FormularioFacade formularioFacade;
    @EJB
    private FormularioCampoFacade formularioCampoFacade;

    public ParametroETRFacade() {
        super(ParametroETR.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormularioFacade getFormularioFacade() {
        return formularioFacade;
    }

    public FormularioCampoFacade getFormularioCampoFacade() {
        return formularioCampoFacade;
    }

    @Override
    public ParametroETR recuperar(Object id) {
        ParametroETR parametroETR = super.recuperar(id);
        Hibernate.initialize(parametroETR.getParametroETRFormularioList());
        Hibernate.initialize(parametroETR.getAceiteList());
        return parametroETR;
    }

    public ParametroETR recuperarParametroETR() {
        String sql = " select * from parametroetr ";
        Query q = em.createNativeQuery(sql, ParametroETR.class);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            ParametroETR parametroETR = (ParametroETR) resultList.get(0);
            Hibernate.initialize(parametroETR.getParametroETRFormularioList());
            if (parametroETR.getParametroETRFormularioList() != null) {
                for (ParametroETRFormulario parametroETRFormulario : parametroETR.getParametroETRFormularioList()) {
                    Hibernate.initialize(parametroETRFormulario.getFormularioCampoDispensaList());
                }
            }
            Hibernate.initialize(parametroETR.getAceiteList());
            return parametroETR;
        }
        return null;
    }

}
