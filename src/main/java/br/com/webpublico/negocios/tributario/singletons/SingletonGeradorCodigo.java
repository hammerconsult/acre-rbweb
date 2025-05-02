package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonGeradorCodigo implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Class, Long> mapaCodigos;

    @Lock(LockType.WRITE)
    public Long getProximoCodigo(Class classe, String campo) {
        if (mapaCodigos == null) {
            mapaCodigos = Maps.newHashMap();
        }
        Long codigo;
        if (mapaCodigos.containsKey(classe)) {
            codigo = mapaCodigos.get(classe);
        } else {
            codigo = recuperaUltimoCodigo(classe, campo);
        }
        codigo++;
        mapaCodigos.put(classe, codigo);
        return codigo;
    }

    private Long recuperaUltimoCodigo(Class classe, String campo) {
        String sql = "select coalesce(max(cast(" + campo + " as INTEGER)),0) from " + Util.getNomeTabela(classe);
        Object obj = em.createNativeQuery(sql).getSingleResult();
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }
        if (obj instanceof String) {
            return Long.valueOf((String) obj);
        }
        try {
            return ((Number) obj).longValue();
        } catch (ClassCastException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível converter " + obj + " para Long");
        }
    }

}
