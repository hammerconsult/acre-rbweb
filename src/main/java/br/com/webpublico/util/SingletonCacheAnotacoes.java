package br.com.webpublico.util;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

/**
 * @author Daniel
 * Date:   10/09/13 14:21
 */

public class SingletonCacheAnotacoes {

    private SingletonCacheAnotacoes() {
    }

    private static HashMap<Class, List<Class>> cache = new HashMap<>();

    public static Boolean buscaDoCache(Class classe, Class anotacao) {
        List<Class> anotacoes = cache.get(classe);
        if (anotacoes == null) {
            anotacoes = Lists.newArrayList();
            Class classeAtual = classe;
            while (true) {
                if (classeAtual.isAnnotationPresent(anotacao)) {
                    anotacoes.add(anotacao);
                }
                classeAtual = classeAtual.getSuperclass();
                if (classeAtual.equals(Object.class)) {
                    break;
                }
            }
            incluiNoCache(classe, anotacoes);
        }
        return anotacoes.contains(anotacao);
    }

    private static synchronized void incluiNoCache(Class chave, List<Class> anotacoes) {
        cache.put(chave, anotacoes);
    }
}
