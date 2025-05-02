package br.com.webpublico.util;

import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Munif
 */
public class EntidadeMetaData implements Serializable {

    private List<AtributoMetadata> atributos;
    private List<AtributoMetadata> atributosVisiveis;
    private List<AtributoMetadata> atributosTabelaveis;
    private Class entidade;
    private String nomeEntidade;

    public EntidadeMetaData(Class classeEntidade) {
        entidade = classeEntidade;
        atributos = new ArrayList<>();
        atributosTabelaveis = new ArrayList<>();
        atributosVisiveis = new ArrayList<>();
        for (Field f : Persistencia.getAtributos(classeEntidade)) {
            AtributoMetadata at = new AtributoMetadata(f);
            atributos.add(at);
            if (f.isAnnotationPresent(Tabelavel.class)) {
                at.setOrdem(f.getAnnotation(Tabelavel.class).ordemApresentacao());
                atributosTabelaveis.add(at);
            }
            if (!f.isAnnotationPresent(Invisivel.class)) {
                if (!f.isAnnotationPresent(Id.class)) {
                    atributosVisiveis.add(at);
                }
            }
        }
        Collections.sort(atributosTabelaveis);
    }

    public List<AtributoMetadata> getAtributos() {
        return atributos;
    }

    public List<AtributoMetadata> getAtributosTabelaveis() {
        Collections.sort(atributosTabelaveis);
        return atributosTabelaveis;
    }

    public List<AtributoMetadata> getAtributosVisiveis() {
        return atributosVisiveis;
    }

    public Class getEntidade() {
        return entidade;
    }

    public String getNomeEntidade() {
        nomeEntidade = entidade.getSimpleName();
        if (entidade.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
            br.com.webpublico.util.anotacoes.Etiqueta e = (Etiqueta) entidade.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class);
            nomeEntidade = e.value();
        }
        return nomeEntidade;
    }

    @Override
    public String toString() {
        return "EntidadeMetaData{" + "entidade=" + entidade + '}';
    }
}
