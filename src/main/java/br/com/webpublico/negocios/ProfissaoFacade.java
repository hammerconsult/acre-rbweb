package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Profissao;
import br.com.webpublico.entidades.Quadra;
import br.com.webpublico.entidades.TipoAutonomo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 03/03/14
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ProfissaoFacade extends AbstractFacade<Profissao> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ProfissaoFacade() {
        super(Profissao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long retornaUltimoCodigo() {
        Query q = em.createNativeQuery("select max(codigo) from profissao");
        if (q.getResultList().get(0) == null) {
            return 1l;
        }
        return ((BigDecimal) q.getResultList().get(0)).longValue() + 1l;
    }

    public Boolean existeCodigo(Long id, Long codigo) {
        StringBuilder sql = new StringBuilder("select codigo from profissao where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }

    public Boolean existeDescricao(Long id, String descricao) {
        StringBuilder sql = new StringBuilder("select codigo from profissao where upper(replace(descricao,' ', '')) = :descricao");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("descricao", descricao.trim().replaceAll(" ", "").toUpperCase());
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    public List<Profissao> lista() {
        String hql = "from Profissao profissao order by profissao.descricao";
        Query q = em.createQuery(hql);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }
}
