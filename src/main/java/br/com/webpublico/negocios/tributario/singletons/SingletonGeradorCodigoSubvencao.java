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
public class SingletonGeradorCodigoSubvencao implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Integer, Long> mapaNumeroProcesso;

    @Lock(LockType.WRITE)
    public Long getProximoCodigo(Date dataLancamento) {
        return buscarUltimoCodigo(dataLancamento);
    }

    private Long buscarUltimoCodigo(Date dataLancamento) {
        if (dataLancamento == null) {
            dataLancamento = new Date();
        }
        Integer ano = DataUtil.getAno(dataLancamento);
        Long codigo;
        if (mapaNumeroProcesso == null) {
            mapaNumeroProcesso = Maps.newHashMap();
        }
        if (mapaNumeroProcesso.containsKey(ano)) {
            codigo = mapaNumeroProcesso.get(ano);
        } else {
            codigo = buscarNumeroProcessoSubvencao(ano);
        }
        codigo++;
        mapaNumeroProcesso.put(ano, codigo);
        return codigo;
    }

    private Long buscarNumeroProcessoSubvencao(Integer ano) {
        Long codigo = null;
        String sql = "select coalesce(max(cast(numeroDoProcesso as number)),0) as numeroDoProcesso from SubvencaoProcesso where substr(cast(numeroDoProcesso as varchar(20)), 0, 4) = :anoLancamento";
        Object obj = em.createNativeQuery(sql).setParameter("anoLancamento", ano + "").getSingleResult();

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
