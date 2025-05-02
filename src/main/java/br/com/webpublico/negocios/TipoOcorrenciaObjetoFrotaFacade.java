/*
 * Codigo gerado automaticamente em Thu May 26 15:46:57 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoOcorrenciaObjetoFrota;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoOcorrenciaObjetoFrotaFacade extends AbstractFacade<TipoOcorrenciaObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoOcorrenciaObjetoFrotaFacade() {
        super(TipoOcorrenciaObjetoFrota.class);
    }
}
