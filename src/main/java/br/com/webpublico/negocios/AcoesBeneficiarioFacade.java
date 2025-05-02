/*
 * Codigo gerado automaticamente em Mon Apr 09 21:04:03 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcoesBeneficiario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AcoesBeneficiarioFacade extends AbstractFacade<AcoesBeneficiario> {

    @EJB
    private EntidadeBeneficiariaFacade entidadeBeneficiariaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AcoesBeneficiarioFacade() {
        super(AcoesBeneficiario.class);
    }

    public EntidadeBeneficiariaFacade getEntidadeBeneficiariaFacade() {
        return entidadeBeneficiariaFacade;
    }
}
