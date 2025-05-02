package br.com.webpublico.negocios.tributario.singletons;

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
public class SingletonProcesso implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Integer, Integer> mapaAnos;

    private Integer recuperaUltimoCodigo(Integer ano) {
        return ((BigDecimal) em.createNativeQuery("select coalesce(max(numero),0) from Processo where ano = :ano and coalesce(protocolo,0) = 0")
                .setParameter("ano", ano)
                .getSingleResult()).intValue();
    }

    @Lock(LockType.WRITE)
    public Integer getProximoNumero(Integer ano) {
        if (mapaAnos == null) {
            mapaAnos = Maps.newHashMap();
        }
        Integer codigo;
        if (mapaAnos.containsKey(ano)) {
            codigo = mapaAnos.get(ano);
        } else {
            codigo = recuperaUltimoCodigo(ano);
        }
        codigo++;
        mapaAnos.put(ano, codigo);
        return codigo;
    }
}
