package br.com.webpublico.util;


import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.reflections.Reflections;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jujuba
 * Date: 06/01/15
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class ClassFinder {

    private static final char DOT = '.';

    private static final char SLASH = '/';

    private static final String CLASS_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<Class<?>> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(DOT, SLASH);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }
        return classes;
    }

    public static List<Class<?>> findClassByReflection(String scannedPackage) {
        Reflections reflections = new Reflections(scannedPackage);
        Set<Class<? extends Object>> allClasses = reflections.getTypesAnnotatedWith(URLMappings.class);
        return Lists.newArrayList(allClasses);
    }

    private static List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + DOT + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_SUFFIX)) {
            int endIndex = resource.length() - CLASS_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }
}
