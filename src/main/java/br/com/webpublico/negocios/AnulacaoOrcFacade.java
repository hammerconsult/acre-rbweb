/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AnulacaoLiberacao;
import br.com.webpublico.entidades.AnulacaoORC;
import br.com.webpublico.entidades.AnulacaoReserva;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author venon
 */
@Stateless
public class AnulacaoOrcFacade extends AbstractFacade<AnulacaoORC> {

    @EJB
    private ReservaFonteDespesaOrcFacade reservaFonteDespesaOrcFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnulacaoOrcFacade() {
        super(AnulacaoORC.class);
    }

    public void removerReservaAnulacao(AnulacaoORC ao) {
        List<AnulacaoReserva> anuRes = recuperaAnulacaoReservaPorAnulacaoOrc(ao);
        for (AnulacaoReserva ar : anuRes) {
            em.remove(ar);
        }
    }

    public List<AnulacaoReserva> recuperaAnulacaoReservaPorAnulacaoOrc(AnulacaoORC ao) {
        String hql = "from AnulacaoReserva where anulacaoORC = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", ao);
        return q.getResultList();
    }

    public List<AnulacaoLiberacao> recuperaAnulacaoLiberacaoPorAnulacaoOrc(AnulacaoORC ao) {
        String hql = "from AnulacaoLiberacao where anulacaoORC = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", ao);
        return q.getResultList();
    }
}
