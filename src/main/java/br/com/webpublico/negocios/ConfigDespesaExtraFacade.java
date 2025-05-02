package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigDespesaExtra;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoLancamento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Major
 */
@Stateless
public class ConfigDespesaExtraFacade extends AbstractFacade<ConfigDespesaExtra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private PagamentoExtraFacade pagametExtraFacade;
    @EJB
    private TipoContaExtraFacade tipoContaExtraFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigDespesaExtraFacade() {
        super(ConfigDespesaExtra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public PagamentoExtraFacade getPagametExtraFacade() {
        return pagametExtraFacade;
    }

    public TipoContaExtraFacade getTipoContaExtraFacade() {
        return tipoContaExtraFacade;
    }

    public ConfigDespesaExtra verificaConfiguracaoExistente(ConfigDespesaExtra config, Date dataVigencia) {
        String sql = "SELECT CE.*,CONFIG.* ";
        sql += " FROM CONFIGDESPESAEXTRA CONFIG";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento";
        sql += " AND CONFIG.TIPOCONTAEXTRAORCAMENTARIA = :tipoContaExtra";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigDespesaExtra.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoContaExtra", config.getTipoContaExtraorcamentaria().name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDespesaExtra) q.getSingleResult();
        }
        return null;
    }

    public ConfigDespesaExtra recuperaEvento(TipoLancamento tipoLancamento, TipoContaExtraorcamentaria tipoContaExtraorcamentaria, Date dataV) {
        String sql = "SELECT CE.*,CONFIG.* "
                + " FROM CONFIGDESPESAEXTRA CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND config.tipocontaextraorcamentaria = :tipo"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigDespesaExtra.class);
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipo", tipoContaExtraorcamentaria.name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataV));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDespesaExtra) q.getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public ConfigDespesaExtra recuperar(Object id) {
        ConfigDespesaExtra cde = em.find(ConfigDespesaExtra.class, id);
        return cde;
    }

    private void verificaAlteracoesEvento(ConfigDespesaExtra configDespExtraNaoAlterado, ConfigDespesaExtra cde) {

        if (cde.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configDespExtraNaoAlterado.getTipoLancamento().equals(cde.getTipoLancamento())) {
            alterou = true;
        }
        if (!configDespExtraNaoAlterado.getTipoContaExtraorcamentaria().equals(cde.getTipoContaExtraorcamentaria())) {
            alterou = true;
        }
        if (!configDespExtraNaoAlterado.getInicioVigencia().equals(cde.getInicioVigencia())) {
            alterou = true;
        }
        if (!configDespExtraNaoAlterado.getEventoContabil().equals(cde.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(cde.getEventoContabil(), cde.getId(), cde.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configDespExtraNaoAlterado.getEventoContabil(), configDespExtraNaoAlterado.getId(), configDespExtraNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(cde.getEventoContabil(), cde.getId(), cde.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigDespesaExtra configDespExtraNaoAlterado, ConfigDespesaExtra cde) {
        verificaAlteracoesEvento(configDespExtraNaoAlterado, cde);
        if (cde.getId() == null) {
            salvarNovo(cde);
        } else {
            salvar(cde);
        }
    }

    public void encerrarVigencia(ConfigDespesaExtra entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
