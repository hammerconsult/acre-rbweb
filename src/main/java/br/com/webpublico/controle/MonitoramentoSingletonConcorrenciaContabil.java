package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.negocios.ReprocessamentoLancamentoContabilFacade;
import br.com.webpublico.negocios.SaldoContaContabilFacade;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonContaAuxiliar;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Criado por Mateus
 * Data: 20/12/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "monitoramento-singleton-contabil", pattern = "/monitoramento/singleton/contabil/", viewId = "/faces/financeiro/monitoramento.xhtml")
})
public class MonitoramentoSingletonConcorrenciaContabil implements Serializable {

    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SingletonContaAuxiliar singletonContaAuxiliar;
    @EJB
    private ReprocessamentoLancamentoContabilFacade reprocessamentoLancamentoContabilFacade;

    @URLAction(mappingId = "monitoramento-singleton-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }

    public void reiniciarBloqueioMovimentosContabeis() {
        singletonConcorrenciaContabil.reiniciarBloqueioMovimentosContabeis();
    }

    public void reiniciarUnidadesDespesaExtra() {
        singletonConcorrenciaContabil.reiniciarUnidadesDespesaExtra();
    }

    public void reiniciarUnidadesPagamento() {
        singletonConcorrenciaContabil.reiniciarUnidadesPagamento();
    }

    public void reiniciarUnidadesEmpenho() {
        singletonConcorrenciaContabil.reiniciarUnidadesEmpenho();
    }

    public void reiniciarBorderos() {
        singletonConcorrenciaContabil.reiniciarBorderos();
    }

    public void reiniciarSolicitacoesFinanceiras() {
        singletonConcorrenciaContabil.reiniciarSolicitacoesFinanceiras();
    }

    public void reiniciarLiberacoesFinanceiras() {
        singletonConcorrenciaContabil.reiniciarLiberacoesFinanceiras();
    }

    public void reiniciarTransferenciasFinanceiras() {
        singletonConcorrenciaContabil.reiniciarTransferenciasFinanceiras();
    }

    public void reiniciarPagamentosExtras() {
        singletonConcorrenciaContabil.reiniciarPagamentosExtras();
    }

    public void reiniciarReceitasExtras() {
        singletonConcorrenciaContabil.reiniciarReceitasExtras();
    }

    public void reiniciarPagamentos() {
        singletonConcorrenciaContabil.reiniciarPagamentos();
    }

    public void reiniciarLiquidacoes() {
        singletonConcorrenciaContabil.reiniciarLiquidacoes();
    }

    public void reiniciarEmpenhos() {
        singletonConcorrenciaContabil.reiniciarEmpenhos();
    }

    public void reiniciarContasAuxiliares() {
        singletonContaAuxiliar.limparContasAuxiliares();
    }

    public void reiniciarContasDetalhadas() {
        singletonContaAuxiliar.limparContasDetalhadas();
    }

    public Set<Empenho> getEmpenhos() {
        return singletonConcorrenciaContabil.getEmpenhos();
    }

    public Set<Liquidacao> getLiquidacoes() {
        return singletonConcorrenciaContabil.getLiquidacoes();
    }

    public Set<Pagamento> getPagamentos() {
        return singletonConcorrenciaContabil.getPagamentos();
    }

    public Set<PagamentoExtra> getPagamentoExtras() {
        return singletonConcorrenciaContabil.getPagamentoExtras();
    }

    public Set<ReceitaExtra> getReceitaExtras() {
        return singletonConcorrenciaContabil.getReceitaExtras();
    }

    public Set<TransferenciaContaFinanceira> getTransferenciasFinanceira() {
        return singletonConcorrenciaContabil.getTransferenciasFinanceira();
    }

    public Set<LiberacaoCotaFinanceira> getLiberacoesFinanceira() {
        return singletonConcorrenciaContabil.getLiberacoesFinanceira();
    }

    public Set<SolicitacaoCotaFinanceira> getSolicitacoesFinanceira() {
        return singletonConcorrenciaContabil.getSolicitacoesFinanceira();
    }

    public Set<Bordero> getBorderos() {
        return singletonConcorrenciaContabil.getBorderos();
    }

    public Set<UnidadeOrganizacional> getUnidadesOrganizacionaisEmpenho() {
        return singletonConcorrenciaContabil.getUnidadesOrganizacionaisEmpenho();
    }

    public Set<UnidadeOrganizacional> getUnidadesOrganizacionaisPagamento() {
        return singletonConcorrenciaContabil.getUnidadesOrganizacionaisPagamento();
    }

    public Set<UnidadeOrganizacional> getUnidadesOrganizacionaisDespesaExtra() {
        return singletonConcorrenciaContabil.getUnidadesOrganizacionaisDespesaExtra();
    }

    public List<ContaAuxiliar> getContasAuxiliares() {
        return Lists.newArrayList(singletonContaAuxiliar.getContasAuxiliares().values());
    }

    public List<ContaAuxiliarDetalhada> getContasAuxiliaresDetalhadas() {
        return Lists.newArrayList(singletonContaAuxiliar.getContasAuxiliaresDetalhadas().values());
    }

    public void limparSingleton(){
        reprocessamentoLancamentoContabilFacade.limparEMs();
    }
}
