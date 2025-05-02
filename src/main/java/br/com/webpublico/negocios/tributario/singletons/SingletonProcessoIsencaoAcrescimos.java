package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.Exercicio;
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
public class SingletonProcessoIsencaoAcrescimos implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Exercicio, Long> mapaAnos;

    private Long recuperaUltimoCodigo(Exercicio exercicio) {
        return ((BigDecimal) em.createNativeQuery("select coalesce(max(codigo),0) from ProcessoIsencaoAcrescimos where exercicio_id = :exercicio")
                .setParameter("exercicio", exercicio.getId())
                .getSingleResult()).longValue();
    }

    @Lock(LockType.WRITE)
    public Long getProximoNumero(Exercicio exercicio) {
        if (mapaAnos == null) {
            mapaAnos = Maps.newHashMap();
        }
        Long codigo;
        if (mapaAnos.containsKey(exercicio)) {
            codigo = mapaAnos.get(exercicio);
        } else {
            codigo = recuperaUltimoCodigo(exercicio);
        }
        codigo++;
        mapaAnos.put(exercicio, codigo);
        return codigo;
    }
}
