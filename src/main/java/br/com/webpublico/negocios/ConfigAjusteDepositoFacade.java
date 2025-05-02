package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AjusteDeposito;
import br.com.webpublico.entidades.ConfigAjusteDeposito;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author Major
 */
@Stateless
public class ConfigAjusteDepositoFacade extends AbstractFacade<ConfigAjusteDeposito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigAjusteDepositoFacade() {
        super(ConfigAjusteDeposito.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigAjusteDeposito verificaConfiguracaoExistente(ConfigAjusteDeposito config, Date dataVigencia) {
        String sql = "SELECT CE.*,CONFIG.* "
            + " FROM CONFIGAJUSTEDEPOSITO CONFIG"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
            + " WHERE CE.TIPOLANCAMENTO = :tipolancamento"
            + " AND CONFIG.TIPOAJUSTE = :tipoAjuste"
            + " AND CONFIG.TIPOCONTAEXTRAORCAMENTARIA = :tipoContaExtra"
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " and CONFIG.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigAjusteDeposito.class);

        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("tipoAjuste", config.getTipoAjuste().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("tipoContaExtra", config.getTipoContaExtraorcamentaria().name());
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAjusteDeposito) q.getSingleResult();
        }
        return null;
    }

    public ConfigAjusteDeposito buscarConfiguracaoCDE(AjusteDeposito ajusteDeposito, TipoLancamento tipoLancamento) {
        String sql = "SELECT CE.*,CONFIG.* "
            + " FROM CONFIGAJUSTEDEPOSITO CONFIG"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
            + " WHERE CE.TIPOLANCAMENTO = :tipolancamento"
            + " AND CONFIG.TIPOAJUSTE = :tipoAjuste"
            + " AND CONFIG.TIPOCONTAEXTRAORCAMENTARIA = :tipoContaExtra"
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigAjusteDeposito.class);

        q.setParameter("tipolancamento", tipoLancamento.name());
        q.setParameter("tipoAjuste", ajusteDeposito.getTipoAjuste().name());
        q.setParameter("data", DataUtil.getDataFormatada(ajusteDeposito.getDataAjuste()));
        q.setParameter("tipoContaExtra", ajusteDeposito.getContaExtraorcamentaria().getTipoContaExtraorcamentaria().name());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAjusteDeposito) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para o Tipo Lançamento: " + tipoLancamento.getDescricao()
                + ", Tipo Ajuste: " + ajusteDeposito.getTipoAjuste().getDescricao()
                + ", Tipo de Conta Extra: " + ajusteDeposito.getContaExtraorcamentaria().getTipoContaExtraorcamentaria().getDescricao()
                + " na data: " + DataUtil.getDataFormatada(ajusteDeposito.getDataAjuste()));
        }
    }

    @Override
    public ConfigAjusteDeposito recuperar(Object id) {
        ConfigAjusteDeposito cad = em.find(ConfigAjusteDeposito.class, id);
        return cad;
    }

    private void verificaAlteracoesEvento(ConfigAjusteDeposito configAjusDepNaoAlterado, ConfigAjusteDeposito cad) {

        if (cad.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configAjusDepNaoAlterado.getTipoLancamento().equals(cad.getTipoLancamento())) {
            alterou = true;
        }
        if (!configAjusDepNaoAlterado.getTipoAjuste().equals(cad.getTipoAjuste())) {
            alterou = true;
        }
        if (!configAjusDepNaoAlterado.getTipoContaExtraorcamentaria().equals(cad.getTipoContaExtraorcamentaria())) {
            alterou = true;
        }
        if (!configAjusDepNaoAlterado.getInicioVigencia().equals(cad.getInicioVigencia())) {
            alterou = true;
        }
        if (!configAjusDepNaoAlterado.getEventoContabil().equals(cad.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(cad.getEventoContabil(), cad.getId(), cad.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configAjusDepNaoAlterado.getEventoContabil(), configAjusDepNaoAlterado.getId(), configAjusDepNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(cad.getEventoContabil(), cad.getId(), cad.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigAjusteDeposito configAjusDepNaoAlterado, ConfigAjusteDeposito cad) {
        verificaAlteracoesEvento(configAjusDepNaoAlterado, cad);
        if (cad.getId() == null) {
            salvarNovo(cad);
        } else {
            salvar(cad);
        }
    }

    public void encerrarVigencia(ConfigAjusteDeposito entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
