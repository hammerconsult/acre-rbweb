/*
 * Codigo gerado automaticamente em Tue Aug 23 01:49:40 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoMultaVeiculo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class TipoMultaVeiculoFacade extends AbstractFacade<TipoMultaVeiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoMultaVeiculoFacade() {
        super(TipoMultaVeiculo.class);
    }
}
