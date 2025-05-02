/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HoraExtra;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoHoraExtra;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Stateless
public class HoraExtraFacade extends AbstractFacade<HoraExtra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HoraExtraFacade() {
        super(HoraExtra.class);
    }

    public List<HoraExtra> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select hora from HoraExtra hora "
                + " inner join hora.contratoFP contrato "
                + " inner join contrato.matriculaFP matricula "
                + " inner join matricula.pessoa pf "
                + " where (lower(matricula.matricula) like :filtro) "
                + " or (lower(pf.nome) like :filtro) "
                + " or (lower(contrato.numero) like :filtro) "
                + " or (hora.tipoHoraExtra in (:listaTipoHoraExtra)) "
                + " or (cast(hora.totalHoras as string) like :filtro) "
                + " or ((replace('/'||'0'|| cast(hora.mes as string),'00','0') "
                + " ||'/'|| cast(hora.ano as string) like :filtro)) ";


        List<TipoHoraExtra> listaTipoHoraExtra = new ArrayList<TipoHoraExtra>();
        for (TipoHoraExtra tipo : TipoHoraExtra.values()) {
            if (tipo.getDescricao().toLowerCase().contains(s)) {
                listaTipoHoraExtra.add(tipo);
            }
        }

        if (listaTipoHoraExtra.isEmpty()) {
            listaTipoHoraExtra = null;
        }

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("listaTipoHoraExtra", listaTipoHoraExtra);

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<HoraExtra> totalHorasExtrasMesAnoPorServidor(VinculoFP ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select hora ");
        sql.append("  from HoraExtra hora");
        sql.append(" where hora.contratoFP.id = :contrato");
        sql.append("   and hora.ano = :ano");
        sql.append("   and hora.mes = :mes");

        Query q = em.createQuery(sql.toString(), HoraExtra.class);
//        dt = dt.minusMonths(1);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("contrato", ep.getId());

        return q.getResultList();
    }

    public List<HoraExtra> totalHorasExtrasPorServidor(VinculoFP ep) {
        StringBuilder sql = new StringBuilder();
        sql.append("select hora ");
        sql.append("  from HoraExtra hora");
        sql.append(" where hora.contratoFP.id = :contrato");
        sql.append("   order by hora.dataCadastro asc");
        Query q = em.createQuery(sql.toString(), HoraExtra.class);
        q.setParameter("contrato", ep.getId());
        return q.getResultList();
    }


    public BigDecimal totalHorasExtrasNoMesDoAno(VinculoFP vinculo, Integer ano, Integer mes) {
        try {
            StringBuilder hql = new StringBuilder();
            hql.append("select SUM(hora.totalHoras) ");
            hql.append("  from HoraExtra hora");
            hql.append(" where hora.contratoFP = :contrato");
            hql.append("   and hora.ano = :ano");
            hql.append("   and hora.mes = :mes");

            Query q = em.createQuery(hql.toString());

            q.setParameter("ano", ano);
            q.setParameter("mes", mes);
            q.setParameter("contrato", vinculo);


            Object obj = q.getSingleResult();
            if (obj != null) {
                if (obj instanceof Long) {
                    return new BigDecimal(((Long) obj));
                }
                else if(obj instanceof BigDecimal) {
                    return (BigDecimal) obj;
                }
            }
            return BigDecimal.ZERO;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public Integer buscarQuantidadeDeHoraExtra(Date inicio) {
        Integer total = 0;
        String hql = "select count(lancamento) from HoraExtra lancamento where lancamento.dataCadastro = :data ";

        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;

    }

    public List<HoraExtra> buscarHorasExtrasPorContrato(ContratoFP contrato) {
        String sql = " select h.* from HORAEXTRA h " +
            " where h.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, HoraExtra.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
