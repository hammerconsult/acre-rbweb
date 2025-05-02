package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Historico;
import br.com.webpublico.negocios.tributario.services.ServiceHistoricoCadastro;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

public class ListenerHistoricoCadastro {
    private static final Logger logger = LoggerFactory.getLogger(ListenerHistoricoCadastro.class);

    @PostPersist
    @PostUpdate
    public synchronized void inserirAuditoria(final Historico historico) {
        try {
            TransactionSynchronizationRegistry synchronizationRegistry = (TransactionSynchronizationRegistry) Util.getFacadeViaLookup("java:module/TransactionSynchronizationRegistry");
            synchronizationRegistry.registerInterposedSynchronization(new ListenerSyncronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == Status.STATUS_COMMITTED) {
                        ServiceHistoricoCadastro service = (ServiceHistoricoCadastro) Util.getSpringBeanPeloNome("serviceHistoricoCadastro");
                        service.inserirAuditoria(historico);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Erro ao inserir auditoria. ", e);
        }
    }
}
