package br.com.webpublico.negocios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.annotation.Resource;

@Interceptor
public class TransactionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TransactionInterceptor.class);

    @Resource
    private TransactionSynchronizationRegistry txRegistry;

    @AroundInvoke
    public Object logTransaction(InvocationContext context) throws Exception {
        logger.debug("Iniciando método: " + context.getMethod().getName());

        if (txRegistry.getTransactionKey() != null) {
            logger.debug("Transação ativa. Key: " + txRegistry.getTransactionKey());
        } else {
            logger.debug("Nenhuma transação ativa.");
        }

        try {
            Object result = context.proceed();
            logger.debug("Método completado: " + context.getMethod().getName());
            return result;
        } catch (Exception e) {
            logger.debug("Erro no método: " + context.getMethod().getName());
            throw e;
        } finally {
            if (txRegistry.getTransactionKey() != null) {
                logger.debug("Transação encerrada. Key: " + txRegistry.getTransactionKey());
            } else {
                logger.debug("Nenhuma transação ativa.");
            }
        }
    }
}
