package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteTransporteConfiguracaoContabil;
import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author Fabio
 */
@Stateless
public class ConfigCreditoReceberFacade extends AbstractFacade<ConfigCreditoReceber> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @EJB
    ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigCreditoReceberFacade() {
        super(ConfigCreditoReceber.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigCreditoReceber verificarConfiguracaoExistente(ConfigCreditoReceber config, Date dataVigencia) {

        String sql = " select * from ConfigCreditoReceber ccr ";
        sql += " inner join configuracaoevento ce on ccr.id = ce.id ";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and ccr.operacaoCreditoReceber = :operacao ";
        sql += " and ccr.contareceita_id = :idContaReceita  ";
        if (config.getId() != null) {
            sql += " AND CCR.ID <> :config ";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigCreditoReceber.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoCreditoReceber().name());
        q.setParameter("idContaReceita", config.getContaReceita().getId());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigCreditoReceber) q.getSingleResult();
        }
        return new ConfigCreditoReceber();
    }

    public ConfigCreditoReceber recuperarEventoContabil(CreditoReceber creditoReceber) {

        Conta conta = ((ContaReceita) creditoReceber.getReceitaLOA().getContaDeReceita());

        String sql = "SELECT C.*, CE.* "
            + " FROM CONFIGCREDITORECEBER C "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON C.ID = CE.ID "
            + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND C.OPERACAOCREDITORECEBER = :operacao "
            + " AND C.CONTARECEITA_ID = :idContaReceita";
        Query q = em.createNativeQuery(sql, ConfigCreditoReceber.class);
        q.setParameter("tipoLancamento", creditoReceber.getTipoLancamento().name());
        q.setParameter("operacao", creditoReceber.getOperacaoCreditoReceber().name());
        q.setParameter("idContaReceita", ((ContaReceita) creditoReceber.getReceitaLOA().getContaDeReceita()).getId());
        q.setParameter("data", DataUtil.getDataFormatada(creditoReceber.getDataCredito()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigCreditoReceber) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Evento Contábil não encontrado para a conta de receita: " + conta + "; lançamento: " + creditoReceber.getTipoLancamento().getDescricao() + "; operação: " + creditoReceber.getOperacaoCreditoReceber().getDescricao() + ", na data: " + DataUtil.getDataFormatada(creditoReceber.getDataCredito()));
        }
    }

    public ConfigCreditoReceber recuperarEventoContabil(Conta contaReceita, OperacaoCreditoReceber operacaoCreditoReceber, TipoLancamento tipoLancamento, Date data) {

        String sql = "SELECT C.*, CE.* "
            + " FROM CONFIGCREDITORECEBER C "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON C.ID = CE.ID "
            + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND C.OPERACAOCREDITORECEBER = :operacao "
            + " AND C.CONTARECEITA_ID = :idContaReceita";
        Query q = em.createNativeQuery(sql, ConfigCreditoReceber.class);
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacao", operacaoCreditoReceber.name());
        q.setParameter("idContaReceita", contaReceita.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigCreditoReceber) q.getSingleResult();
        } else {
            return null;
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ConfigCreditoReceber recuperarConfigCreditoReceber(ConfigCreditoReceber configOrigem, ConfigCreditoReceber novaConfig, Conta contaDestino) {
        return recuperarEventoContabil(contaDestino, novaConfig.getOperacaoCreditoReceber(), configOrigem.getTipoLancamento(), novaConfig.getInicioVigencia());
    }


    @Override
    public ConfigCreditoReceber recuperar(Object id) {
        ConfigCreditoReceber ccr = em.find(ConfigCreditoReceber.class, id);
        return ccr;
    }

    public void verificaAlteracoesEvento(ConfigCreditoReceber configCredRecNaoAlterado, ConfigCreditoReceber selecionado) {

        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configCredRecNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        if (!configCredRecNaoAlterado.getOperacaoCreditoReceber().equals(selecionado.getOperacaoCreditoReceber())) {
            alterou = true;
        }
        if (configCredRecNaoAlterado.getContaReceita() != null) {
            if (!configCredRecNaoAlterado.getContaReceita().equals(selecionado.getContaReceita())) {
                alterou = true;
            }
        }
        if (!configCredRecNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configCredRecNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configCredRecNaoAlterado.getEventoContabil(), configCredRecNaoAlterado.getId(), configCredRecNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigCreditoReceber configCreditoReceberNaoAlterado, ConfigCreditoReceber ccr) {
        verificaAlteracoesEvento(configCreditoReceberNaoAlterado, ccr);
        if (ccr.getId() == null) {
            salvarNovo(ccr);
        } else {
            salvar(ccr);
        }
    }

    public void encerrarVigencia(ConfigCreditoReceber entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public CreditoReceberFacade getCreditoReceberFacade() {
        return creditoReceberFacade;
    }
}
