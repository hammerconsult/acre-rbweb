/*
 * Codigo gerado automaticamente em Fri Oct 07 09:24:31 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FaixaReferenciaFP;
import br.com.webpublico.entidades.ItemFaixaReferenciaFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FaixaReferenciaFPFacade extends AbstractFacade<FaixaReferenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaixaReferenciaFPFacade() {
        super(FaixaReferenciaFP.class);
    }

    @Override
    public FaixaReferenciaFP recuperar(Object id) {
        FaixaReferenciaFP faixaReferenciaFP = super.recuperar(id);
        faixaReferenciaFP.getItensFaixaReferenciaFP().size();
        return faixaReferenciaFP;
    }
}
