package br.com.webpublico.util;

import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class AsyncExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutor.class);
    private static AsyncExecutor asyncExecutor;
    private final List<ProcessAsync> processAsyncList = Lists.newArrayList();
    private Boolean block = Boolean.FALSE;

    public <U> CompletableFuture<U> execute(DetailProcessAsync detailProcessAsync,
                                            Supplier<U> supplier) {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);

        final CompletableFuture<U> cf = new CompletableFuture<U>() {
            @Override
            public boolean complete(U value) {
                if (isDone()) return false;
                executorService.shutdownNow();
                return super.complete(value);
            }

            @Override
            public boolean completeExceptionally(Throwable ex) {
                logger.error("Erro AsyncExecutor", ex);
                if (isDone()) return false;
                executorService.shutdownNow();
                return super.completeExceptionally(ex);
            }
        };

        executorService.submit(() -> {
            try {
                SistemaFacade.threadLocalUsuario.set(detailProcessAsync.getUsuario());
                cf.complete(supplier.get());
            } catch (Throwable ex) {
                cf.completeExceptionally(ex);
            }
        });

        ProcessAsync processAsync = new ProcessAsync();
        processAsync.setDetailProcessAsync(detailProcessAsync);
        processAsync.setCompletableFuture(cf);
        addProcessAsync(processAsync);
        CompletableFuture.runAsync(() -> {
            while (!processAsync.getCompletableFuture().isDone() && !processAsync.getCompletableFuture().isCancelled()) {
            }
            removeProcessAsync(processAsync.getId());
        });
        return cf;
    }

    private synchronized Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

    public List<ProcessAsync> getProcessAsyncList() {
        return processAsyncList;
    }

    private synchronized void removeProcessAsync(String idProcess) {
        while (getBlock()) {
        }
        try {
            setBlock(Boolean.TRUE);
            processAsyncList.removeIf((item) -> item.getId().equals(idProcess));
        } finally {
            setBlock(Boolean.FALSE);
        }
    }

    private synchronized void addProcessAsync(ProcessAsync processAsync) {
        while (getBlock()) {
        }
        try {
            setBlock(Boolean.TRUE);
            processAsyncList.add(processAsync);
        } finally {
            setBlock(Boolean.FALSE);
        }
    }

    public synchronized static AsyncExecutor getInstance() {
        if (asyncExecutor == null) {
            asyncExecutor = new AsyncExecutor();
        }
        return asyncExecutor;
    }
}
