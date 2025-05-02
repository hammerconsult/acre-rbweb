package br.com.webpublico.negocios.contabil.singleton;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonFuturesContabil implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SingletonFuturesContabil.class);
    public static final String KEY_FUTURE_ANALISE_CONTABIL = "ANALISE CONTABIL";
    private HashMap<String, List<CompletableFuture>> futures = Maps.newHashMap();

    public List<CompletableFuture> buscarFutures(String key) {
        try {
            return futures.get(key);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public void adicionarFutures(String key, CompletableFuture future) {
        if (futures == null) {
            futures = Maps.newHashMap();
        }
        if (futures.containsKey(key)) {
            this.futures.get(key).add(future);
        } else {
            List<CompletableFuture> lista = Lists.newArrayList();
            lista.add(future);
            this.futures.put(key, lista);
        }
    }

    public void removerFutures(String key, CompletableFuture future) {
        if (futures == null) {
            futures = Maps.newHashMap();
        }
        if (futures.containsKey(key)) {
            this.futures.get(key).remove(future);
        }
    }
}
