package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoRegime;
import br.com.webpublico.entidades.rh.ParametroFerias;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class ParametroFeriasFacade extends AbstractFacade<ParametroFerias> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroFeriasFacade() {
        super(ParametroFerias.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroFerias recuperarVigentePorTipoRegime(Date data, TipoRegime tipoRegime) {
        String hql = "select p from ParametroFerias p " +
            " where :dataVigencia between p.inicioVigencia and coalesce(p.finalVigencia, :dataVigencia)" +
            " and tipoRegime.codigo = :tipo " +
            " order by p.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("dataVigencia", data);
        q.setParameter("tipo", tipoRegime.getCodigo());
        List<ParametroFerias> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }

        return null;
    }
}
