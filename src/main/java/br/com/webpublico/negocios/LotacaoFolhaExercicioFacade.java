/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.LotacaoFolhaExercicio;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Usuario
 */
@Stateless
public class LotacaoFolhaExercicioFacade extends AbstractFacade<LotacaoFolhaExercicio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LotacaoFolhaExercicioFacade() {
        super(LotacaoFolhaExercicio.class);
    }

    public List<LotacaoFolhaExercicio> recuperaLotacaoFolhaExercicio(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from LotacaoFolhaExercicio lotacao");
        hql.append(" where lotacao.contratoFP = :contrato");
        hql.append(" and :dataProvimento >= lotacao.inicioVigencia");
        hql.append(" order by lotacao.inicioVigencia");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);
        List<LotacaoFolhaExercicio> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new ArrayList<LotacaoFolhaExercicio>();
        }
        return q.getResultList();
    }

    public LotacaoFolhaExercicio recuperaLotacaoFolhaExercicioVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from LotacaoFolhaExercicio lotacao");
        hql.append(" where lotacao.contratoFP = :contrato");
        hql.append(" and lotacao.inicioVigencia <= :dataProvimento ");
        hql.append(" and coalesce(lotacao.fimVigencia, :dataProvimento) >= :dataProvimento ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);
        List<LotacaoFolhaExercicio> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new LotacaoFolhaExercicio();
        }
        return lista.get(0);
    }
}
