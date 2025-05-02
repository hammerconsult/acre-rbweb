/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.FichaFinanceiraFPSimulacao;
import br.com.webpublico.entidades.rh.administracaodepagamento.FolhaDePagamentoSimulacao;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class FolhaDePagamentoSimulacaoFacade extends AbstractFacade<FolhaDePagamentoSimulacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FolhaDePagamentoSimulacaoFacade() {
        super(FolhaDePagamentoSimulacao.class);
    }

    @Override
    public FolhaDePagamentoSimulacao recuperar(Object id) {
        FolhaDePagamentoSimulacao a = em.find(FolhaDePagamentoSimulacao.class, id);
        return a;
    }

    public void efetivarFolhaDePagamento(FolhaDePagamentoSimulacao folhaDePagamento, Date dataEfetivacao) {
        folhaDePagamento.setEfetivadaEm(dataEfetivacao);
        salvar(folhaDePagamento);
    }


    public List<FolhaDePagamentoSimulacao> buscarFolhasPorMesAnoTipoAndVersao(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {

        String hql = "select fp" +
            "       from FolhaDePagamentoSimulacao fp" +
            "            inner join fp.competenciaFP c" +
            "      where fp.mes = :mes" +
            "            and fp.ano = :ano" +
            "            and fp.tipoFolhaDePagamento = :tipo" +
            " order by fp.mes, fp.ano ";
        Query q = em.createQuery(hql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipoFolhaDePagamento);
        return q.getResultList();
    }

    public List<FolhaDePagamentoSimulacao> buscarFolhaPorCompetenciaEStatus(CompetenciaFP competencia) {
        String hql = "from FolhaDePagamentoSimulacao folha where folha.competenciaFP = :competencia " +
            " and folha.competenciaFP.statusCompetencia = :statusCompetencia";
        Query q = em.createQuery(hql);

        q.setParameter("competencia", competencia);
        q.setParameter("statusCompetencia", competencia.getStatusCompetencia());
        return q.getResultList();
    }

    public List<FichaFinanceiraFPSimulacao> buscarFichaFinanceiraSimulacaPorVinculo(Mes mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {
        Query q = em.createQuery("select ficha from FolhaDePagamentoSimulacao folha"
            + " inner join folha.fichaFinanceiraFPs ficha"
            + "  "
            + " where ficha.vinculoFP = :vinculo"
            + "   and folha.ano = :ano"
            + "   and folha.mes = :mes"
            + "   and folha.tipoFolhaDePagamento in :tipo and folha.efetivadaEm is null");
        q.setParameter("vinculo", vinculo);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tipo", Arrays.asList(tipoFolhaDePagamento));
        return q.getResultList();

    }


    public void removerFichaFinanceira(FichaFinanceiraFPSimulacao ficha) {
        ficha = em.find(FichaFinanceiraFPSimulacao.class, ficha.getId());
        getEntityManager().remove(ficha);
    }
}
