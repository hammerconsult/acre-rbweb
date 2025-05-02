/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ContribuinteDebitoOutorgaFacade extends AbstractFacade<ContribuinteDebitoOutorga> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubvencaoProcessoFacade subvencaoProcessoFacade;

    public ContribuinteDebitoOutorgaFacade() {
        super(ContribuinteDebitoOutorga.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubvencaoProcessoFacade getSubvencaoProcessoFacade() {
        return subvencaoProcessoFacade;
    }

    public ContribuinteDebitoOutorga obterPorContribuinte(CadastroEconomico cmc, Exercicio exercicio) {
        String hql = " from ContribuinteDebitoOutorga cdo "
            + "   where cdo.cadastroEconomico = :cmc and cdo.exercicio = :exercicio";

        Query q = em.createQuery(hql);
        q.setParameter("cmc", cmc);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ContribuinteDebitoOutorga) q.getSingleResult();
        }
        return null;
    }

    public ContribuinteDebitoOutorga recuperarContribuinteCalculoOutorga(LancamentoOutorga lancamentoOutorga) {
        String hql = " select distinct cdo from ContribuinteDebitoOutorga cdo "
            + "   where cdo.cadastroEconomico = :cmc and cdo.exercicio = :exercicio";

        Query q = em.createQuery(hql);
        q.setParameter("cmc", lancamentoOutorga.getCmc());
        q.setParameter("exercicio", lancamentoOutorga.getExercicio());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ContribuinteDebitoOutorga) q.getSingleResult();
        }
        return null;
    }


    @Override
    public ContribuinteDebitoOutorga recuperar(Object id) {
        ContribuinteDebitoOutorga contribuinteDebitoOutorga = em.find(ContribuinteDebitoOutorga.class, id);
        contribuinteDebitoOutorga.getListaIpo().size();
        return contribuinteDebitoOutorga;
    }

    public ContribuinteDebitoOutorga recuperarLacancamentoPorCMC(CadastroEconomico cmc, Exercicio exercicio, Mes mes) {
        String hql = "select contruibuinte from ContribuinteDebitoOutorga contruibuinte" +
            " inner join contruibuinte.listaIpo ipo" +
            " where (contruibuinte.cadastroEconomico= :cmc and ipo.mes = :mes and contruibuinte.exercicio = :exercicio) ";
        Query q = em.createQuery(hql);
        q.setParameter("cmc", cmc);
        q.setParameter("mes", mes);
        q.setParameter("exercicio", exercicio);
        if (!q.getResultList().isEmpty()) {
            ContribuinteDebitoOutorga contribuinteDebitoOutorga = (ContribuinteDebitoOutorga) q.getResultList().get(0);
            contribuinteDebitoOutorga.getListaIpo().size();
            return contribuinteDebitoOutorga;
        } else {
            return null;
        }

    }

    public List<OutorgaIPO> buscarLancamentoOutorgaIPO(Mes mes, Exercicio exercicio, CadastroEconomico cadastroEconomico) {
        String sql = " select outorgaipo.* from outorgaipo " +
            " inner join CONTRIBUINTEDEBITOOUTORGA contruibuinte on outorgaipo.CONTRIBUINTEDEBITOOUTORGA_ID = contruibuinte.id " +
            " inner join CADASTROECONOMICO cmc on contruibuinte.CADASTROECONOMICO_ID = cmc.id " +
            " where mes = :mes " +
            " and contruibuinte.EXERCICIO_ID = :exercicio " +
            " and cmc.id = :cmc " +
            " order by outorgaipo.dataInicial";
        Query q = em.createNativeQuery(sql, OutorgaIPO.class);
        q.setParameter("mes", mes.name());
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("cmc", cadastroEconomico.getId());
        return q.getResultList();
    }
}
