/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAdmissaoFGTS;
import br.com.webpublico.entidades.TipoProvimento;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoProvimentoFacade extends AbstractFacade<TipoProvimento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoProvimentoFacade() {
        super(TipoProvimento.class);
    }

    public boolean existeCodigo(TipoProvimento tipoProvimento) {
        String hql = " from TipoProvimento tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoProvimento.getCodigo());

        List<TipoAdmissaoFGTS> lista = new ArrayList<TipoAdmissaoFGTS>();
        lista = q.getResultList();

        if (lista.contains(tipoProvimento)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public TipoProvimento recuperaTipoProvimentoPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from TipoProvimento tipo where tipo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (TipoProvimento) q.getSingleResult();
    }

    public List<TipoProvimento> buscarTipoProvimentoPorCodigoAndDescricao(String filtro) {
        String sql = " select * " +
            "            from tipoprovimento tipo " +
            "          where (to_char(tipo.codigo) like :filtro " +
            "            or lower(tipo.descricao) like :filtro) " +
            "           order by tipo.codigo";

        Query q = em.createNativeQuery(sql, TipoProvimento.class);
        q.setParameter("filtro", "%"+filtro.trim().toLowerCase()+"%");

        return q.getResultList();
    }

    public boolean existeTipoProvimentoPorCodigo(int codigo) {
        if (recuperaTipoProvimentoPorCodigo(codigo) != null) {
            return true;
        }
        return false;
    }

    public List<TipoProvimento> buscarProvimentosProgressaoAndPromocao() {
        String query = " SELECT * " +
            "            FROM TIPOPROVIMENTO " +
            "            WHERE CODIGO in (:progressao,:promocao,:enquadramentoFuncional,:nomeacao) ";
        Query q = em.createNativeQuery(query,TipoProvimento.class);
        q.setParameter("progressao",TipoProvimento.PROVIMENTO_PROGRESSAO);
        q.setParameter("promocao",TipoProvimento.PROMOCAO);
        q.setParameter("enquadramentoFuncional",TipoProvimento.ENQUADRAMENTO_FUNCIONAL);
        q.setParameter("nomeacao", TipoProvimento.NOMEACAO);
        return q.getResultList() == null ? Lists.<TipoProvimento>newArrayList() : q.getResultList();
    }
}
