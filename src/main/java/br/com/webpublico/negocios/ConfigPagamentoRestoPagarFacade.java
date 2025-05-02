/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class ConfigPagamentoRestoPagarFacade extends AbstractFacade<ConfigPagamentoRestoPagar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigPagamentoRestoPagarFacade() {
        super(ConfigPagamentoRestoPagar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    @Override
    public ConfigPagamentoRestoPagar recuperar(Object id) {
        ConfigPagamentoRestoPagar conf = em.find(ConfigPagamentoRestoPagar.class, id);
        conf.getConfigPagResPagContDesp().size();
        return conf;
    }

    public Boolean verificaConfiguracaoExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento ce, EventoContabil evento, Date dataVigencia, TipoContaDespesa tipoContaDespesa, TipoRestosProcessado tipoResto) {
        String sql = "SELECT CE.*,CPAG.* "
                + " FROM CONFIGPAGAMENTORESTOPAGAR CPAG "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CPAG.ID = CE.ID"
                + " INNER JOIN CONFIGPAGRESPAGCONTDESP C ON C.CONFIGPAGAMENTORESTOPAGAR_ID = CPAG.ID "
                + " WHERE C.CONTADESPESA_ID = :conta "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND CPAG.TIPOCONTADESPESA = :tipoConta "
                + " AND CPAG.TIPORESTOSPROCESSADOS = :tipoResto "
                + " AND CE.EVENTOCONTABIL_ID = :evento";
        if (ce.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigPagamentoRestoPagar.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("evento", evento.getId());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        q.setParameter("tipoResto", tipoResto.name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (ce.getId() != null) {
            q.setParameter("config", ce.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public ConfigPagamentoRestoPagar recuperaEventoRestoPorContaDespesa(Conta c, TipoLancamento tipoLancamento, Date data, TipoContaDespesa tipoContaDespesa, TipoRestosProcessado tipoRestosProcessado) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(tipoRestosProcessado, "Tipo de Restos não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(c.getId(), " A conta de Receita esta vazia ");
        try {
            return recuperaConfiguracaoPagamentoResto(c, tipoLancamento, data, tipoContaDespesa,tipoRestosProcessado);

        } catch (NoResultException nr) {
            msgErro = "Configuração contábil não encontrada para a conta: " + c + "; tipo conta de despesa: " + tipoContaDespesa.getDescricao() + "; lLançamento " + tipoLancamento.getDescricao() + "; Tipo de Restos: "+tipoRestosProcessado.getDescricao()+" na data: " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigPagamentoRestoPagar recuperaConfiguracaoPagamentoResto(Conta c, TipoLancamento tipoLancamento, Date data, TipoContaDespesa tipoContaDespesa,TipoRestosProcessado tipoRestosProcessado) throws Exception {
        String sql = "SELECT CE.*, CEOPG.* "
                + " FROM CONFIGPAGAMENTORESTOPAGAR CEOPG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CEOPG.ID = CE.ID"
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
                + " INNER JOIN configpagrespagcontdesp CC ON CC.CONFIGPAGAMENTORESTOPAGAR_ID = CEOPG.ID"
                + " INNER JOIN CONTA CO ON CC.CONTADESPESA_ID = CO.ID"
                + " WHERE CO.CODIGO = :conta "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND CEOPG.tipoRestosProcessados = :tipoResto"
                + " AND CEOPG.TIPOCONTADESPESA = :tipoConta ";

        Query q = em.createNativeQuery(sql, ConfigPagamentoRestoPagar.class);
        q.setParameter("conta", c.getCodigo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        q.setParameter("tipoResto", tipoRestosProcessado.name());
        return (ConfigPagamentoRestoPagar) q.getSingleResult();
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void meusalvar(ConfigPagamentoRestoPagar configPagamentRestoPagaroNaoAlterado, ConfigPagamentoRestoPagar selecionado) {
        verifcaAlteracoesEvento(configPagamentRestoPagaroNaoAlterado, selecionado);
        if (selecionado.getId() == null) {
            salvarNovo(selecionado);
        } else {
            salvar(selecionado);
        }
    }

    private void verifcaAlteracoesEvento(ConfigPagamentoRestoPagar configPagamentRestoPagarNaoAlterado, ConfigPagamentoRestoPagar selecionado) {
        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configPagamentRestoPagarNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        if (!configPagamentRestoPagarNaoAlterado.getTipoRestosProcessados().equals(selecionado.getTipoRestosProcessados())) {
            alterou = true;
        }
        for (ConfigPagResPagContDesp configPagResPagContDesp : selecionado.getConfigPagResPagContDesp()) {
            if (!configPagamentRestoPagarNaoAlterado.getConfigPagResPagContDesp().contains(configPagResPagContDesp)) {
                alterou = true;
            }
        }
        if (!configPagamentRestoPagarNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configPagamentRestoPagarNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configPagamentRestoPagarNaoAlterado.getEventoContabil(), configPagamentRestoPagarNaoAlterado.getId(), configPagamentRestoPagarNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            //System.out.println("alterou alguma coisa vai gerar reprocessamento");
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }

    public void encerrarVigencia(ConfigPagamentoRestoPagar entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
