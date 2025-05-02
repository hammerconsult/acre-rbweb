package br.com.webpublico.singletons;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by wellington on 06/11/17.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonEntidade implements Serializable {

    private Set<Class<?>> entidades;
    private Reflections reflections;

    @PostConstruct
    public void init() {
        reflections = new Reflections("br.com.webpublico");
        entidades = reflections.getTypesAnnotatedWith(Entity.class);
    }

    public Set<Class> classesQueExtendem(Class classe){
        return reflections.getSubTypesOf(classe);
    }

    public String buscarNomeDoCasoDeUsoPeloNomeDaTabela(String nomeTabela) {
        Class<?> entidade = buscarEntidadePeloNomeTabela(nomeTabela);
        if (entidade != null && entidade.isAnnotationPresent(Etiqueta.class)) {
            return entidade.getAnnotation(Etiqueta.class).value();
        }
        return StringUtils.capitalize(nomeTabela.toLowerCase().replace("_", " "));
    }

    public Class<?> buscarEntidadePeloNomeTabela(String nomeTabela) {
        for (Class<?> entidade : entidades) {
            if (entidade.getSimpleName().toLowerCase().equals(nomeTabela.toLowerCase())) {
                return entidade;
            } else if (entidade.isAnnotationPresent(Table.class)) {
                Table table = entidade.getAnnotation(Table.class);
                if (table.name().toLowerCase().equals(nomeTabela.toLowerCase())) {
                    return entidade;
                }
            }
        }
        return null;
    }
}
