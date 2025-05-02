package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractExecutor<T> implements Callable<T>, Runnable, Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractExecutor.class);

    private T assistente;

    protected T executeInTransaction(T assistente) {
        return null;
    }

    protected void executeInTransactionWithoutResult(T assistente) {
    }

    public void executeFutureWithoutResult() {
        executeFutureWithoutResult(null);
    }

    public void executeFutureWithoutResult(T assistente) {
        this.assistente = assistente;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            executorService.submit((Runnable) this);
        } finally {
            executorService.shutdown();
        }
    }

    public Future<T> executeFuture(T assistente) {
        this.assistente = assistente;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            Future<T> submit = executorService.submit((Callable<T>) this);
            return submit;
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public void run() {
        TransactionTemplate transactionTemplate = Util.recuperarSpringTransaction(Propagation.REQUIRES_NEW, 36000);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    executeInTransactionWithoutResult(AbstractExecutor.this.assistente);
                } catch (Exception e) {
                    logger.error("Erro ao executar transacao sem retorno. Efetuando rollback...", e);
                    transactionStatus.setRollbackOnly();
                }
            }
        });
    }

    @Override
    public T call() {
        TransactionTemplate transactionTemplate = Util.recuperarSpringTransaction(Propagation.REQUIRES_NEW, 36000);
        return transactionTemplate.execute(new TransactionCallback<T>() {
            @Override
            public T doInTransaction(TransactionStatus transactionStatus) throws TransactionException {
                try {
                    return executeInTransaction(AbstractExecutor.this.assistente);
                } catch (Exception e) {
                    logger.error("Erro ao executar transacao com retorno. Efetuando rollback...", e);
                    transactionStatus.setRollbackOnly();
                }
                return null;
            }
        });
    }
}
