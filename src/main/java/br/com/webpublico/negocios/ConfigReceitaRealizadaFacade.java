/*
 * Codigo gerado automaticamente em Wed Oct 10 11:09:05 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteTransporteConfiguracaoContabil;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class ConfigReceitaRealizadaFacade extends AbstractFacade<ConfigReceitaRealizada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigReceitaRealizadaFacade() {
        super(ConfigReceitaRealizada.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public Boolean verificarContaExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento cr, Date dataVigencia, OperacaoReceita operacaoReceitaRealizada) {
        String sql = "SELECT CE.*,CR.* "
            + " FROM CONFIGRECEITAREALIZADA CR "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CR.ID = CE.ID"
            + " INNER JOIN CONFIGRECREALIACONTAREC C ON C.CONFIGRECEITAREALIZADA_ID = CR.ID "
            + " INNER JOIN CONTA ON C.CONTARECEITA_ID = CONTA.ID "
            + " WHERE CONTA.ID = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CR.OPERACAORECEITAREALIZADA = :operacao ";
        if (cr.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigReceitaRealizada.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacao", operacaoReceitaRealizada.name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        if (cr.getId() != null) {
            q.setParameter("config", cr.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public TiposCredito retornaTipoCredito(Conta c) {
        String sql = "SELECT CONTARECEITA.TIPOSCREDITO "
            + " FROM CONFIGRECEITAREALIZADA CRR"
            + " INNER JOIN CONFIGRECREALIACONTAREC CRRR ON CRRR.CONFIGRECEITAREALIZADA_ID = CRR.ID"
            + " INNER JOIN CONTA ON CRRR.CONTARECEITA_ID = CONTA.ID"
            + " INNER JOIN CONTARECEITA ON CONTA.ID = CONTARECEITA.ID"
            + " where CONTA.ID = :conta";
        Query q = em.createNativeQuery(sql, TiposCredito.class);
        q.setParameter("conta", c.getId());
        return (TiposCredito) q.getSingleResult();
    }

    @Override
    public ConfigReceitaRealizada recuperar(Object id) {
        ConfigReceitaRealizada cd = em.find(ConfigReceitaRealizada.class, id);
        cd.getConfigRecRealizadaContaRecs().size();
        return cd;
    }

    private ConfigReceitaRealizada buscarEventoDesconsiderandoContaPadrao(Conta c, TipoLancamento tipoLancamento, Date data, OperacaoReceita operacaoReceitaRealizada) {
        String sql = "SELECT CE.*, CRR.* "
            + " FROM CONFIGRECEITAREALIZADA CRR"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CRR.ID = CE.ID"
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " INNER JOIN CONFIGRECREALIACONTAREC CC ON CC.CONFIGRECEITAREALIZADA_ID = CRR.ID"
            + " WHERE CC.CONTARECEITA_ID = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CRR.OPERACAORECEITAREALIZADA = :operacao";

        Query q = em.createNativeQuery(sql, ConfigReceitaRealizada.class);

        q.setParameter("conta", c.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacao", operacaoReceitaRealizada.name());
        return (ConfigReceitaRealizada) q.getSingleResult();
    }

    public ConfigReceitaRealizada buscarEventoPorContaReceita(Conta c, TipoLancamento tipoLancamento, Date data, OperacaoReceita operacaoReceitaRealizada) throws ExcecaoNegocioGenerica {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(c.getId(), " A conta de Receita esta vazia ");
        Preconditions.checkNotNull(operacaoReceitaRealizada, " A operação está vazia.");
        try {
            return buscarEventoDesconsiderandoContaPadrao(c, tipoLancamento, data, operacaoReceitaRealizada);
        } catch (NoResultException nr) {
            msgErro = "Evento Contábil não encontrado para a Conta " + c + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + "; Operação:  " + operacaoReceitaRealizada.getDescricao() + " na data " + DataUtil.getDataFormatada(data) + ". Verifique se o evento padrão para as contas de receitas, já esta cadastrado.";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (NonUniqueResultException nu) {
            msgErro = "Foi encontrado mais de um Evento com a Conta " + c + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + "; Data " + DataUtil.getDataFormatada(data) + ". Erro: " + nu.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ConfigReceitaRealizada recuperarConfigReceitaRealizada(ConfigReceitaRealizada configOrigem, ConfigReceitaRealizada novaConfig, Conta contaDestino) {
        try {
            return buscarEventoDesconsiderandoContaPadrao(contaDestino, configOrigem.getTipoLancamento(), novaConfig.getInicioVigencia(), novaConfig.getOperacaoReceitaRealizada());
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void verifcaAlteracoesEvento(ConfigReceitaRealizada configRecRealizadaNaoAlterado, ConfigReceitaRealizada crr) {

        if (crr.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configRecRealizadaNaoAlterado.getTipoLancamento().equals(crr.getTipoLancamento())) {
            alterou = true;
        }
        for (ConfigRecRealizadaContaRec configRecRealizadaContaRec : crr.getConfigRecRealizadaContaRecs()) {
            if (!configRecRealizadaNaoAlterado.getConfigRecRealizadaContaRecs().contains(configRecRealizadaContaRec)) {
                alterou = true;
            }
        }
        if (!configRecRealizadaNaoAlterado.getInicioVigencia().equals(crr.getInicioVigencia())) {
            alterou = true;
        }
        if (!configRecRealizadaNaoAlterado.getEventoContabil().equals(crr.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(crr.getEventoContabil(), crr.getId(), crr.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configRecRealizadaNaoAlterado.getEventoContabil(), configRecRealizadaNaoAlterado.getId(), configRecRealizadaNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(crr.getEventoContabil(), crr.getId(), crr.getClass().getSimpleName());
        }

    }

    public void meuSalvar(ConfigReceitaRealizada configRecRealizadaNaoAlterado, ConfigReceitaRealizada cr) {
        verifcaAlteracoesEvento(configRecRealizadaNaoAlterado, cr);
        if (cr.getId() == null) {
            salvarNovo(cr);
        } else {
            salvar(cr);
        }
    }

    public void encerrarVigencia(ConfigReceitaRealizada entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
