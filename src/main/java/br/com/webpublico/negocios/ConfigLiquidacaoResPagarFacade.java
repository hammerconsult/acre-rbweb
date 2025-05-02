/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigLiquidacaoResPagar;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;


@Stateless
public class ConfigLiquidacaoResPagarFacade extends AbstractFacade<ConfigLiquidacaoResPagar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigLiquidacaoResPagarFacade() {
        super(ConfigLiquidacaoResPagar.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfigLiquidacaoResPagar recuperar(Object id) {
        ConfigLiquidacaoResPagar conf = em.find(ConfigLiquidacaoResPagar.class, id);
        conf.getConfigLiqResPagContDesp().size();
        return conf;
    }

    public Boolean verificaContaExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento cr, Date dataVigencia, TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        try {
            Preconditions.checkNotNull(cr, "A configuração de evento de Liquidação teve problemas ao ser instanciada");
            ConfigLiquidacaoResPagar cl = (ConfigLiquidacaoResPagar) cr;
            String sql = "SELECT CE.*,CLIQ.* "
                + " FROM CONFIGLIQUIDACAORESPAGAR CLIQ "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CLIQ.ID = CE.ID"
                + " INNER JOIN CONFIGLIQRESPAGCONTDESP C ON C.CONFIGLIQUIDACAORESPAGAR_ID = CLIQ.ID "
                + " INNER JOIN CONTA CO ON C.CONTADESPESA_ID = CO.ID"
                + " WHERE CO.CODIGO = :conta "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND cliq.tipoReconhecimento = :tipoReconhecimento"
                + " AND cliq.subtipodespesa=:subTipoDesp";
            if (cr.getId() != null) {
                sql += " AND CE.ID <> :config";
            }
            Query q = em.createNativeQuery(sql, ConfigLiquidacaoResPagar.class);
            q.setParameter("conta", conta.getCodigo());
            q.setParameter("subTipoDesp", cl.getSubTipoDespesa().name());
            q.setParameter("tipoLancamento", tipoLancamento.name());
            q.setParameter("tipoReconhecimento", tipoReconhecimento.name());
            q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
            if (cr.getId() != null) {
                q.setParameter("config", cr.getId());
            }
            if (q.getResultList() != null && !q.getResultList().isEmpty()) {
                return true;
            } else {
                return false;

            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void meuSalvar(ConfigLiquidacaoResPagar configLiquidacaoResPagarNaoAlterado, ConfigLiquidacaoResPagar selecionado) {
        if (selecionado.getId() == null) {
            salvarNovo(selecionado);
        } else {
            salvar(selecionado);
        }
    }

    public ConfigLiquidacaoResPagar recuperaEventoPorContaDespesa(Conta c, TipoLancamento tipoLancamento, Date data, SubTipoDespesa subTipoDespesa, TipoReconhecimentoObrigacaoPagar tipoReconhecimento) throws ExcecaoNegocioGenerica, javax.persistence.NonUniqueResultException {
        String msgErro;

        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
            Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
            Preconditions.checkNotNull(subTipoDespesa, " Sub-Tipo de Despesa não informado");
            Preconditions.checkNotNull(c, " A conta de Despesa esta vazia ");
            Preconditions.checkNotNull(tipoReconhecimento, " O tipo de reconhecimento está vazio ");

            return recuperaConfiguracaoLiquidacao(c, tipoLancamento, data, subTipoDespesa, tipoReconhecimento);

        } catch (NoResultException nr) {
            msgErro = "Não foi encontrada nenhum Evento com a Conta " + c + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + ";Sub-Tipo de Despesa " + subTipoDespesa.getDescricao() + " ; Tipo de Reconhecimento " + tipoReconhecimento.getDescricao() + "; Data : " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigLiquidacaoResPagar recuperaConfiguracaoLiquidacao(Conta c, TipoLancamento tipoLancamento, Date data, SubTipoDespesa subTipoDespesa, TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        String sql = "SELECT CE.*, CLIQ.* "
            + " FROM CONFIGLIQUIDACAORESPAGAR CLIQ"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CLIQ.ID = CE.ID"
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " INNER JOIN CONFIGLIQRESPAGCONTDESP CC ON CC.CONFIGLIQUIDACAORESPAGAR_ID = CLIQ.ID"
            + " INNER JOIN CONTA C ON CC.CONTADESPESA_ID = C.ID"
            + " WHERE C.CODIGO = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND CLIQ.SUBTIPODESPESA = :subTipoDespesa"
            + " AND CLIQ.tipoReconhecimento = :tipoReconhecimento ";

        Query q = em.createNativeQuery(sql, ConfigLiquidacaoResPagar.class);

        q.setParameter("conta", c.getCodigo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("subTipoDespesa", subTipoDespesa.name());
        q.setParameter("tipoReconhecimento", tipoReconhecimento.name());
        return (ConfigLiquidacaoResPagar) q.getSingleResult();
    }

    public void encerrarVigencia(ConfigLiquidacaoResPagar entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
