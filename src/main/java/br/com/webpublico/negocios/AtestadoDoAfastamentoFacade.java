/*
 * Codigo gerado automaticamente em Thu Sep 22 14:45:16 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtestadoDoAfastamento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AtestadoDoAfastamentoFacade extends AbstractFacade<AtestadoDoAfastamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtestadoDoAfastamentoFacade() {
        super(AtestadoDoAfastamento.class);
    }
}
