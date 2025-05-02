/*
 * Codigo gerado automaticamente em Mon Jun 13 16:53:22 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoFiscalLiquidacao;
import br.com.webpublico.entidades.HistoricoContabil;
import br.com.webpublico.entidades.ObrigacaoAPagar;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class HistoricoContabilFacade extends AbstractFacade<HistoricoContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoContabilFacade() {
        super(HistoricoContabil.class);
    }

    @Override
    public Long retornaUltimoCodigoLong() {
        Long num;
        String sql = " SELECT max(coalesce(to_number(obj.codigo),0)) FROM HistoricoContabil obj ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();

            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public List<HistoricoContabil> buscarFiltrandoHistoricoContabil(String parte) {
        String sql = "" +
            "  select hist.* " +
            "   from historicocontabil hist " +
            "    where (hist.codigo like :parte " +
            "     or lower(hist.descricao) like :parte )";
        Query q = em.createNativeQuery(sql, HistoricoContabil.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public HistoricoContabil meuSalvar(HistoricoContabil entity) {
        return em.merge(entity);
    }
}
