package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroLicencaPremio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ParametroLicencaPremioFacade extends AbstractFacade<ParametroLicencaPremio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;

    public ParametroLicencaPremioFacade() {
        super(ParametroLicencaPremio.class);
    }

    public ReferenciaFPFacade getReferenciaFPFacade() {
        return referenciaFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroLicencaPremio recuperaVigente() {
        String hql = "select p from ParametroLicencaPremio p " +
                " where :dataVigencia between p.inicioVigencia and coalesce(p.finalVigencia, :dataVigencia) " +
                " order by p.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", getSistemaFacade().getDataOperacao());
        List<ParametroLicencaPremio> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }

        return null;
    }

    public ParametroLicencaPremio recuperarVigente(Date data) {
        String hql = "select p from ParametroLicencaPremio p " +
            " where :dataVigencia between p.inicioVigencia and coalesce(p.finalVigencia, :dataVigencia) " +
            " order by p.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", data);
        List<ParametroLicencaPremio> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }

        return null;
    }
}
