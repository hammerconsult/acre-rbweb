package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 08:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TaxaVeiculoFacade extends AbstractFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TaxaVeiculoFacade() {
        super(TaxaVeiculo.class);
    }

    public boolean existeTaxaVeiculoCadastrada(TaxaVeiculo taxaVeiculo) {
        String hql = " select taxa from TaxaVeiculo taxa " +
            " where trim(lower(taxa.descricao)) = :descricao";
        if (taxaVeiculo.getId() != null) {
            hql += " and taxa.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", taxaVeiculo.getDescricao().trim().toLowerCase());
        if (taxaVeiculo.getId() != null) {
            q.setParameter("id", taxaVeiculo.getId());
        }
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public List<TaxaVeiculo> listarTaxaVeiculo(String parte, Boolean obrigatoria, List<Long> idNotIn) {
        String hql = " select taxa from TaxaVeiculo taxa " +
            " where (sysdate between taxa.inicioVigencia and coalesce(taxa.fimVigencia, sysdate)) " +
            " and trim(lower(taxa.descricao)) like :parte and taxa.obrigatoria = :obrigatoria ";
        if (idNotIn != null && !idNotIn.isEmpty()) {
            hql += " and id not in :idNotIn ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("obrigatoria", obrigatoria);
        if (idNotIn != null && !idNotIn.isEmpty()) {
            q.setParameter("idNotIn", idNotIn);
        }
        return q.getResultList();
    }

    public boolean hasTaxaObrigatoriaNaoSelecionada(List<Long> idNotIn, Date dataOperacao) {
        String sql = " select taxa.* from TaxaVeiculo taxa " +
            " where to_date(:data, 'dd/MM/yyyy') between trunc(taxa.inicioVigencia) and coalesce(trunc(taxa.fimVigencia), to_date(:data, 'dd/MM/yyyy')) " +
            " and taxa.obrigatoria = 1 ";
        if (idNotIn != null && !idNotIn.isEmpty()) {
            sql += " and id not in :idNotIn ";
        }
        Query q = em.createNativeQuery(sql);
        if (idNotIn != null && !idNotIn.isEmpty()) {
            q.setParameter("idNotIn", idNotIn);
        }
        q.setParameter("data", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList() != null && q.getResultList().size() > 0;
    }
}
