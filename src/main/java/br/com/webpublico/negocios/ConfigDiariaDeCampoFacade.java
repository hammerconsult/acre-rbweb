/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigDiariaDeCampo;
import br.com.webpublico.entidades.DiariaContabilizacao;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfigDiariaDeCampoFacade extends AbstractFacade<ConfigDiariaDeCampo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigDiariaDeCampoFacade() {
        super(ConfigDiariaDeCampo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    @Override
    public ConfigDiariaDeCampo recuperar(Object id) {
        ConfigDiariaDeCampo cdc = em.find(ConfigDiariaDeCampo.class, id);
        return cdc;
    }

    public boolean verificaConfiguracaoExistente(ConfigDiariaDeCampo config, Date dataVigencia) {
        String sql = "select * from ConfigDiariaDeCampo cdc";
        sql += " inner join configuracaoevento ce on cdc.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and cdc.operacaoDiariaContabilizacao = :operacao ";
        if (config.getId() != null) {
            sql += " AND CDC.ID <> :config";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigDiariaDeCampo.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public ConfigDiariaDeCampo recuperaConfiguracaoExistente(DiariaContabilizacao d) {
        String sql = "select * from ConfigDiariaDeCampo cdc";
        sql += " inner join configuracaoevento ce on cdc.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and cdc.operacaoDiariaContabilizacao = :operacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigDiariaDeCampo.class);
        q.setParameter("tipolancamento", d.getTipoLancamento().name());
        q.setParameter("operacao", d.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(d.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDiariaDeCampo) q.getSingleResult();
        }
        return null;
    }

    public ConfigDiariaDeCampo recuperaEvento(DiariaContabilizacao diariaContabilizacao) {
        String sql = "SELECT C.*, CE.* "
                + " FROM CONFIGDIARIADECAMPO C"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON C.ID = CE.ID "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND C.OPERACAODIARIACONTABILIZACAO = :operacao ";
        Query q = em.createNativeQuery(sql, ConfigDiariaDeCampo.class);
        q.setParameter("tipoLancamento", diariaContabilizacao.getTipoLancamento().name());
        q.setParameter("operacao", diariaContabilizacao.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(diariaContabilizacao.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDiariaDeCampo) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado");
        }
    }

    public void verificaAlteracoesEvento(ConfigDiariaDeCampo configDiariaDeCampoNaoAlterado, ConfigDiariaDeCampo cdc) {
        boolean alterou = false;
        if (cdc.getId() == null) {
            return;
        }
        if (!configDiariaDeCampoNaoAlterado.getTipoLancamento().equals(cdc.getTipoLancamento())) {
            alterou = true;
        }
        if (!configDiariaDeCampoNaoAlterado.getOperacaoDiariaContabilizacao().equals(cdc.getOperacaoDiariaContabilizacao())) {
            alterou = true;
        }
        if (!configDiariaDeCampoNaoAlterado.getInicioVigencia().equals(cdc.getInicioVigencia())) {
            alterou = true;
        }
        if (!configDiariaDeCampoNaoAlterado.getEventoContabil().equals(cdc.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(cdc.getEventoContabil(), cdc.getId(), cdc.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configDiariaDeCampoNaoAlterado.getEventoContabil(), configDiariaDeCampoNaoAlterado.getId(), configDiariaDeCampoNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(cdc.getEventoContabil(), cdc.getId(), cdc.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigDiariaDeCampo configDiariaDeCampoNaoAlterado, ConfigDiariaDeCampo cdc) {
        verificaAlteracoesEvento(configDiariaDeCampoNaoAlterado, cdc);
        if (cdc.getId() == null) {
            salvarNovo(cdc);
        } else {
            salvar(cdc);
        }
    }

    public void encerrarVigencia(ConfigDiariaDeCampo entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
