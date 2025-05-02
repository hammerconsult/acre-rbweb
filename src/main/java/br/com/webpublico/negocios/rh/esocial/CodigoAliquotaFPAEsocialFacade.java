package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.CodigoAliquotaFPAEsocial;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by William on 22/06/2018.
 */
@Stateless
public class CodigoAliquotaFPAEsocialFacade extends AbstractFacade<CodigoAliquotaFPAEsocial> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CodigoAliquotaFPAEsocialFacade() {
        super(CodigoAliquotaFPAEsocial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCodigoCadastrado(CodigoAliquotaFPAEsocial selecionado) {
        String sql = "select * from CodigoAliquotaFPAEsocial where codigo = :codigo";
        if (selecionado.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<CodigoAliquotaFPAEsocial> buscarCodigoAliquotaFPAEsocial() {
        String sql = "select * from CodigoAliquotaFPAEsocial order by codigo";
        Query q = em.createNativeQuery(sql, CodigoAliquotaFPAEsocial.class);
        return q.getResultList();
    }
}
