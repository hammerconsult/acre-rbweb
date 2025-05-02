package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ValorDivida;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 * Created with IntelliJ IDEA.
 * User: scarpini
 * Date: 08/05/14
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PersisteIsencaoIptu {
    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvar(Object entity) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.merge(entity);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }
}
