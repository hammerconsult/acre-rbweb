package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CodigoCO;
import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.FonteDeRecursos;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CodigoCOFacade extends AbstractFacade<CodigoCO> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CodigoCOFacade() {
        super(CodigoCO.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<CodigoCO> buscarCodigosCOsPorFonteDeRecursosFiltrandoPorCodigoEDescricao(String parte, FonteDeRecursos fonteDeRecursos) {
        if (fonteDeRecursos == null) return Lists.newArrayList();
        String sql = " select cco.* " +
            " from codigoco cco " +
            "    inner join fontecodigoco fcco on fcco.codigoco_id = cco.id " +
            " where fcco.fontederecursos_id = :idFonte " +
            "   and (trim(cco.codigo) like :parte or trim(lower(cco.descricao)) like :parte)";
        Query q = em.createNativeQuery(sql, CodigoCO.class);
        q.setParameter("idFonte", fonteDeRecursos.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public boolean hasPagamentoComSubContaObrigaCodigoCOPorEmpenho(Empenho empenho) {
        if (empenho == null || empenho.getId() == null) return false;
        String sql = "select pag.* " +
            " from pagamento pag " +
            "    inner join liquidacao liq on liq.id = pag.liquidacao_id " +
            "    inner join subconta sub on sub.id = pag.subconta_id " +
            " where liq.empenho_id = :idEmpenho " +
            "   and sub.obrigarcodigoco = 1 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", empenho.getId());
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }
}
