/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OperacaoConciliacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class OperacaoConciliacaoFacade extends AbstractFacade<OperacaoConciliacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public OperacaoConciliacaoFacade() {
        super(OperacaoConciliacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long retornaUltimoCodigoLong() {
        Long num;
        String sql = " SELECT max(coalesce(obj.numero,0)) FROM OperacaoConciliacao obj ";
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

    public List<OperacaoConciliacao> completaOperacaoPorCodigoDescricao(String parte) {
        Query consulta = em.createQuery("select o from OperacaoConciliacao o where lower(o.descricao) like :parte or o.numero like :parte");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }
}
