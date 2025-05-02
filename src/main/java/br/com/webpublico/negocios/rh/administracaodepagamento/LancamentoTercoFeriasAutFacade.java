/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.rh.administracaodepagamento.ItemReprocessamentoLancamentoTercoFeriasAutomatico;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.negocios.*;
import com.google.common.collect.Lists;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class LancamentoTercoFeriasAutFacade extends AbstractFacade<LancamentoTercoFeriasAut> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReprocessamentoLancamentoTercoFeriasAutomaticoFacade reprocessamentoLancamentoTercoFeriasAutomaticoFacade;

    public LancamentoTercoFeriasAutFacade() {
        super(LancamentoTercoFeriasAut.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoTercoFeriasAut recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(PeriodoAquisitivoFL pa) {
        Query q = em.createQuery("from LancamentoTercoFeriasAut lanc " +
            " where lanc.periodoAquisitivoFL.id = :pa ");
        q.setParameter("pa", pa.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (LancamentoTercoFeriasAut) resultList.get(0);
        }
        return null;
    }

    public List<LancamentoTercoFeriasAut> recuperaLancamentosTercoFeriasAutPorContrato(ContratoFP contrato) {
        Query q = em.createQuery("from LancamentoTercoFeriasAut lanc " +
            " where lanc.contratoFP.id = :contrato ");
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public LancamentoTercoFeriasAut recuperaLancamentosTercoFeriasAutPorContratoMesAno(ContratoFP contrato, Integer mes, Integer ano) {
        Query q = em.createQuery("from LancamentoTercoFeriasAut lanc " +
            " where lanc.contratoFP.id = :contrato " +
            " and lanc.mes = :mes " +
            " and lanc.ano = :ano " +
            " order by lanc.id desc ");
        q.setParameter("contrato", contrato.getId());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (LancamentoTercoFeriasAut) resultList.get(0);
        }
        return null;
    }

    @Override
    public void remover(LancamentoTercoFeriasAut entity) {
        ItemReprocessamentoLancamentoTercoFeriasAutomatico item = reprocessamentoLancamentoTercoFeriasAutomaticoFacade.recuperaItemReprocessamentoPorLancamentoTercoFerias(entity);
        if (item != null) {
            em.remove(item);
        }
        super.remover(entity);
    }
}
