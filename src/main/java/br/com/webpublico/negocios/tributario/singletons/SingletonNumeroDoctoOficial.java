package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.entidadesauxiliares.NumeroDoctoOficial;
import br.com.webpublico.enums.TipoSequenciaDoctoOficial;
import br.com.webpublico.enums.tributario.TipoNumeroDoctoOficial;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SingletonNumeroDoctoOficial {
    private final Logger logger = LoggerFactory.getLogger(SingletonNumeroDoctoOficial.class);
    private final Map<NumeroDoctoOficial, Integer> sequenciaDoctoOficial;

    @PersistenceContext
    protected transient EntityManager em;

    public SingletonNumeroDoctoOficial() {
        this.sequenciaDoctoOficial = Maps.newHashMap();
    }

    public synchronized Integer recuperarProximoNumero(Long id, TipoDoctoOficial tipoDoctoOficial, TipoNumeroDoctoOficial tipo) {
        return recuperarProximoNumero(id, null, tipoDoctoOficial, tipo);
    }

    public synchronized Integer recuperarProximoNumero(Long id, Long idExercicio, TipoDoctoOficial tipoDoctoOficial, TipoNumeroDoctoOficial tipo) {
        Integer numero;
        NumeroDoctoOficial chave = idExercicio != null ? new NumeroDoctoOficial(id, idExercicio, tipo) : new NumeroDoctoOficial(id, tipo);
        if (sequenciaDoctoOficial.containsKey(chave)) {
            numero = sequenciaDoctoOficial.get(chave);
        } else {
            numero = recuperarSequenciaNumero(idExercicio, tipoDoctoOficial, tipo);
        }
        sequenciaDoctoOficial.put(chave, ++numero);
        return numero;
    }

    private Integer recuperarSequenciaNumero(Long idExercicio, TipoDoctoOficial tipoDoctoOficial, TipoNumeroDoctoOficial tipo) {
        try {
            String sql = " select coalesce(max(do.numero), 0) " +
                " from documentooficial do " +
                " inner join modelodoctooficial mdo on do.modelodoctooficial_id = mdo.id " +
                " inner join tipodoctooficial tdo on mdo.tipodoctooficial_id = tdo.id " +
                " inner join grupodoctooficial gdo on tdo.grupodoctooficial_id = gdo.id ";
            sql += montarComplemento(tipo);

            Query q = em.createNativeQuery(sql);
            adicionarParametros(tipo, tipoDoctoOficial, idExercicio, q);

            List<BigDecimal> numeros = q.getResultList();
            return (numeros != null && !numeros.isEmpty() && numeros.get(0) != null) ? numeros.get(0).intValue() : 0;
        } catch (Exception e) {
            logger.error("Erro ao recuperar proximo codigo do tipo {} ", tipo.getDescricao(), e);
        }
        return 0;
    }

    private String montarComplemento(TipoNumeroDoctoOficial tipo) {
        if (tipo.isExercicioGrupo()) {
            return " where gdo.id = :idGrupo and do.exercicio_id = :idExercicio and gdo.tipoSequencia = :tipoSequencia ";
        } else if (tipo.isGrupo()) {
            return " where gdo.id = :idGrupo and gdo.tipoSequencia is null ";
        } else if (tipo.isExercicioTipo()) {
            return " where tdo.id = :idTipo and do.exercicio_id = :idExercicio ";
        } else if (tipo.isTipo()) {
            return " where tdo.id = :idTipo and tdo.tipoSequencia is null ";
        }
        return "";
    }

    private void adicionarParametros(TipoNumeroDoctoOficial tipo, TipoDoctoOficial tipoDoctoOficial, Long idExercicio, Query q) {
        if (tipo.isExercicioGrupo()) {
            q.setParameter("idGrupo", tipoDoctoOficial.getGrupoDoctoOficial().getId());
            q.setParameter("idExercicio", idExercicio);
            q.setParameter("tipoSequencia", TipoSequenciaDoctoOficial.EXERCICIO.name());
        } else if (tipo.isGrupo()) {
            q.setParameter("idGrupo", tipoDoctoOficial.getGrupoDoctoOficial().getId());
        } else if (tipo.isExercicioTipo()) {
            q.setParameter("idTipo", tipoDoctoOficial.getId());
            q.setParameter("idExercicio", idExercicio);
        } else if (tipo.isTipo()) {
            q.setParameter("idTipo", tipoDoctoOficial.getId());
        }
    }
}
