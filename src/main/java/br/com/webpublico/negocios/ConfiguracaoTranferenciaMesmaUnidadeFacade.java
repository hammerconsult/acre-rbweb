/*
 * Codigo gerado automaticamente em Wed Dec 12 10:09:56 AMT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaMesmaUnidade;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoTransferenciaMesmaUnidade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class ConfiguracaoTranferenciaMesmaUnidadeFacade extends AbstractFacade<ConfiguracaoTranferenciaMesmaUnidade> {

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

    public ConfiguracaoTranferenciaMesmaUnidadeFacade() {
        super(ConfiguracaoTranferenciaMesmaUnidade.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfiguracaoTranferenciaMesmaUnidade verificaConfiguracaoExistente(ConfiguracaoTranferenciaMesmaUnidade config, Date dataVigencia) {
        String sql = " SELECT CONF.*, CE.* FROM CONFIGTRANSFMESMAUNID CONF ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONF.ID = CE.ID ";
        sql += " AND CONF.TIPOTRANSFMESMAUNID = :operacao ";
        sql += " AND CONF.ORIGEMTIPOTRANSF = :tipoTransferencia";
        sql += " AND CE.TIPOLANCAMENTO = :tipoLancamento";
        sql += " AND CONF.RESULTANTEINDEPENDENTE = :resultanteIndependente";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CONF.ID <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfiguracaoTranferenciaMesmaUnidade.class);
        q.setParameter("operacao", config.getOperacao().name());
        q.setParameter("tipoTransferencia", config.getTipoTransferencia().name());
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("resultanteIndependente", config.getResultanteIndependente().name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfiguracaoTranferenciaMesmaUnidade) q.getSingleResult();
        }
        return new ConfiguracaoTranferenciaMesmaUnidade();
    }

    @Override
    public ConfiguracaoTranferenciaMesmaUnidade recuperar(Object id) {
        ConfiguracaoTranferenciaMesmaUnidade cd = em.find(ConfiguracaoTranferenciaMesmaUnidade.class, id);
        return cd;
    }

    public ConfiguracaoTranferenciaMesmaUnidade recuperaEvento(TipoLancamento tipoLancamento, ResultanteIndependente resultanteIndependente, TipoTransferenciaMesmaUnidade tipoTransferenciaMesmaUnidade, OrigemTipoTransferencia origemTipoTransferencia, Date dataV) {
        String sql = "SELECT CONFIG.*, CE.*"
                + " FROM CONFIGTRANSFMESMAUNID CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CONFIG.TIPOTRANSFMESMAUNID = :operacao"
                + " AND CONFIG.ORIGEMTIPOTRANSF = :tipoTransferencia"
                + " AND CONFIG.RESULTANTEINDEPENDENTE = :resulIndep"
                + " AND CE.TIPOLANCAMENTO = :tipolancamento"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoTranferenciaMesmaUnidade.class);
        q.setParameter("operacao", tipoTransferenciaMesmaUnidade.name());
        q.setParameter("tipoTransferencia", origemTipoTransferencia.name());
        q.setParameter("resulIndep", resultanteIndependente.name());
        q.setParameter("tipolancamento", tipoLancamento.name());
        q.setParameter("data", DataUtil.getDataFormatada(dataV));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfiguracaoTranferenciaMesmaUnidade) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado");
        }
    }

    private void verificaAlteracoesDoEvento(ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidadeNaoAlterada, ConfiguracaoTranferenciaMesmaUnidade config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getOperacao().equals(config.getOperacao())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getResultanteIndependente().equals(config.getResultanteIndependente())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getTipoTransferencia().equals(config.getTipoTransferencia())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configuracaoTranferenciaMesmaUnidadeNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configuracaoTranferenciaMesmaUnidadeNaoAlterada.getEventoContabil(), configuracaoTranferenciaMesmaUnidadeNaoAlterada.getId(), configuracaoTranferenciaMesmaUnidadeNaoAlterada.getClass().getSimpleName());
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfiguracaoTranferenciaMesmaUnidade configuracaoTranferenciaMesmaUnidadeNaoAlterada, ConfiguracaoTranferenciaMesmaUnidade transf) {
        verificaAlteracoesDoEvento(configuracaoTranferenciaMesmaUnidadeNaoAlterada, transf);
        if (transf.getId() == null) {
            salvarNovo(transf);
        } else {
            salvar(transf);
        }
    }

    public void encerrarVigencia(ConfiguracaoTranferenciaMesmaUnidade entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
