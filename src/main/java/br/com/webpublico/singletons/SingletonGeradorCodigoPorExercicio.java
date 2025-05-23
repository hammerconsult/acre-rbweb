package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import com.google.common.collect.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonGeradorCodigoPorExercicio implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Class, Map<Exercicio, Long>> mapaCodigosPorExercicio;

    @Lock(LockType.READ)
    public Long getProximoCodigoDoExercicio(Class classe, String campo, String campoExercicio, Exercicio exercicio){
        if (mapaCodigosPorExercicio == null) {
            mapaCodigosPorExercicio = Maps.newHashMap();
        }
        Long codigo;
        Map<Exercicio, Long> mapa;
        if (mapaCodigosPorExercicio.containsKey(classe)) {
            mapa = mapaCodigosPorExercicio.get(classe);
            if (mapa.containsKey(exercicio)) {
                codigo = mapa.get(exercicio);
            } else {
                mapa = Maps.newHashMap();
                codigo = buscarUltimoCodigoDoAno(classe, campo, campoExercicio, exercicio);
            }
        } else {
            mapa = Maps.newHashMap();
            codigo = buscarUltimoCodigoDoAno(classe, campo, campoExercicio, exercicio);
        }
        codigo++;
        mapa.put(exercicio, codigo);
        mapaCodigosPorExercicio.put(classe, mapa);

        return codigo;
    }

    private Long buscarUltimoCodigoDoAno(Class classe, String campo, String campoExercicio, Exercicio exercicio) {
        String hql = "select coalesce(max(" + campo + "),0) from " + classe.getSimpleName() + " where " + campoExercicio + " = :exercicio";
        Object obj = em.createQuery(hql).setParameter("exercicio", exercicio).getSingleResult();
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
