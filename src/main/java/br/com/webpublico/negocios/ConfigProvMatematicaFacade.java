package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigProvMatematicaPrev;
import br.com.webpublico.entidades.ProvAtuarialMatematica;
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
public class ConfigProvMatematicaFacade extends AbstractFacade<ConfigProvMatematicaPrev> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ProvAtuarialMatematicaFacade provAtuarialMatematicaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigProvMatematicaFacade() {
        super(ConfigProvMatematicaPrev.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ProvAtuarialMatematicaFacade getProvAtuarialMatematicaFacade() {
        return provAtuarialMatematicaFacade;
    }

    public ConfigProvMatematicaPrev verificaConfiguracaoExistente(ConfigProvMatematicaPrev config, Date dataVigencia) {
        String sql = "SELECT CE.*,CONFIG.* "
                + " FROM CONFIGPROVMATEMATICAPREV CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CE.TIPOLANCAMENTO = :tipolancamento"
                + " AND CONFIG.TIPOOPERACAOATUARIAL = :operacao"
                + " AND CONFIG.TIPOPLANO = :tipoplano"
                + " AND CONFIG.TIPOPROVISAO = :tipoprovisao"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CONFIG.ID <> :config ";
        }
        Query q = em.createNativeQuery(sql, ConfigProvMatematicaPrev.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getTipoOperacaoAtuarial().name());
        q.setParameter("tipoplano", config.getTipoPlano().name());
        q.setParameter("tipoprovisao", config.getTipoProvisao().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigProvMatematicaPrev) q.getSingleResult();
        }
        return null;
    }

    public ConfigProvMatematicaPrev recuperaEvento(ProvAtuarialMatematica prov) {

        String sql = "SELECT CE.*,CONFIG.* "
                + " FROM CONFIGPROVMATEMATICAPREV CONFIG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID"
                + " WHERE CE.TIPOLANCAMENTO = :tipolancamento"
                + " AND CONFIG.TIPOOPERACAOATUARIAL = :operacao"
                + " AND CONFIG.TIPOPLANO = :tipoplano"
                + " AND CONFIG.TIPOPROVISAO = :tipoprovisao"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, ConfigProvMatematicaPrev.class);
        q.setParameter("tipolancamento", prov.getTipoLancamento().name());
        q.setParameter("operacao", prov.getTipoOperacaoAtuarial().name());
        q.setParameter("tipoplano", prov.getTipoPlano().name());
        q.setParameter("tipoprovisao", prov.getTipoProvisao().name());
        q.setParameter("data", DataUtil.getDataFormatada(prov.getDataLancamento()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigProvMatematicaPrev) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para o Tipo de Lançamento: " + prov.getTipoLancamento().getDescricao() + "; Operação: " + prov.getTipoOperacaoAtuarial().getDescricao() + "; Tipo Plano: " + prov.getTipoPlano().getDescricao() + "; Tipo Provisão: " + prov.getTipoProvisao().getDescricao() + "; Data: " + DataUtil.getDataFormatada(prov.getDataLancamento()));
        }
    }

    private void verificaAlteracoesDoEvento(ConfigProvMatematicaPrev configProvMatematicaPrevNaoAlterada, ConfigProvMatematicaPrev config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configProvMatematicaPrevNaoAlterada.getTipoOperacaoAtuarial().equals(config.getTipoOperacaoAtuarial())) {
            alteroEvento = true;
        }
        if (!configProvMatematicaPrevNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configProvMatematicaPrevNaoAlterada.getTipoPlano().equals(config.getTipoPlano())) {
            alteroEvento = true;
        }
        if (!configProvMatematicaPrevNaoAlterada.getTipoProvisao().equals(config.getTipoProvisao())) {
            alteroEvento = true;
        }
        if (!configProvMatematicaPrevNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configProvMatematicaPrevNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {

            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            //System.out.println("gero Eventos reprocessar alterado");
            eventoContabilFacade.geraEventosReprocessar(configProvMatematicaPrevNaoAlterada.getEventoContabil(), configProvMatematicaPrevNaoAlterada.getId(), configProvMatematicaPrevNaoAlterada.getClass().getSimpleName());
            //System.out.println("gero Eventos reprocessar nao alterado");
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigProvMatematicaPrev configProvMatematicaPrevNaoAlterada, ConfigProvMatematicaPrev config) {
        verificaAlteracoesDoEvento(configProvMatematicaPrevNaoAlterada, config);
        if (config.getId() == null) {
            salvarNovo(config);
        } else {
            salvar(config);
        }
    }

    public void encerrarVigencia(ConfigProvMatematicaPrev entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
