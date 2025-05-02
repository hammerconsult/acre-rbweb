/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigReceitaExtra;
import br.com.webpublico.entidades.ContaExtraorcamentaria;
import br.com.webpublico.entidades.ReceitaExtra;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Paschualleto
 */
@Stateless
public class ConfigReceitaExtraFacade extends AbstractFacade<ConfigReceitaExtra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private TipoContaExtraFacade tipoContaExtraFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigReceitaExtraFacade() {
        super(ConfigReceitaExtra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public boolean verificaEventoExistente(ConfigReceitaExtra config, Date dataVigencia) {
        String sql = "SELECT CRE.*, C.* "
            + " FROM CONFIGRECEITAEXTRA CRE"
            + " INNER JOIN CONFIGURACAOEVENTO C ON CRE.ID = C.ID"
            + " WHERE C.TIPOLANCAMENTO = :tipolancamento "
            + " AND CRE.TIPOCONTAEXTRAORCAMENTARIA = :tipo "
            + " AND CRE.TIPOCONSIGNACAO = :consignacao "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(C.INICIOVIGENCIA) AND coalesce(trunc(C.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CRE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigReceitaExtra.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("tipo", config.getTipoContaExtraorcamentaria().name());
        q.setParameter("consignacao", config.getTipoConsignacao().name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ConfigReceitaExtra recuperar(Object id) {
        ConfigReceitaExtra cd = em.find(ConfigReceitaExtra.class, id);
        return cd;
    }

    public TipoContaExtraFacade getTipoContaExtraFacade() {
        return tipoContaExtraFacade;
    }

    public ConfigReceitaExtra recuperaEventoPorContaReceita(TipoLancamento tipoLancamento, ReceitaExtra rec) {
        try {
            String sql = "SELECT CRR.*, CE.*  FROM CONFIGRECEITAEXTRA CRR"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CRR.ID = CE.ID"
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
                + " WHERE CRR.TIPOCONTAEXTRAORCAMENTARIA = :TIPO "
                + " AND CRR.TIPOCONSIGNACAO = :CONSIGNACAO "
                + " AND to_date(:data,'dd/MM/yyyy')  between trunc(Ce.INICIOVIGENCIA) AND coalesce(trunc(Ce.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :TIPOLANCAMENTO ";

            Query q = em.createNativeQuery(sql, ConfigReceitaExtra.class);
            if (rec.getContaExtraorcamentaria() instanceof ContaExtraorcamentaria) {
                q.setParameter("TIPO", ((ContaExtraorcamentaria) rec.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria().name());
                q.setParameter("TIPOLANCAMENTO", tipoLancamento.name());
                q.setParameter("CONSIGNACAO", rec.getTipoConsignacao().name());
                q.setParameter("data", DataUtil.getDataFormatada(rec.getDataReceita()));
                List<ConfigReceitaExtra> resultado = q.getResultList();
                if (resultado != null && !resultado.isEmpty()) {
                    return resultado.get(0);
                }
            }
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar Configuração de Receita Extra para a Conta " + rec.getContaExtraorcamentaria() + " !");
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível encontrar Configuração de Receita Extra para a Conta " + rec.getContaExtraorcamentaria() + "; Tipo de Conta Extra: " + ((ContaExtraorcamentaria) rec.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria() + "; Tipo de Consignação: " + rec.getTipoConsignacao() + " na Data: " + DataUtil.getDataFormatada(rec.getDataReceita()) + ". ");
        }
    }

    private void verificaAlteracoesDoEvento(ConfigReceitaExtra configReceitaExtraNaoAlterada, ConfigReceitaExtra config) {

        if (config.getId() == null) {
            return;
        }
        boolean alteroEvento = false;
        if (!configReceitaExtraNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alteroEvento = true;
        }
        if (!configReceitaExtraNaoAlterada.getTipoContaExtraorcamentaria().equals(config.getTipoContaExtraorcamentaria())) {
            alteroEvento = true;
        }
        if (configReceitaExtraNaoAlterada.getTipoConsignacao() != null) {
            if (!configReceitaExtraNaoAlterada.getTipoConsignacao().equals(config.getTipoConsignacao())) {
                alteroEvento = true;
            }
        }
        if (!configReceitaExtraNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alteroEvento = true;
        }
        if (!configReceitaExtraNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configReceitaExtraNaoAlterada.getEventoContabil(), configReceitaExtraNaoAlterada.getId(), configReceitaExtraNaoAlterada.getClass().getSimpleName());
        }
        if (alteroEvento) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigReceitaExtra configReceitaExtraNaoAlterada, ConfigReceitaExtra cr) {
        if (cr.getId() == null) {
            salvarNovo(cr);
        } else {
            verificaAlteracoesDoEvento(configReceitaExtraNaoAlterada, cr);
            salvar(cr);
        }
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public ConfigReceitaExtra recuperaConfiguracaoEventoContabil(ReceitaExtra receitaExtra) throws ExcecaoNegocioGenerica {
        return recuperaEventoPorContaReceita(TipoLancamento.NORMAL, receitaExtra);
    }

    public ConfigReceitaExtra recuperaConfiguracaoEventoContabilEstor(ReceitaExtra receitaExtra) throws ExcecaoNegocioGenerica {
        return recuperaEventoPorContaReceita(TipoLancamento.ESTORNO, receitaExtra);
    }

    public void encerrarVigencia(ConfigReceitaExtra entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
