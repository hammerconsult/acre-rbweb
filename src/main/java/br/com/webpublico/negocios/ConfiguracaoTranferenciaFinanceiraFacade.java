/*
 * Codigo gerado automaticamente em Wed Dec 12 10:09:56 AMT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaFinanceira;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoTransferenciaFinanceira;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfiguracaoTranferenciaFinanceiraFacade extends AbstractFacade<ConfiguracaoTranferenciaFinanceira> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoTranferenciaFinanceiraFacade() {
        super(ConfiguracaoTranferenciaFinanceira.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfiguracaoTranferenciaFinanceira verificaConfiguracaoExistente(ConfiguracaoTranferenciaFinanceira config, Date dataVigencia) {

        String sql = " SELECT CONFIG.*, CE.* ";
        sql += " FROM CONFLIBERACAOTRANSFFINANC CONFIG";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID";
        sql += " AND CONFIG.TIPOTRANSFINANC = :operacao";
        sql += " AND CONFIG.ORIGEMTIPOTRANSF = :tipoTransferencia";
        sql += " AND CONFIG.RESULTANTEINDEPENDENTE = :resulIndep";
        sql += " AND CE.TIPOLANCAMENTO = :tipolancamento";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfiguracaoTranferenciaFinanceira.class);
        q.setParameter("operacao", config.getOperacao().name());
        q.setParameter("tipoTransferencia", config.getTipoTransferencia().name());
        q.setParameter("resulIndep", config.getResultanteIndependente().name());
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("data", Util.formatterDiaMesAno.format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfiguracaoTranferenciaFinanceira) q.getSingleResult();
        }
        return new ConfiguracaoTranferenciaFinanceira();
    }

    public ConfiguracaoTranferenciaFinanceira recuperaEvento(TipoLancamento tipoLancamento, TipoTransferenciaFinanceira operacao, OrigemTipoTransferencia tipoTransferencia, ResultanteIndependente resultanteIndependente, Date dataV) {
        String sql = "SELECT CONFIG.*, CE.* "
                + " FROM CONFLIBERACAOTRANSFFINANC CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CONFIG.TIPOTRANSFINANC = :operacao"
                + " AND CONFIG.ORIGEMTIPOTRANSF = :tipoTransferencia"
                + " AND CONFIG.RESULTANTEINDEPENDENTE = :resulIndep"
                + " AND CE.TIPOLANCAMENTO = :tipolancamento"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoTranferenciaFinanceira.class);
        q.setParameter("operacao", operacao.name());
        q.setParameter("tipoTransferencia", tipoTransferencia.name());
        q.setParameter("resulIndep", resultanteIndependente.name());
        q.setParameter("tipolancamento", tipoLancamento.name());
        q.setParameter("data", DataUtil.getDataFormatada(dataV));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfiguracaoTranferenciaFinanceira) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para o Tipo de Lançamento: " + tipoLancamento.getDescricao() + ", Tipo de Transferência: " + operacao.getDescricao() + " Origem : " + tipoTransferencia + ", Tipo de Resultante : " + resultanteIndependente + " e data " + DataUtil.getDataFormatada(dataV));
        }

    }

    @Override
    public ConfiguracaoTranferenciaFinanceira recuperar(Object id) {
        ConfiguracaoTranferenciaFinanceira cd = em.find(ConfiguracaoTranferenciaFinanceira.class, id);
        return cd;
    }

    private void verificaAlteracoesDoEvento(ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceiraNaoAlterada, ConfiguracaoTranferenciaFinanceira config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getOperacao().equals(config.getOperacao())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getResultanteIndependente().equals(config.getResultanteIndependente())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getTipoTransferencia().equals(config.getTipoTransferencia())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaFinanceiraNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configuracaoTranferenciaFinanceiraNaoAlterada.getEventoContabil(), configuracaoTranferenciaFinanceiraNaoAlterada.getId(), configuracaoTranferenciaFinanceiraNaoAlterada.getClass().getSimpleName());
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfiguracaoTranferenciaFinanceira configuracaoTranferenciaFinanceiraNaoAlterada, ConfiguracaoTranferenciaFinanceira transf) {
        verificaAlteracoesDoEvento(configuracaoTranferenciaFinanceiraNaoAlterada, transf);
        if (transf.getId() == null) {
            salvarNovo(transf);
        } else {
            salvar(transf);
        }
    }

    public void encerrarVigencia(ConfiguracaoTranferenciaFinanceira entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
