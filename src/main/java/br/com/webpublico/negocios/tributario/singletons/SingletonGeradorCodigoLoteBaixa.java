package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonGeradorCodigoLoteBaixa implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Integer, Long> mapaCodigoLote;


    @Lock(LockType.WRITE)
    public Long getProximoCodigo(Date dataPagamento) {
        return buscarUltimoCodigo(dataPagamento);
    }

    private Long buscarUltimoCodigo(Date dataPagamento) {
        if (dataPagamento == null) {
            dataPagamento = new Date();
        }
        Integer ano = DataUtil.getAno(dataPagamento);
        Long codigo;
        if (mapaCodigoLote == null) {
            mapaCodigoLote = Maps.newHashMap();
        }
        if (mapaCodigoLote.containsKey(ano)) {
            codigo = mapaCodigoLote.get(ano);
        } else {
            codigo = buscarCodigoLote(ano);
        }
        codigo++;
        mapaCodigoLote.put(ano, codigo);
        return codigo;
    }

    private Long buscarCodigoLote(Integer ano) {
        Long codigo = null;
        String sql = "select coalesce(max(cast(codigoLote as number)),0) as codigoLote from LoteBaixa where substr(cast(codigoLote as varchar(20)), 0, 4) = :anoPagamento";
        Object obj = em.createNativeQuery(sql).setParameter("anoPagamento", ano + "").getSingleResult();

        if (obj instanceof BigDecimal) {
            codigo = ((BigDecimal) obj).longValue();
        }
        try {
            if (codigo == null) {
                codigo = ((Number) obj).longValue();
            }
        } catch (ClassCastException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível converter " + obj + " para Long");
        }
        return codigo;
    }
}
