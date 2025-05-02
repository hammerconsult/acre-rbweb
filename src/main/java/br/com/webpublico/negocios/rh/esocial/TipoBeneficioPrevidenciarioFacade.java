package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.TipoBeneficioPrevidenciario;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoBeneficioPrevidenciarioFacade extends AbstractFacade<TipoBeneficioPrevidenciario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoBeneficioPrevidenciarioFacade() {
        super(TipoBeneficioPrevidenciario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean validarCodigoBeneficio(TipoBeneficioPrevidenciario tipoBeneficioPrev) {
        String sql = " SELECT T.ID FROM TIPOBENEFPREVIDENCIARIO T" +
                     " WHERE T.CODIGO = :CODIGO";

        if(tipoBeneficioPrev.getId() != null){
            sql += " AND T.ID <> :ID";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("CODIGO", tipoBeneficioPrev.getCodigo());
        if(tipoBeneficioPrev.getId() !=  null){
            q.setParameter("ID", tipoBeneficioPrev.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<TipoBeneficioPrevidenciario> buscarTipoBeneficioPrevidanciario(){
        String sql = " SELECT T.* FROM TIPOBENEFPREVIDENCIARIO T" +
                     " ORDER BY T.CODIGO";
        Query q = em.createNativeQuery(sql,TipoBeneficioPrevidenciario.class);
        return q.getResultList();
    }
}

