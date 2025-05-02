/*
 * Codigo gerado automaticamente em Fri Nov 18 15:19:15 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAcaoPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TipoAcaoPPAFacade extends AbstractFacade<TipoAcaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAcaoPPAFacade() {
        super(TipoAcaoPPA.class);
    }

    public boolean validaCodigoRepetido(TipoAcaoPPA tipoAcaoPPA) throws ExcecaoNegocioGenerica {
        try {
            String sql = "select * from tipoacaoppa where codigo = :codigo";
            if (tipoAcaoPPA.getId() != null) {
                sql += " and id <> :id";
            }
            Query consulta = em.createNativeQuery(sql);
            consulta.setParameter("codigo", tipoAcaoPPA.getCodigo());
            if (tipoAcaoPPA.getId() != null) {
                consulta.setParameter("id", tipoAcaoPPA.getId());
            }
            if (consulta.getResultList().isEmpty()) {
                return true;
            }
            return false;
        } catch (NonUniqueResultException ex) {
            return false;
        }
    }
}
