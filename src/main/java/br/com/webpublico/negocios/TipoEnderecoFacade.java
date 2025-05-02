/*
 * Codigo gerado automaticamente em Fri Feb 11 08:38:53 BRST 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.enums.TipoEndereco;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoEnderecoFacade extends AbstractFacade<TipoEndereco> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoEnderecoFacade() {
        super(TipoEndereco.class);
    }
}
