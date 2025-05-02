/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 *
 * @author terminal1
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ScannerEntidades {

    private static final Logger logger = LoggerFactory.getLogger(ScannerEntidades.class);

    private List<Class> classesEntidade;

    public ScannerEntidades() {
        classesEntidade = new ArrayList<Class>();
    }

    public List<Class> getClassesEntidade() {
        procurarEntidade();
        return classesEntidade;
    }

    private void procurarEntidade() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlLoader = (URLClassLoader) classLoader;
        URL[] urls = urlLoader.getURLs();

        File local = null;
        local = new File("/home/" + System.getProperty("user.name") + "/NetBeansProjects/webpublico/build/web/WEB-INF/classes/br/com/webpublico/entidades");
        if (local.isDirectory()) {
            getClassesNoDiretorio(null, local);
        }
    }

    private void getClassesNoDiretorio(String superior, File local) {

        File[] files = local.listFiles();
        StringBuilder montaCaminho = null;

        for (File file : files) {
            montaCaminho = new StringBuilder();
            montaCaminho.append(superior).append(".").append(file.getName());
            String pacoteOuClasse = (superior == null ? file.getName() : montaCaminho.toString());
            if (file.isDirectory()) {
                getClassesNoDiretorio(pacoteOuClasse, file);
            } else if (file.getName().endsWith(".class")) {
                try {
                    Class c = Class.forName("br.com.webpublico.entidades." +
                            pacoteOuClasse.replace(".class", ""));

                    if (c.isAnnotationPresent(Entity.class)) {
                        classesEntidade.add(c);
                    }
                } catch (ClassNotFoundException ex) {
                    logger.error("Erro: ", ex);
                }
            }
        }
    }
}
