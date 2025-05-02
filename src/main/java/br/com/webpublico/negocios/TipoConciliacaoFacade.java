/*
 * Codigo gerado automaticamente em Tue Nov 20 10:39:10 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoConciliacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TipoConciliacaoFacade extends AbstractFacade<TipoConciliacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoConciliacaoFacade() {
        super(TipoConciliacao.class);
    }

    public String getUltimoNumeroMaisUm() {
        String sql = "SELECT max(to_number(tipo.numero))+1 AS ultimoNumero FROM tipoconciliacao tipo";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }
}
