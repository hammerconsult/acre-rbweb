package br.com.webpublico.negocios.contabil.execucao;

import br.com.webpublico.entidades.Desdobramento;
import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by renato on 26/09/18.
 */
@Stateless
public class DesdobramentoPagamentoFacade extends AbstractFacade<DesdobramentoPagamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DesdobramentoPagamentoFacade() {
        super(DesdobramentoPagamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DesdobramentoPagamento> buscarDesdobramentoPorPagamento(String s, Pagamento pagamento) {
        String sql = " "
            + " select de.* " +
            "    from conta c " +
            "       inner join contadespesa cd on c.id = cd.id " +
            "       inner join planodecontas plano on c.planodecontas_id = plano.id " +
            "       inner join planodecontasexercicio pe on plano.id = pe.planodedespesas_id " +
            "       inner join Desdobramento desd on desd.conta_id = c.id " +
            "       inner join DesdobramentoPagamento de on desd.id = de.desdobramento_id " +
            "    where c.dtype = 'ContaDespesa' " +
            "    and de.pagamento_id = :pagamento" +
            "    and (LOWER(C.DESCRICAO) LIKE :filtro OR (replace(C.CODIGO,'.','') LIKE :codigoSemPonto or C.CODIGO LIKE :codigoNormal))" +
            "  order by c.codigo ";
        Query q = em.createNativeQuery(sql, DesdobramentoPagamento.class);
        q.setParameter("codigoNormal", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + s.replace(".", "").toLowerCase().trim() + "%");
        q.setParameter("pagamento", pagamento.getId());
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }
}
