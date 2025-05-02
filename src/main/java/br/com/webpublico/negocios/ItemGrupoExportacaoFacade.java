/*
 * Codigo gerado automaticamente em Tue Jan 10 14:40:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemGrupoExportacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ItemGrupoExportacaoFacade extends AbstractFacade<ItemGrupoExportacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemGrupoExportacaoFacade() {
        super(ItemGrupoExportacao.class);
    }
}
