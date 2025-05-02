package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DesdobramentoEmpenho;
import br.com.webpublico.entidades.Empenho;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DesdobramentoEmpenhoFacade extends AbstractFacade<DesdobramentoEmpenho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DesdobramentoEmpenhoFacade() {
        super(DesdobramentoEmpenho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<DesdobramentoEmpenho> buscarDesdobramentoObrigacaoPagarPorEmpenho(String filtro, Empenho empenho) {
        String sql = " " +
            " select desd.* " +
            "from desdobramentoempenho desd " +
            "  inner join conta c on c.id = desd.conta_id " +
            "  inner join contadespesa cd on c.id = cd.id " +
            "  inner join planodecontas plano on c.planodecontas_id = plano.id " +
            "  inner join planodecontasexercicio pe on plano.id = pe.planodedespesas_id " +
            "where desd.empenho_id = :idEmpenho " +
            " and (lower(c.descricao) like :filtro or (replace(c.codigo,'.','') like :codigoSemPonto or c.codigo like :filtro))";
        Query q = em.createNativeQuery(sql, DesdobramentoEmpenho.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("codigoSemPonto", "" + filtro.replace(".", "").toLowerCase().trim() + "%");
        List<DesdobramentoEmpenho> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return Lists.newArrayList();
    }
}
