package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mga on 14/07/2017.
 */
@Stateless
public class DesdobramentoObrigacaoAPagarFacade extends AbstractFacade<DesdobramentoObrigacaoPagar>{

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DesdobramentoObrigacaoAPagarFacade() {
        super(DesdobramentoObrigacaoPagar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DesdobramentoObrigacaoPagar> buscarDesdobramentoObrigacaoPagar(String s, Exercicio e, List<Long> idsObrigacao) {
        String sql = " "
            + " select desd.* " +
            "    from conta c " +
            "       inner join contadespesa cd on c.id = cd.id " +
            "       inner join planodecontas plano on c.planodecontas_id = plano.id " +
            "       inner join planodecontasexercicio pe on plano.id = pe.planodedespesas_id " +
            "       inner join desdobramentoobrigacaopaga desd on desd.conta_id = c.id " +
            "         and pe.exercicio_id = :exercicio " +
            "    where c.dtype = 'ContaDespesa' " +
            "    and desd.obrigacaoapagar_id in (:idsObrigacao) " +
            "    and (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','') LIKE :codigoSemPonto or C.CODIGO LIKE :codigoNormal))" +
            "  order by c.codigo ";
        Query q = em.createNativeQuery(sql, DesdobramentoObrigacaoPagar.class);
        q.setParameter("codigoNormal", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("exercicio", e.getId());
        q.setParameter("idsObrigacao", idsObrigacao);
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<DesdobramentoObrigacaoPagar> buscarDesdobramentoObrigacaoPagarPorObrigacao(ObrigacaoAPagar obrigacaoAPagar) {
        String sql = " " +
            "   select desd.* " +
            "    from desdobramentoobrigacaopaga desd " +
            "    where desd.obrigacaoapagar_id = :idObrigacao ";
        Query q = em.createNativeQuery(sql, DesdobramentoObrigacaoPagar.class);
        q.setParameter("idObrigacao", obrigacaoAPagar.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public List<DesdobramentoObrigacaoPagar> buscarDesdobramentoObrigacaoPagarPorLiquidacao(String filtro, Liquidacao liquidacao) {
        String sql = " " +
            " select desdop.* " +
            "from desdobramentoobrigacaopaga desdop " +
            "  inner join desdobramento desd on desd.desdobramentoobrigacaopagar_id = desdop.id " +
            "  inner join conta c on c.id = desdop.conta_id " +
            "  inner join contadespesa cd on c.id = cd.id " +
            "  inner join planodecontas plano on c.planodecontas_id = plano.id " +
            "  inner join planodecontasexercicio pe on plano.id = pe.planodedespesas_id " +
            "where desd.liquidacao_id = :idLiquidacao " +
            " and (lower(c.descricao) like :filtro or (replace(c.codigo,'.','') like :codigoSemPonto or c.codigo like :filtro))";
        Query q = em.createNativeQuery(sql, DesdobramentoObrigacaoPagar.class);
        q.setParameter("idLiquidacao", liquidacao.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + filtro.replace(".", "").toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public List<DesdobramentoObrigacaoPagar> buscarDesdobramentoObrigacaoPagarPorEmpenho(String filtro, Empenho empenho) {
        String sql = " " +
            " select desdop.* " +
            "from desdobramentoobrigacaopaga desdop " +
            "  inner join desdobramentoempenho desd on desd.desdobramentoobrigacaopagar_id = desdop.id " +
            "  inner join conta c on c.id = desdop.conta_id " +
            "  inner join contadespesa cd on c.id = cd.id " +
            "  inner join planodecontas plano on c.planodecontas_id = plano.id " +
            "  inner join planodecontasexercicio pe on plano.id = pe.planodedespesas_id " +
            "where desd.empenho_id = :idEmpenho " +
            " and (lower(c.descricao) like :filtro or (replace(c.codigo,'.','') like :codigoSemPonto or c.codigo like :filtro))";
        Query q = em.createNativeQuery(sql, DesdobramentoObrigacaoPagar.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + filtro.replace(".", "").toLowerCase().trim() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }
}
