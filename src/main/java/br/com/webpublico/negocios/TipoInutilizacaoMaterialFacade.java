/*
 * Codigo gerado automaticamente em Wed Feb 22 20:21:33 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoInutilizacaoMaterial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoInutilizacaoMaterialFacade extends AbstractFacade<TipoInutilizacaoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoInutilizacaoMaterialFacade() {
        super(TipoInutilizacaoMaterial.class);
    }
}
