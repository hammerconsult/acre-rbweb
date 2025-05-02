package br.com.webpublico.negocios;

import br.com.webpublico.entidades.VinculoFP;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 04/09/13
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class GraficosRHFacade extends AbstractFacade<VinculoFP> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public GraficosRHFacade() {
        super(VinculoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


//    public TotalServidoresCalculados recuperaDadosCalculo(Integer mes, Integer ano) {
//        String hql = "select distinct coalesce(count(ficha.vinculoFP),0) from FolhaDePagamento folha inner join " +
//                " folha.fichaFinanceiraFPs ficha where folha.ano = :ano and folha.mes = :mes ";
//
//        Query q = em.createQuery(hql);
//        q.setParameter("ano", 2013);
//        q.setParameter("mes", Mes.AGOSTO);
//        Long totalCalculadosMes = (Long) q.getSingleResult();
//
////        String totalServidores = "";
////        Query qu = em.createQuery(totalServidores);
//        Integer totalServidores = folhaDePagamentoFacade.recuperarTodasMatriculas().size();
////        TotalServidoresCalculados total = new TotalServidoresCalculados(totalServidores, totalCalculadosMes.intValue(), totalServidores - totalCalculadosMes.intValue());
//        return total;
//    }


}
