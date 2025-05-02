/*
 * Codigo gerado automaticamente em Thu Nov 10 15:02:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FormulaPadraoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FormulaPadraoFPFacade extends AbstractFacade<FormulaPadraoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormulaPadraoFPFacade() {
        super(FormulaPadraoFP.class);
    }
}
