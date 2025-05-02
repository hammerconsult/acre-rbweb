/*
 * Codigo gerado automaticamente em Mon Aug 08 13:59:17 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.entidades;

import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ModalidadeBeneficioFPFacade extends AbstractFacade<ModalidadeBeneficioFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModalidadeBeneficioFPFacade() {
        super(ModalidadeBeneficioFP.class);
    }

    @Override
    public ModalidadeBeneficioFP recuperar(Object id) {
        ModalidadeBeneficioFP mbfp = em.find(ModalidadeBeneficioFP.class, id);
        mbfp.getBeneficios().size();
        return mbfp;
    }


}
