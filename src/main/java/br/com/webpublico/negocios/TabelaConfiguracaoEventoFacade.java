/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoEvento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class TabelaConfiguracaoEventoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public List filtar(String sql) {
        return em.createQuery(sql).getResultList();
    }

    public List<ConfiguracaoEvento> getListaVigentesDecrescente(ConfiguracaoEvento configuracaoEvento, Class c) {

        String sql = "select obj from " + c.getSimpleName() + " obj "
                + " where obj.fimVigencia is null or obj.fimVigencia >= :data order by id desc";
        Query q = em.createQuery(sql, configuracaoEvento.getClass());
        q.setParameter("data", new Date());
        List<ConfiguracaoEvento> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ConfiguracaoEvento>();
        }
        return lista;
    }

    public List<ConfiguracaoEvento> getListaEncerradosDecrescente(ConfiguracaoEvento configuracaoEvento, Class c, Date dataVigencia) {
        String sql = "select obj from " + c.getSimpleName() + " obj ";
        if (dataVigencia != null) {
            sql += "where obj.fimVigencia <= :data order by id desc";
        } else {
            sql += "where obj.fimVigencia is not null order by id desc";
        }
        Query q = em.createQuery(sql, configuracaoEvento.getClass());
        if (dataVigencia != null) {
            q.setParameter("data", dataVigencia);
        }
        List<ConfiguracaoEvento> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista;
        }
        return new ArrayList<ConfiguracaoEvento>();
    }

    public List<ConfiguracaoEvento> getListaEncerradosDecrescente(ConfiguracaoEvento configuracaoEvento, Class c, Date dataInicio, Date dataFim) {
        String sql = "select obj from " + c.getSimpleName() + " obj "
                + "where 1=1";
        if (dataFim != null) {
            sql += "  and obj.fimVigencia <= :dataFim ";
        }
        if (dataInicio != null) {
            sql += "  and obj.inicioVigencia >= :dataInicio order by id desc";
        }
        Query q = em.createQuery(sql, configuracaoEvento.getClass());
        if (dataInicio != null) {
            q.setParameter("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            q.setParameter("dataFim", dataFim);
        }
        List<ConfiguracaoEvento> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista;
        }
        return new ArrayList<ConfiguracaoEvento>();
    }
}
