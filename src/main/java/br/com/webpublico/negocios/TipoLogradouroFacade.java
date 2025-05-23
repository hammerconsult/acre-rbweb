/*
 * Codigo gerado automaticamente em Fri Feb 11 08:07:27 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoLogradouro;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoLogradouroFacade extends AbstractFacade<TipoLogradouro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoLogradouroFacade() {
        super(TipoLogradouro.class);
    }


    public TipoLogradouro tipoLogradouroPorSigla(String sigla) {
        String sql = "SELECT tipo FROM TipoLogradouro tipo WHERE lower(tipo.sigla) = :valor";
        Query q = em.createQuery(sql);
        q.setParameter("valor", sigla.toLowerCase().trim());
        if (!q.getResultList().isEmpty()) {
            return (TipoLogradouro) q.getResultList().get(0);
        }
        return null;
    }

    public boolean existeValorCampo(String campo, String valor, Long id) {
        StringBuilder sql = new StringBuilder("SELECT * FROM TipoLogradouro WHERE lower(trim(" + campo + ")) = :valor");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("valor", valor.toLowerCase().trim());
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }
}
