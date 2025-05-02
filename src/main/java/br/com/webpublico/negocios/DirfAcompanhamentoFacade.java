/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Dirf;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DirfAcompanhamentoFacade implements Serializable {

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
    public void deleteDirf(Dirf d) {
        abrirTransacao();
        Query q = em.createQuery("delete from Dirf d where d.exercicio = :exercicio and d.entidade = :entidade");
        q.setParameter("exercicio", d.getExercicio());
        q.setParameter("entidade", d.getEntidade());
        q.executeUpdate();
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteDirfInfoComplementares(DirfVinculoFP dirfVinculoFP) {
        abrirTransacao();
        Query q = em.createQuery("delete from DirfInfoComplementares info where info.dirfVinculoFP = :dirfVinculoFP");
        q.setParameter("dirfVinculoFP", dirfVinculoFP);
        q.executeUpdate();
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteDirfPessoa(DirfVinculoFP dirfVinculoFP) {
        abrirTransacao();
        Query q = em.createQuery("delete from DirfPessoa pessoa where pessoa.dirfVinculoFP = :dirfVinculoFP");
        q.setParameter("dirfVinculoFP", dirfVinculoFP);
        q.executeUpdate();
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void deleteDirfVinculoFP(Dirf dirf) {
        abrirTransacao();
        Query q = em.createQuery("delete from DirfVinculoFP dirfVinculo where dirfVinculo.dirf = :dirf");
        q.setParameter("dirf", dirf);
        q.executeUpdate();
        fecharTransacao();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void excluirDirfContabil(Dirf d) {
        abrirTransacao();
        Query q = em.createQuery("delete from Dirf d " +
            " where d.exercicio = :exercicio " +
            " and d.entidade = :entidade " +
            " and d.tipoDirf = :tipoDirf " +
            " and d.tipoEmissaoDirf = :tipoEmissaoDirf ");
        q.setParameter("exercicio", d.getExercicio());
        q.setParameter("entidade", d.getEntidade());
        q.setParameter("tipoDirf", d.getTipoDirf());
        q.setParameter("tipoEmissaoDirf", d.getTipoEmissaoDirf());
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
