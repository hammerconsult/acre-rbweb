/*
 * Codigo gerado automaticamente em Mon Oct 24 15:04:14 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BaseCargo;
import br.com.webpublico.entidades.BasePeriodoAquisitivo;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class BaseCargoFacade extends AbstractFacade<BaseCargo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BaseCargoFacade() {
        super(BaseCargo.class);
    }

    public List<BaseCargo> buscaBasesCargosVigentes() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());//Data Atual do Sistema
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Query q = em.createQuery(" from BaseCargo bc"
                + " where :periodo >= bc.inicioVigencia "
                + " and :periodo <= coalesce(bc.finalVigencia,:periodo) ");
        q.setParameter("periodo", c.getTime());
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public BaseCargo buscaBasesCargosPorBasePeriodoAquisitivoCargo(BasePeriodoAquisitivo basePeriodoAquisitivo, Cargo cargo, Date data) {
        Query q = em.createQuery(" from BaseCargo bc" +
            "   where bc.basePeriodoAquisitivo = :basePeriodoAquisitivo " +
            "     and :data between bc.inicioVigencia and coalesce(bc.finalVigencia, :data) " +
            "     and bc.cargo = :cargo ");
        q.setParameter("cargo", cargo);
        q.setParameter("basePeriodoAquisitivo", basePeriodoAquisitivo);
        q.setParameter("data", data);
        q.setMaxResults(1);

        try {
            return (BaseCargo) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<BaseCargo> buscarBasesDoCargo(Cargo cargo, Date dataReferencia) {
        String sql = "    select bc.* from baseperiodoaquisitivo bpa" +
            " inner join basecargo bc on bc.baseperiodoaquisitivo_id = bpa.id" +
            "      where bc.cargo_id = :cargo_id" +
            "        and :data_referencia between bc.iniciovigencia and coalesce(bc.finalvigencia, :data_referencia)";
        Query q = em.createNativeQuery(sql, BaseCargo.class);
        q.setParameter("cargo_id", cargo.getId());
        q.setParameter("data_referencia", dataReferencia);
        return q.getResultList();
    }

    public BaseCargo buscarBaseCargosPorTipoPeriodoAquisitivoAndCargoAndDataReferencia(TipoPeriodoAquisitivo tipoPeriodoAquisitivo, Cargo cargo, Date data) {
        String sql = " select bc.* from basecargo bc" +
            " inner join baseperiodoaquisitivo bpa on bpa.id = bc.baseperiodoaquisitivo_id" +
            "      where bc.cargo_id = :cargo_id" +
            "        and bpa.tipoperiodoaquisitivo = :tipoperiodo" +
            "        and :data_referencia between bc.inicioVigencia and coalesce(bc.finalVigencia, :data_referencia) ";

        Query q = em.createNativeQuery(sql, BaseCargo.class);
        q.setParameter("cargo_id", cargo.getId());
        q.setParameter("tipoperiodo", tipoPeriodoAquisitivo.name());
        q.setParameter("data_referencia", data);
        q.setMaxResults(1);

        try {
            return (BaseCargo) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
