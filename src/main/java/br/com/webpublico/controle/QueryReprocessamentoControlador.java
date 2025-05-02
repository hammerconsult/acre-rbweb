package br.com.webpublico.controle;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "queryReprocessamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "querys-reprocessamento", pattern = "/reprocessamento/querys/", viewId = "/faces/financeiro/orcamentario/query-reprocessamento/edita.xhtml")
})
public class QueryReprocessamentoControlador {

    protected static final Logger logger = LoggerFactory.getLogger(QueryReprocessamentoControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    private QueryReprocessamentoService queryReprocessamentoService;
    private String reprocessamentoFinanceiro;
    //Reprocessamento Divida Ativa
    private String reprocessamentoDividaAtiva;
    private String reprocessamentoDividaAtivaReceita;
    private String reprocessamentoDividaAtivaEstornoReceita;

    private String reprocessamentoDividaPublica;
    //Reprocessamento Extraorçamentário
    private String reprocessamentoExtraorcamentarioReceita;
    private String reprocessamentoExtraorcamentarioReceitaEstorno;
    private String reprocessamentoExtraorcamentarioPagamento;
    private String reprocessamentoExtraorcamentarioPagamentoEstorno;
    private String reprocessamentoExtraorcamentarioAjuste;
    private String reprocessamentoExtraorcamentarioAjusteEstorno;
    //Reprocessamento Orçamentário
    private String reprocessamentoOrcamentarioDotacao;
    private String reprocessamentoOrcamentarioSolicitacoEmpenho;
    private String reprocessamentoOrcamentarioAlteracaoOrcamentaria;
    private String reprocessamentoOrcamentarioEmpenhado;
    private String reprocessamentoOrcamentarioLiquidado;
    private String reprocessamentoOrcamentarioPago;
    private String reprocessamentoOrcamentarioReservaDotacao;
    private String reprocessamentoOrcamentarioReservadoPorLicitacaoNormal;
    private String reprocessamentoOrcamentarioCancelamentoReservaDotacao;
    private String reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno;
    //Reprocessamento Patrimonial
    private String reprocessamentoBensMoveis;
    private String reprocessamentoBensImoveis;
    private String reprocessamentoBensIntangiveis;
    private String reprocessamentoBensEstoque;
    //Reprocessamento Creditos a Receber
    private String reprocessamentoCreditoReceber;
    private String reprocessamentoCreditoReceberReceita;
    private String reprocessamentoCreditoReceberReceitaEstorno;

    private String queryOccConta;
    //Conciliação Contábil
    private String querySaldoDisponibidadeCaixaBruta;
    private String querySaldoCreditoReceber;
    private String querySaldoNaturezaTipoGrupoMaterial;
    private String querySaldoDividaAtiva;
    private String querySaldoGrupoBemMovel;
    private String querySaldoGrupoBemImovel;
    private String querySaldoCategoriaDividaPublica;
    private String querySaldoPassivoAtuarial;
    private String queryMovimentoReceitaRealizada;
    private String queryMovimentoLiquidacao;
    private String queryMovimentoLiquidacaoPorConta;
    private String queryMovimentoGrupomaterial;
    private String queryMovimentoCategoriaDividaPublica;

    public QueryReprocessamentoControlador() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        try {
            queryReprocessamentoService = (QueryReprocessamentoService) ap.getBean("queryReprocessamentoService");
        } catch (Exception ex) {
            logger.trace("Não foi possível recuperar o bean das querys de reprocessamento.");
        }
    }

    @URLAction(mappingId = "querys-reprocessamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        if (!getUsuarioCorrente().hasRole("ROLE_ADMIN")) {
            FacesUtil.redirecionamentoInterno("/");
        }
        buscarConsultasDoService();
    }

    private void buscarConsultasDoService() {
        reprocessamentoFinanceiro = queryReprocessamentoService.getReprocessamentoFinanceiro();
        reprocessamentoDividaPublica = queryReprocessamentoService.getReprocessamentoDividaPublica();
        reprocessamentoExtraorcamentarioReceita = queryReprocessamentoService.getReprocessamentoExtraorcamentarioReceita();
        reprocessamentoExtraorcamentarioReceitaEstorno = queryReprocessamentoService.getReprocessamentoExtraorcamentarioReceitaEstorno();
        reprocessamentoExtraorcamentarioPagamento = queryReprocessamentoService.getReprocessamentoExtraorcamentarioPagamento();
        reprocessamentoExtraorcamentarioPagamentoEstorno = queryReprocessamentoService.getReprocessamentoExtraorcamentarioPagamentoEstorno();
        reprocessamentoExtraorcamentarioAjuste = queryReprocessamentoService.getReprocessamentoExtraorcamentarioAjuste();
        reprocessamentoExtraorcamentarioAjusteEstorno = queryReprocessamentoService.getReprocessamentoExtraorcamentarioAjusteEstorno();
        reprocessamentoOrcamentarioDotacao = queryReprocessamentoService.getReprocessamentoOrcamentarioDotacao();
        reprocessamentoOrcamentarioSolicitacoEmpenho = queryReprocessamentoService.getReprocessamentoOrcamentarioSolicitacoEmpenho();
        reprocessamentoOrcamentarioAlteracaoOrcamentaria = queryReprocessamentoService.getReprocessamentoOrcamentarioAlteracaoOrcamentaria();
        reprocessamentoOrcamentarioEmpenhado = queryReprocessamentoService.getReprocessamentoOrcamentarioEmpenhado();
        reprocessamentoOrcamentarioLiquidado = queryReprocessamentoService.getReprocessamentoOrcamentarioLiquidado();
        reprocessamentoOrcamentarioPago = queryReprocessamentoService.getReprocessamentoOrcamentarioPago();
        reprocessamentoOrcamentarioReservaDotacao = queryReprocessamentoService.getReprocessamentoOrcamentarioReservaDotacao();
        reprocessamentoOrcamentarioReservadoPorLicitacaoNormal = queryReprocessamentoService.getReprocessamentoOrcamentarioReservadoPorLicitacaoNormal();
        reprocessamentoOrcamentarioCancelamentoReservaDotacao = queryReprocessamentoService.getReprocessamentoOrcamentarioCancelamentoReservaDotacao();
        reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno = queryReprocessamentoService.getReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno();
        reprocessamentoBensMoveis = queryReprocessamentoService.getReprocessamentoBensMoveis();
        reprocessamentoBensImoveis = queryReprocessamentoService.getReprocessamentoBensImoveis();
        reprocessamentoBensIntangiveis = queryReprocessamentoService.getReprocessamentoBensIntangiveis();
        reprocessamentoBensEstoque = queryReprocessamentoService.getReprocessamentoBensEstoque();
        reprocessamentoCreditoReceber = queryReprocessamentoService.getReprocessamentoCreditoReceber();
        reprocessamentoCreditoReceberReceita = queryReprocessamentoService.getReprocessamentoCreditoReceberReceita();
        reprocessamentoCreditoReceberReceitaEstorno = queryReprocessamentoService.getReprocessamentoCreditoReceberReceitaEstorno();
        reprocessamentoDividaAtiva = queryReprocessamentoService.getReprocessamentoDividaAtiva();
        reprocessamentoDividaAtivaReceita = queryReprocessamentoService.getReprocessamentoDividaAtivaReceita();
        reprocessamentoDividaAtivaEstornoReceita = queryReprocessamentoService.getReprocessamentoDividaAtivaEstornoReceita();
        queryOccConta = queryReprocessamentoService.getQueryOccConta();
        querySaldoDisponibidadeCaixaBruta = queryReprocessamentoService.getQuerySaldoDisponibidadeCaixaBruta();
        querySaldoCreditoReceber = queryReprocessamentoService.getQuerySaldoCreditoReceber();
        querySaldoNaturezaTipoGrupoMaterial = queryReprocessamentoService.getQuerySaldoNaturezaTipoGrupoMaterial();
        querySaldoDividaAtiva = queryReprocessamentoService.getQuerySaldoDividaAtiva();
        querySaldoGrupoBemMovel = queryReprocessamentoService.getQuerySaldoGrupoBemMovel();
        querySaldoGrupoBemImovel = queryReprocessamentoService.getQuerySaldoGrupoBemImovel();
        querySaldoCategoriaDividaPublica = queryReprocessamentoService.getQuerySaldoCategoriaDividaPublica();
        querySaldoPassivoAtuarial = queryReprocessamentoService.getQuerySaldoPassivoAtuarial();
        queryMovimentoReceitaRealizada = queryReprocessamentoService.getQueryMovimentoReceitaRealizada();
        queryMovimentoLiquidacao = queryReprocessamentoService.getQueryMovimentoLiquidacao();
        queryMovimentoLiquidacaoPorConta = queryReprocessamentoService.getQueryMovimentoLiquidacaoPorConta();
        queryMovimentoGrupomaterial = queryReprocessamentoService.getQueryMovimentoGrupoMaterial();
        queryMovimentoCategoriaDividaPublica = queryReprocessamentoService.getQueryMovimentoCategoriaDividaPublica();
    }

    public void salvar() {
        salvarSemRedirecionar();
        FacesUtil.addOperacaoRealizada("Querys salvas com sucesso.");
        FacesUtil.redirecionamentoInterno("/reprocessamento/querys/");
    }

    private void salvarSemRedirecionar() {
        queryReprocessamentoService.setReprocessamentoFinanceiro(reprocessamentoFinanceiro);
        queryReprocessamentoService.setReprocessamentoDividaPublica(reprocessamentoDividaPublica);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioReceita(reprocessamentoExtraorcamentarioReceita);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioReceitaEstorno(reprocessamentoExtraorcamentarioReceitaEstorno);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioPagamento(reprocessamentoExtraorcamentarioPagamento);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioPagamentoEstorno(reprocessamentoExtraorcamentarioPagamentoEstorno);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioAjuste(reprocessamentoExtraorcamentarioAjuste);
        queryReprocessamentoService.setReprocessamentoExtraorcamentarioAjusteEstorno(reprocessamentoExtraorcamentarioAjusteEstorno);
        queryReprocessamentoService.setReprocessamentoOrcamentarioDotacao(reprocessamentoOrcamentarioDotacao);
        queryReprocessamentoService.setReprocessamentoOrcamentarioSolicitacoEmpenho(reprocessamentoOrcamentarioSolicitacoEmpenho);
        queryReprocessamentoService.setReprocessamentoOrcamentarioAlteracaoOrcamentaria(reprocessamentoOrcamentarioAlteracaoOrcamentaria);
        queryReprocessamentoService.setReprocessamentoOrcamentarioEmpenhado(reprocessamentoOrcamentarioEmpenhado);
        queryReprocessamentoService.setReprocessamentoOrcamentarioLiquidado(reprocessamentoOrcamentarioLiquidado);
        queryReprocessamentoService.setReprocessamentoOrcamentarioPago(reprocessamentoOrcamentarioPago);
        queryReprocessamentoService.setReprocessamentoOrcamentarioReservaDotacao(reprocessamentoOrcamentarioReservaDotacao);
        queryReprocessamentoService.setReprocessamentoOrcamentarioReservadoPorLicitacaoNormal(reprocessamentoOrcamentarioReservadoPorLicitacaoNormal);
        queryReprocessamentoService.setReprocessamentoOrcamentarioCancelamentoReservaDotacao(reprocessamentoOrcamentarioCancelamentoReservaDotacao);
        queryReprocessamentoService.setReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno(reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno);
        queryReprocessamentoService.setReprocessamentoBensMoveis(reprocessamentoBensMoveis);
        queryReprocessamentoService.setReprocessamentoBensImoveis(reprocessamentoBensImoveis);
        queryReprocessamentoService.setReprocessamentoBensIntangiveis(reprocessamentoBensIntangiveis);
        queryReprocessamentoService.setReprocessamentoBensEstoque(reprocessamentoBensEstoque);
        queryReprocessamentoService.setReprocessamentoCreditoReceber(reprocessamentoCreditoReceber);
        queryReprocessamentoService.setReprocessamentoCreditoReceberReceita(reprocessamentoCreditoReceberReceita);
        queryReprocessamentoService.setReprocessamentoCreditoReceberReceitaEstorno(reprocessamentoCreditoReceberReceitaEstorno);
        queryReprocessamentoService.setReprocessamentoDividaAtiva(reprocessamentoDividaAtiva);
        queryReprocessamentoService.setReprocessamentoDividaAtivaReceita(reprocessamentoDividaAtivaReceita);
        queryReprocessamentoService.setReprocessamentoDividaAtivaEstornoReceita(reprocessamentoDividaAtivaEstornoReceita);
        queryReprocessamentoService.setQueryOccConta(queryOccConta);
        queryReprocessamentoService.setQuerySaldoDisponibidadeCaixaBruta(querySaldoDisponibidadeCaixaBruta);
        queryReprocessamentoService.setQuerySaldoCreditoReceber(querySaldoCreditoReceber);
        queryReprocessamentoService.setQuerySaldoNaturezaTipoGrupoMaterial(querySaldoNaturezaTipoGrupoMaterial);
        queryReprocessamentoService.setQuerySaldoDividaAtiva(querySaldoDividaAtiva);
        queryReprocessamentoService.setQuerySaldoGrupoBemMovel(querySaldoGrupoBemMovel);
        queryReprocessamentoService.setQuerySaldoGrupoBemImovel(querySaldoGrupoBemImovel);
        queryReprocessamentoService.setQuerySaldoCategoriaDividaPublica(querySaldoCategoriaDividaPublica);
        queryReprocessamentoService.setQuerySaldoPassivoAtuarial(querySaldoPassivoAtuarial);
        queryReprocessamentoService.setQueryMovimentoReceitaRealizada(queryMovimentoReceitaRealizada);
        queryReprocessamentoService.setQueryMovimentoLiquidacao(queryMovimentoLiquidacao);
        queryReprocessamentoService.setQueryMovimentoLiquidacaoPorConta(queryMovimentoLiquidacaoPorConta);
        queryReprocessamentoService.setQueryMovimentoGrupoMaterial(queryMovimentoGrupomaterial);
        queryReprocessamentoService.setQueryMovimentoCategoriaDividaPublica(queryMovimentoCategoriaDividaPublica);
    }

    public void persistir() {
        salvarSemRedirecionar();
        queryReprocessamentoService.atualizarItemProcesso();
        queryReprocessamentoService.salvarItemProcesso();
        FacesUtil.addOperacaoRealizada("Querys persistidas com sucesso.");
        FacesUtil.redirecionamentoInterno("/reprocessamento/querys/");
    }


    public void limparConsultas() {
        queryReprocessamentoService.limparConsultas();
        queryReprocessamentoService.limparConsultasItemProcesso();
        novo();
    }

    public void recarregarConsultas() {
        queryReprocessamentoService.carregarConsultas();
        buscarConsultasDoService();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public String getReprocessamentoFinanceiro() {
        return reprocessamentoFinanceiro;
    }

    public void setReprocessamentoFinanceiro(String reprocessamentoFinanceiro) {
        this.reprocessamentoFinanceiro = reprocessamentoFinanceiro;
    }

    public String getReprocessamentoDividaPublica() {
        return reprocessamentoDividaPublica;
    }

    public void setReprocessamentoDividaPublica(String reprocessamentoDividaPublica) {
        this.reprocessamentoDividaPublica = reprocessamentoDividaPublica;
    }

    public String getReprocessamentoExtraorcamentarioReceita() {
        return reprocessamentoExtraorcamentarioReceita;
    }

    public void setReprocessamentoExtraorcamentarioReceita(String reprocessamentoExtraorcamentarioReceita) {
        this.reprocessamentoExtraorcamentarioReceita = reprocessamentoExtraorcamentarioReceita;
    }

    public String getReprocessamentoExtraorcamentarioReceitaEstorno() {
        return reprocessamentoExtraorcamentarioReceitaEstorno;
    }

    public void setReprocessamentoExtraorcamentarioReceitaEstorno(String reprocessamentoExtraorcamentarioReceitaEstorno) {
        this.reprocessamentoExtraorcamentarioReceitaEstorno = reprocessamentoExtraorcamentarioReceitaEstorno;
    }

    public String getReprocessamentoExtraorcamentarioPagamento() {
        return reprocessamentoExtraorcamentarioPagamento;
    }

    public void setReprocessamentoExtraorcamentarioPagamento(String reprocessamentoExtraorcamentarioPagamento) {
        this.reprocessamentoExtraorcamentarioPagamento = reprocessamentoExtraorcamentarioPagamento;
    }

    public String getReprocessamentoExtraorcamentarioPagamentoEstorno() {
        return reprocessamentoExtraorcamentarioPagamentoEstorno;
    }

    public void setReprocessamentoExtraorcamentarioPagamentoEstorno(String reprocessamentoExtraorcamentarioPagamentoEstorno) {
        this.reprocessamentoExtraorcamentarioPagamentoEstorno = reprocessamentoExtraorcamentarioPagamentoEstorno;
    }

    public String getReprocessamentoExtraorcamentarioAjuste() {
        return reprocessamentoExtraorcamentarioAjuste;
    }

    public void setReprocessamentoExtraorcamentarioAjuste(String reprocessamentoExtraorcamentarioAjuste) {
        this.reprocessamentoExtraorcamentarioAjuste = reprocessamentoExtraorcamentarioAjuste;
    }

    public String getReprocessamentoExtraorcamentarioAjusteEstorno() {
        return reprocessamentoExtraorcamentarioAjusteEstorno;
    }

    public void setReprocessamentoExtraorcamentarioAjusteEstorno(String reprocessamentoExtraorcamentarioAjusteEstorno) {
        this.reprocessamentoExtraorcamentarioAjusteEstorno = reprocessamentoExtraorcamentarioAjusteEstorno;
    }

    public String getReprocessamentoOrcamentarioDotacao() {
        return reprocessamentoOrcamentarioDotacao;
    }

    public void setReprocessamentoOrcamentarioDotacao(String reprocessamentoOrcamentarioDotacao) {
        this.reprocessamentoOrcamentarioDotacao = reprocessamentoOrcamentarioDotacao;
    }

    public String getReprocessamentoOrcamentarioSolicitacoEmpenho() {
        return reprocessamentoOrcamentarioSolicitacoEmpenho;
    }

    public void setReprocessamentoOrcamentarioSolicitacoEmpenho(String reprocessamentoOrcamentarioSolicitacoEmpenho) {
        this.reprocessamentoOrcamentarioSolicitacoEmpenho = reprocessamentoOrcamentarioSolicitacoEmpenho;
    }

    public String getReprocessamentoOrcamentarioAlteracaoOrcamentaria() {
        return reprocessamentoOrcamentarioAlteracaoOrcamentaria;
    }

    public void setReprocessamentoOrcamentarioAlteracaoOrcamentaria(String reprocessamentoOrcamentarioAlteracaoOrcamentaria) {
        this.reprocessamentoOrcamentarioAlteracaoOrcamentaria = reprocessamentoOrcamentarioAlteracaoOrcamentaria;
    }

    public String getReprocessamentoOrcamentarioEmpenhado() {
        return reprocessamentoOrcamentarioEmpenhado;
    }

    public void setReprocessamentoOrcamentarioEmpenhado(String reprocessamentoOrcamentarioEmpenhado) {
        this.reprocessamentoOrcamentarioEmpenhado = reprocessamentoOrcamentarioEmpenhado;
    }

    public String getReprocessamentoOrcamentarioLiquidado() {
        return reprocessamentoOrcamentarioLiquidado;
    }

    public void setReprocessamentoOrcamentarioLiquidado(String reprocessamentoOrcamentarioLiquidado) {
        this.reprocessamentoOrcamentarioLiquidado = reprocessamentoOrcamentarioLiquidado;
    }

    public String getReprocessamentoOrcamentarioPago() {
        return reprocessamentoOrcamentarioPago;
    }

    public void setReprocessamentoOrcamentarioPago(String reprocessamentoOrcamentarioPago) {
        this.reprocessamentoOrcamentarioPago = reprocessamentoOrcamentarioPago;
    }

    public String getReprocessamentoOrcamentarioReservaDotacao() {
        return reprocessamentoOrcamentarioReservaDotacao;
    }

    public void setReprocessamentoOrcamentarioReservaDotacao(String reprocessamentoOrcamentarioReservaDotacao) {
        this.reprocessamentoOrcamentarioReservaDotacao = reprocessamentoOrcamentarioReservaDotacao;
    }

    public String getReprocessamentoOrcamentarioReservadoPorLicitacaoNormal() {
        return reprocessamentoOrcamentarioReservadoPorLicitacaoNormal;
    }

    public void setReprocessamentoOrcamentarioReservadoPorLicitacaoNormal(String reprocessamentoOrcamentarioReservadoPorLicitacaoNormal) {
        this.reprocessamentoOrcamentarioReservadoPorLicitacaoNormal = reprocessamentoOrcamentarioReservadoPorLicitacaoNormal;
    }

    public String getReprocessamentoOrcamentarioCancelamentoReservaDotacao() {
        return reprocessamentoOrcamentarioCancelamentoReservaDotacao;
    }

    public void setReprocessamentoOrcamentarioCancelamentoReservaDotacao(String reprocessamentoOrcamentarioCancelamentoReservaDotacao) {
        this.reprocessamentoOrcamentarioCancelamentoReservaDotacao = reprocessamentoOrcamentarioCancelamentoReservaDotacao;
    }

    public String getReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno() {
        return reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno;
    }

    public void setReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno(String reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno) {
        this.reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno = reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno;
    }

    public String getReprocessamentoBensMoveis() {
        return reprocessamentoBensMoveis;
    }

    public void setReprocessamentoBensMoveis(String reprocessamentoBensMoveis) {
        this.reprocessamentoBensMoveis = reprocessamentoBensMoveis;
    }

    public String getReprocessamentoBensImoveis() {
        return reprocessamentoBensImoveis;
    }

    public void setReprocessamentoBensImoveis(String reprocessamentoBensImoveis) {
        this.reprocessamentoBensImoveis = reprocessamentoBensImoveis;
    }

    public String getReprocessamentoBensIntangiveis() {
        return reprocessamentoBensIntangiveis;
    }

    public void setReprocessamentoBensIntangiveis(String reprocessamentoBensIntangiveis) {
        this.reprocessamentoBensIntangiveis = reprocessamentoBensIntangiveis;
    }

    public String getReprocessamentoBensEstoque() {
        return reprocessamentoBensEstoque;
    }

    public void setReprocessamentoBensEstoque(String reprocessamentoBensEstoque) {
        this.reprocessamentoBensEstoque = reprocessamentoBensEstoque;
    }

    public String getReprocessamentoCreditoReceber() {
        return reprocessamentoCreditoReceber;
    }

    public void setReprocessamentoCreditoReceber(String reprocessamentoCreditoReceber) {
        this.reprocessamentoCreditoReceber = reprocessamentoCreditoReceber;
    }

    public String getReprocessamentoCreditoReceberReceita() {
        return reprocessamentoCreditoReceberReceita;
    }

    public void setReprocessamentoCreditoReceberReceita(String reprocessamentoCreditoReceberReceita) {
        this.reprocessamentoCreditoReceberReceita = reprocessamentoCreditoReceberReceita;
    }

    public String getReprocessamentoCreditoReceberReceitaEstorno() {
        return reprocessamentoCreditoReceberReceitaEstorno;
    }

    public void setReprocessamentoCreditoReceberReceitaEstorno(String reprocessamentoCreditoReceberReceitaEstorno) {
        this.reprocessamentoCreditoReceberReceitaEstorno = reprocessamentoCreditoReceberReceitaEstorno;
    }

    public String getReprocessamentoDividaAtiva() {
        return reprocessamentoDividaAtiva;
    }

    public void setReprocessamentoDividaAtiva(String reprocessamentoDividaAtiva) {
        this.reprocessamentoDividaAtiva = reprocessamentoDividaAtiva;
    }

    public String getQueryOccConta() {
        return queryOccConta;
    }

    public void setQueryOccConta(String queryOccConta) {
        this.queryOccConta = queryOccConta;
    }

    public String getReprocessamentoDividaAtivaReceita() {
        return reprocessamentoDividaAtivaReceita;
    }

    public void setReprocessamentoDividaAtivaReceita(String reprocessamentoDividaAtivaReceita) {
        this.reprocessamentoDividaAtivaReceita = reprocessamentoDividaAtivaReceita;
    }

    public String getReprocessamentoDividaAtivaEstornoReceita() {
        return reprocessamentoDividaAtivaEstornoReceita;
    }

    public void setReprocessamentoDividaAtivaEstornoReceita(String reprocessamentoDividaAtivaEstornoReceita) {
        this.reprocessamentoDividaAtivaEstornoReceita = reprocessamentoDividaAtivaEstornoReceita;
    }

    public String getQuerySaldoDisponibidadeCaixaBruta() {
        return querySaldoDisponibidadeCaixaBruta;
    }

    public void setQuerySaldoDisponibidadeCaixaBruta(String querySaldoDisponibidadeCaixaBruta) {
        this.querySaldoDisponibidadeCaixaBruta = querySaldoDisponibidadeCaixaBruta;
    }

    public String getQuerySaldoCreditoReceber() {
        return querySaldoCreditoReceber;
    }

    public void setQuerySaldoCreditoReceber(String querySaldoCreditoReceber) {
        this.querySaldoCreditoReceber = querySaldoCreditoReceber;
    }

    public String getQuerySaldoNaturezaTipoGrupoMaterial() {
        return querySaldoNaturezaTipoGrupoMaterial;
    }

    public void setQuerySaldoNaturezaTipoGrupoMaterial(String querySaldoNaturezaTipoGrupoMaterial) {
        this.querySaldoNaturezaTipoGrupoMaterial = querySaldoNaturezaTipoGrupoMaterial;
    }

    public String getQuerySaldoDividaAtiva() {
        return querySaldoDividaAtiva;
    }

    public void setQuerySaldoDividaAtiva(String querySaldoDividaAtiva) {
        this.querySaldoDividaAtiva = querySaldoDividaAtiva;
    }

    public String getQuerySaldoGrupoBemMovel() {
        return querySaldoGrupoBemMovel;
    }

    public void setQuerySaldoGrupoBemMovel(String querySaldoGrupoBemMovel) {
        this.querySaldoGrupoBemMovel = querySaldoGrupoBemMovel;
    }

    public String getQuerySaldoGrupoBemImovel() {
        return querySaldoGrupoBemImovel;
    }

    public void setQuerySaldoGrupoBemImovel(String querySaldoGrupoBemImovel) {
        this.querySaldoGrupoBemImovel = querySaldoGrupoBemImovel;
    }

    public String getQuerySaldoCategoriaDividaPublica() {
        return querySaldoCategoriaDividaPublica;
    }

    public void setQuerySaldoCategoriaDividaPublica(String querySaldoCategoriaDividaPublica) {
        this.querySaldoCategoriaDividaPublica = querySaldoCategoriaDividaPublica;
    }

    public String getQuerySaldoPassivoAtuarial() {
        return querySaldoPassivoAtuarial;
    }

    public void setQuerySaldoPassivoAtuarial(String querySaldoPassivoAtuarial) {
        this.querySaldoPassivoAtuarial = querySaldoPassivoAtuarial;
    }

    public String getQueryMovimentoReceitaRealizada() {
        return queryMovimentoReceitaRealizada;
    }

    public void setQueryMovimentoReceitaRealizada(String queryMovimentoReceitaRealizada) {
        this.queryMovimentoReceitaRealizada = queryMovimentoReceitaRealizada;
    }

    public String getQueryMovimentoLiquidacao() {
        return queryMovimentoLiquidacao;
    }

    public void setQueryMovimentoLiquidacao(String queryMovimentoLiquidacao) {
        this.queryMovimentoLiquidacao = queryMovimentoLiquidacao;
    }

    public String getQueryMovimentoLiquidacaoPorConta() {
        return queryMovimentoLiquidacaoPorConta;
    }

    public void setQueryMovimentoLiquidacaoPorConta(String queryMovimentoLiquidacaoPorConta) {
        this.queryMovimentoLiquidacaoPorConta = queryMovimentoLiquidacaoPorConta;
    }

    public String getQueryMovimentoGrupomaterial() {
        return queryMovimentoGrupomaterial;
    }

    public void setQueryMovimentoGrupomaterial(String queryMovimentoGrupomaterial) {
        this.queryMovimentoGrupomaterial = queryMovimentoGrupomaterial;
    }

    public String getQueryMovimentoCategoriaDividaPublica() {
        return queryMovimentoCategoriaDividaPublica;
    }

    public void setQueryMovimentoCategoriaDividaPublica(String queryMovimentoCategoriaDividaPublica) {
        this.queryMovimentoCategoriaDividaPublica = queryMovimentoCategoriaDividaPublica;
    }
}
