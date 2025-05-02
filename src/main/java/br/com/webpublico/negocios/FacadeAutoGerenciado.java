package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CalculoIPTU;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.ValorDivida;
import com.google.common.collect.Lists;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacadeAutoGerenciado {

    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DAMFacade damFacade;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void persisteSituacaoParcela(List<SituacaoParcelaValorDivida> situacoes) throws SystemException {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            for (SituacaoParcelaValorDivida situacao : situacoes) {
                em.persist(situacao);
            }
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            userTransaction.rollback();
        }
    }

    @TransactionAttribute
    public void persisteValoresDivida(List<ValorDivida> debitos, UsuarioSistema usuario) throws SystemException {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            for (ValorDivida vd : debitos) {
                em.persist(vd);

            }
            userTransaction.commit();
            for (ValorDivida vd : debitos) {
                damFacade.geraDAM(vd, vd.getExercicio(), usuario);
            }
//            em.flush();
        } catch (Exception e) {
            e.printStackTrace();
            userTransaction.rollback();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object merge(Object obj) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            obj = em.merge(obj);
            userTransaction.commit();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List mergeList(List objs) {
        try {
            List lista = Lists.newArrayList();
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            for (Object obj : objs) {
                lista.add(em.merge(obj));
            }
            userTransaction.commit();
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public CalculoIPTU buscaCalculoIPTU(Long id) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            CalculoIPTU calculo = em.find(CalculoIPTU.class, id);
            userTransaction.commit();
            return calculo;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
