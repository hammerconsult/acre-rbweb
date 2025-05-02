/*
 * Codigo gerado automaticamente em Thu Mar 31 11:54:43 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoObjetoCompra;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UnidadeMedidaFacade extends AbstractFacade<UnidadeMedida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadeMedidaFacade() {
        super(UnidadeMedida.class);
    }

    public List<UnidadeMedida> verificarSeExisteDescricaoOrSiglaRepetida(String sigla, String descricao, Long id) {
        String sql = " select um.* from unidademedida um "
            + "        where (lower(trim(um.sigla)) = lower(trim(:sigla)) "
            + "           or lower(trim(um.descricao)) = lower(trim(:descricao))) " +
            "           and um.ativo = :ativo ";
        sql += id != null ? "   and um.id <> :id " : "";
        Query q = em.createNativeQuery(sql, UnidadeMedida.class);
        q.setParameter("sigla", sigla.toLowerCase().trim());
        q.setParameter("descricao", descricao.toLowerCase().trim());
        q.setParameter("ativo", Boolean.TRUE);
        if (id != null) {
            q.setParameter("id", id);
        }
        return q.getResultList();
    }

    public UnidadeMedida buscarUnidadeMedidaPorDescricao(String descricao) {
        String sql = " select unidade.* from UNIDADEMEDIDA unidade " +
            " where lower(trim(unidade.descricao)) = :descricao ";
        Query q = em.createNativeQuery(sql, UnidadeMedida.class);
        q.setParameter("descricao", descricao.toLowerCase().trim());
        return (UnidadeMedida) q.getResultList().get(0);
    }

    public List<UnidadeMedida> buscarUnidadeMedidaAtiva(String parte) {
        String sql = " select um.* from unidademedida um " +
            "          where um.ativo = :ativo " +
            "          and (lower(um.descricao) like :parte or lower(um.sigla) like :parte) ";
        Query q = em.createNativeQuery(sql, UnidadeMedida.class);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();

    }

    public List<UnidadeMedida> buscarUnidadeMedidaAtiva() {
        String sql = " select um.* from unidademedida um where um.ativo = :ativo ";
        Query q = em.createNativeQuery(sql, UnidadeMedida.class);
        q.setParameter("ativo", Boolean.TRUE);
        return q.getResultList();

    }

    public List<UnidadeMedida> buscarUnidadeMedidaAtiva(TipoObjetoCompra tipoObjetoCompra) {
        String sql = " " +
            "select um.* from unidademedida um " +
            "  where um.ativo = :ativo ";
        if (tipoObjetoCompra != null && tipoObjetoCompra.isMaterialPermanente()) {
            sql += " and um.mascaraquantidade = :mascaraQuantidade " +
                "    and um.mascaravalorunitario = :mascaraValor ";
        }
        Query q = em.createNativeQuery(sql, UnidadeMedida.class);
        q.setParameter("ativo", Boolean.TRUE);
        if (tipoObjetoCompra != null && tipoObjetoCompra.isMaterialPermanente()) {
            q.setParameter("mascaraQuantidade", TipoMascaraUnidadeMedida.ZERO_CASA_DECIMAL.name());
            q.setParameter("mascaraValor", TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.name());
        }
        return q.getResultList();
    }
}
