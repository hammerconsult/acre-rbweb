package br.com.webpublico.util;

import java.util.HashMap;

/**
 * @author Daniel
 *         Date:   10/09/13 14:21
 */

public class SingletonCacheEntidade {

    private SingletonCacheEntidade() {
    }

    private static HashMap<Class, CacheEntidade> cache = new HashMap<>();

    public static CacheEntidade buscaDoCache(Class classe) {
        CacheEntidade retorno = cache.get(classe);
        if (retorno == null) {
            retorno = CacheEntidade.getCacheEntidade(classe);
            incluiNoCache(classe, retorno);
        }
        return retorno;
    }

    private static synchronized void incluiNoCache(Class chave, CacheEntidade valor) {
        cache.put(chave, valor);
    }
}
