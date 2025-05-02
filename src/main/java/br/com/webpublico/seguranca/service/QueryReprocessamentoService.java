package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.contabil.ItemProcesso;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class QueryReprocessamentoService implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(QueryReprocessamentoService.class);

    @PersistenceContext
    protected transient EntityManager em;
    private String reprocessamentoFinanceiro;
    private String reprocessamentoDividaPublica;
    private String reprocessamentoExtraorcamentarioReceita;
    private String reprocessamentoExtraorcamentarioReceitaEstorno;
    private String reprocessamentoExtraorcamentarioPagamento;
    private String reprocessamentoExtraorcamentarioPagamentoEstorno;
    private String reprocessamentoExtraorcamentarioAjuste;
    private String reprocessamentoExtraorcamentarioAjusteEstorno;
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
    private String reprocessamentoBensMoveis;
    private String reprocessamentoBensImoveis;
    private String reprocessamentoBensIntangiveis;
    private String reprocessamentoBensEstoque;
    private String reprocessamentoCreditoReceber;
    private String reprocessamentoCreditoReceberReceita;
    private String reprocessamentoCreditoReceberReceitaEstorno;
    private String reprocessamentoDividaAtiva;
    private String reprocessamentoDividaAtivaReceita;
    private String reprocessamentoDividaAtivaEstornoReceita;
    private String queryOccConta;
    //Conciliacao Contábil
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
    private String queryMovimentoGrupoMaterial;
    private String queryMovimentoCategoriaDividaPublica;
    private ItemProcesso itemProcesso;

    public QueryReprocessamentoService() {
    }

    @PostConstruct
    public void init() {
        carregarConsultas();
    }

    public void carregarConsultas() {
        itemProcesso = buscarItemProcesso();
        reprocessamentoFinanceiro = itemProcesso.getQuery1();
        reprocessamentoDividaPublica = itemProcesso.getQuery2();
        reprocessamentoExtraorcamentarioReceita = itemProcesso.getQuery3();
        reprocessamentoExtraorcamentarioReceitaEstorno = itemProcesso.getQuery4();
        reprocessamentoExtraorcamentarioPagamento = itemProcesso.getQuery5();
        reprocessamentoExtraorcamentarioPagamentoEstorno = itemProcesso.getQuery6();
        reprocessamentoExtraorcamentarioAjuste = itemProcesso.getQuery7();
        reprocessamentoExtraorcamentarioAjusteEstorno = itemProcesso.getQuery8();
        reprocessamentoOrcamentarioDotacao = itemProcesso.getQuery9();
        reprocessamentoOrcamentarioSolicitacoEmpenho = itemProcesso.getQuery10();
        reprocessamentoOrcamentarioAlteracaoOrcamentaria = itemProcesso.getQuery11();
        reprocessamentoOrcamentarioEmpenhado = itemProcesso.getQuery12();
        reprocessamentoOrcamentarioLiquidado = itemProcesso.getQuery13();
        reprocessamentoOrcamentarioPago = itemProcesso.getQuery14();
        reprocessamentoOrcamentarioReservaDotacao = itemProcesso.getQuery15();
        reprocessamentoOrcamentarioReservadoPorLicitacaoNormal = itemProcesso.getQuery16();
        reprocessamentoOrcamentarioCancelamentoReservaDotacao = itemProcesso.getQuery17();
        reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno = itemProcesso.getQuery18();
        reprocessamentoBensMoveis = itemProcesso.getQuery20();
        reprocessamentoBensImoveis = itemProcesso.getQuery21();
        reprocessamentoBensIntangiveis = itemProcesso.getQuery22();
        reprocessamentoBensEstoque = itemProcesso.getQuery23();
        reprocessamentoCreditoReceber = itemProcesso.getQuery24();
        reprocessamentoCreditoReceberReceita = itemProcesso.getQuery25();
        reprocessamentoCreditoReceberReceitaEstorno = itemProcesso.getQuery26();
        reprocessamentoDividaAtiva = itemProcesso.getQuery27();
        reprocessamentoDividaAtivaReceita = itemProcesso.getQuery28();
        reprocessamentoDividaAtivaEstornoReceita = itemProcesso.getQuery29();
        queryOccConta = itemProcesso.getQuery30();
        querySaldoDisponibidadeCaixaBruta = itemProcesso.getQuery31();
        querySaldoCreditoReceber = itemProcesso.getQuery32();
        querySaldoNaturezaTipoGrupoMaterial = itemProcesso.getQuery33();
        querySaldoDividaAtiva = itemProcesso.getQuery34();
        querySaldoGrupoBemMovel = itemProcesso.getQuery35();
        querySaldoGrupoBemImovel = itemProcesso.getQuery36();
        querySaldoCategoriaDividaPublica = itemProcesso.getQuery37();
        querySaldoPassivoAtuarial = itemProcesso.getQuery38();
        queryMovimentoReceitaRealizada = itemProcesso.getQuery39();
        queryMovimentoLiquidacao = itemProcesso.getQuery40();
        queryMovimentoLiquidacaoPorConta = itemProcesso.getQuery41();
        queryMovimentoGrupoMaterial = itemProcesso.getQuery42();
        queryMovimentoCategoriaDividaPublica = itemProcesso.getQuery43();
    }

    public void limparConsultas() {
        reprocessamentoFinanceiro = "";
        reprocessamentoDividaPublica = "";
        reprocessamentoExtraorcamentarioReceita = "";
        reprocessamentoExtraorcamentarioReceitaEstorno = "";
        reprocessamentoExtraorcamentarioPagamento = "";
        reprocessamentoExtraorcamentarioPagamentoEstorno = "";
        reprocessamentoExtraorcamentarioAjuste = "";
        reprocessamentoExtraorcamentarioAjusteEstorno = "";
        reprocessamentoOrcamentarioDotacao = "";
        reprocessamentoOrcamentarioSolicitacoEmpenho = "";
        reprocessamentoOrcamentarioAlteracaoOrcamentaria = "";
        reprocessamentoOrcamentarioEmpenhado = "";
        reprocessamentoOrcamentarioLiquidado = "";
        reprocessamentoOrcamentarioPago = "";
        reprocessamentoOrcamentarioReservaDotacao = "";
        reprocessamentoOrcamentarioReservadoPorLicitacaoNormal = "";
        reprocessamentoOrcamentarioCancelamentoReservaDotacao = "";
        reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno = "";
        reprocessamentoBensMoveis = "";
        reprocessamentoBensImoveis = "";
        reprocessamentoBensIntangiveis = "";
        reprocessamentoBensEstoque = "";
        reprocessamentoCreditoReceber = "";
        reprocessamentoCreditoReceberReceita = "";
        reprocessamentoCreditoReceberReceitaEstorno = "";
        reprocessamentoDividaAtiva = "";
        reprocessamentoDividaAtivaReceita = "";
        reprocessamentoDividaAtivaEstornoReceita = "";
        queryOccConta = "";
        querySaldoDisponibidadeCaixaBruta = "";
        querySaldoCreditoReceber = "";
        querySaldoNaturezaTipoGrupoMaterial = "";
        querySaldoDividaAtiva = "";
        querySaldoGrupoBemMovel = "";
        querySaldoGrupoBemImovel = "";
        querySaldoCategoriaDividaPublica = "";
        querySaldoPassivoAtuarial = "";
        queryMovimentoReceitaRealizada = "";
        queryMovimentoLiquidacao = "";
        queryMovimentoLiquidacaoPorConta = "";
        queryMovimentoGrupoMaterial = "";
        queryMovimentoCategoriaDividaPublica = "";
    }

    public void limparConsultasItemProcesso() {
        itemProcesso.setQuery1("");
        itemProcesso.setQuery2("");
        itemProcesso.setQuery3("");
        itemProcesso.setQuery4("");
        itemProcesso.setQuery5("");
        itemProcesso.setQuery6("");
        itemProcesso.setQuery7("");
        itemProcesso.setQuery8("");
        itemProcesso.setQuery9("");
        itemProcesso.setQuery10("");
        itemProcesso.setQuery11("");
        itemProcesso.setQuery12("");
        itemProcesso.setQuery13("");
        itemProcesso.setQuery14("");
        itemProcesso.setQuery15("");
        itemProcesso.setQuery16("");
        itemProcesso.setQuery17("");
        itemProcesso.setQuery18("");
        itemProcesso.setQuery19("");
        itemProcesso.setQuery20("");
        itemProcesso.setQuery21("");
        itemProcesso.setQuery22("");
        itemProcesso.setQuery23("");
        itemProcesso.setQuery24("");
        itemProcesso.setQuery25("");
        itemProcesso.setQuery26("");
        itemProcesso.setQuery27("");
        itemProcesso.setQuery28("");
        itemProcesso.setQuery29("");
        itemProcesso.setQuery30("");
        itemProcesso.setQuery31("");
        itemProcesso.setQuery32("");
        itemProcesso.setQuery33("");
        itemProcesso.setQuery34("");
        itemProcesso.setQuery35("");
        itemProcesso.setQuery36("");
        itemProcesso.setQuery38("");
        itemProcesso.setQuery39("");
        itemProcesso.setQuery40("");
        itemProcesso.setQuery41("");
        itemProcesso.setQuery42("");
        itemProcesso.setQuery43("");
    }

    public static QueryReprocessamentoService getService() {
        return (QueryReprocessamentoService) Util.getSpringBeanPeloNome("queryReprocessamentoService");
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

    public String getQuerySaldoPassivoAtuarial() {
        return querySaldoPassivoAtuarial;
    }

    public void setQuerySaldoPassivoAtuarial(String querySaldoPassivoAtuarial) {
        this.querySaldoPassivoAtuarial = querySaldoPassivoAtuarial;
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

    public String getQueryMovimentoGrupoMaterial() {
        return queryMovimentoGrupoMaterial;
    }

    public void setQueryMovimentoGrupoMaterial(String queryMovimentoGrupoMaterial) {
        this.queryMovimentoGrupoMaterial = queryMovimentoGrupoMaterial;
    }

    private ItemProcesso buscarItemProcesso() {
        String sql = " select * from ItemProcesso ";
        Query q = em.createNativeQuery(sql, ItemProcesso.class);
        q.setMaxResults(1);
        try {
            return (ItemProcesso) q.getSingleResult();
        } catch (Exception nre) {
            return new ItemProcesso();
        }
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

    public String getQueryMovimentoReceitaRealizada() {
        return queryMovimentoReceitaRealizada;
    }

    public void setQueryMovimentoReceitaRealizada(String queryMovimentoReceitaRealizada) {
        this.queryMovimentoReceitaRealizada = queryMovimentoReceitaRealizada;
    }

    public String getQueryMovimentoCategoriaDividaPublica() {
        return queryMovimentoCategoriaDividaPublica;
    }

    public void setQueryMovimentoCategoriaDividaPublica(String queryMovimentoCategoriaDividaPublica) {
        this.queryMovimentoCategoriaDividaPublica = queryMovimentoCategoriaDividaPublica;
    }

    public void atualizarItemProcesso() {
        itemProcesso.setQuery1(reprocessamentoFinanceiro);
        itemProcesso.setQuery2(reprocessamentoDividaPublica);
        itemProcesso.setQuery3(reprocessamentoExtraorcamentarioReceita);
        itemProcesso.setQuery4(reprocessamentoExtraorcamentarioReceitaEstorno);
        itemProcesso.setQuery5(reprocessamentoExtraorcamentarioPagamento);
        itemProcesso.setQuery6(reprocessamentoExtraorcamentarioPagamentoEstorno);
        itemProcesso.setQuery7(reprocessamentoExtraorcamentarioAjuste);
        itemProcesso.setQuery8(reprocessamentoExtraorcamentarioAjusteEstorno);
        itemProcesso.setQuery9(reprocessamentoOrcamentarioDotacao);
        itemProcesso.setQuery10(reprocessamentoOrcamentarioSolicitacoEmpenho);
        itemProcesso.setQuery11(reprocessamentoOrcamentarioAlteracaoOrcamentaria);
        itemProcesso.setQuery12(reprocessamentoOrcamentarioEmpenhado);
        itemProcesso.setQuery13(reprocessamentoOrcamentarioLiquidado);
        itemProcesso.setQuery14(reprocessamentoOrcamentarioPago);
        itemProcesso.setQuery15(reprocessamentoOrcamentarioReservaDotacao);
        itemProcesso.setQuery16(reprocessamentoOrcamentarioReservadoPorLicitacaoNormal);
        itemProcesso.setQuery17(reprocessamentoOrcamentarioCancelamentoReservaDotacao);
        itemProcesso.setQuery18(reprocessamentoOrcamentarioReservadoPorLicitacaoEstorno);
        itemProcesso.setQuery20(reprocessamentoBensMoveis);
        itemProcesso.setQuery21(reprocessamentoBensImoveis);
        itemProcesso.setQuery22(reprocessamentoBensIntangiveis);
        itemProcesso.setQuery23(reprocessamentoBensEstoque);
        itemProcesso.setQuery24(reprocessamentoCreditoReceber);
        itemProcesso.setQuery25(reprocessamentoCreditoReceberReceita);
        itemProcesso.setQuery26(reprocessamentoCreditoReceberReceitaEstorno);
        itemProcesso.setQuery27(reprocessamentoDividaAtiva);
        itemProcesso.setQuery28(reprocessamentoDividaAtivaReceita);
        itemProcesso.setQuery29(reprocessamentoDividaAtivaEstornoReceita);
        itemProcesso.setQuery30(queryOccConta);
        itemProcesso.setQuery31(querySaldoDisponibidadeCaixaBruta);
        itemProcesso.setQuery32(querySaldoCreditoReceber);
        itemProcesso.setQuery33(querySaldoNaturezaTipoGrupoMaterial);
        itemProcesso.setQuery34(querySaldoDividaAtiva);
        itemProcesso.setQuery35(querySaldoGrupoBemMovel);
        itemProcesso.setQuery36(querySaldoGrupoBemImovel);
        itemProcesso.setQuery37(querySaldoCategoriaDividaPublica);
        itemProcesso.setQuery38(querySaldoPassivoAtuarial);
        itemProcesso.setQuery39(queryMovimentoReceitaRealizada);
        itemProcesso.setQuery40(queryMovimentoLiquidacao);
        itemProcesso.setQuery41(queryMovimentoLiquidacaoPorConta);
        itemProcesso.setQuery42(queryMovimentoGrupoMaterial);
        itemProcesso.setQuery43(queryMovimentoCategoriaDividaPublica);
    }

    public void salvarItemProcesso() {
        itemProcesso = em.merge(itemProcesso);
    }
}
