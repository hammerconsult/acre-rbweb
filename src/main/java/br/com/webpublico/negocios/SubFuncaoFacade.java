/*
 * Codigo gerado automaticamente em Thu Mar 31 11:54:49 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.SubFuncao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SubFuncaoFacade extends AbstractFacade<SubFuncao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FuncaoFacade funcaoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubFuncaoFacade() {
        super(SubFuncao.class);
    }

    public List<SubFuncao> listaFiltrandoSubFuncao(String parte) {
        String sql = "SELECT f.* "
            + " FROM SUBFUNCAO F"
            + " WHERE((LOWER(F.CODIGO) LIKE :parte) OR (LOWER(F.DESCRICAO) LIKE :parte))"
            + " ORDER BY CODIGO";
        Query q = em.createNativeQuery(sql, SubFuncao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<SubFuncao> buscarSubFuncaoPorFuncao(String parte, Funcao funcao) {
        String sql = " SELECT f.* "
            + " FROM SUBFUNCAO F "
            + " WHERE((LOWER(F.CODIGO) LIKE :parte) OR (LOWER(F.DESCRICAO) LIKE :parte)) "
            + " AND f.funcao_id = :idFuncao "
            + " ORDER BY CODIGO ";
        Query q = em.createNativeQuery(sql, SubFuncao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idFuncao", funcao.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public boolean validaCodigoIgual(SubFuncao funcao) {
        String sql = "select * from subfuncao where codigo = :codigo";
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

    public SubFuncao buscarSubFuncaoPorCodigo(String codigo) {
        String sql = "SELECT sf.* FROM SUBFUNCAO sf WHERE trim(sf.CODIGO) LIKE :codigo ORDER BY sf.id desc ";
        Query q = em.createNativeQuery(sql, SubFuncao.class);
        q.setParameter("codigo", codigo.trim());
        List<SubFuncao> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }
}
