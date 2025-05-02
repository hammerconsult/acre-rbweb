package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonProcessoFiscalizacao implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Integer, Integer> mapaPorAno;

    private String recuperaUltimoCodigo(Integer ano) {
        return em.createNativeQuery("SELECT coalesce(max(pf.codigo), '0') FROM processofiscalizacao pf " +
                " WHERE substr(pf.codigo, 0, 4) = :ano ORDER BY id DESC").setMaxResults(1)
                .setParameter("ano", ano)
                .getSingleResult().toString();
    }

    @Lock(LockType.WRITE)
    public Integer getProximoNumero(Integer ano) {
        if (mapaPorAno == null) {
            mapaPorAno = Maps.newHashMap();
        }
        Integer codigo;
        if (mapaPorAno.containsKey(ano)) {
            codigo = mapaPorAno.get(ano);
        } else {
            String strCodigo = recuperaUltimoCodigo(ano);
            if (strCodigo.length() <= 5) {
                strCodigo = ano + Util.zerosAEsquerda(Integer.parseInt(strCodigo), 5);
            }
            codigo = Integer.parseInt(strCodigo);
        }
        codigo++;
        mapaPorAno.put(ano, codigo);
        return codigo;
    }

}
