package br.com.webpublico.geradores;

import br.com.webpublico.util.ClassFinder;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.util.List;

public class ProcuraLink {
    String classesProcurar[];
    private List<String> resultado;

    public ProcuraLink(String classes) {
        resultado = Lists.newArrayList();
        if (classes != null && !classes.isEmpty()) {
            classesProcurar = classes.split(",");
        }
    }

    public ProcuraLink procurar() {

        try {
            List<Class<?>> classes = listaClasses();
            for (String novaClass : classesProcurar) {
                for (Class<?> classe : classes) {
                    if (classe.getSimpleName().toLowerCase()
                            .contains(novaClass.toLowerCase())
                            && classe.isAnnotationPresent(URLMappings.class)) {
                        addMapping(classe);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return this;
    }

    private List<Class<?>> listaClasses() {
        List<Class<?>> classes = ClassFinder.find("br.com.webpublico.controlerelatorio");
        classes.addAll(ClassFinder.find("br.com.webpublico.controle"));
        return classes;
    }

    private List<Class<?>> listaClassesReflection() {
        List<Class<?>> classes = ClassFinder.findClassByReflection("br.com.webpublico.controlerelatorio");
        classes.addAll(ClassFinder.findClassByReflection("br.com.webpublico.controle"));
        return classes;
    }

    public ProcuraLink ListaTodos() {
        try {
            List<Class<?>> classes = listaClasses();
            for (Class<?> classe : classes) {
                if (classe.isAnnotationPresent(URLMappings.class)) {
                    addMapping(classe);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return this;
    }

    public ProcuraLink ListaTodosReflection() {
        try {
            List<Class<?>> classes = listaClassesReflection();
            for (Class<?> classe : classes) {
                if (classe.isAnnotationPresent(URLMappings.class)) {
                    addMapping(classe);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return this;
    }

    private void addMapping(Class<?> classe) {
        URLMappings mapp = (URLMappings) classe.getAnnotation(URLMappings.class);
        for (URLMapping url : mapp.mappings()) {
            resultado.add(url.viewId().replace("/faces", ""));
        }
    }

    public List<String> getResultado() {
        return resultado;
    }

    public String getResultadoAsString() {
        String resultado = "";
        for (String s : this.resultado) {
            resultado += s + "\n";
        }

        return resultado;
    }
}
