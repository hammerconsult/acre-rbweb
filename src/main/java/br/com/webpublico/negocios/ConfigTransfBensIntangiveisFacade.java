package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigTransfBensIntang;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
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
public class ConfigTransfBensIntangiveisFacade extends AbstractFacade<ConfigTransfBensIntang> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ConfigTransfBensIntangiveisFacade configTransfBensIntangiveisFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigTransfBensIntangiveisFacade() {
        super(ConfigTransfBensIntang.class);
    }

    public ConfigTransfBensIntang verificarConfiguracaoExistente(ConfigTransfBensIntang config, Date dataVigencia) {

        String sql = " SELECT CONFIG.*, CE.* ";
        sql += " FROM CONFIGTRANSFBENSINTANG CONFIG ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
        sql += " WHERE CONFIG.TIPOOPERACAOBENSINTANGIVEIS = :tipoOperacao ";
        sql += " AND CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigTransfBensIntang.class);
        q.setParameter("tipoOperacao", config.getTipoOperacaoBensIntangiveis().name());
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("data", Util.formatterDiaMesAno.format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigTransfBensIntang) q.getSingleResult();
        }
        return new ConfigTransfBensIntang();
    }

    public ConfigTransfBensIntang recuperarConfiguracaoEvento(TipoLancamento tipoLancamento, TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis, Date dataTransf) {
        try {
            String sql = " SELECT CONFIG.*, CE.* "
                + " FROM CONFIGTRANSFBENSINTANG CONFIG "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
                + " WHERE CONFIG.TIPOOPERACAOBENSINTANGIVEIS = :tipoOperacao "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
                + "  and to_date(:dataLanc, 'dd/MM/yyyy') between trunc(ce.iniciovigencia) and coalesce(trunc(ce.fimvigencia), to_date(:dataLanc, 'dd/MM/yyyy')) ";
            Query q = em.createNativeQuery(sql, ConfigTransfBensIntang.class);
            q.setParameter("tipoOperacao", tipoOperacaoBensIntangiveis.name());
            q.setParameter("tipoLancamento", tipoLancamento.name());
            q.setParameter("dataLanc", DataUtil.getDataFormatada(dataTransf));

            if (q.getResultList() != null && !q.getResultList().isEmpty()) {
                return (ConfigTransfBensIntang) q.getSingleResult();
            } else {
                throw new ExcecaoNegocioGenerica(" Nenhum Evento Contábil encontrado para lançamento: " + tipoLancamento.getDescricao() + ";" +
                    " Operação: " + tipoOperacaoBensIntangiveis.getDescricao() + ";" +
                    " na data: " + DataUtil.getDataFormatada(dataTransf));
            }
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma configuração vigente. Por favor, verificar as configurações de eventos contábeis para está operação.");
        }
    }

    @Override
    public ConfigTransfBensIntang recuperar(Object id) {
        ConfigTransfBensIntang cf = em.find(ConfigTransfBensIntang.class, id);
        return cf;
    }

    private void verificarAlteracoesDoEvento(ConfigTransfBensIntang configuracaoNaoAlterada, ConfigTransfBensIntang config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configuracaoNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configuracaoNaoAlterada.getTipoOperacaoBensIntangiveis().equals(config.getTipoOperacaoBensIntangiveis())) {
            alteroEvento = true;
        }
        if (!configuracaoNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configuracaoNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configuracaoNaoAlterada.getEventoContabil(), configuracaoNaoAlterada.getId(), configuracaoNaoAlterada.getClass().getSimpleName());
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigTransfBensIntang configuracaoNaoAlterada, ConfigTransfBensIntang transf) {
        verificarAlteracoesDoEvento(configuracaoNaoAlterada, transf);
        if (transf.getId() == null) {
            salvarNovo(transf);
        } else {
            salvar(transf);
        }
    }

    public void encerrarVigencia(ConfigTransfBensIntang entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }
}
