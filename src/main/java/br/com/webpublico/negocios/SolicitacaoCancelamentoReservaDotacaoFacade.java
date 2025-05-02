package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/06/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoCancelamentoReservaDotacaoFacade extends AbstractFacade<SolicitacaoCancelamentoReservaDotacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoCancelamentoReservaDotacaoFacade() {
        super(SolicitacaoCancelamentoReservaDotacao.class);
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    @Override
    public void salvarNovo(SolicitacaoCancelamentoReservaDotacao entity) {
        entity.setNumero(singletonGeradorCodigo.getProximoCodigo(entity.getClass(), "numero"));
        super.salvarNovo(entity);
    }

    public List<SolicitacaoCancelamentoReservaDotacao> listarSolicitacaoAbertaPorNumeroEConta(String parte) {
        String sql = "select solicitacao.* from SOLCANCELARESERVADOTACAO solicitacao " +
                " inner join fontedespesaorc fonte on solicitacao.fontedespesaorc_id = fonte.id" +
                " inner join despesaorc despesa on fonte.despesaorc_id = despesa.id" +
                " inner join provisaoppadespesa provisao on despesa.provisaoppadespesa_id = provisao.id" +
                " inner join conta conta on provisao.contadedespesa_id = conta.id" +
                " where (solicitacao.numero like :numero or replace(conta.codigo,'.','') like :numero )" +
                " and solicitacao.situacao = 'ABERTA'" +
                " order by solicitacao.numero asc";
        Query consulta = em.createNativeQuery(sql, SolicitacaoCancelamentoReservaDotacao.class);
        consulta.setParameter("numero", "%" + parte.trim().replace(".","") + "%");
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }
}
