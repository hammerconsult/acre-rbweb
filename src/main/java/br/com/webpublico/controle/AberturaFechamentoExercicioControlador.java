package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteAberturaFechamentoExercicio;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AberturaFechamentoExercicioFacade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ISuperLista;
import br.com.webpublico.util.SuperLista;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/09/14
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-abertura-fechamento-exercicio", pattern = "/abertura-fechamento-exercicio/novo/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/edita.xhtml"),
    @URLMapping(id = "ver-abertura-fechamento-exercicio", pattern = "/abertura-fechamento-exercicio/ver/#{aberturaFechamentoExercicioControlador.id}/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/visualizar.xhtml"),
    @URLMapping(id = "editar-abertura-fechamento-exercicio", pattern = "/abertura-fechamento-exercicio/editar/#{aberturaFechamentoExercicioControlador.id}/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/edita.xhtml"),
    @URLMapping(id = "listar-abertura-fechamento-exercicio", pattern = "/abertura-fechamento-exercicio/listar/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/lista.xhtml"),
    @URLMapping(id = "acompanhamento-abertura-fechamento-exercicio", pattern = "/abertura-fechamento-exercicio/acompanhamento/", viewId = "/faces/financeiro/abertura-fechamento-exercicio/log.xhtml")
})
public class AberturaFechamentoExercicioControlador extends PrettyControlador<AberturaFechamentoExercicio> implements Serializable, CRUD {

    private Boolean pesquisarPorApuracao;
    private Boolean pesquisarPorEncerramento;
    private Boolean pesquisarPorPlanoDeContas;
    private Boolean pesquisarPorTransporteDeSaldo;
    private Boolean pesquisarPorTransporteDeSaldoFinanceiro;
    private Boolean pesquisarPorTransporteDeSaldoContabil;
    private Boolean pesquisarPorTransporteDeSaldoCreditoReceber;
    private Boolean pesquisarPorTransporteDeSaldoDividaAtiva;
    private Boolean pesquisarPorTransporteDeSaldoExtra;
    private Boolean pesquisarPorTransporteDeSaldoDividaPublica;
    private Boolean pesquisarPorTransporteDeReceitaExtra;
    private Boolean pesquisarPorTransporteDeContaAuxiliar;
    private Boolean pesquisarPorAbertura;
    private Boolean pesquisarPorConfiguracoes;
    private Boolean pesquisarPorConfiguracoesRECEITA;
    @EJB
    private AberturaFechamentoExercicioFacade facade;
    //FILTROS
    private List<HierarquiaOrganizacional> listaUnidades;
    private UnidadeGestora unidadeGestora;
    //PRESCRICAO
    private ISuperLista<PrescricaoEmpenho> prescricaoEmpenhos;
    private ISuperLista<PrescricaoEmpenho> prescricaoEmpenhosNaoProcessados;
    //INSCRIÇÃO
    private ISuperLista<InscricaoEmpenho> inscricaoEmpenhos;
    private ISuperLista<InscricaoEmpenho> inscricaoEmpenhosNaoProcessado;
    //RECEITA
    private ISuperLista<ReceitaFechamentoExercicio> receitasARealizar;
    private ISuperLista<ReceitaFechamentoExercicio> reestimativas;
    private ISuperLista<ReceitaFechamentoExercicio> deducoesIniciais;
    private ISuperLista<ReceitaFechamentoExercicio> deducoesReceitaRealizada;
    private ISuperLista<ReceitaFechamentoExercicio> receitaRealizada;
    private ISuperLista<ReceitaExtraFechamentoExercicio> receitasExtras;
    //DESPESA
    private ISuperLista<DotacaoFechamentoExercicio> creditosDisponiveis;
    private ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisSuplementares;
    private ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisEspecial;
    private ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisExtraordinario;
    private ISuperLista<DotacaoFechamentoExercicio> anulacaoDotacao;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorSuperavitFinanceira;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorExcessoDeArrecadacao;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeDotacao;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorOperacaoDeCredito;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorReservaDeContigencia;
    private ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeCredito;
    //ENCERRAMENTO RESTO A PAGAR
    private ISuperLista<DespesaFechamentoExercicio> empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    private ISuperLista<DespesaFechamentoExercicio> empenhosLiquidadosInscritosRestoAPagarProcessados;
    private ISuperLista<DespesaFechamentoExercicio> empenhosCreditoEmpenhadoPago;
    private ISuperLista<DespesaFechamentoExercicio> pagoDosRestosAPagarProcessados;
    private ISuperLista<DespesaFechamentoExercicio> pagoDosRestosAPagarNaoProcessados;
    private ISuperLista<DespesaFechamentoExercicio> canceladoDosRestosAPagarProcessados;
    private ISuperLista<DespesaFechamentoExercicio> canceladoDosRestosAPagarNaoProcessados;
    private ISuperLista<FonteDeRecursoFechamentoExercicio> destinacaoDeRecurso;
    private ISuperLista<ContaFechamentoExercicio> resultadoDiminutivoExercicio;
    private ISuperLista<ContaFechamentoExercicio> resultadoAumentativoExercicio;
    private ISuperLista<TransporteObrigacaoAPagar> obrigacoesAPagar;
    //PLANO DE CONTAS
    private ISuperLista<PlanoDeContasFechamentoExercicio> planoDeContas;
    //CONFIGURACOES
    private ISuperLista<ConfiguracaoEventoFechamentoExercicio> configuracoes;
    //OCC
    private ISuperLista<OCCFechamentoExercicio> occs;
    //TRANPORTE
    private ISuperLista<TransporteDeSaldoFechamentoExercicio> transporteDeSaldo;
    private ISuperLista<TransporteSaldoFinanceiro> transporteDeSaldoFinanceiro;
    private ISuperLista<TransporteCreditoReceber> transporteDeSaldoCreditoReceber;
    private ISuperLista<TransporteDividaAtiva> transporteDeSaldoDividaAtiva;
    private ISuperLista<TransporteExtra> transporteDeSaldoExtra;
    private ISuperLista<TransporteSaldoDividaPublica> transporteDeSaldoDividaPublica;
    private ISuperLista<TransporteSaldoContaAuxiliarDetalhada> transporteDeSaldoDeContasAuxiliares;
    //ABERTURA
    private ISuperLista<AberturaInscricaoResto> inscricaoRestoPagarProcessados;
    private ISuperLista<AberturaInscricaoResto> inscricaoRestoPagarNaoProcessados;
    private ISuperLista<TransporteDeSaldoAbertura> transferenciaResultadoPublico;
    private ISuperLista<TransporteDeSaldoAbertura> transferenciaResultadoPrivado;
    private ISuperLista<TransporteDeSaldoAbertura> transferenciaAjustesPublico;
    private ISuperLista<TransporteDeSaldoAbertura> transferenciaAjustesPrivado;
    private ConfiguracaoContabil configuracaoContabil;
    private AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio;
    private Future<AberturaFechamentoExercicio> future;

    public AberturaFechamentoExercicioControlador() {
        super(AberturaFechamentoExercicio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/abertura-fechamento-exercicio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado = new AberturaFechamentoExercicio();
        selecionado.setDataGeracao(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        listaUnidades = Lists.newArrayList();
        instanciarListas();
        configuracaoContabil = facade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        assistenteAberturaFechamentoExercicio = new AssistenteAberturaFechamentoExercicio();
        pesquisarPorApuracao = false;
        pesquisarPorEncerramento = false;
        pesquisarPorPlanoDeContas = false;
        pesquisarPorTransporteDeSaldo = false;
        pesquisarPorTransporteDeSaldoFinanceiro = false;
        pesquisarPorTransporteDeSaldoContabil = false;
        pesquisarPorTransporteDeSaldoCreditoReceber = false;
        pesquisarPorTransporteDeSaldoDividaAtiva = false;
        pesquisarPorTransporteDeSaldoExtra = false;
        pesquisarPorTransporteDeSaldoDividaPublica = false;
        pesquisarPorTransporteDeReceitaExtra = false;
        pesquisarPorTransporteDeContaAuxiliar = false;
        pesquisarPorAbertura = false;
        pesquisarPorConfiguracoes = false;
        pesquisarPorConfiguracoesRECEITA = false;
    }

    private void instanciarListas() {

        prescricaoEmpenhos = new SuperLista<>(selecionado.getPrescricaoEmpenhosProcessados(), PrescricaoEmpenho.class);
        prescricaoEmpenhosNaoProcessados = new SuperLista<>(selecionado.getPrescricaoEmpenhosNaoProcessados(), PrescricaoEmpenho.class);
        inscricaoEmpenhos = new SuperLista<>(selecionado.getInscricaoEmpenhosProcessados(), InscricaoEmpenho.class);
        inscricaoEmpenhosNaoProcessado = new SuperLista<>(selecionado.getInscricaoEmpenhosNaoProcessados(), InscricaoEmpenho.class);
        receitasARealizar = new SuperLista<>(selecionado.getReceitasARealizar(), ReceitaFechamentoExercicio.class);
        reestimativas = new SuperLista<>(selecionado.getReceitasReestimativas(), ReceitaFechamentoExercicio.class);
        deducoesIniciais = new SuperLista<>(selecionado.getReceitasDeducaoPrevisaoInicial(), ReceitaFechamentoExercicio.class);
        deducoesReceitaRealizada = new SuperLista<>(selecionado.getReceitasDeducaoReceitaRealizada(), ReceitaFechamentoExercicio.class);
        receitaRealizada = new SuperLista<>(selecionado.getReceitasRealizada(), ReceitaFechamentoExercicio.class);
        receitasExtras = new SuperLista<>(selecionado.getReceitasExtras(), ReceitaExtraFechamentoExercicio.class);
        creditosDisponiveis = new SuperLista<>(selecionado.getDotacoes(), DotacaoFechamentoExercicio.class);
        creditosAdicionaisSuplementares = new SuperLista<>(selecionado.getCreditosAdicionaisSuplementares(), DotacaoFechamentoExercicio.class);
        creditosAdicionaisEspecial = new SuperLista<>(selecionado.getCreditosAdicionaisEspecial(), DotacaoFechamentoExercicio.class);
        creditosAdicionaisExtraordinario = new SuperLista<>(selecionado.getCreditosAdicionaisExtraordinario(), DotacaoFechamentoExercicio.class);
        anulacaoDotacao = new SuperLista<>(selecionado.getAnulacaoDotacao(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorSuperavitFinanceira = new SuperLista<>(selecionado.getCretitosAdicionaisPorSuperavitFinanceira(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorExcessoDeArrecadacao = new SuperLista<>(selecionado.getCretitosAdicionaisPorExcessoDeArrecadacao(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorAnulacaoDeDotacao = new SuperLista<>(selecionado.getCretitosAdicionaisPorAnulacaoDeDotacao(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorOperacaoDeCredito = new SuperLista<>(selecionado.getCretitosAdicionaisPorOperacaoDeCredito(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorReservaDeContigencia = new SuperLista<>(selecionado.getCretitosAdicionaisPorReservaDeContigencia(), DotacaoFechamentoExercicio.class);
        cretitosAdicionaisPorAnulacaoDeCredito = new SuperLista<>(selecionado.getCretitosAdicionaisPorAnulacaoDeCredito(), DotacaoFechamentoExercicio.class);
        empenhosALiquidarInscritosRestoAPagarNaoProcessados = new SuperLista<>(selecionado.getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados(), DespesaFechamentoExercicio.class);
        empenhosLiquidadosInscritosRestoAPagarProcessados = new SuperLista<>(selecionado.getEmpenhosLiquidadosInscritosRestoAPagarProcessados(), DespesaFechamentoExercicio.class);
        empenhosCreditoEmpenhadoPago = new SuperLista<>(selecionado.getEmpenhosCreditoEmpenhadoPago(), DespesaFechamentoExercicio.class);
        pagoDosRestosAPagarProcessados = new SuperLista<>(selecionado.getPagoDosRestosAPagarProcessados(), DespesaFechamentoExercicio.class);
        pagoDosRestosAPagarNaoProcessados = new SuperLista<>(selecionado.getPagoDosRestosAPagarNaoProcessados(), DespesaFechamentoExercicio.class);
        canceladoDosRestosAPagarProcessados = new SuperLista<>(selecionado.getCanceladoDosRestosAPagarProcessados(), DespesaFechamentoExercicio.class);
        canceladoDosRestosAPagarNaoProcessados = new SuperLista<>(selecionado.getCanceladoDosRestosAPagarNaoProcessados(), DespesaFechamentoExercicio.class);
        destinacaoDeRecurso = new SuperLista<>(selecionado.getDestinacaoDeRecurso(), FonteDeRecursoFechamentoExercicio.class);
        resultadoDiminutivoExercicio = new SuperLista<>(selecionado.getResultadoDiminutivoExercicio(), ContaFechamentoExercicio.class);
        resultadoAumentativoExercicio = new SuperLista<>(selecionado.getResultadoAumentativoExercicio(), ContaFechamentoExercicio.class);
        obrigacoesAPagar = new SuperLista<>(selecionado.getObrigacoesAPagar(), TransporteObrigacaoAPagar.class);
        planoDeContas = new SuperLista<>(selecionado.getPlanoDeContas(), PlanoDeContasFechamentoExercicio.class);
        configuracoes = new SuperLista<>(selecionado.getConfiguracoes(), ConfiguracaoEventoFechamentoExercicio.class);
        occs = new SuperLista<>(selecionado.getOccs(), OCCFechamentoExercicio.class);
        transporteDeSaldo = new SuperLista<>(selecionado.getTransporteDeSaldo(), TransporteDeSaldoFechamentoExercicio.class);
        transporteDeSaldoFinanceiro = new SuperLista<>(selecionado.getTransporteDeSaldoFinanceiro(), TransporteSaldoFinanceiro.class);
        transporteDeSaldoDeContasAuxiliares = new SuperLista<>(selecionado.getTransporteDeSaldoDeContasAuxiliares(), TransporteSaldoContaAuxiliarDetalhada.class);
        transporteDeSaldoCreditoReceber = new SuperLista<>(selecionado.getTransporteDeSaldoCreditoReceber(), TransporteCreditoReceber.class);
        transporteDeSaldoDividaAtiva = new SuperLista<>(selecionado.getTransporteDeSaldoDividaAtiva(), TransporteDividaAtiva.class);
        transporteDeSaldoExtra = new SuperLista<>(selecionado.getTransporteDeSaldoExtra(), TransporteExtra.class);
        transporteDeSaldoDividaPublica = new SuperLista<>(selecionado.getTransporteDeSaldoDividaPublica(), TransporteSaldoDividaPublica.class);
        inscricaoRestoPagarProcessados = new SuperLista<>(selecionado.getInscricaoRestoPagarProcessados(), AberturaInscricaoResto.class);
        inscricaoRestoPagarNaoProcessados = new SuperLista<>(selecionado.getInscricaoRestoPagarNaoProcessados(), AberturaInscricaoResto.class);
        transferenciaResultadoPublico = new SuperLista<>(selecionado.getTransferenciaResultadoPublico(), TransporteDeSaldoAbertura.class);
        transferenciaResultadoPrivado = new SuperLista<>(selecionado.getTransferenciaResultadoPrivado(), TransporteDeSaldoAbertura.class);
        transferenciaAjustesPublico = new SuperLista<>(selecionado.getTransferenciaAjustesPublico(), TransporteDeSaldoAbertura.class);
        transferenciaAjustesPrivado = new SuperLista<>(selecionado.getTransferenciaAjustesPrivado(), TransporteDeSaldoAbertura.class);
    }

    private void recuperarListas() {
        listaUnidades.clear();

        prescricaoEmpenhos.addAll(selecionado.getPrescricaoEmpenhosProcessados());
        prescricaoEmpenhosNaoProcessados.addAll(selecionado.getPrescricaoEmpenhosNaoProcessados());
        inscricaoEmpenhos.addAll(selecionado.getInscricaoEmpenhosProcessados());
        inscricaoEmpenhosNaoProcessado.addAll(selecionado.getInscricaoEmpenhosNaoProcessados());
        receitasARealizar.addAll(selecionado.getReceitasARealizar());
        reestimativas.addAll(selecionado.getReceitasReestimativas());
        deducoesIniciais.addAll(selecionado.getReceitasDeducaoPrevisaoInicial());
        deducoesReceitaRealizada.addAll(selecionado.getReceitasDeducaoReceitaRealizada());
        receitaRealizada.addAll(selecionado.getReceitasRealizada());
        receitasExtras.addAll(selecionado.getReceitasExtras());
        creditosDisponiveis.addAll(selecionado.getDotacoes());
        creditosAdicionaisSuplementares.addAll(selecionado.getCreditosAdicionaisSuplementares());
        creditosAdicionaisEspecial.addAll(selecionado.getCreditosAdicionaisEspecial());
        creditosAdicionaisExtraordinario.addAll(selecionado.getCreditosAdicionaisExtraordinario());
        anulacaoDotacao.addAll(selecionado.getAnulacaoDotacao());
        cretitosAdicionaisPorSuperavitFinanceira.addAll(selecionado.getCretitosAdicionaisPorSuperavitFinanceira());
        cretitosAdicionaisPorExcessoDeArrecadacao.addAll(selecionado.getCretitosAdicionaisPorExcessoDeArrecadacao());
        cretitosAdicionaisPorAnulacaoDeDotacao.addAll(selecionado.getCretitosAdicionaisPorAnulacaoDeDotacao());
        cretitosAdicionaisPorOperacaoDeCredito.addAll(selecionado.getCretitosAdicionaisPorOperacaoDeCredito());
        cretitosAdicionaisPorReservaDeContigencia.addAll(selecionado.getCretitosAdicionaisPorReservaDeContigencia());
        cretitosAdicionaisPorAnulacaoDeCredito.addAll(selecionado.getCretitosAdicionaisPorAnulacaoDeCredito());
        empenhosALiquidarInscritosRestoAPagarNaoProcessados.addAll(selecionado.getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados());
        empenhosLiquidadosInscritosRestoAPagarProcessados.addAll(selecionado.getEmpenhosLiquidadosInscritosRestoAPagarProcessados());
        empenhosCreditoEmpenhadoPago.addAll(selecionado.getEmpenhosCreditoEmpenhadoPago());
        pagoDosRestosAPagarProcessados.addAll(selecionado.getPagoDosRestosAPagarProcessados());
        pagoDosRestosAPagarNaoProcessados.addAll(selecionado.getPagoDosRestosAPagarNaoProcessados());
        canceladoDosRestosAPagarProcessados.addAll(selecionado.getCanceladoDosRestosAPagarProcessados());
        canceladoDosRestosAPagarNaoProcessados.addAll(selecionado.getCanceladoDosRestosAPagarNaoProcessados());
        destinacaoDeRecurso.addAll(selecionado.getDestinacaoDeRecurso());
        resultadoAumentativoExercicio.addAll(selecionado.getResultadoAumentativoExercicio());
        obrigacoesAPagar.addAll(selecionado.getObrigacoesAPagar());
        resultadoDiminutivoExercicio.addAll(selecionado.getResultadoDiminutivoExercicio());
        planoDeContas.addAll(selecionado.getPlanoDeContas());
        configuracoes.addAll(selecionado.getConfiguracoes());
        occs.addAll(selecionado.getOccs());
        transporteDeSaldo.addAll(selecionado.getTransporteDeSaldo());
        transporteDeSaldoFinanceiro.addAll(selecionado.getTransporteDeSaldoFinanceiro());
        transporteDeSaldoDeContasAuxiliares.addAll(selecionado.getTransporteDeSaldoDeContasAuxiliares());
        transporteDeSaldoCreditoReceber.addAll(selecionado.getTransporteDeSaldoCreditoReceber());
        transporteDeSaldoDividaAtiva.addAll(selecionado.getTransporteDeSaldoDividaAtiva());
        transporteDeSaldoExtra.addAll(selecionado.getTransporteDeSaldoExtra());
        transporteDeSaldoDividaPublica.addAll(selecionado.getTransporteDeSaldoDividaPublica());
        inscricaoRestoPagarProcessados.addAll(selecionado.getInscricaoRestoPagarProcessados());
        inscricaoRestoPagarNaoProcessados.addAll(selecionado.getInscricaoRestoPagarNaoProcessados());
        transferenciaResultadoPublico.addAll(selecionado.getTransferenciaResultadoPublico());
        transferenciaResultadoPrivado.addAll(selecionado.getTransferenciaResultadoPrivado());
        transferenciaAjustesPublico.addAll(selecionado.getTransferenciaAjustesPublico());
        transferenciaAjustesPrivado.addAll(selecionado.getTransferenciaAjustesPrivado());
    }

    private void limparListas() {
        listaUnidades.clear();

        prescricaoEmpenhos.clear();
        prescricaoEmpenhosNaoProcessados.clear();
        inscricaoEmpenhos.clear();
        inscricaoEmpenhosNaoProcessado.clear();
        receitasARealizar.clear();
        reestimativas.clear();
        deducoesIniciais.clear();
        deducoesReceitaRealizada.clear();
        receitaRealizada.clear();
        receitasExtras.clear();
        creditosDisponiveis.clear();
        creditosAdicionaisSuplementares.clear();
        creditosAdicionaisEspecial.clear();
        creditosAdicionaisExtraordinario.clear();
        anulacaoDotacao.clear();
        cretitosAdicionaisPorSuperavitFinanceira.clear();
        cretitosAdicionaisPorExcessoDeArrecadacao.clear();
        cretitosAdicionaisPorAnulacaoDeDotacao.clear();
        cretitosAdicionaisPorOperacaoDeCredito.clear();
        cretitosAdicionaisPorReservaDeContigencia.clear();
        cretitosAdicionaisPorAnulacaoDeCredito.clear();
        empenhosALiquidarInscritosRestoAPagarNaoProcessados.clear();
        empenhosLiquidadosInscritosRestoAPagarProcessados.clear();
        empenhosCreditoEmpenhadoPago.clear();
        pagoDosRestosAPagarProcessados.clear();
        pagoDosRestosAPagarNaoProcessados.clear();
        canceladoDosRestosAPagarProcessados.clear();
        canceladoDosRestosAPagarNaoProcessados.clear();
        destinacaoDeRecurso.clear();
        resultadoAumentativoExercicio.clear();
        obrigacoesAPagar.clear();
        resultadoDiminutivoExercicio.clear();
        planoDeContas.clear();
        configuracoes.clear();
        occs.clear();
        transporteDeSaldo.clear();
        transporteDeSaldoFinanceiro.clear();
        transporteDeSaldoDeContasAuxiliares.clear();
        transporteDeSaldoCreditoReceber.clear();
        transporteDeSaldoDividaAtiva.clear();
        transporteDeSaldoExtra.clear();
        transporteDeSaldoDividaPublica.clear();
        inscricaoRestoPagarProcessados.clear();
        inscricaoRestoPagarNaoProcessados.clear();
        transferenciaResultadoPublico.clear();
        transferenciaResultadoPrivado.clear();
        transferenciaAjustesPublico.clear();
        transferenciaAjustesPrivado.clear();

        selecionado.getPrescricaoEmpenhosProcessados().clear();
        selecionado.getPrescricaoEmpenhosNaoProcessados().clear();
        selecionado.getInscricaoEmpenhosProcessados().clear();
        selecionado.getInscricaoEmpenhosNaoProcessados().clear();
        selecionado.getReceitasARealizar().clear();
        selecionado.getReceitasReestimativas().clear();
        selecionado.getReceitasDeducaoPrevisaoInicial().clear();
        selecionado.getReceitasDeducaoReceitaRealizada().clear();
        selecionado.getReceitasRealizada().clear();
        selecionado.getReceitasExtras().clear();
        selecionado.getDotacoes().clear();
        selecionado.getCreditosAdicionaisSuplementares().clear();
        selecionado.getCreditosAdicionaisEspecial().clear();
        selecionado.getCreditosAdicionaisExtraordinario().clear();
        selecionado.getAnulacaoDotacao().clear();
        selecionado.getCretitosAdicionaisPorSuperavitFinanceira().clear();
        selecionado.getCretitosAdicionaisPorExcessoDeArrecadacao().clear();
        selecionado.getCretitosAdicionaisPorAnulacaoDeDotacao().clear();
        selecionado.getCretitosAdicionaisPorOperacaoDeCredito().clear();
        selecionado.getCretitosAdicionaisPorReservaDeContigencia().clear();
        selecionado.getCretitosAdicionaisPorAnulacaoDeCredito().clear();
        selecionado.getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados().clear();
        selecionado.getEmpenhosLiquidadosInscritosRestoAPagarProcessados().clear();
        selecionado.getEmpenhosCreditoEmpenhadoPago().clear();
        selecionado.getPagoDosRestosAPagarProcessados().clear();
        selecionado.getPagoDosRestosAPagarNaoProcessados().clear();
        selecionado.getCanceladoDosRestosAPagarProcessados().clear();
        selecionado.getCanceladoDosRestosAPagarNaoProcessados().clear();
        selecionado.getDestinacaoDeRecurso().clear();
        selecionado.getResultadoDiminutivoExercicio().clear();
        selecionado.getResultadoAumentativoExercicio().clear();
        selecionado.getObrigacoesAPagar().clear();
        selecionado.getPlanoDeContas().clear();
        selecionado.getConfiguracoes().clear();
        selecionado.getOccs().clear();
        selecionado.getTransporteDeSaldo().clear();
        selecionado.getTransporteDeSaldoFinanceiro().clear();
        selecionado.getTransporteDeSaldoDeContasAuxiliares().clear();
        selecionado.getTransporteDeSaldoCreditoReceber().clear();
        selecionado.getTransporteDeSaldoDividaAtiva().clear();
        selecionado.getTransporteDeSaldoExtra().clear();
        selecionado.getTransporteDeSaldoDividaPublica().clear();
        selecionado.getInscricaoRestoPagarProcessados().clear();
        selecionado.getInscricaoRestoPagarNaoProcessados().clear();
        selecionado.getTransferenciaAjustesPublico().clear();
        selecionado.getTransferenciaAjustesPrivado().clear();
        selecionado.getTransferenciaResultadoPublico().clear();
        selecionado.getTransferenciaResultadoPrivado().clear();
    }

    @URLAction(mappingId = "ver-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-abertura-fechamento-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        listaUnidades = new ArrayList<HierarquiaOrganizacional>();
        instanciarListas();
        recuperarListas();
    }


    //GETTERS E SETTERS

    public ISuperLista<AberturaInscricaoResto> getInscricaoRestoPagarNaoProcessados() {
        return inscricaoRestoPagarNaoProcessados;
    }

    public void setInscricaoRestoPagarNaoProcessados(ISuperLista<AberturaInscricaoResto> inscricaoRestoPagarNaoProcessados) {
        this.inscricaoRestoPagarNaoProcessados = inscricaoRestoPagarNaoProcessados;
    }

    public ISuperLista<AberturaInscricaoResto> getInscricaoRestoPagarProcessados() {
        return inscricaoRestoPagarProcessados;
    }

    public void setInscricaoRestoPagarProcessados(ISuperLista<AberturaInscricaoResto> inscricaoRestoPagarProcessados) {
        this.inscricaoRestoPagarProcessados = inscricaoRestoPagarProcessados;
    }

    public ISuperLista<TransporteDeSaldoFechamentoExercicio> getTransporteDeSaldo() {
        return transporteDeSaldo;
    }

    public void setTransporteDeSaldo(ISuperLista<TransporteDeSaldoFechamentoExercicio> transporteDeSaldo) {
        this.transporteDeSaldo = transporteDeSaldo;
    }

    public Boolean getPesquisarPorConfiguracoesRECEITA() {
        return pesquisarPorConfiguracoesRECEITA;
    }

    public void setPesquisarPorConfiguracoesRECEITA(Boolean pesquisarPorConfiguracoesRECEITA) {
        this.pesquisarPorConfiguracoesRECEITA = pesquisarPorConfiguracoesRECEITA;
    }

    public ISuperLista<ConfiguracaoEventoFechamentoExercicio> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(ISuperLista<ConfiguracaoEventoFechamentoExercicio> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public ISuperLista<OCCFechamentoExercicio> getOccs() {
        return occs;
    }

    public void setOccs(ISuperLista<OCCFechamentoExercicio> occs) {
        this.occs = occs;
    }

    public Boolean getPesquisarPorAbertura() {
        return pesquisarPorAbertura;
    }

    public void setPesquisarPorAbertura(Boolean pesquisarPorAbertura) {
        this.pesquisarPorAbertura = pesquisarPorAbertura;
    }

    public Boolean getPesquisarPorTransporteDeSaldo() {
        return pesquisarPorTransporteDeSaldo;
    }

    public void setPesquisarPorTransporteDeSaldo(Boolean pesquisarPorTransporteDeSaldo) {
        this.pesquisarPorTransporteDeSaldo = pesquisarPorTransporteDeSaldo;
    }

    public Boolean getPesquisarPorConfiguracoes() {
        return pesquisarPorConfiguracoes;
    }

    public void setPesquisarPorConfiguracoes(Boolean pesquisarPorConfiguracoes) {
        this.pesquisarPorConfiguracoes = pesquisarPorConfiguracoes;
    }

    public Boolean getPesquisarPorPlanoDeContas() {
        return pesquisarPorPlanoDeContas;
    }

    public void setPesquisarPorPlanoDeContas(Boolean pesquisarPorPlanoDeContas) {
        this.pesquisarPorPlanoDeContas = pesquisarPorPlanoDeContas;
    }

    public Boolean getPesquisarPorEncerramento() {
        return pesquisarPorEncerramento;
    }

    public void setPesquisarPorEncerramento(Boolean pesquisarPorEncerramento) {
        this.pesquisarPorEncerramento = pesquisarPorEncerramento;
    }

    public Boolean getPesquisarPorApuracao() {
        return pesquisarPorApuracao;
    }

    public void setPesquisarPorApuracao(Boolean pesquisarPorApuracao) {
        this.pesquisarPorApuracao = pesquisarPorApuracao;
    }

    public ISuperLista<PlanoDeContasFechamentoExercicio> getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(ISuperLista<PlanoDeContasFechamentoExercicio> planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public ISuperLista<ContaFechamentoExercicio> getResultadoAumentativoExercicio() {
        return resultadoAumentativoExercicio;
    }

    public void setResultadoAumentativoExercicio(ISuperLista<ContaFechamentoExercicio> resultadoAumentativoExercicio) {
        this.resultadoAumentativoExercicio = resultadoAumentativoExercicio;
    }

    public ISuperLista<ContaFechamentoExercicio> getResultadoDiminutivoExercicio() {
        return resultadoDiminutivoExercicio;
    }

    public void setResultadoDiminutivoExercicio(ISuperLista<ContaFechamentoExercicio> resultadoDiminutivoExercicio) {
        this.resultadoDiminutivoExercicio = resultadoDiminutivoExercicio;
    }

    public ISuperLista<FonteDeRecursoFechamentoExercicio> getDestinacaoDeRecurso() {
        return destinacaoDeRecurso;
    }

    public void setDestinacaoDeRecurso(ISuperLista<FonteDeRecursoFechamentoExercicio> destinacaoDeRecurso) {
        this.destinacaoDeRecurso = destinacaoDeRecurso;
    }

    public ISuperLista<DespesaFechamentoExercicio> getCanceladoDosRestosAPagarNaoProcessados() {
        return canceladoDosRestosAPagarNaoProcessados;
    }

    public void setCanceladoDosRestosAPagarNaoProcessados(ISuperLista<DespesaFechamentoExercicio> canceladoDosRestosAPagarNaoProcessados) {
        this.canceladoDosRestosAPagarNaoProcessados = canceladoDosRestosAPagarNaoProcessados;
    }

    public ISuperLista<DespesaFechamentoExercicio> getCanceladoDosRestosAPagarProcessados() {
        return canceladoDosRestosAPagarProcessados;
    }

    public void setCanceladoDosRestosAPagarProcessados(ISuperLista<DespesaFechamentoExercicio> canceladoDosRestosAPagarProcessados) {
        this.canceladoDosRestosAPagarProcessados = canceladoDosRestosAPagarProcessados;
    }

    public ISuperLista<DespesaFechamentoExercicio> getPagoDosRestosAPagarNaoProcessados() {
        return pagoDosRestosAPagarNaoProcessados;
    }

    public void setPagoDosRestosAPagarNaoProcessados(ISuperLista<DespesaFechamentoExercicio> pagoDosRestosAPagarNaoProcessados) {
        this.pagoDosRestosAPagarNaoProcessados = pagoDosRestosAPagarNaoProcessados;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ISuperLista<PrescricaoEmpenho> getPrescricaoEmpenhos() {
        return prescricaoEmpenhos;
    }

    public void setPrescricaoEmpenhos(ISuperLista<PrescricaoEmpenho> prescricaoEmpenhos) {
        this.prescricaoEmpenhos = prescricaoEmpenhos;
    }

    public ISuperLista<PrescricaoEmpenho> getPrescricaoEmpenhosNaoProcessados() {
        return prescricaoEmpenhosNaoProcessados;
    }

    public void setPrescricaoEmpenhosNaoProcessados(ISuperLista<PrescricaoEmpenho> prescricaoEmpenhosNaoProcessados) {
        this.prescricaoEmpenhosNaoProcessados = prescricaoEmpenhosNaoProcessados;
    }

    public ISuperLista<InscricaoEmpenho> getInscricaoEmpenhos() {
        return inscricaoEmpenhos;
    }

    public void setInscricaoEmpenhos(ISuperLista<InscricaoEmpenho> inscricaoEmpenhos) {
        this.inscricaoEmpenhos = inscricaoEmpenhos;
    }

    public ISuperLista<InscricaoEmpenho> getInscricaoEmpenhosNaoProcessado() {
        return inscricaoEmpenhosNaoProcessado;
    }

    public void setInscricaoEmpenhosNaoProcessado(ISuperLista<InscricaoEmpenho> inscricaoEmpenhosNaoProcessado) {
        this.inscricaoEmpenhosNaoProcessado = inscricaoEmpenhosNaoProcessado;
    }

    public ISuperLista<ReceitaFechamentoExercicio> getReceitasARealizar() {
        return receitasARealizar;
    }

    public void setReceitasARealizar(ISuperLista<ReceitaFechamentoExercicio> receitasARealizar) {
        this.receitasARealizar = receitasARealizar;
    }

    public ISuperLista<ReceitaFechamentoExercicio> getReestimativas() {
        return reestimativas;
    }

    public void setReestimativas(ISuperLista<ReceitaFechamentoExercicio> reestimativas) {
        this.reestimativas = reestimativas;
    }

    public ISuperLista<ReceitaFechamentoExercicio> getDeducoesIniciais() {
        return deducoesIniciais;
    }

    public void setDeducoesIniciais(ISuperLista<ReceitaFechamentoExercicio> deducoesIniciais) {
        this.deducoesIniciais = deducoesIniciais;
    }

    public ISuperLista<ReceitaFechamentoExercicio> getDeducoesReceitaRealizada() {
        return deducoesReceitaRealizada;
    }

    public void setDeducoesReceitaRealizada(ISuperLista<ReceitaFechamentoExercicio> deducoesReceitaRealizada) {
        this.deducoesReceitaRealizada = deducoesReceitaRealizada;
    }

    public ISuperLista<ReceitaFechamentoExercicio> getReceitaRealizada() {
        return receitaRealizada;
    }

    public void setReceitaRealizada(ISuperLista<ReceitaFechamentoExercicio> receitaRealizada) {
        this.receitaRealizada = receitaRealizada;
    }

    public ISuperLista<ReceitaExtraFechamentoExercicio> getReceitasExtras() {
        return receitasExtras;
    }

    public void setReceitasExtras(ISuperLista<ReceitaExtraFechamentoExercicio> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCreditosDisponiveis() {
        return creditosDisponiveis;
    }

    public void setCreditosDisponiveis(ISuperLista<DotacaoFechamentoExercicio> creditosDisponiveis) {
        this.creditosDisponiveis = creditosDisponiveis;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCreditosAdicionaisSuplementares() {
        return creditosAdicionaisSuplementares;
    }

    public void setCreditosAdicionaisSuplementares(ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisSuplementares) {
        this.creditosAdicionaisSuplementares = creditosAdicionaisSuplementares;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCreditosAdicionaisEspecial() {
        return creditosAdicionaisEspecial;
    }

    public void setCreditosAdicionaisEspecial(ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisEspecial) {
        this.creditosAdicionaisEspecial = creditosAdicionaisEspecial;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCreditosAdicionaisExtraordinario() {
        return creditosAdicionaisExtraordinario;
    }

    public void setCreditosAdicionaisExtraordinario(ISuperLista<DotacaoFechamentoExercicio> creditosAdicionaisExtraordinario) {
        this.creditosAdicionaisExtraordinario = creditosAdicionaisExtraordinario;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getAnulacaoDotacao() {
        return anulacaoDotacao;
    }

    public void setAnulacaoDotacao(ISuperLista<DotacaoFechamentoExercicio> anulacaoDotacao) {
        this.anulacaoDotacao = anulacaoDotacao;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorSuperavitFinanceira() {
        return cretitosAdicionaisPorSuperavitFinanceira;
    }

    public void setCretitosAdicionaisPorSuperavitFinanceira(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorSuperavitFinanceira) {
        this.cretitosAdicionaisPorSuperavitFinanceira = cretitosAdicionaisPorSuperavitFinanceira;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorExcessoDeArrecadacao() {
        return cretitosAdicionaisPorExcessoDeArrecadacao;
    }

    public void setCretitosAdicionaisPorExcessoDeArrecadacao(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorExcessoDeArrecadacao) {
        this.cretitosAdicionaisPorExcessoDeArrecadacao = cretitosAdicionaisPorExcessoDeArrecadacao;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorAnulacaoDeDotacao() {
        return cretitosAdicionaisPorAnulacaoDeDotacao;
    }

    public void setCretitosAdicionaisPorAnulacaoDeDotacao(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeDotacao) {
        this.cretitosAdicionaisPorAnulacaoDeDotacao = cretitosAdicionaisPorAnulacaoDeDotacao;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorOperacaoDeCredito() {
        return cretitosAdicionaisPorOperacaoDeCredito;
    }

    public void setCretitosAdicionaisPorOperacaoDeCredito(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorOperacaoDeCredito) {
        this.cretitosAdicionaisPorOperacaoDeCredito = cretitosAdicionaisPorOperacaoDeCredito;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorReservaDeContigencia() {
        return cretitosAdicionaisPorReservaDeContigencia;
    }

    public void setCretitosAdicionaisPorReservaDeContigencia(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorReservaDeContigencia) {
        this.cretitosAdicionaisPorReservaDeContigencia = cretitosAdicionaisPorReservaDeContigencia;
    }

    public ISuperLista<DotacaoFechamentoExercicio> getCretitosAdicionaisPorAnulacaoDeCredito() {
        return cretitosAdicionaisPorAnulacaoDeCredito;
    }

    public void setCretitosAdicionaisPorAnulacaoDeCredito(ISuperLista<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeCredito) {
        this.cretitosAdicionaisPorAnulacaoDeCredito = cretitosAdicionaisPorAnulacaoDeCredito;
    }

    public ISuperLista<DespesaFechamentoExercicio> getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados() {
        return empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    }

    public void setEmpenhosALiquidarInscritosRestoAPagarNaoProcessados(ISuperLista<DespesaFechamentoExercicio> empenhosALiquidarInscritosRestoAPagarNaoProcessados) {
        this.empenhosALiquidarInscritosRestoAPagarNaoProcessados = empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    }

    public ISuperLista<DespesaFechamentoExercicio> getEmpenhosLiquidadosInscritosRestoAPagarProcessados() {
        return empenhosLiquidadosInscritosRestoAPagarProcessados;
    }

    public void setEmpenhosLiquidadosInscritosRestoAPagarProcessados(ISuperLista<DespesaFechamentoExercicio> empenhosLiquidadosInscritosRestoAPagarProcessados) {
        this.empenhosLiquidadosInscritosRestoAPagarProcessados = empenhosLiquidadosInscritosRestoAPagarProcessados;
    }

    public ISuperLista<DespesaFechamentoExercicio> getEmpenhosCreditoEmpenhadoPago() {
        return empenhosCreditoEmpenhadoPago;
    }

    public void setEmpenhosCreditoEmpenhadoPago(ISuperLista<DespesaFechamentoExercicio> empenhosCreditoEmpenhadoPago) {
        this.empenhosCreditoEmpenhadoPago = empenhosCreditoEmpenhadoPago;
    }

    public ISuperLista<DespesaFechamentoExercicio> getPagoDosRestosAPagarProcessados() {
        return pagoDosRestosAPagarProcessados;
    }

    public void setPagoDosRestosAPagarProcessados(ISuperLista<DespesaFechamentoExercicio> pagoDosRestosAPagarProcessados) {
        this.pagoDosRestosAPagarProcessados = pagoDosRestosAPagarProcessados;
    }

    public ISuperLista<TransporteDeSaldoAbertura> getTransferenciaAjustesPublico() {
        return transferenciaAjustesPublico;
    }

    public void setTransferenciaAjustesPublico(ISuperLista<TransporteDeSaldoAbertura> transferenciaAjustesPublico) {
        this.transferenciaAjustesPublico = transferenciaAjustesPublico;
    }

    public ISuperLista<TransporteDeSaldoAbertura> getTransferenciaResultadoPublico() {
        return transferenciaResultadoPublico;
    }

    public void setTransferenciaResultadoPublico(ISuperLista<TransporteDeSaldoAbertura> transferenciaResultadoPublico) {
        this.transferenciaResultadoPublico = transferenciaResultadoPublico;
    }

    public ISuperLista<TransporteDeSaldoAbertura> getTransferenciaResultadoPrivado() {
        return transferenciaResultadoPrivado;
    }

    public void setTransferenciaResultadoPrivado(ISuperLista<TransporteDeSaldoAbertura> transferenciaResultadoPrivado) {
        this.transferenciaResultadoPrivado = transferenciaResultadoPrivado;
    }

    public ISuperLista<TransporteDeSaldoAbertura> getTransferenciaAjustesPrivado() {
        return transferenciaAjustesPrivado;
    }

    public void setTransferenciaAjustesPrivado(ISuperLista<TransporteDeSaldoAbertura> transferenciaAjustesPrivado) {
        this.transferenciaAjustesPrivado = transferenciaAjustesPrivado;
    }

    public ISuperLista<TransporteSaldoFinanceiro> getTransporteDeSaldoFinanceiro() {
        return transporteDeSaldoFinanceiro;
    }

    public void setTransporteDeSaldoFinanceiro(ISuperLista<TransporteSaldoFinanceiro> transporteDeSaldoFinanceiro) {
        this.transporteDeSaldoFinanceiro = transporteDeSaldoFinanceiro;
    }

    public ISuperLista<TransporteSaldoContaAuxiliarDetalhada> getTransporteDeSaldoDeContasAuxiliares() {
        return transporteDeSaldoDeContasAuxiliares;
    }

    public void setTransporteDeSaldoDeContasAuxiliares(ISuperLista<TransporteSaldoContaAuxiliarDetalhada> transporteDeSaldoDeContasAuxiliares) {
        this.transporteDeSaldoDeContasAuxiliares = transporteDeSaldoDeContasAuxiliares;
    }

    public Boolean getPesquisarPorTransporteDeSaldoFinanceiro() {
        return pesquisarPorTransporteDeSaldoFinanceiro;
    }

    public void setPesquisarPorTransporteDeSaldoFinanceiro(Boolean pesquisarPorTransporteDeSaldoFinanceiro) {
        this.pesquisarPorTransporteDeSaldoFinanceiro = pesquisarPorTransporteDeSaldoFinanceiro;
    }

    public Boolean getPesquisarPorTransporteDeSaldoContabil() {
        return pesquisarPorTransporteDeSaldoContabil;
    }

    public void setPesquisarPorTransporteDeSaldoContabil(Boolean pesquisarPorTransporteDeSaldoContabil) {
        this.pesquisarPorTransporteDeSaldoContabil = pesquisarPorTransporteDeSaldoContabil;
    }

    public ISuperLista<TransporteCreditoReceber> getTransporteDeSaldoCreditoReceber() {
        return transporteDeSaldoCreditoReceber;
    }

    public void setTransporteDeSaldoCreditoReceber(ISuperLista<TransporteCreditoReceber> transporteDeSaldoCreditoReceber) {
        this.transporteDeSaldoCreditoReceber = transporteDeSaldoCreditoReceber;
    }

    public Boolean getPesquisarPorTransporteDeSaldoCreditoReceber() {
        return pesquisarPorTransporteDeSaldoCreditoReceber;
    }

    public void setPesquisarPorTransporteDeSaldoCreditoReceber(Boolean pesquisarPorTransporteDeSaldoCreditoReceber) {
        this.pesquisarPorTransporteDeSaldoCreditoReceber = pesquisarPorTransporteDeSaldoCreditoReceber;
    }

    public ISuperLista<TransporteDividaAtiva> getTransporteDeSaldoDividaAtiva() {
        return transporteDeSaldoDividaAtiva;
    }

    public void setTransporteDeSaldoDividaAtiva(ISuperLista<TransporteDividaAtiva> transporteDeSaldoDividaAtiva) {
        this.transporteDeSaldoDividaAtiva = transporteDeSaldoDividaAtiva;
    }

    public Boolean getPesquisarPorTransporteDeSaldoDividaAtiva() {
        return pesquisarPorTransporteDeSaldoDividaAtiva;
    }

    public void setPesquisarPorTransporteDeSaldoDividaAtiva(Boolean pesquisarPorTransporteDeSaldoDividaAtiva) {
        this.pesquisarPorTransporteDeSaldoDividaAtiva = pesquisarPorTransporteDeSaldoDividaAtiva;
    }

    public Boolean getPesquisarPorTransporteDeSaldoExtra() {
        return pesquisarPorTransporteDeSaldoExtra;
    }

    public void setPesquisarPorTransporteDeSaldoExtra(Boolean pesquisarPorTransporteDeSaldoExtra) {
        this.pesquisarPorTransporteDeSaldoExtra = pesquisarPorTransporteDeSaldoExtra;
    }

    public Boolean getPesquisarPorTransporteDeSaldoDividaPublica() {
        return pesquisarPorTransporteDeSaldoDividaPublica;
    }

    public void setPesquisarPorTransporteDeSaldoDividaPublica(Boolean pesquisarPorTransporteDeSaldoDividaPublica) {
        this.pesquisarPorTransporteDeSaldoDividaPublica = pesquisarPorTransporteDeSaldoDividaPublica;
    }

    public ISuperLista<TransporteExtra> getTransporteDeSaldoExtra() {
        return transporteDeSaldoExtra;
    }

    public void setTransporteDeSaldoExtra(ISuperLista<TransporteExtra> transporteDeSaldoExtra) {
        this.transporteDeSaldoExtra = transporteDeSaldoExtra;
    }

    public ISuperLista<TransporteSaldoDividaPublica> getTransporteDeSaldoDividaPublica() {
        return transporteDeSaldoDividaPublica;
    }

    public void setTransporteDeSaldoDividaPublica(ISuperLista<TransporteSaldoDividaPublica> transporteDeSaldoDividaPublica) {
        this.transporteDeSaldoDividaPublica = transporteDeSaldoDividaPublica;
    }

    //METODOS RECUPERADORES
    public void recuperarInformacoes() {
        instanciarListas();
        try {
            if (pesquisarPorApuracao) {
                recuperarEmpenhosPrescricao();
                recuperarEmpenhosInscricao();
                buscarObrigacoesAPagar();
            }
            if (pesquisarPorEncerramento) {
                recuperarReceitas();
                recuperarDespesa();
                recuperarRestos();
                recuperarDisponibilidadeDeDestinacaoDeRecurso();
                recuperarResultadoDiminutivoDoExercicio();
            }
            if (pesquisarPorPlanoDeContas) {
                recuperarPlanoDeContas();
            }
            if (pesquisarPorTransporteDeSaldo) {
                if (pesquisarPorTransporteDeSaldoContabil) {
                    recuperarTransporteContabil();
                }
                if (pesquisarPorTransporteDeSaldoFinanceiro) {
                    recuperarTransporteSaldoFinanceiro();
                }
                if (pesquisarPorTransporteDeContaAuxiliar) {
                    recuperarTransporteContaAuxiliar();
                }
                if (pesquisarPorTransporteDeSaldoCreditoReceber) {
                    recuperarTransporteCreditoReceber();
                }
                if (pesquisarPorTransporteDeSaldoDividaAtiva) {
                    recuperarTransporteSaldoDividaAtiva();
                }
                if (pesquisarPorTransporteDeSaldoExtra) {
                    recuperarTransporteSaldoExtra();
                }
                if (pesquisarPorTransporteDeSaldoDividaPublica) {
                    recuperarTransporteSaldoDividaPublica();
                }
                if (pesquisarPorTransporteDeReceitaExtra) {
                    recuperarTransporteReceitasExtras();
                }
            }
            if (pesquisarPorAbertura) {
                recuperarInscricaoRestoAPagar();
                recuperarTranportesAbertura();
            }
            atribuirEvento();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }

    }


    private void recuperarTranportesAbertura() {
        transferenciaAjustesPublico.clear();
        transferenciaAjustesPublico.adicionarTodasNasDuasListas(facade.getTransportesSaldoAbertura(selecionado, listaUnidades, configuracaoContabil.getContasContabeisTransferenciaAjustesPublico()));

        transferenciaAjustesPrivado.clear();
        transferenciaAjustesPrivado.adicionarTodasNasDuasListas(facade.getTransportesSaldoAbertura(selecionado, listaUnidades, configuracaoContabil.getContasContabeisTransferenciaAjustesPrivado()));

        transferenciaResultadoPublico.clear();
        transferenciaResultadoPublico.adicionarTodasNasDuasListas(facade.getTransportesSaldoAbertura(selecionado, listaUnidades, configuracaoContabil.getContasContabeisTransferenciaResultadoPublico()));

        transferenciaResultadoPrivado.clear();
        transferenciaResultadoPrivado.adicionarTodasNasDuasListas(facade.getTransportesSaldoAbertura(selecionado, listaUnidades, configuracaoContabil.getContasContabeisTransferenciaResultadoPrivado()));
    }

    private void recuperarInscricaoRestoAPagar() {
        inscricaoRestoPagarProcessados.clear();

        List<Empenho> empenhos = facade.getEmpenhosRestosProcessadosAInscreverDoExercicioAnterior(selecionado, listaUnidades);
        for (Empenho empenho : empenhos) {
            inscricaoRestoPagarProcessados.add(new AberturaInscricaoResto(empenho, selecionado, TipoRestosProcessado.PROCESSADOS, empenho.getValor()));
        }
        inscricaoRestoPagarProcessados.adicionarTodas();

        inscricaoRestoPagarNaoProcessados.clear();
        inscricaoRestoPagarNaoProcessados.adicionarTodasNasDuasListas(facade.getEmpenhosRestosNaoProcessadosAInscreverAberturaDoExercicioAnterior(selecionado, listaUnidades));
    }

    private void recuperarTransporteContabil() {
        try {
            transporteDeSaldo.clear();
            transporteDeSaldo.adicionarTodasNasDuasListas(facade.getContasTransporte(selecionado, listaUnidades));

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldos Contábeis para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarTransporteContaAuxiliar() {
        try {
            transporteDeSaldoDeContasAuxiliares.clear();

            assistenteAberturaFechamentoExercicio.setMensagens(Lists.<String>newArrayList());
            assistenteAberturaFechamentoExercicio.setAberturaFechamentoExercicio(selecionado);
            assistenteAberturaFechamentoExercicio.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            assistenteAberturaFechamentoExercicio.setDataAtual(getSistemaControlador().getDataOperacao());
            assistenteAberturaFechamentoExercicio.setListaDeUnidades(listaUnidades);

            abrirDialogProgressBar();
            executarPoll();
            future = facade.buscarContaAuxiliar(assistenteAberturaFechamentoExercicio);

        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldos Auxiliares para Transporte. Entre em contato com o suporte. Detalhes: " + e.getMessage());
        }
    }

    private void recuperarTransporteSaldoFinanceiro() {
        try {
            transporteDeSaldoFinanceiro.clear();
            transporteDeSaldoFinanceiro.adicionarTodasNasDuasListas(facade.recuperarSaldoFinanceiro(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldo Financeiro para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarTransporteCreditoReceber() {
        try {
            transporteDeSaldoCreditoReceber.clear();
            transporteDeSaldoCreditoReceber.adicionarTodasNasDuasListas(facade.recuperarSaldoCreditoReceber(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldo de Créditos a Receber para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarTransporteSaldoDividaAtiva() {
        try {
            transporteDeSaldoDividaAtiva.clear();
            transporteDeSaldoDividaAtiva.adicionarTodasNasDuasListas(facade.recuperarSaldoDividaAtiva(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldo de Dívida Ativa para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarTransporteSaldoExtra() {
        try {
            transporteDeSaldoExtra.clear();
            transporteDeSaldoExtra.adicionarTodasNasDuasListas(facade.recuperarSaldoExtra(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldo Extraorçamentário para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarTransporteSaldoDividaPublica() {
        try {
            transporteDeSaldoDividaPublica.clear();
            transporteDeSaldoDividaPublica.adicionarTodasNasDuasListas(facade.recuperarSaldoDividaPublica(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Saldo da Dívida Pública para Transporte. Entre em contato com o suporte. Detalhe: " + e.getMessage());
        }
    }

    private void recuperarTransporteReceitasExtras() {
        try {
            receitasExtras.clear();
            receitasExtras.adicionarTodasNasDuasListas(facade.buscarReceitasExtras(selecionado, listaUnidades));
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar as Receitas Extraorçamentárias para Transporte. Entre em contato com o suporte.");
        }
    }

    private void recuperarConfiguracoes() {
        try {
            configuracoes.clear();
            occs.clear();
            if (pesquisarPorConfiguracoesRECEITA) {
                recuperarConfiguracoesDeReceita();
            }

        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar as Configurações. Entre em contato com o suporte.");
        }
    }

    private void recuperarConfiguracoesDeReceita() {

        List<TipoEventoContabil> tipos = new ArrayList<TipoEventoContabil>();
        tipos.add(TipoEventoContabil.PREVISAO_INICIAL_RECEITA);
        tipos.add(TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA);
        tipos.add(TipoEventoContabil.RECEITA_REALIZADA);
        tipos.add(TipoEventoContabil.CREDITO_RECEBER);
        tipos.add(TipoEventoContabil.DIVIDA_ATIVA);
        configuracoes.addAll(facade.getConfiguracoesDeEventoRECEITA(selecionado, tipos, TipoDuplicarConfiguracaoFechamentoExercicio.RECEITA));
        occs.addAll(facade.getConfiguracoesDeOccPorTipo(selecionado, EntidadeOCC.CONTARECEITA, TipoDuplicarConfiguracaoFechamentoExercicio.RECEITA));
    }

    private void recuperarPlanoDeContas() {
        PlanoDeContasExercicio planoDeContasExercicio = facade.getPlanoDeContasFacade().getPlanoDeContasExercicioFacade().recuperarPorExercicio(selecionado.getExercicio());
        planoDeContas.clear();
        planoDeContas.add(new PlanoDeContasFechamentoExercicio(planoDeContasExercicio.getPlanoContabil(), selecionado));
        planoDeContas.add(new PlanoDeContasFechamentoExercicio(planoDeContasExercicio.getPlanoExtraorcamentario(), selecionado));
        planoDeContas.add(new PlanoDeContasFechamentoExercicio(planoDeContasExercicio.getPlanoDeDestinacaoDeRecursos(), selecionado));
        planoDeContas.add(new PlanoDeContasFechamentoExercicio(planoDeContasExercicio.getPlanoDeDespesas(), selecionado));
        planoDeContas.add(new PlanoDeContasFechamentoExercicio(planoDeContasExercicio.getPlanoDeReceitas(), selecionado));
    }

    public void recuperarEmpenhosPrescricao() {
        prescricaoEmpenhos.clear();
        prescricaoEmpenhos.adicionarTodasNasDuasListas(facade.getPrescricaoRestoAPagar(selecionado, listaUnidades, TipoRestosProcessado.PROCESSADOS));

        prescricaoEmpenhosNaoProcessados.clear();
        prescricaoEmpenhosNaoProcessados.adicionarTodasNasDuasListas(facade.getPrescricaoRestoAPagar(selecionado, listaUnidades, TipoRestosProcessado.NAO_PROCESSADOS));
    }

    public void recuperarEmpenhosInscricao() {
        inscricaoEmpenhos.clear();
        inscricaoEmpenhos.adicionarTodasNasDuasListas(facade.getEmpenhosRestosProcessadosAInscrever(selecionado, listaUnidades));

        inscricaoEmpenhosNaoProcessado.clear();
        inscricaoEmpenhosNaoProcessado.adicionarTodasNasDuasListas(facade.getEmpenhosRestosNaoProcessadosAInscrever(selecionado, listaUnidades));
    }

    public void recuperarReceitas() {
        try {
            receitasARealizar.clear();
            reestimativas.clear();
            deducoesIniciais.clear();
            deducoesReceitaRealizada.clear();
            receitaRealizada.clear();

            receitasARealizar.adicionarTodasNasDuasListas(facade.getReceitasARealizar(selecionado, listaUnidades));
            reestimativas.adicionarTodasNasDuasListas(facade.getReceitasParaReestimativa(selecionado, listaUnidades));
            deducoesIniciais.adicionarTodasNasDuasListas(facade.getReceitasParaDeducaoPrevisaoInicialDaReceita(selecionado, listaUnidades));
            deducoesReceitaRealizada.adicionarTodasNasDuasListas(facade.getReceitasParaDeducaoReceitaRealizada(selecionado, listaUnidades));
            receitaRealizada.adicionarTodasNasDuasListas(facade.getReceitasRealizadas(selecionado, listaUnidades));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar a Receita. Entre em contato com o suporte.");
        }

    }

    public void recuperarDespesa() {
        try {
            creditosDisponiveis.clear();
            creditosDisponiveis.adicionarTodasNasDuasListas(facade.getCreditoDisponivel(selecionado, listaUnidades));
            creditosAdicionaisSuplementares.clear();
            creditosAdicionaisSuplementares.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorTipo(selecionado, listaUnidades, TipoDespesaORC.SUPLEMENTAR, TipoDotacaoFechamentoExercicio.SUPLEMENTAR));
            creditosAdicionaisEspecial.clear();
            creditosAdicionaisEspecial.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorTipo(selecionado, listaUnidades, TipoDespesaORC.ESPECIAL, TipoDotacaoFechamentoExercicio.ESPECIAL));
            creditosAdicionaisExtraordinario.clear();
            creditosAdicionaisExtraordinario.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorTipo(selecionado, listaUnidades, TipoDespesaORC.EXTRAORDINARIA, TipoDotacaoFechamentoExercicio.EXTRAORDINARIO));
            anulacaoDotacao.clear();
            anulacaoDotacao.adicionarTodasNasDuasListas(facade.getAnulacaoDotacao(selecionado, listaUnidades));

            cretitosAdicionaisPorSuperavitFinanceira.clear();
            cretitosAdicionaisPorSuperavitFinanceira.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorOrigem(selecionado, listaUnidades, OrigemSuplementacaoORC.SUPERAVIT, TipoDotacaoFechamentoExercicio.RESERVA_CONTIGENCIA));
            cretitosAdicionaisPorExcessoDeArrecadacao.clear();
            cretitosAdicionaisPorExcessoDeArrecadacao.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorOrigem(selecionado, listaUnidades, OrigemSuplementacaoORC.EXCESSO, TipoDotacaoFechamentoExercicio.EXCESSO));
            cretitosAdicionaisPorAnulacaoDeDotacao.clear();
            cretitosAdicionaisPorAnulacaoDeDotacao.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorOrigem(selecionado, listaUnidades, OrigemSuplementacaoORC.ANULACAO, TipoDotacaoFechamentoExercicio.ANULACAO));
            cretitosAdicionaisPorOperacaoDeCredito.clear();
            cretitosAdicionaisPorOperacaoDeCredito.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorOrigem(selecionado, listaUnidades, OrigemSuplementacaoORC.OPERACAO_CREDITO, TipoDotacaoFechamentoExercicio.OPERACAO_CREDITO));
            cretitosAdicionaisPorReservaDeContigencia.clear();
            cretitosAdicionaisPorReservaDeContigencia.adicionarTodasNasDuasListas(facade.getCreditoAdicionalPorOrigem(selecionado, listaUnidades, OrigemSuplementacaoORC.RESERVA_CONTINGENCIA, TipoDotacaoFechamentoExercicio.RESERVA_CONTIGENCIA));
            cretitosAdicionaisPorAnulacaoDeCredito.clear();
            cretitosAdicionaisPorAnulacaoDeCredito.adicionarTodasNasDuasListas(facade.getAnulacaoDotacao(selecionado, listaUnidades));

            empenhosALiquidarInscritosRestoAPagarNaoProcessados.clear();
            empenhosALiquidarInscritosRestoAPagarNaoProcessados.adicionarTodasNasDuasListas(facade.getEmpenhosALiquidarRestosNaoProcessados(selecionado, listaUnidades));
            empenhosLiquidadosInscritosRestoAPagarProcessados.clear();
            empenhosLiquidadosInscritosRestoAPagarProcessados.adicionarTodasNasDuasListas(facade.getEmpenhosLiquidadosRestosProcessados(selecionado, listaUnidades));
            empenhosCreditoEmpenhadoPago.clear();
            empenhosCreditoEmpenhadoPago.adicionarTodasNasDuasListas(facade.getEmpenhosCreditosEmpenhoPago(selecionado, listaUnidades));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar a Despesa. Entre em contato com o suporte.");
        }
    }

    public void recuperarRestos() {
        try {
            pagoDosRestosAPagarProcessados.clear();
            pagoDosRestosAPagarProcessados.adicionarTodasNasDuasListas(facade.getTotalPagoDosRestosAPagar(selecionado, listaUnidades, TipoRestosProcessado.PROCESSADOS));

            pagoDosRestosAPagarNaoProcessados.clear();
            pagoDosRestosAPagarNaoProcessados.adicionarTodasNasDuasListas(facade.getTotalPagoDosRestosAPagar(selecionado, listaUnidades, TipoRestosProcessado.NAO_PROCESSADOS));

            canceladoDosRestosAPagarProcessados.clear();
            canceladoDosRestosAPagarProcessados.adicionarTodasNasDuasListas(facade.getTotalCanceladosProcessadosDoExercicioAnterior(selecionado, listaUnidades));

            canceladoDosRestosAPagarNaoProcessados.clear();
            canceladoDosRestosAPagarNaoProcessados.adicionarTodasNasDuasListas(facade.getTotalCanceladosNaoProcessadosDoExercicioAnterior(selecionado, listaUnidades));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar os Restos a Pagar. Entre em contato com o suporte.");
        }
    }

    public void recuperarDisponibilidadeDeDestinacaoDeRecurso() {
        try {
            destinacaoDeRecurso.clear();
            destinacaoDeRecurso.adicionarTodasNasDuasListas(facade.getDisponibilidadePorDestinacaoDeRecurso(selecionado, listaUnidades));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar a Disponibilidade de Destinação de Recurso. Entre em contato com o suporte.");
        }
    }

    public void recuperarResultadoDiminutivoDoExercicio() {
        try {
            resultadoDiminutivoExercicio.clear();
            resultadoDiminutivoExercicio.adicionarTodasNasDuasListas(facade.getResultadoDiminutivoOuAumentativoDoExercicio(selecionado, listaUnidades, configuracaoContabil.getContaContabilResultadoDimin(), TipoContaFechamentoExercicio.RESULTADO_DIMINUTIVO));

            resultadoAumentativoExercicio.clear();
            resultadoAumentativoExercicio.adicionarTodasNasDuasListas(facade.getResultadoDiminutivoOuAumentativoDoExercicio(selecionado, listaUnidades, configuracaoContabil.getContaContabilResultadoAumen(), TipoContaFechamentoExercicio.RESULTADO_AUMENTATIVO));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar a Disponibilidade de Destinação de Recurso. Entre em contato com o suporte.");
        }
    }

    private void buscarObrigacoesAPagar() {
        try {
            obrigacoesAPagar.clear();
            obrigacoesAPagar.adicionarTodasNasDuasListas(facade.getObrigacoesAPagar(selecionado, listaUnidades));
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Foi encontrado um problema ao recuperar as Obrigações a Pagar. Entre em contato com o suporte.");
        }
    }

    public String getCaminhoOrigem() {
        return "/abertura-fechamento-exercicio/";
    }


    public void salvarEncerramento() {
        try {
            Util.validarCampos(selecionado);
            validarTransporteSaldoFinanceiro();
            assistenteAberturaFechamentoExercicio.setMensagens(Lists.<String>newArrayList());
            assistenteAberturaFechamentoExercicio.setAberturaFechamentoExercicio(selecionado);
            assistenteAberturaFechamentoExercicio.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            assistenteAberturaFechamentoExercicio.setDataAtual(getSistemaControlador().getDataOperacao());
            assistenteAberturaFechamentoExercicio.setListaDeUnidades(null);
            assistenteAberturaFechamentoExercicio.setContasAuxiliaresTransporte(null);
            abrirDialogProgressBar();
            executarPoll();
            future = facade.inicializarFechamento(assistenteAberturaFechamentoExercicio);
        } catch (ValidacaoException ve) {
            assistenteAberturaFechamentoExercicio.zerarContadoresProcesso();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            assistenteAberturaFechamentoExercicio.zerarContadoresProcesso();
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            assistenteAberturaFechamentoExercicio.zerarContadoresProcesso();
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarTransporteSaldoFinanceiro() {
        ValidacaoException ve = new ValidacaoException();
        if (pesquisarPorTransporteDeSaldoFinanceiro) {
            for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : selecionado.getTransporteDeSaldoFinanceiro()) {
                if (transporteSaldoFinanceiro.getErroDuranteProcessamento()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Existem inconsistências no transporte de saldo financeiro. Por favor, verifique as inconsistências antes de prosseguir.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgressBar.show()");
    }

    public void executarPoll() {
        FacesUtil.executaJavaScript("poll.start()");
    }


    public void finalizarBarraProgressao() {
        if (!assistenteAberturaFechamentoExercicio.getBarraProgressoItens().getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            verificarMensagens();
        }
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            assistenteAberturaFechamentoExercicio.getBarraProgressoItens().finaliza();
        }
    }

    public void verificarMensagens() {
        if (future != null && future.isDone()) {
            if (assistenteAberturaFechamentoExercicio.getMensagens().isEmpty()) {
                if (assistenteAberturaFechamentoExercicio.getContasAuxiliaresTransporte() == null
                    || assistenteAberturaFechamentoExercicio.getContasAuxiliaresTransporte().isEmpty()) {
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    redireciona();
                } else {
                    transporteDeSaldoDeContasAuxiliares.clear();
                    transporteDeSaldoDeContasAuxiliares.adicionarTodasNasDuasListas(assistenteAberturaFechamentoExercicio.getContasAuxiliaresTransporte());
                    FacesUtil.atualizarComponente("Formulario");
                    FacesUtil.addOperacaoRealizada("Busca realizada com sucesso.");
                }
            } else {
                String mensagemCompleta = "Ocorreram os seguintes erros durante a abertura/fechamento: ";
                for (String mensagem : assistenteAberturaFechamentoExercicio.getMensagens()) {
                    mensagemCompleta += mensagem;
                }
                FacesUtil.addOperacaoNaoRealizada(mensagemCompleta);
            }
        }
    }

    private void atribuirEvento() {
        if (pesquisarPorApuracao) {
            atribuirEventoPrescricao();
            atribuirEventoInscricao();
        }
        if (pesquisarPorEncerramento) {
            atribuirEventoReceita();
            atribuirEventoDespesa();
            atribuirEventoRestoAPagar();
            atribuirEventoDestinacaoDeRecurso();
            atribuirEventoResultadoDiminutivoDeExercicio();
            atribuirEventoResultadoAumentativoDeExercicio();
        }

        if (pesquisarPorAbertura) {
            atribuirEventoAberturaRestoAPagar();
            atribuirEventoTransporteAbertura();
        }
    }

    private void atribuirEventoTransporteAbertura() {
        atribuirEventoAberturaTransferenciaResultadoPublico();
        atribuirEventoAberturaTransferenciaResultadoPrivado();
        atribuirEventoAberturaTransferenciaAjustesPublico();
        atribuirEventoAberturaTransferenciaAjustesPrivado();
    }

    private void atribuirEventoAberturaTransferenciaAjustesPublico() {
        EventoContabil eventoContabilCredorNegativo = null;
        EventoContabil eventoContabilDevedorPositivo = null;
        for (TransporteDeSaldoAbertura conta : selecionado.getTransferenciaAjustesPublico()) {
            eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PUBLICO, conta.getUnidade());
            eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PUBLICO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) > 0) {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PUBLICO);
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PUBLICO);
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoAberturaTransferenciaAjustesPrivado() {
        EventoContabil eventoContabilCredorNegativo = null;
        EventoContabil eventoContabilDevedorPositivo = null;
        for (TransporteDeSaldoAbertura conta : selecionado.getTransferenciaAjustesPrivado()) {
            eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PRIVADO, conta.getUnidade());
            eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PRIVADO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) > 0) {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PRIVADO);
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PRIVADO);
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoAberturaTransferenciaResultadoPublico() {
        EventoContabil eventoContabilCredorNegativo = null;
        EventoContabil eventoContabilDevedorPositivo = null;
        for (TransporteDeSaldoAbertura conta : selecionado.getTransferenciaResultadoPublico()) {
            eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PUBLICO, conta.getUnidade());
            eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PUBLICO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) > 0) {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PUBLICO);
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PUBLICO);
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoAberturaTransferenciaResultadoPrivado() {
        EventoContabil eventoContabilCredorNegativo = null;
        EventoContabil eventoContabilDevedorPositivo = null;
        for (TransporteDeSaldoAbertura conta : selecionado.getTransferenciaResultadoPrivado()) {
            eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PRIVADO, conta.getUnidade());
            eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PRIVADO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) > 0) {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PRIVADO);
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setTipoMovimentoContabil(TipoMovimentoContabil.ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PRIVADO);
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoAberturaRestoAPagar() {
        atribuirEventoAberturaRestoAPagarProcessados();
        atribuirEventoAberturaRestoAPagarNaoProcessados();
    }

    private void atribuirEventoAberturaRestoAPagarProcessados() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.ABERTURA_RESTO_PAGAR_PROCESSADOS);
        for (AberturaInscricaoResto aberturaInscricaoResto : selecionado.getInscricaoRestoPagarProcessados()) {
            aberturaInscricaoResto.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoAberturaRestoAPagarNaoProcessados() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.ABERTURA_RESTO_PAGAR_NAO_PROCESSADOS);
        EventoContabil eventoContabilEmLiquidacao = buscarEventoPelaConfiguracao(TipoMovimentoContabil.ABERTURA_RESTO_PAGAR_NAO_PROCESSADOS_EM_LIQUIDACAO);
        for (AberturaInscricaoResto aberturaInscricaoResto : selecionado.getInscricaoRestoPagarNaoProcessados()) {
            Empenho empenho = facade.getEmpenhoFacade().recuperarComFind(aberturaInscricaoResto.getEmpenho().getId());
            if (empenho.getObrigacoesPagar().isEmpty()) {
                aberturaInscricaoResto.setEventoContabil(eventoContabil);
            } else {
                aberturaInscricaoResto.setEventoContabil(eventoContabilEmLiquidacao);
            }
        }
    }

    private void atribuirEventoResultadoAumentativoDeExercicio() {
        for (ContaFechamentoExercicio conta : selecionado.getResultadoAumentativoExercicio()) {
            EventoContabil eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.RESULTADO_AUMENTATIVO_DO_EXERCICIO_NEGATIVO, conta.getUnidade());
            EventoContabil eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.RESULTADO_AUMENTATIVO_DO_EXERCICIO_POSITIVO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) > 0) {
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoResultadoDiminutivoDeExercicio() {
        for (ContaFechamentoExercicio conta : selecionado.getResultadoDiminutivoExercicio()) {
            EventoContabil eventoContabilDevedorPositivo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.RESULTADO_DIMINUTIVO_DO_EXERCICIO_POSITIVO, conta.getUnidade());
            EventoContabil eventoContabilCredorNegativo = buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil.RESULTADO_DIMINUTIVO_DO_EXERCICIO_NEGATIVO, conta.getUnidade());
            if (conta.getValor().compareTo(BigDecimal.ZERO) < 0) {
                conta.setEventoContabil(eventoContabilDevedorPositivo);
            } else {
                conta.setEventoContabil(eventoContabilCredorNegativo);
            }
        }
    }

    private void atribuirEventoDestinacaoDeRecurso() {
        EventoContabil eventoContabilCredor = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESTINACAO_DE_RECURSO_CREDOR_NEGATIVO);
        EventoContabil eventoContabilDevedor = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESTINACAO_DE_RECURSO_DEVEDOR_POSITIVO);
        for (FonteDeRecursoFechamentoExercicio fonteDeRecursoFechamentoExercicio : selecionado.getDestinacaoDeRecurso()) {
            if (fonteDeRecursoFechamentoExercicio.getValor().compareTo(BigDecimal.ZERO) < 0) {
                fonteDeRecursoFechamentoExercicio.setEventoContabil(eventoContabilDevedor);
            } else {
                fonteDeRecursoFechamentoExercicio.setEventoContabil(eventoContabilCredor);
            }
        }
    }

    private void atribuirEventoRestoAPagar() {
        atribuirEventoRestoAPagarEmpenhoALiquidarNaoProcessados();
        atribuirEventoRestoAPagarEmpenhoLiquidadosProcessados();
        atribuirEventoRestoAPagarEmpenhoCreditoEmpenhadoPago();
        atribuirEventoRestoAPagarPagoProcessadoDoExercicioAnterior();
        atribuirEventoRestoAPagarPagoNaoProcessadoDoExercicioAnterior();
        atribuirEventoRestoAPagarCanceladoProcessadoDoExercicioAnterior();
        atribuirEventoRestoAPagarCanceladoNaoProcessadoDoExercicioAnterior();
    }

    private void atribuirEventoRestoAPagarPagoProcessadoDoExercicioAnterior() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.RESTO_PAGAR_PAGO_PROCESSADO_EXERCICIO_ANTERIOR);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getPagoDosRestosAPagarProcessados()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarCanceladoNaoProcessadoDoExercicioAnterior() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.RESTO_PAGAR_CANCELADO_NAO_PROCESSADO_EXERCICIO_ANTERIOR);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getCanceladoDosRestosAPagarNaoProcessados()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarCanceladoProcessadoDoExercicioAnterior() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.RESTO_PAGAR_CANCELADO_PROCESSADO_EXERCICIO_ANTERIOR);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getCanceladoDosRestosAPagarProcessados()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarPagoNaoProcessadoDoExercicioAnterior() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.RESTO_PAGAR_PAGO_NAO_PROCESSADO_EXERCICIO_ANTERIOR);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getPagoDosRestosAPagarNaoProcessados()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarEmpenhoCreditoEmpenhadoPago() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.EMPENHOS_CREDITO_EMPENHO_PAGO);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getEmpenhosCreditoEmpenhadoPago()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarEmpenhoLiquidadosProcessados() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.EMPENHOS_LIQUIDADOS_INSCRITOS_PROCESSADOS);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getEmpenhosLiquidadosInscritosRestoAPagarProcessados()) {
            despesaFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoRestoAPagarEmpenhoALiquidarNaoProcessados() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.EMPENHOS_LIQUIDAR_INSCRITOS_NAO_PROCESSADOS);
        EventoContabil eventoContabilEmLiquidacao = buscarEventoPelaConfiguracao(TipoMovimentoContabil.EMPENHOS_EM_LIQUIDACAO_INSCRITOS_NAO_PROCESSADOS);
        for (DespesaFechamentoExercicio despesaFechamentoExercicio : selecionado.getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados()) {
            Empenho empenho = facade.getEmpenhoFacade().recuperarComFind(despesaFechamentoExercicio.getEmpenho().getId());
            if (empenho.getObrigacoesPagar().isEmpty()) {
                despesaFechamentoExercicio.setEventoContabil(eventoContabil);
            } else {
                despesaFechamentoExercicio.setEventoContabil(eventoContabilEmLiquidacao);
            }
        }
    }

    private void atribuirEventoDespesa() {
        atribuirEventoDespesaCreditoDisponivel();
        atribuirEventoCreditoAdicional();
        atribuirEventoCancelamentoDotacao();
        atribuirEventoCreditoAdicionalPorOrigem();
    }

    private void atribuirEventoCreditoAdicionalPorOrigem() {
        atribuirEventoCreditoAdicionalPorOrigemSuperavit();
        atribuirEventoCreditoAdicionalPorOrigemExcesso();
        atribuirEventoCreditoAdicionalPorOrigemAnulacaoDotacao();
        atribuirEventoCreditoAdicionalPorOrigemOperacoesCreditos();
        atribuirEventoCreditoAdicionalPorOrigemReservaContigencia();
        atribuirEventoCreditoAdicionalPorOrigemAnulacaoCredito();
    }

    private void atribuirEventoCreditoAdicionalPorOrigemAnulacaoCredito() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_ANULACAO_CREDITO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorAnulacaoDeCredito()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalPorOrigemReservaContigencia() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_RESERVA_CONTIGENCIA);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorReservaDeContigencia()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalPorOrigemOperacoesCreditos() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_OPERACAO_CREDITO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorOperacaoDeCredito()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalPorOrigemAnulacaoDotacao() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_ANULACAO_DOTACAO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorAnulacaoDeDotacao()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalPorOrigemExcesso() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_EXCESSO_ARRECADACAO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorExcessoDeArrecadacao()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalPorOrigemSuperavit() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_POR_SUPERAVIT_FINANCEIRO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCretitosAdicionaisPorSuperavitFinanceira()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCancelamentoDotacao() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_ANULACAO_DOTACAO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getAnulacaoDotacao()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicional() {
        atribuirEventoCreditoAdicionalSuplementar();
        atribuirEventoCreditoAdicionalEspecial();
        atribuirEventoCreditoAdicionalExtraordinario();
    }

    private void atribuirEventoCreditoAdicionalExtraordinario() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_EXTRAORDINARIO);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCreditosAdicionaisExtraordinario()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalEspecial() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_ESPECIAL);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCreditosAdicionaisEspecial()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoCreditoAdicionalSuplementar() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_ADICIONAL_SUPLEMENTAR);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getCreditosAdicionaisSuplementares()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoDespesaCreditoDisponivel() {
        EventoContabil eventoContabil = buscarEventoPelaConfiguracao(TipoMovimentoContabil.DESPESA_CREDITO_DISPONIVEL);
        for (DotacaoFechamentoExercicio dotacaoFechamentoExercicio : selecionado.getDotacoes()) {
            dotacaoFechamentoExercicio.setEventoContabil(eventoContabil);
        }
    }

    private void atribuirEventoReceita() {

        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : selecionado.getReceitasARealizar()) {
            atribuirEventoReceitaExercicio(receitaFechamentoExercicio);
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : selecionado.getReceitasReestimativas()) {
            atribuirEventoReceitaExercicio(receitaFechamentoExercicio);
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : selecionado.getReceitasDeducaoPrevisaoInicial()) {
            atribuirEventoReceitaExercicio(receitaFechamentoExercicio);
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : selecionado.getReceitasDeducaoReceitaRealizada()) {
            atribuirEventoReceitaExercicio(receitaFechamentoExercicio);
        }
        for (ReceitaFechamentoExercicio receitaFechamentoExercicio : selecionado.getReceitasRealizada()) {
            atribuirEventoReceitaExercicio(receitaFechamentoExercicio);
        }
    }

    private void atribuirEventoReceitaExercicio(ReceitaFechamentoExercicio receitaFechamentoExercicio) {
        Boolean isPositivo = receitaFechamentoExercicio.getValor().compareTo(BigDecimal.ZERO) > 0;
        TipoReceitaFechamentoExercicio tipoReceita = receitaFechamentoExercicio.getTipoReceita();
        EventoContabil eventoContabil1 = recuperarEventoContabilPorTipo(tipoReceita, isPositivo);
        receitaFechamentoExercicio.setEventoContabil(eventoContabil1);
    }

    private EventoContabil recuperarEventoContabilPorTipo(TipoReceitaFechamentoExercicio tipoReceita, Boolean isPositivo) {
        switch (tipoReceita) {
            case RECEITA_A_REALIZAR:
                if (isPositivo) {
                    return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_A_REALIZAR_NEGATIVO_CREDOR);
                } else {
                    return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_A_REALIZAR_POSITIVO_DEVEDOR);
                }
            case REESTIMATIVA:
                return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_REESTIMATIVA);
            case DEDUCAO_INICIAL_RECEITA:
                return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_DEDUCAO_PREVISAO_INICIAL_RECEITA);
            case DEDUCAO_RECEITA_REALIZADA:
                return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_DEDUCAO_RECEITA_REALIZADA);
            case RECEITA_REALIZADA:
                if (isPositivo) {
                    return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_RECEITA_REALIZADA_POSITIVO);
                } else {
                    return buscarEventoPelaConfiguracao(TipoMovimentoContabil.RECEITA_RECEITA_REALIZADA_NEGATIVO);
                }
        }
        throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum evento");
    }

    private EventoContabil buscarEventoPelaConfiguracao(TipoMovimentoContabil receitaARealizarPositivoDevedor) {
        return facade.getConfigAberturaFechamentoExercicioFacade().buscarEventoAberturaFechamentoExercicio(
            getSistemaControlador().getDataOperacao(),
            PatrimonioLiquido.PUBLICO,
            receitaARealizarPositivoDevedor);
    }

    private EventoContabil buscarEventoPelaConfiguracaoVerificandoNatureza(TipoMovimentoContabil receitaARealizarPositivoDevedor, HierarquiaOrganizacional hierarquiaOrganizacional) {
        return facade.getConfigAberturaFechamentoExercicioFacade().buscarEventoAberturaFechamentoExercicio(
            getSistemaControlador().getDataOperacao(),
            PatrimonioLiquido.buscarPorNatureza(facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(hierarquiaOrganizacional.getSubordinada()).getTipoUnidade()),
            receitaARealizarPositivoDevedor);
    }

    private void atribuirEventoInscricao() {
        atribuirEventoInscricaoProcessados();
        atribuirEventoInscricaoNaoProcessados();
    }

    private void atribuirEventoInscricaoNaoProcessados() {
        for (InscricaoEmpenho inscricaoEmpenho : selecionado.getInscricaoEmpenhosNaoProcessados()) {
            EventoContabil eventoContabilNaoProcessado = facade.getConfigEmpenhoRestoFacade().recuperarEventoPorContaDespesa(
                inscricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                TipoLancamento.NORMAL,
                inscricaoEmpenho.getEmpenho().getDataEmpenho(),
                inscricaoEmpenho.getTipoRestos(),
                !inscricaoEmpenho.getEmpenho().getObrigacoesPagar().isEmpty()
            ).getEventoContabil();
            inscricaoEmpenho.setEventoContabil(eventoContabilNaoProcessado);
        }
    }

    private void atribuirEventoInscricaoProcessados() {
        for (InscricaoEmpenho inscricaoEmpenho : selecionado.getInscricaoEmpenhosProcessados()) {
            EventoContabil eventoContabilProcessado = facade.getConfigEmpenhoRestoFacade().recuperarEventoPorContaDespesa(
                inscricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                TipoLancamento.NORMAL,
                inscricaoEmpenho.getEmpenho().getDataEmpenho(),
                inscricaoEmpenho.getTipoRestos(),
                !inscricaoEmpenho.getEmpenho().getObrigacoesPagar().isEmpty()
            ).getEventoContabil();
            inscricaoEmpenho.setEventoContabil(eventoContabilProcessado);
        }
    }

    private void atribuirEventoPrescricao() {
        atribuirEventoPrescricaoProcessados();
        atribuirEventoPrescricaoNaoProcessados();
    }

    private void atribuirEventoPrescricaoProcessados() {
        for (PrescricaoEmpenho prescricaoEmpenho : selecionado.getPrescricaoEmpenhosProcessados()) {
            prescricaoEmpenho.setEventoContabil(
                (facade.getConfigCancelamentoRestoFacade().recuperarEventoPorContaDespesaAndTipoEmpenhoEstorno(
                    prescricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                    prescricaoEmpenho.getEmpenhoEstorno() != null ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL,
                    getSistemaControlador().getDataOperacao(),
                    prescricaoEmpenho.getTipoRestos(),
                    TipoEmpenhoEstorno.PRESCRICAO,
                    PatrimonioLiquido.buscarPorNatureza(facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(prescricaoEmpenho.getEmpenho().getUnidadeOrganizacional()).getTipoUnidade()))
                ).getEventoContabil());
        }

    }

    private void atribuirEventoPrescricaoNaoProcessados() {
        for (PrescricaoEmpenho prescricaoEmpenho : selecionado.getPrescricaoEmpenhosNaoProcessados()) {
            prescricaoEmpenho.setEventoContabil(
                (facade.getConfigCancelamentoRestoFacade().recuperarEventoPorContaDespesaAndTipoEmpenhoEstorno(
                    prescricaoEmpenho.getEmpenho().getDespesaORC().getContaDeDespesa(),
                    prescricaoEmpenho.getEmpenhoEstorno() != null ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL,
                    getSistemaControlador().getDataOperacao(),
                    prescricaoEmpenho.getTipoRestos(),
                    TipoEmpenhoEstorno.PRESCRICAO,
                    PatrimonioLiquido.buscarPorNatureza(facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrcamentaria(prescricaoEmpenho.getEmpenho().getUnidadeOrganizacional()).getTipoUnidade()))
                ).getEventoContabil());
        }

    }

    public AssistenteAberturaFechamentoExercicio getAssistenteAberturaFechamentoExercicio() {
        return assistenteAberturaFechamentoExercicio;
    }

    public void setAssistenteAberturaFechamentoExercicio(AssistenteAberturaFechamentoExercicio assistenteAberturaFechamentoExercicio) {
        this.assistenteAberturaFechamentoExercicio = assistenteAberturaFechamentoExercicio;
    }

    public Boolean getPesquisarPorTransporteDeReceitaExtra() {
        return pesquisarPorTransporteDeReceitaExtra;
    }

    public void setPesquisarPorTransporteDeReceitaExtra(Boolean pesquisarPorTransporteDeReceitaExtra) {
        this.pesquisarPorTransporteDeReceitaExtra = pesquisarPorTransporteDeReceitaExtra;
    }

    public Boolean getPesquisarPorTransporteDeContaAuxiliar() {
        return pesquisarPorTransporteDeContaAuxiliar;
    }

    public void setPesquisarPorTransporteDeContaAuxiliar(Boolean pesquisarPorTransporteDeContaAuxiliar) {
        this.pesquisarPorTransporteDeContaAuxiliar = pesquisarPorTransporteDeContaAuxiliar;
    }

    public ISuperLista<TransporteObrigacaoAPagar> getObrigacoesAPagar() {
        return obrigacoesAPagar;
    }

    public void setObrigacoesAPagar(ISuperLista<TransporteObrigacaoAPagar> obrigacoesAPagar) {
        this.obrigacoesAPagar = obrigacoesAPagar;
    }
}
