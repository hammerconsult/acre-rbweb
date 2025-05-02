/*
 * Codigo gerado automaticamente em Fri Mar 04 09:38:44 BRT 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Documento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DocumentoFacade extends AbstractFacade<Documento> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoFacade() {
        super(Documento.class);
    }
}
