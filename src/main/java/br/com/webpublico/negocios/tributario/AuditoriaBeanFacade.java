package br.com.webpublico.negocios.tributario;

import br.com.webpublico.util.AssistenteBarraProgresso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.Serializable;

/**
 * Created by wellington on 18/04/2017.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class AuditoriaBeanFacade implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaBeanFacade.class);

    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeInsertOrUpdate(String insertOrUpdate) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.createNativeQuery(insertOrUpdate).executeUpdate();
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            logger.debug("Erro ao realizar inserirOrUpdate [{}]", e);
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                logger.error("Erro ao dar rollback", e1);
            }
        }
    }


    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void criarRevisaoInicial() {
        Query q = em.createNativeQuery("select 1 from revisaoauditoria where id = 1 ");
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            try {
                userTransaction = context.getUserTransaction();
                userTransaction.begin();
                em.createNativeQuery(" insert into revisaoauditoria(id, datahora, ip, usuario) values(1, '01/01/1980', 'localhost', 'WebPúblico (Migração de Dados)') ").executeUpdate();
                em.flush();
                userTransaction.commit();
            } catch (Exception e) {
                logger.debug("Erro ao inserir revisao auditoria [{}]", e);
                try {
                    userTransaction.rollback();
                } catch (SystemException e1) {
                    logger.error("Erro ao dar rollback", e1);
                }
            }
        }
    }
}
