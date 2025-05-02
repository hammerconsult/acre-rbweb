/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Localizacao;
import br.com.webpublico.entidades.PontoComercial;
import br.com.webpublico.enums.SituacaoContratoCEASA;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class PontoComercialFacade extends AbstractFacade<PontoComercial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PontoComercialFacade() {
        super(PontoComercial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean JaExisteBoxNaLocalizacao(PontoComercial ponto) {
        String hql = " select p from PontoComercial p where p.numeroBox = :numeroBox and p.localizacao = :localizacao";
        if (ponto.getId() != null) {
            hql += " and p != :p";
        }
        Query q = em.createQuery(hql);
        q.setParameter("numeroBox", ponto.getNumeroBox());
        q.setParameter("localizacao", ponto.getLocalizacao());
        if (ponto.getId() != null) {
            q.setParameter("p", ponto);
        }
        return !q.getResultList().isEmpty();
    }

    public List<PontoComercial> listaPorLocalizacao(Localizacao localizacao, String parte) {
        String hql = "select p from PontoComercial p where p.localizacao = :localizacao and p.numeroBox like :parte";

        Query q = em.createQuery(hql);
        q.setParameter("localizacao", localizacao);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();

    }

    public List<PontoComercial> buscarPontosQueNaoEstaoEmContratoRendas(Localizacao localizacao, String parte) {
        if (localizacao != null) {
            String sql = ("select pt.* from pontocomercial pt" +
                " where not exists(select pc.id from ptocomercialcontratorendas pc" +
                "                 inner join contratorendaspatrimoniais contrato on contrato.id = pc.contratorendaspatrimoniais_id" +
                "                 where pc.pontocomercial_id = pt.id and contrato.situacaocontrato = :situacaoContrato)" +
                " and pt.localizacao_id = :localizacao and pt.numeroBox like :parte");
            Query q = em.createNativeQuery(sql, PontoComercial.class);
            q.setParameter("localizacao", localizacao.getId());
            q.setParameter("parte", "%" + parte + "%");
            q.setParameter("situacaoContrato", SituacaoContratoRendasPatrimoniais.ATIVO.name());
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) return resultList;
        }
        return Lists.newArrayList();
    }

    public List<PontoComercial> buscarPontosQueNaoEstaoEmContratoCeasa(Localizacao localizacao, String parte) {
        if (localizacao != null) {
            String sql = ("select pt.* from pontocomercial pt" +
                " where not exists(select pc.id from ptocomercialcontratoceasa pc" +
                "                 inner join contratoceasa contrato on contrato.id = pc.contratoceasa_id" +
                "                 where pc.pontocomercial_id = pt.id and contrato.situacaocontrato = 'ATIVO')" +
                " and pt.localizacao_id = :localizacao and pt.numeroBox like :parte");
            Query q = em.createNativeQuery(sql, PontoComercial.class);
            q.setParameter("localizacao", localizacao.getId());
            q.setParameter("parte", "%" + parte + "%");
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return resultList;
            }
        }
        return new ArrayList<>();
    }


    public boolean verificaPontosComercialEmContratoCeasa(PontoComercial pontoComercial) {
        String sql = "select 1 from contratoceasa ceasa inner join PTOCOMERCIALCONTRATOCEASA ptceasa on ptceasa.contratoceasa_id = ceasa.id"
            + " where ptceasa.pontocomercial_id = :filtro and ceasa.situacaocontrato <> :situacao and sysdate < add_months(ceasa.datainicio, ceasa.periodovigencia)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", pontoComercial.getId());
        q.setParameter("situacao", "'" + SituacaoContratoCEASA.ENCERRADO + "'");
        return q.getResultList().isEmpty();
    }

    @Override
    public PontoComercial recuperar(Object id) {
        PontoComercial p = em.find(PontoComercial.class, id);
        return p;
    }
}
