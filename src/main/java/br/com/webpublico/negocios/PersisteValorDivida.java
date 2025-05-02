package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CalculoISS;
import br.com.webpublico.entidades.EfetivacaoIssFixoGeral;
import br.com.webpublico.entidades.ValorDivida;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 01/08/13
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PersisteValorDivida {
    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    protected GeraValorDividaISS geraValorDividaISS;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvar(ValorDivida valordivida) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            geraValorDividaISS.salvaValorDivida(valordivida);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
     public EfetivacaoIssFixoGeral salvarEfetivacao(EfetivacaoIssFixoGeral efetivacao) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            efetivacao = em.merge(efetivacao);
            em.flush();
            userTransaction.commit();
            return efetivacao;
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }

        return efetivacao;
    }
}
