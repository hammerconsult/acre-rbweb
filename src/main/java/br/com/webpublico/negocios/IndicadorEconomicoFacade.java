/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.IndicadorEconomico;
import br.com.webpublico.entidades.ValorIndicadorEconomico;
import br.com.webpublico.enums.PeriodicidadeIndicador;
import br.com.webpublico.enums.TipoIndicador;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author java
 */
@Stateless
public class IndicadorEconomicoFacade extends AbstractFacade<IndicadorEconomico> {

    /**
     * Creates a new instance of indicadorEconomicoFacade
     */
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public IndicadorEconomicoFacade() {
        super(IndicadorEconomico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public IndicadorEconomico recuperar(Object id) {
        IndicadorEconomico i = em.find(IndicadorEconomico.class, id);
        i.getListaDeValorIndicador().size();
        return i;
    }

    public List<ValorIndicadorEconomico> listaValorPorIndicador(IndicadorEconomico indicador) {
        String sql = " SELECT VIE.* FROM VALORINDICADORECONOMICO VIE "
            + " INNER JOIN INDICADORECONOMICO IE ON VIE.INDICADORECONOMICO_ID = IE.ID "
            + " WHERE :INICIO BETWEEN TO_CHAR(VIE.INICIOVIGENCIA, 'DD/MM/YYYY') AND COALESCE(TO_CHAR(VIE.FIMVIGENCIA, 'DD/MM/YYYY'), :FIM) "
            + " AND IE.ID = :INDICADOR ";
        Query q = em.createNativeQuery(sql, ValorIndicadorEconomico.class);
        q.setParameter("INDICADOR", indicador.getId());
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        q.setParameter("INICIO", formato.format(new Date()));
        q.setParameter("FIM", formato.format(new Date()));
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<ValorIndicadorEconomico>) q.getResultList();
        }
    }

    public List<IndicadorEconomico> listaPorTipoIndicador(String parte, TipoIndicador tipoIndicador) {
        String consulta = " from IndicadorEconomico ie where ie.tipoIndicador = :tipoIndicador or lower(ie.nome) like :parte or lower(ie.sigla) like :parte ";
        Query q = getEntityManager().createQuery(consulta);
        q.setParameter("tipoIndicador", tipoIndicador);
        q.setParameter("parte", parte.trim().toLowerCase());
        return q.getResultList();
    }


    public List<IndicadorEconomico> buscarIndicadorEnconomicoPorPeriodicidadeAndTipoIndicador(PeriodicidadeIndicador periodicidade, TipoIndicador tipoIndicador) {
        String consulta = " select ind.* from IndicadorEconomico ind              " +
            "                   where ind.periodicidadeindicador = :periodicidade " +
            "                   and ind.tipoIndicador = :tipoIndicador            ";
        Query q = em.createNativeQuery(consulta, IndicadorEconomico.class);
        q.setParameter("tipoIndicador", tipoIndicador.name());
        q.setParameter("periodicidade", periodicidade.name());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<IndicadorEconomico> listaIndicadores = q.getResultList();
            for (IndicadorEconomico indicador : listaIndicadores) {
                indicador.getListaDeValorIndicador().size();
            }
            return q.getResultList();
        }
    }

    public List<ValorIndicadorEconomico> burcarValorIndicadorPorIndicadorEconomico(IndicadorEconomico indicador) {
        String sql = " SELECT VIE.* FROM VALORINDICADORECONOMICO VIE "
            + " INNER JOIN INDICADORECONOMICO IE ON VIE.INDICADORECONOMICO_ID = IE.ID "
            + " where IE.ID = :INDICADOR ";
        Query q = em.createNativeQuery(sql, ValorIndicadorEconomico.class);
        q.setParameter("INDICADOR", indicador.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<ValorIndicadorEconomico>) q.getResultList();
        }
    }
}
