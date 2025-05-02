/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteTransporteConfiguracaoContabil;
import br.com.webpublico.enums.OperacaoDividaAtiva;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

/**
 * @author claudio
 */
@Stateless
public class ConfigDividaAtivaContabilFacade extends AbstractFacade<ConfigDividaAtivaContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigDividaAtivaContabilFacade() {
        super(ConfigDividaAtivaContabil.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public DividaAtivaContabilFacade getDividaAtivaContabilFacade() {
        return dividaAtivaContabilFacade;
    }

    public ConfigDividaAtivaContabil verificaConfiguracaoExistente(ConfigDividaAtivaContabil config) {

        String sql = " SELECT CE.*, C.* FROM CONFIGDIVIDAATIVACONTABIL C ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CE.ID = C.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipolancamento ";
        sql += " AND C.OPERACAODIVIDAATIVA = :operacao ";
        sql += " AND C.CONTARECEITA_ID = :idContaReceita";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND C.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigDividaAtivaContabil.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoDividaAtiva().name());
        q.setParameter("idContaReceita", config.getContaReceita().getId());
        q.setParameter("data", DataUtil.getDataFormatada(config.getInicioVigencia()));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDividaAtivaContabil) q.getSingleResult();
        }
        return new ConfigDividaAtivaContabil();
    }

    public ConfigDividaAtivaContabil recuperaEvento(DividaAtivaContabil dividaAtivaContabil) {
        ContaReceita contaReceita = null;
        String sql = " SELECT ce.*, c.* FROM CONFIGDIVIDAATIVACONTABIL C "
            + " INNER JOIN CONFIGURACAOEVENTO ce ON ce.id = c.id "
            + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND ce.tipolancamento = :tipolancamento "
            + " AND C.OPERACAODIVIDAATIVA = :operacao "
            + " AND C.CONTARECEITA_ID = :idContaReceita";
        Query q = em.createNativeQuery(sql, ConfigDividaAtivaContabil.class);
        q.setParameter("tipolancamento", dividaAtivaContabil.getTipoLancamento().name());
        q.setParameter("operacao", dividaAtivaContabil.getOperacaoDividaAtiva().name());
        try {
            if (dividaAtivaContabil.getOperacaoDividaAtiva().equals(OperacaoDividaAtiva.INSCRICAO)) {
                contaReceita = ((ContaReceita) dividaAtivaContabil.getReceitaLOA().getContaDeReceita()).getCorrespondente();
            } else {
                contaReceita = ((ContaReceita) dividaAtivaContabil.getReceitaLOA().getContaDeReceita());
            }
            q.setParameter("idContaReceita", contaReceita.getId());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" A conta de Receita " + ((ContaReceita) dividaAtivaContabil.getReceitaLOA().getContaDeReceita()) + " não possui Conta Correspondente.");
        }

        q.setParameter("data", DataUtil.getDataFormatada(dividaAtivaContabil.getDataDivida()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDividaAtivaContabil) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para a Conta de Receita:" + contaReceita + "; Tipo de Lançamento: " + dividaAtivaContabil.getTipoLancamento().getDescricao() + "; Operação: " + dividaAtivaContabil.getOperacaoDividaAtiva().getDescricao() + " na data: " + DataUtil.getDataFormatada(dividaAtivaContabil.getDataDivida()) + ".");
        }
    }

    public ConfigDividaAtivaContabil recuperarEventoEventoContabil(Conta contaReceita, OperacaoDividaAtiva operacaoDividaAtiva, TipoLancamento tipoLancamento, Date dataDivida) {
        String sql = " SELECT ce.*, c.* FROM CONFIGDIVIDAATIVACONTABIL C "
            + " INNER JOIN CONFIGURACAOEVENTO ce ON ce.id = c.id "
            + " WHERE to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND ce.tipolancamento = :tipolancamento "
            + " AND C.OPERACAODIVIDAATIVA = :operacao "
            + " AND C.CONTARECEITA_ID = :idContaReceita";
        Query q = em.createNativeQuery(sql, ConfigDividaAtivaContabil.class);

        q.setParameter("tipolancamento", tipoLancamento.name());
        q.setParameter("operacao", operacaoDividaAtiva.name());
        try {
            q.setParameter("idContaReceita", contaReceita.getId());
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" A conta de Receita " + contaReceita + " não possui Conta Correspondente.");
        }

        q.setParameter("data", DataUtil.getDataFormatada(dataDivida));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDividaAtivaContabil) q.getSingleResult();
        } else {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ConfigDividaAtivaContabil recuperarConfigDividaAtivaContabil(ConfigDividaAtivaContabil configOrigem, ConfigDividaAtivaContabil novaConfig, Conta contaDestino) {
        return recuperarEventoEventoContabil(contaDestino, novaConfig.getOperacaoDividaAtiva(), configOrigem.getTipoLancamento(), novaConfig.getInicioVigencia());
    }

    @Override
    public ConfigDividaAtivaContabil recuperar(Object id) {
        ConfigDividaAtivaContabil cda = em.find(ConfigDividaAtivaContabil.class, id);
        return cda;
    }

    public void verifcaAlteracoesEvento(ConfigDividaAtivaContabil configDividaAtivaNaoAlterado, ConfigDividaAtivaContabil selecionado) {
        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configDividaAtivaNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        if (!configDividaAtivaNaoAlterado.getOperacaoDividaAtiva().equals(selecionado.getOperacaoDividaAtiva())) {
            alterou = true;
        }
        if (configDividaAtivaNaoAlterado.getContaReceita() != null) {
            if (!configDividaAtivaNaoAlterado.getContaReceita().equals(selecionado.getContaReceita())) {
                alterou = true;
            }
        }
        if (!configDividaAtivaNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configDividaAtivaNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configDividaAtivaNaoAlterado.getEventoContabil(), configDividaAtivaNaoAlterado.getId(), configDividaAtivaNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigDividaAtivaContabil configDividaAtivaNaoAlterado, ConfigDividaAtivaContabil configuracaoEncontrada) {
        verifcaAlteracoesEvento(configDividaAtivaNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }

    public void encerrarVigencia(ConfigDividaAtivaContabil entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
