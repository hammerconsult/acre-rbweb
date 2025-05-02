/*
 * Codigo gerado automaticamente em Fri Oct 21 11:57:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BasePeriodoAquisitivo;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class BasePeriodoAquisitivoFacade extends AbstractFacade<BasePeriodoAquisitivo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BasePeriodoAquisitivoFacade() {
        super(BasePeriodoAquisitivo.class);
    }

    public BasePeriodoAquisitivo buscaBasePeriodoAquisitivoPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select basePeriodoAquisitivo from ContratoFP contrato "
            + " inner join contrato.cargo cargo "
            + " inner join cargo.baseCargos baseCargos "
            + " inner join baseCargos.basePeriodoAquisitivo basePeriodoAquisitivo "
            + " where contrato = :parametro ");
        q.setParameter("parametro", contratoFP);
        try {
            return (BasePeriodoAquisitivo) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public BasePeriodoAquisitivo buscaBasePeriodoAquisitivoFeriasPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select basePeriodoAquisitivo from ContratoFP contrato "
            + " inner join contrato.cargo cargo "
            + " inner join cargo.baseCargos baseCargos "
            + " inner join baseCargos.basePeriodoAquisitivo basePeriodoAquisitivo "
            + " where contrato = :parametro and basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("parametro", contratoFP);
        q.setParameter("tipo", TipoPeriodoAquisitivo.FERIAS);
        try {
            return (BasePeriodoAquisitivo) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public BasePeriodoAquisitivo buscaBasePeriodoAquisitivoLicencaPremioPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select basePeriodoAquisitivo from ContratoFP contrato "
            + " inner join contrato.cargo cargo "
            + " inner join cargo.baseCargos baseCargos "
            + " inner join baseCargos.basePeriodoAquisitivo basePeriodoAquisitivo "
            + " where contrato = :parametro and basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo ");
        q.setParameter("parametro", contratoFP);
        q.setParameter("tipo", TipoPeriodoAquisitivo.LICENCA);
        try {
            return (BasePeriodoAquisitivo) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public List<BasePeriodoAquisitivo> buscaBasePeriodoAquisitivoTipoDescricao(TipoPeriodoAquisitivo tipoPeriodoAquisitivo, String descricao) {
        Query q = em.createQuery(" from BasePeriodoAquisitivo base "
            + " where lower(base.descricao) like :parametroDescricao "
            + " and tipoPeriodoAquisitivo = :parametroTipo ");
        q.setParameter("parametroDescricao", '%' + descricao.toLowerCase().trim() + '%');
        q.setParameter("parametroTipo", tipoPeriodoAquisitivo);
        return q.getResultList();
    }

    public List<BasePeriodoAquisitivo> buscarTodasBasesPeriodoAquisitivo() {
        Query q = em.createQuery(" from BasePeriodoAquisitivo base ");
        return q.getResultList();
    }

    public BasePeriodoAquisitivo buscarBasePeriodoAquisitivoPorContratoFPAndCargoAndTipo(ContratoFP contratoFP, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        Query q = em.createQuery(" select basePeriodoAquisitivo from ContratoFP contrato "
            + " inner join contrato.cargos cargo "
            + " inner join cargo.basePeriodoAquisitivo basePeriodoAquisitivo "
            + " where contrato = :contrato "
            + " and basePeriodoAquisitivo.tipoPeriodoAquisitivo = :tipo  "
            + " and cargo.cargo = :cargo ");
        q.setParameter("contrato", contratoFP);
        q.setParameter("cargo", contratoFP.getCargo());
        q.setParameter("tipo", tipoPeriodoAquisitivo);
        try {
            return (BasePeriodoAquisitivo) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
