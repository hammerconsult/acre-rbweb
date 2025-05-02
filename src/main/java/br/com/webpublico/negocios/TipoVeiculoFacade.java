/*
 * Codigo gerado automaticamente em Tue Aug 16 01:24:03 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoVeiculo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoVeiculoFacade extends AbstractFacade<TipoVeiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoVeiculoFacade() {
        super(TipoVeiculo.class);
    }

    public List<TipoVeiculo> buscarTodosVeiculos() {
        String hql = " select veiculo from TipoVeiculo veiculo ";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }
}
