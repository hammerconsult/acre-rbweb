package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 20000)
public class SingletonGeradorCodigoTributario implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(SingletonGeradorCodigoTributario.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Class, Map<Exercicio, Long>> mapaCodigosExercicio;
    private Map<Class, Long> mapaCodigos;

    @Lock(LockType.WRITE)
    public Long getProximoNumero(Exercicio exercicio, Class classe, String campo, Integer digitos) {
        if (mapaCodigosExercicio == null) {
            mapaCodigosExercicio = Maps.newHashMap();
        }
        if (!mapaCodigosExercicio.containsKey(classe)) {
            mapaCodigosExercicio.put(classe, Maps.<Exercicio, Long>newHashMap());
        }
        Long codigo;
        if (mapaCodigosExercicio.get(classe).containsKey(exercicio)) {
            codigo = mapaCodigosExercicio.get(classe).get(exercicio);
        } else {
            codigo = ultimoNumeroMaisUm(exercicio.getAno(), classe, campo, digitos);
        }
        codigo++;
        mapaCodigosExercicio.get(classe).put(exercicio, codigo);
        return codigo;
    }

    @Lock(LockType.WRITE)
    public Long getProximoNumero(Exercicio exercicio, Class classe, String campo, String campoExercicio) {
        if (mapaCodigosExercicio == null) {
            mapaCodigosExercicio = Maps.newHashMap();
        }
        if (!mapaCodigosExercicio.containsKey(classe)) {
            mapaCodigosExercicio.put(classe, Maps.<Exercicio, Long>newHashMap());
        }
        Long codigo;
        if (mapaCodigosExercicio.get(classe).containsKey(exercicio)) {
            codigo = mapaCodigosExercicio.get(classe).get(exercicio);
        } else {
            codigo = ultimoNumeroMaisUmPorExercicio(exercicio.getId(), classe, campo, campoExercicio);
        }
        codigo++;
        mapaCodigosExercicio.get(classe).put(exercicio, codigo);
        return codigo;
    }

    private Long ultimoNumeroMaisUmPorExercicio(Long idExercicio, Class classe, String campo, String campoExercicio) {
        String sql = "select coalesce(max(" + campo + "), 0) + 1 as numero from " + classe.getSimpleName() + " where " + campoExercicio + " = :exercicio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", idExercicio);
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        String format = resultado.toString();
        return Long.valueOf(format);
    }

    private Long ultimoNumeroMaisUm(Integer exercicio, Class classe, String campo, Integer digitos) {
        String sql = "select coalesce(max(" + campo + "), 0) + 1 as numero from " + classe.getSimpleName() + " where to_char(" + campo + ") like :exercicio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio + "%");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        String format = resultado.toString();
        if (resultado.equals(BigDecimal.ONE)) {
            format = exercicio + StringUtil.cortarOuCompletarEsquerda(resultado.toString(), digitos, "0");
        }
        return Long.valueOf(format);
    }

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
        String hql = "select coalesce(max(" + campo + "),0) from " + classe.getSimpleName();
        Object obj = em.createQuery(hql).getSingleResult();
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
