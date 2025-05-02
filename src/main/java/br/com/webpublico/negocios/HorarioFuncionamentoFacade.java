/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HorarioFuncionamento;
import br.com.webpublico.tributario.dto.HorarioFuncionamentoDTO;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * @author Gustavo
 */
@Stateless
public class HorarioFuncionamentoFacade extends AbstractFacade<HorarioFuncionamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HorarioFuncionamentoFacade() {
        super(HorarioFuncionamento.class);
    }

    public List<HorarioFuncionamento> listaComItens() {
        String hql = "select distinct horario from HorarioFuncionamento horario join fetch horario.itens";
        Query q = em.createQuery(hql);
        List<HorarioFuncionamento> toReturn = q.getResultList();
        Collections.sort(toReturn);
        return toReturn;
    }

    @Override
    public HorarioFuncionamento recuperar(Object id) {
        HorarioFuncionamento hf = em.find(HorarioFuncionamento.class, id);
        hf.getItens().size();
        return hf;
    }

    public boolean semDependencia(HorarioFuncionamento selecionado) {
        String hql = "select e from EconomicoCNAE e where e.horarioFuncionamento = :horario";
        Query q = em.createQuery(hql);
        q.setParameter("horario", selecionado);
        return q.getResultList().isEmpty();
    }

    public HorarioFuncionamento buscarHorarioFuncionamentoPorDescricao(String descricao) {
        Query q = em.createQuery("from HorarioFuncionamento where lower(descricao) = :descricao");
        q.setParameter("descricao", descricao.toLowerCase());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            HorarioFuncionamento horarioFuncionamento = (HorarioFuncionamento) q.getResultList().get(0);
            Hibernate.initialize(horarioFuncionamento.getItens());
            return horarioFuncionamento;
        }
        return null;
    }

    public HorarioFuncionamento buscarHorarioFuncionamentoPorItensAndDescricao(List<HorarioFuncionamentoDTO> horarios, String descricao) {
        StringBuilder sql = new StringBuilder(" select hf.* from horariofuncionamento hf " +
            " where lower(hf.descricao) = :descricao ");

        for (HorarioFuncionamentoDTO horario : horarios) {
            String diaSemana = horario.getDiaSemana().name();
            String horarioInicio = horario.getHorarioInicio();
            String horarioFim = horario.getHorarioFim();

            sql.append(" and hf.id in (select i.horariofuncionamento_id from horariofuncionamentoitem i ")
                .append(" where i.diaentrada = '").append(diaSemana).append("' and i.diasaida = '").append(diaSemana)
                .append("' and to_char(i.horarioentrada, 'hh24:mi') = '").append(horarioInicio)
                .append("' and to_char(i.horariosaida, 'hh24:mi') = '").append(horarioFim).append("' )");
        }

        Query q = em.createNativeQuery(sql.toString(), HorarioFuncionamento.class);
        q.setParameter("descricao", descricao.toLowerCase());

        List<HorarioFuncionamento> horariosFuncionamento = q.getResultList();

        return (horariosFuncionamento != null && !horariosFuncionamento.isEmpty()) ? horariosFuncionamento.get(0) : null;
    }
}
