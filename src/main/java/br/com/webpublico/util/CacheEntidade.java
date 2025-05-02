package br.com.webpublico.util;

import br.com.webpublico.util.anotacoes.Etiqueta;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta classe tem como objetivo armazenar as informações relativas a uma
 * entidade (classe anotada com javax.persistence.Entity) necessárias pelos
 * diversos métodos da classe Persistencia
 *
 * @author Daniel
 *         Date:   10/09/13 11:53
 *
 */
public class CacheEntidade implements Serializable {

    private static final String NOME_CAMPO_DATAREGISTRO = "dataRegistro";
    private static final String NOME_CAMPO_ATIVO = "ativo";
    private Class classe;
    private transient List<Field> atributos;
    private transient Field atributoId;
    private transient Field primeiroAtributo;
    private transient Field atributoDataRegistro;
    private transient Field atributoAtivo;
    private transient Map<Field, String> etiquetas = new HashMap<>();
    private String nomeClasse;

    private CacheEntidade() {
    }

    public static CacheEntidade getCacheEntidade(Class classe) {
        /*if (!classe.isAnnotationPresent(javax.persistence.Entity.class)) {
            return null;
        }*/
        CacheEntidade retorno = new CacheEntidade();
        retorno.classe = classe;
        if (classe.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
            retorno.nomeClasse = ((Etiqueta) classe.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class)).value();
        } else {
            retorno.nomeClasse = classe.getSimpleName();
        }
        retorno.atributos = getNonStaticEntityFieldsFromClass(classe);
        for (Field field : retorno.atributos) {
            if (!field.isAnnotationPresent(javax.persistence.GeneratedValue.class) && retorno.primeiroAtributo == null) {
                retorno.primeiroAtributo = field;
            }
            if (field.isAnnotationPresent(javax.persistence.Id.class)) {
                retorno.atributoId = field;
            }
            if (NOME_CAMPO_DATAREGISTRO.equalsIgnoreCase(field.getName())) {
                retorno.atributoDataRegistro = field;
            } else if (NOME_CAMPO_ATIVO.equalsIgnoreCase(field.getName())) {
                retorno.atributoAtivo = field;
            }
            if (field.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
                retorno.etiquetas.put(field, field.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class).value());
            } else {
                retorno.etiquetas.put(field, field.getName().substring(0, 1) + field.getName().substring(1, field.getName().length()));
            }
        }
        return retorno;
    }

    private static List<Field> getNonStaticEntityFieldsFromClass(Class classe) {
        List<Field> retorno = new ArrayList<>();
        if (classe.isAnnotationPresent(javax.persistence.Entity.class)) {
            for (Field f : classe.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    retorno.add(f);
                }
            }
        }
        if (classe.getSuperclass() != null && classe.getSuperclass().isAnnotationPresent(javax.persistence.Entity.class)) {
            retorno.addAll(getNonStaticEntityFieldsFromClass(classe.getSuperclass()));
        }
        return retorno;
    }

    public Class getClasse() {
        return this.classe;
    }

    public List<Field> getAtributos() {
        return this.atributos;
    }

    public Field getAtributoId() {
        return this.atributoId;
    }

    public Field getPrimeiroAtributo() {
        return this.primeiroAtributo;
    }

    public Field getAtributoDataRegistro() {
        return this.atributoDataRegistro;
    }

    public Field getAtributoAtivo() {
        return this.atributoAtivo;
    }

    public Map<Field, String> getEtiquetas() {
        return this.etiquetas;
    }

    public String getNomeClasse() {
        return this.nomeClasse;
    }
}
