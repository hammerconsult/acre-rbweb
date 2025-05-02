package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigDiariaCivil;
import br.com.webpublico.entidades.DiariaContabilizacao;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;

/**
 * @author Fabio
 */
@Stateless
public class ConfigDiariaCivilFacade extends AbstractFacade<ConfigDiariaCivil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigDiariaCivilFacade() {
        super(ConfigDiariaCivil.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public DiariaContabilizacaoFacade getDiariaContabilizacaoFacade() {
        return diariaContabilizacaoFacade;
    }

    public boolean verificaConfiguracaoExistente(ConfigDiariaCivil config) {
        String sql = "select * from ConfigDiariaCivil ccr";
        sql += " inner join configuracaoevento ce on ccr.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and ccr.operacaoDiariaContabilizacao = :operacao ";
        if (config.getId() != null) {
            sql += " AND CCR.ID <> :config";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigDiariaCivil.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data",  DataUtil.getDataFormatada(config.getInicioVigencia()));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public ConfigDiariaCivil recuperaConfiguracaoExistente(DiariaContabilizacao c) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String sql = "select ce.*, ccr.operacaoDiariaContabilizacao from ConfigDiariaCivil ccr";
        sql += " inner join configuracaoevento ce on ccr.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and ccr.operacaoDiariaContabilizacao = :operacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigDiariaCivil.class);
        q.setParameter("tipolancamento", c.getTipoLancamento().name());
        q.setParameter("operacao", c.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", formato.format(c.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDiariaCivil) q.getSingleResult();
        }
        return null;
    }

    public ConfigDiariaCivil recuperaEvento(DiariaContabilizacao diariaContabilizacao) {
        String sql = "SELECT C1.*, CE1.* "
            + " FROM CONFIGDIARIACIVIL C1"
            + " INNER JOIN CONFIGURACAOEVENTO CE1 ON C1.ID = CE1.ID "
            + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE1.INICIOVIGENCIA) AND coalesce(trunc(CE1.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE1.TIPOLANCAMENTO = :tipoLancamento "
            + " AND C1.OPERACAODIARIACONTABILIZACAO = :operacao ";
        Query q = em.createNativeQuery(sql, ConfigDiariaCivil.class);
        q.setParameter("tipoLancamento", diariaContabilizacao.getTipoLancamento().name());
        q.setParameter("operacao", diariaContabilizacao.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data",  DataUtil.getDataFormatada(diariaContabilizacao.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDiariaCivil) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado");
        }
    }

    @Override
    public ConfigDiariaCivil recuperar(Object id) {
        ConfigDiariaCivil ccr = em.find(ConfigDiariaCivil.class, id);
        return ccr;
    }

    public void verificaAlteracoesEvento(ConfigDiariaCivil configDiariaCivilNaoAlterada, ConfigDiariaCivil ccr) {

        if (ccr.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configDiariaCivilNaoAlterada.getTipoLancamento().equals(ccr.getTipoLancamento())) {
            alterou = true;
        }
        if (!configDiariaCivilNaoAlterada.getOperacaoDiariaContabilizacao().equals(ccr.getOperacaoDiariaContabilizacao())) {
            alterou = true;
        }
        if (!configDiariaCivilNaoAlterada.getInicioVigencia().equals(ccr.getInicioVigencia())) {
            alterou = true;
        }
        if (!configDiariaCivilNaoAlterada.getEventoContabil().equals(ccr.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(ccr.getEventoContabil(), ccr.getId(), ccr.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configDiariaCivilNaoAlterada.getEventoContabil(), configDiariaCivilNaoAlterada.getId(), configDiariaCivilNaoAlterada.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(ccr.getEventoContabil(), ccr.getId(), ccr.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigDiariaCivil configDiariaCivilNaoAlt, ConfigDiariaCivil ccr) {
        verificaAlteracoesEvento(configDiariaCivilNaoAlt, ccr);
        if (ccr.getId() == null) {
            salvarNovo(ccr);
        } else {
            salvar(ccr);
        }
    }

    public void encerrarVigencia(ConfigDiariaCivil entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
