package br.com.webpublico.negocios;

import br.com.webpublico.entidades.rh.creditodesalario.CreditoSalario;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.Serializable;

/**
 * Created by Edi on 11/05/2016.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CreditoSalarioAcompanhamentoFacade implements Serializable {

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
    public void deleteCreditoSalario(CreditoSalario creditoSalario) {
        abrirTransacao();
        Query q = em.createQuery(" delete from CreditoSalario c " +
            " where c.contaBancariaEntidade = :contaBancaria " +
            " and c.folhaDePagamento = :folhaPagamento " +
            " and c.tipoGeracaoCreditoSalario = :tipoGeracaoCreditoSalario ");
        q.setParameter("contaBancaria", creditoSalario.getContaBancariaEntidade());
        q.setParameter("folhaPagamento", creditoSalario.getFolhaDePagamento());
        q.setParameter("tipoGeracaoCreditoSalario", creditoSalario.getTipoGeracaoCreditoSalario());
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


    public void jaExisteCreditoSalarioGeradoMesmosParametros(CreditoSalario creditoSalario) {
        abrirTransacao();

        String hql = "select c from CreditoSalario c " +
            " where c.contaBancariaEntidade = :contaBancaria " +
            " and c.folhaDePagamento = :folhaPagamento " +
            " and c.tipoGeracaoCreditoSalario = :tipoGeracaoCreditoSalario ";
        Query q = em.createQuery(hql);
        q.setParameter("contaBancaria", creditoSalario.getContaBancariaEntidade());
        q.setParameter("folhaPagamento", creditoSalario.getFolhaDePagamento());
        q.setParameter("tipoGeracaoCreditoSalario", creditoSalario.getTipoGeracaoCreditoSalario());
        q.setMaxResults(1);
        try {
            CreditoSalario c = (CreditoSalario) q.getSingleResult();
            fecharTransacao();
            if (c != null) {
                throw new ExcecaoNegocioGenerica("Já existe um arquivo gerado para a conta bancária: " + c.getContaBancariaEntidade().getNumeroContaComDigitoVerificador() + " e folha: " + c.getFolhaDePagamento() + " do tipo: " + c.getTipoGeracaoCreditoSalario() + ".");
            }
            return;
        } catch (NoResultException nre) {
        }
        fecharTransacao();
    }

}
