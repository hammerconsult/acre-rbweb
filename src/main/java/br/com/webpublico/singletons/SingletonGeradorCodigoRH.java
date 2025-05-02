package br.com.webpublico.singletons;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by carlos on 18/06/15.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonGeradorCodigoRH implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private String codigo;

    @Lock(LockType.READ)
    public Integer gerarCodigoExame() {
        String sql = " select COALESCE (max(e.codigo), 0) from exame e ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public Integer gerarCodigoAreaFormacao() {
        String sql = " select COALESCE (max(CAST(a.codigo AS NUMBER)), 0) from areaFormacao a ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public Integer gerarCodigoMetodoCientifico() {
        String sql = " select COALESCE (max(CAST(m.codigo AS NUMBER)), 0) from metodoCientifico m ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public Integer gerarCodigoHabilidade() {
        String sql = " select COALESCE (max(CAST(h.codigo AS NUMBER)), 0) from habilidade h ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public Integer gerarCodigoMetodologia() {
        String sql = " select COALESCE (max(CAST(m.codigo AS NUMBER)), 0) from metodologia m ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    @Lock(LockType.READ)
    public String getProximoCodigoBaseFPPensaoAlimenticia() {
        if (codigo == null || (codigo != null && codigo.equals(""))) {
            String hql = " select max(cast(base.codigo as integer)) from BaseFP base ";
            Query q = em.createQuery(hql);
            if (q.getResultList().isEmpty()) {
                codigo = 10000 + "";
            } else {
                Integer b = (Integer) q.getResultList().get(0);
                if (b == null) {
                    return 10000 + "";
                } else {
                    b++;
                    codigo = b + "";
                }
            }
        } else {
            Integer i = Integer.parseInt(codigo);
            i++;
            codigo = i + "";
        }
        return codigo;
    }


}

