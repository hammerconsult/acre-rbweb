/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigSuprimentoDeFundos;
import br.com.webpublico.entidades.DiariaContabilizacao;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ConfigSuprimentoDeFundosFacade extends AbstractFacade<ConfigSuprimentoDeFundos>{

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigSuprimentoDeFundosFacade(){
        super(ConfigSuprimentoDeFundos.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void setEventoContabilFacade(EventoContabilFacade eventoContabilFacade) {
        this.eventoContabilFacade = eventoContabilFacade;
    }

    @Override
    public ConfigSuprimentoDeFundos recuperar(Object id) {
        ConfigSuprimentoDeFundos cdf = em.find(ConfigSuprimentoDeFundos.class, id);
        return cdf;
    }

    public boolean verificaConfiguracaoExistente(ConfigSuprimentoDeFundos config, Date dataVigencia) {
        String sql = "select * from ConfigSuprimentoDeFundos csf";
        sql += " inner join configuracaoevento ce on csf.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and csf.operacaoDiariaContabilizacao = :operacao ";
        if (config.getId() != null) {
            sql += " AND CSF.ID <> :config";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigSuprimentoDeFundos.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public ConfigSuprimentoDeFundos recuperaConfiguracaoExistente(DiariaContabilizacao d) {
        String sql = "select * from ConfigSuprimentoDeFundos csf";
        sql += " inner join configuracaoevento ce on csf.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and csf.operacaoDiariaContabilizacao = :operacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigSuprimentoDeFundos.class);
        q.setParameter("tipolancamento", d.getTipoLancamento().name());
        q.setParameter("operacao", d.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(d.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigSuprimentoDeFundos) q.getSingleResult();
        }
        return null;
    }

    public ConfigSuprimentoDeFundos recuperaEvento(DiariaContabilizacao diariaContabilizacao) {
        String sql = "SELECT C.*, CE.* "
                + " FROM CONFIGSUPRIMENTODEFUNDOS C "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON C.ID = CE.ID "
                + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND C.OPERACAODIARIACONTABILIZACAO = :operacao ";
        Query q = em.createNativeQuery(sql, ConfigSuprimentoDeFundos.class);
        q.setParameter("tipoLancamento", diariaContabilizacao.getTipoLancamento().name());
        q.setParameter("operacao", diariaContabilizacao.getOperacaoDiariaContabilizacao().name());
        q.setParameter("data", DataUtil.getDataFormatada(diariaContabilizacao.getDataDiaria()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigSuprimentoDeFundos) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado");
        }
    }

    public void verificaAlteracoesEvento(ConfigSuprimentoDeFundos configSupriFundosNaoAlterado, ConfigSuprimentoDeFundos csf) {

        if (csf.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configSupriFundosNaoAlterado.getTipoLancamento().equals(csf.getTipoLancamento())) {
            alterou = true;
        }
        if (!configSupriFundosNaoAlterado.getOperacaoDiariaContabilizacao().equals(csf.getOperacaoDiariaContabilizacao())) {
            alterou = true;
        }
        if (!configSupriFundosNaoAlterado.getInicioVigencia().equals(csf.getInicioVigencia())) {
            alterou = true;
        }
        if (!configSupriFundosNaoAlterado.getEventoContabil().equals(csf.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(csf.getEventoContabil(), csf.getId(), csf.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configSupriFundosNaoAlterado.getEventoContabil(), configSupriFundosNaoAlterado.getId(), configSupriFundosNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(csf.getEventoContabil(), csf.getId(), csf.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigSuprimentoDeFundos configSupriFundosNaoAlterado, ConfigSuprimentoDeFundos csf) {
        verificaAlteracoesEvento(configSupriFundosNaoAlterado, csf);
        if (csf.getId() == null) {
            salvarNovo(csf);
        } else {
            salvar(csf);
        }
    }

    public void encerrarVigencia(ConfigSuprimentoDeFundos entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
