package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigLiberacaoFinanceira;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/02/14
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigLiberacaoFinanceiraFacade extends AbstractFacade<ConfigLiberacaoFinanceira> {

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

    public ConfigLiberacaoFinanceiraFacade() {
        super(ConfigLiberacaoFinanceira.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigLiberacaoFinanceira verificaConfiguracaoExistente(ConfigLiberacaoFinanceira config, Date dataVigencia) {
        String sql = " SELECT CONFIG.*, CE.* ";
        sql += " FROM CONFIGLIBERACAOFINANCEIRA CONFIG";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID";
        sql += " AND CONFIG.OPERACAO = :operacao";
        sql += " AND CONFIG.TIPOTRANSFERENCIA = :tipoliberacao";
        sql += " AND CONFIG.RESULTANTEINDEPENDENTE = :tipoExecucao";
        sql += " AND CE.TIPOLANCAMENTO = :tipolancamento";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigLiberacaoFinanceira.class);
        q.setParameter("operacao", config.getOperacao().name());
        q.setParameter("tipoliberacao", config.getTipoTransferencia().name());
        q.setParameter("tipoExecucao", config.getResultanteIndependente().name());
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigLiberacaoFinanceira) q.getSingleResult();
        }
        return new ConfigLiberacaoFinanceira();
    }


    public ConfigLiberacaoFinanceira recuperaEvento(TipoLancamento tipoLancamento, TipoLiberacaoFinanceira operacao, OrigemTipoTransferencia tipoLiberacao, Date dataV, ResultanteIndependente resultanteIndependente) {
        String sql = "SELECT CONFIG.*, CE.* "
                + " FROM CONFIGLIBERACAOFINANCEIRA CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CONFIG.OPERACAO = :operacao"
                + " AND CONFIG.TIPOTRANSFERENCIA = :tipoLiberacao"
                + " AND CONFIG.RESULTANTEINDEPENDENTE = :tipoExecucao"
                + " AND CE.TIPOLANCAMENTO = :tipolancamento"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigLiberacaoFinanceira.class);
        q.setParameter("operacao", operacao.name());
        q.setParameter("tipoLiberacao", tipoLiberacao.name());
        q.setParameter("tipoExecucao", resultanteIndependente.name());
        q.setParameter("tipolancamento", tipoLancamento.name());
        q.setParameter("data", DataUtil.getDataFormatada(dataV));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigLiberacaoFinanceira) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica(" Nenhum Evento Encontrado para o Tipo de Lançamento: " + tipoLancamento.getDescricao() + ", Tipo de Transferência: " + operacao.getDescricao() + " Origem : " + tipoLiberacao + " na data " + DataUtil.getDataFormatada(dataV));
        }
    }

    @Override
    public ConfigLiberacaoFinanceira recuperar(Object id) {
        ConfigLiberacaoFinanceira cf = em.find(ConfigLiberacaoFinanceira.class, id);
        return cf;
    }

    private void verificaAlteracoesDoEvento(ConfigLiberacaoFinanceira configuracaoLiberacaoFinanceiraNaoAlterada, ConfigLiberacaoFinanceira config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configuracaoLiberacaoFinanceiraNaoAlterada.getOperacao().equals(config.getOperacao())) {
            alteroEvento = true;
        }
        if (!configuracaoLiberacaoFinanceiraNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configuracaoLiberacaoFinanceiraNaoAlterada.getTipoTransferencia().equals(config.getTipoTransferencia())) {
            alteroEvento = true;
        }
        if (!configuracaoLiberacaoFinanceiraNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configuracaoLiberacaoFinanceiraNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configuracaoLiberacaoFinanceiraNaoAlterada.getEventoContabil(), configuracaoLiberacaoFinanceiraNaoAlterada.getId(), configuracaoLiberacaoFinanceiraNaoAlterada.getClass().getSimpleName());
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigLiberacaoFinanceira configuracaoLiberacaoFinanceiraNaoAlterada, ConfigLiberacaoFinanceira transf) {
        verificaAlteracoesDoEvento(configuracaoLiberacaoFinanceiraNaoAlterada, transf);
        if (transf.getId() == null) {
            salvarNovo(transf);
        } else {
            salvar(transf);
        }
    }

    public void encerrarVigencia(ConfigLiberacaoFinanceira entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
