/*
 * Codigo gerado automaticamente em Thu Mar 31 11:57:45 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Funcao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FuncaoFacade extends AbstractFacade<Funcao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FuncaoFacade() {
        super(Funcao.class);
    }

    public List<Funcao> listaFiltrandoFuncao(String parte) {
        String sql = "SELECT f.* "
                + "FROM FUNCAO F "
                + "WHERE((LOWER(F.CODIGO) LIKE :parte) OR (LOWER(F.DESCRICAO) LIKE :parte)) "
                + "ORDER BY CODIGO ";
        Query q = em.createNativeQuery(sql, Funcao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();

    }


    public boolean validaCodigoIgual(Funcao funcao) {
        String sql = "select * from funcao where codigo = :codigo";
        if (funcao.getId() != null) {
            sql += " and id <> :id";
        }
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("codigo", funcao.getCodigo());
        if (funcao.getId() != null) {
            consulta.setParameter("id", funcao.getId());
        }
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public Funcao buscarFuncaoPorCodigo(String codigo) {
        String sql = "SELECT f.* "
            + "FROM FUNCAO F "
            + "WHERE F.CODIGO = :codigo "
            + "ORDER BY CODIGO ";
        Query q = em.createNativeQuery(sql, Funcao.class);
        q.setParameter("codigo",codigo);
        q.setMaxResults(1);
        List<Funcao> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }
}
