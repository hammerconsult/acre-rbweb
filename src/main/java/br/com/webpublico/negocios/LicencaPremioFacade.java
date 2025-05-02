/*
 * Codigo gerado automaticamente em Tue Apr 10 15:00:40 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LicencaPremio;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class LicencaPremioFacade extends AbstractFacade<LicencaPremio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicencaPremioFacade() {
        super(LicencaPremio.class);
    }

    public boolean existeLicencaPremioPorBase(LicencaPremio licencaPremio) {
        String hql = " from LicencaPremio licenca "
                + " where licenca.basePeriodoAquisitivo = :parametroBase "
                + " and (:inicioVigencia between licenca.inicioVigencia and coalesce(licenca.finalVigencia,:inicioVigencia) ";

        if (licencaPremio.getFinalVigencia() != null) {
            hql += " or :finalVigencia between licenca.inicioVigencia and coalesce(licenca.finalVigencia,:finalVigencia) ) ";
        } else {
            hql += " ) ";
        }

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroBase", licencaPremio.getBasePeriodoAquisitivo());
        q.setParameter("inicioVigencia", Util.getDataHoraMinutoSegundoZerado(licencaPremio.getInicioVigencia()));
        if (licencaPremio.getFinalVigencia() != null) {
            q.setParameter("finalVigencia", Util.getDataHoraMinutoSegundoZerado(licencaPremio.getFinalVigencia()));
        }

        List<LicencaPremio> lista = new ArrayList<LicencaPremio>();
        lista = q.getResultList();

        if (lista.contains(licencaPremio)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public boolean existeLicencaPremioPorAtoLegal(LicencaPremio licencaPremio) {
        String hql = " from LicencaPremio licenca "
                + " where licenca.atoLegal = :parametroAtoLegal "
                + " and (:inicioVigencia between licenca.inicioVigencia and coalesce(licenca.finalVigencia,:inicioVigencia) ";

        if (licencaPremio.getFinalVigencia() != null) {
            hql += " or :finalVigencia between licenca.inicioVigencia and coalesce(licenca.finalVigencia,:finalVigencia) ) ";
        } else {
            hql += " ) ";
        }

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroAtoLegal", licencaPremio.getAtoLegal());
        q.setParameter("inicioVigencia", Util.getDataHoraMinutoSegundoZerado(licencaPremio.getInicioVigencia()));
        if (licencaPremio.getFinalVigencia() != null) {
            q.setParameter("finalVigencia", Util.getDataHoraMinutoSegundoZerado(licencaPremio.getFinalVigencia()));
        }

        List<LicencaPremio> lista = new ArrayList<LicencaPremio>();
        lista = q.getResultList();

        if (lista.contains(licencaPremio)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
