/*
 * Codigo gerado automaticamente em Wed Aug 24 11:02:36 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ValorBeneficioFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ValorBeneficioFPFacade extends AbstractFacade<ValorBeneficioFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValorBeneficioFPFacade() {
        super(ValorBeneficioFP.class);
    }
}
