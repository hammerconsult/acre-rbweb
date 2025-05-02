/*
 * Codigo gerado automaticamente em Thu Apr 05 09:14:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OcorrenciaConvenioDesp;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OcorrenciaConvenioDespFacade extends AbstractFacade<OcorrenciaConvenioDesp> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcorrenciaConvenioDespFacade() {
        super(OcorrenciaConvenioDesp.class);
    }
}
