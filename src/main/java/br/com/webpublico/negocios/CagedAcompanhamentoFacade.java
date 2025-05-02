/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Caged;
import br.com.webpublico.entidades.Sefip;
import br.com.webpublico.entidades.SefipFolhaDePagamento;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.Serializable;

/**
 * @author Claudio
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CagedAcompanhamentoFacade implements Serializable {

    @Resource
    SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarNovo(Object obj) {
        abrirTransacao();
        em.persist(obj);
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object salvar(Object obj) {
        abrirTransacao();
        obj = em.merge(obj);
        fecharTransacao();
        return obj;
    }


    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteCaged(Caged c) {
        abrirTransacao();
        Query q = em.createQuery("delete from Caged c where c.mes = :mes and c.exercicio = :exercicio and c.entidade = :ent");
        q.setParameter("mes", c.getMes());
        q.setParameter("exercicio", c.getExercicio());
        q.setParameter("ent", c.getEntidade());
        q.executeUpdate();
        fecharTransacao();
    }



    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void abrirTransacao() {
        userTransaction = context.getUserTransaction();
        try {
            userTransaction.begin();
        } catch (NotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void fecharTransacao() {
        em.flush();
        try {
            userTransaction.commit();
        } catch (RollbackException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (HeuristicMixedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (HeuristicRollbackException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            context.setRollbackOnly();
        }
    }
}
