package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigTransfEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
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
public class ConfigTransfEstoqueFacade extends AbstractFacade<ConfigTransfEstoque> {

    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigTransfEstoqueFacade() {
        super(ConfigTransfEstoque.class);
    }

    public ConfigTransfEstoque verificarConfiguracaoExistente(ConfigTransfEstoque config, Date dataVigencia) {
        String sql = " SELECT CONFIG.*, CE.* ";
        sql += " FROM CONFIGTRANSFESTOQUE CONFIG ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
        sql += " WHERE CONFIG.TIPOOPERACAOBENSESTOQUE = :tipoOperacao ";
        sql += " AND CE.TIPOLANCAMENTO = :tipolancamento ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigTransfEstoque.class);
        q.setParameter("tipoOperacao", config.getTipoOperacaoBensEstoque().name());
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("data", Util.formatterDiaMesAno.format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigTransfEstoque) q.getSingleResult();
        }
        return new ConfigTransfEstoque();
    }


    public ConfigTransfEstoque recuperarConfiguracaoEvento(TipoLancamento tipoLancamento, TipoOperacaoBensEstoque tipoOperacaoBensEstoque, Date dataTransf) {
        try {
            String sql = " SELECT CONFIG.*, CE.* "
                + " FROM CONFIGTRANSFESTOQUE CONFIG "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
                + " WHERE CONFIG.TIPOOPERACAOBENSESTOQUE = :tipoOperacao"
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
                + "  and to_date(:dataLanc, 'dd/MM/yyyy') between trunc(ce.iniciovigencia) and coalesce(trunc(ce.fimvigencia), to_date(:dataLanc, 'dd/MM/yyyy')) ";
            Query q = em.createNativeQuery(sql, ConfigTransfEstoque.class);
            q.setParameter("tipoOperacao", tipoOperacaoBensEstoque.name());
            q.setParameter("tipoLancamento", tipoLancamento.name());
            q.setParameter("dataLanc", DataUtil.getDataFormatada(dataTransf));

            if (q.getResultList() != null && !q.getResultList().isEmpty()) {
                return (ConfigTransfEstoque) q.getSingleResult();
            } else {
                throw new ExcecaoNegocioGenerica(" Nenhum Evento Contábil encontrado para lançamento: " + tipoLancamento.getDescricao() + ";" +
                    " Operação: " + tipoOperacaoBensEstoque.getDescricao() + ";" +
                    " na data: " + DataUtil.getDataFormatada(dataTransf));
            }
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma configuração vigente. Por favor, verificar as configurações de eventos contábeis para está operação.");
        }
    }


    @Override
    public ConfigTransfEstoque recuperar(Object id) {
        ConfigTransfEstoque cf = em.find(ConfigTransfEstoque.class, id);
        return cf;
    }

    private void verificarAlteracoesDoEvento(ConfigTransfEstoque configuracaoNaoAlterada, ConfigTransfEstoque config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configuracaoNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configuracaoNaoAlterada.getTipoOperacaoBensEstoque().equals(config.getTipoOperacaoBensEstoque())) {
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

    public void meuSalvar(ConfigTransfEstoque configuracaoNaoAlterada, ConfigTransfEstoque transf) {
        verificarAlteracoesDoEvento(configuracaoNaoAlterada, transf);
        if (transf.getId() == null) {
            salvarNovo(transf);
        } else {
            salvar(transf);
        }
    }

    public void encerrarVigencia(ConfigTransfEstoque entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }
}
