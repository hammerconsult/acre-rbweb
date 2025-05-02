/*
 * Codigo gerado automaticamente em Tue Feb 08 13:51:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.enums.rh.cbo.TipoCBO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CBOFacade extends AbstractFacade<CBO> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CBOFacade() {
        super(CBO.class);
    }

    public List<CBO> listaFiltrandoLong(String s, String... atributos) {
        Long cod;
        try {
            cod = Long.parseLong(s);
        } catch (Exception e) {
            cod = 0l;
        }

        String hql = "from CBO cbo where "
            + " cbo.codigo = :codigo or ";
        for (String atributo : atributos) {
            hql += "lower(cbo." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", cod);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CBO> listarFiltrandoPorCargo(String parte, Cargo cargo) {
        String hql = " select cbo from CBO cbo"
            + "    where (lower(cbo.descricao) like :filtro or lower(cbo.codigo) like :filtro)"
            + "    and :cargo in elements (cbo.cargos)";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("cargo", cargo);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public CBO getCBOCargoOrdenadoPorCodigo(Cargo cargo) {
        String sql = "select cbo.* from CARGOCBO " +
            " inner join cbo on CARGOCBO.CBO_ID = cbo.id " +
            " where CARGO_ID = :idCargo and cbo.ativo = 1 order by cbo.CODIGO";
        Query q = em.createNativeQuery(sql, CBO.class);
        q.setParameter("idCargo", cargo.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (CBO) resultList.get(0);
        }
        return null;
    }

    public List<CBO> buscarCbosAtivos(String parte) {
        String hql = " select cbo from CBO cbo"
            + "    where cbo.ativo = :ativo " +
            "      and (lower(cbo.descricao) like :filtro or lower(cbo.codigo) like :filtro) ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("ativo", true);
        return q.getResultList();
    }

    public List<CBO> buscarCbosPorTipo(TipoCBO tipo) {
        String hql = " select cbo from CBO cbo"
            + "    where cbo.tipoCBO = :tipo ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public void deletarCbosSinonimos() {
        String sql = " delete from cbo where cbo.tipoCBO = :tipo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", TipoCBO.SINONIMO.name());
        q.executeUpdate();
    }

    public void updateInativarCbosPorTipo(TipoCBO tipo) {
        String sql = " update cbo " +
            " set cbo.ativo = 0 " +
            " where cbo.tipoCBO = :tipo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipo.name());
        q.executeUpdate();
    }

    public CBO buscarCboOcupacaoAtivoPorCodigo(Long codigo) {
        String hql = " select cbo from CBO cbo"
            + " where cbo.tipoCBO = :tipo " +
              "   and cbo.codigo = :codigo " +
              "   and cbo.ativo = :ativo ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", TipoCBO.OCUPACAO);
        q.setParameter("codigo", codigo);
        q.setParameter("ativo", true);
        List<CBO> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public Boolean cboVinculadoCargoVigente(Long id) {
        String hql = " select c from Cargo c"
            + "   inner join c.cbos cbo "
            + " where current_date between c.inicioVigencia and coalesce(c.finalVigencia, current_date) and "
            + "  cbo.id = :id ";
        Query q = em.createQuery(hql);
        q.setParameter("id", id);
        return !q.getResultList().isEmpty();
    }
}
