package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.GrupoBeneficioEsocial;
import br.com.webpublico.entidades.rh.esocial.TipoBeneficioEsocial;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoBeneficioEsocialFacade extends AbstractFacade<TipoBeneficioEsocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoBeneficioEsocialFacade() {
        super(TipoBeneficioEsocial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean validarCodigoBeneficio(TipoBeneficioEsocial tipoBeneficioPrev) {
        String sql = " SELECT T.ID FROM TIPOBENEFPREVIDENCIARIO T" +
            " WHERE T.CODIGO = :CODIGO";

        if (tipoBeneficioPrev.getId() != null) {
            sql += " AND T.ID <> :ID";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("CODIGO", tipoBeneficioPrev.getCodigo());
        if (tipoBeneficioPrev.getId() != null) {
            q.setParameter("ID", tipoBeneficioPrev.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<TipoBeneficioEsocial> buscarTipoBeneficioPrevidanciario() {
        String sql = " SELECT T.* FROM TIPOBENEFPREVIDENCIARIO T" +
            " ORDER BY T.CODIGO";
        Query q = em.createNativeQuery(sql, TipoBeneficioEsocial.class);
        return q.getResultList();
    }

    public List<TipoBeneficioEsocial> buscarTipoBeneficioPrevidanciarioPorGrupo(GrupoBeneficioEsocial grupo) {
        String sql = " SELECT T.* FROM TipoBeneficioEsocial T where t.grupoBeneficio = :grupo" +
            " ORDER BY T.CODIGO";
        Query q = em.createNativeQuery(sql, TipoBeneficioEsocial.class);
        q.setParameter("grupo", grupo.name());
        return q.getResultList();
    }
}

