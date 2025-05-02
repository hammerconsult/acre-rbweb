package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteTransporteConfiguracaoContabil;
import br.com.webpublico.entidadesauxiliares.TransporteConfiguracaoCLPLCP;
import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TransporteConfiguracaoContabilFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by HardRock on 26/12/2016.
 */

@ManagedBean(name = "transporteConfiguracaoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transporte-configuracao", pattern = "/transporte-configuracao-contabil/novo/", viewId = "/faces/financeiro/orcamentario/transporte-configuracao-contabil/edita.xhtml"),
    @URLMapping(id = "editar-transporte-configuracao", pattern = "/transporte-configuracao-contabil/editar/#{transporteConfiguracaoContabilControlador.id}/", viewId = "/faces/financeiro/orcamentario/transporte-configuracao-contabil/edita.xhtml"),
    @URLMapping(id = "listar-transporte-configuracao", pattern = "/transporte-configuracao-contabil/listar/", viewId = "/faces/financeiro/orcamentario/transporte-configuracao-contabil/lista.xhtml"),
    @URLMapping(id = "ver-transporte-configuracao", pattern = "/transporte-configuracao-contabil/ver/#{transporteConfiguracaoContabilControlador.id}/", viewId = "/faces/financeiro/orcamentario/transporte-configuracao-contabil/visualiza.xhtml"),
    @URLMapping(id = "log-transporte-configuracao", pattern = "/transporte-configuracao-contabil/acompanhamento/", viewId = "/faces/financeiro/orcamentario/transporte-configuracao-contabil/log.xhtml")
})
public class TransporteConfiguracaoContabilControlador extends PrettyControlador<TransporteConfiguracaoContabil> implements Serializable, CRUD {

    @EJB
    private TransporteConfiguracaoContabilFacade facade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Future<AssistenteTransporteConfiguracaoContabil> futureProcesso;
    private AssistenteTransporteConfiguracaoContabil assistenteTransporteConfiguracaoContabil;
    private TipoConfiguracaoContabil[] tiposConfiguracao;
    @Enumerated(EnumType.STRING)
    private Opcao opcao;
    @Enumerated(EnumType.STRING)
    private Grupo grupo;

    private List<TransporteConfiguracaoCLPLCP> itensEvento;
    private List<TransporteSaldoFinanceiro> unidadesDaContaFinanceira;
    private List<TransporteSaldoFinanceiro> fontesDaContaFinanceira;
    private List<UnidadeGestora> unidadesGestoras;
    private List<ConfigObrigacaoAPagar> obrigacoesAPagar;
    private List<ConfigCreditoReceber> creditosAReceber;
    private List<ConfigReceitaRealizada> receitasRealizadas;
    private List<ConfigPatrimonioLiquido> patrimoniosLiquidos;
    private List<ConfigInvestimento> investimentos;
    private List<ConfigDividaAtivaContabil> dividasAtivas;
    private List<ConfigPagamentoRestoPagar> pagamentosDeResto;
    private List<ConfigLiquidacaoResPagar> liquidacoesDeResto;
    private List<ConfigCancelamentoResto> cancelamentosDeResto;
    private List<ConfigEmpenhoRestoPagar> empenhosDeResto;
    private List<ConfigPagamento> pagamentos;
    private List<ConfigLiquidacao> liquidacoes;
    private List<ConfigEmpenho> empenhos;
    private List<OCCConta> occsConta;
    private List<OCCBanco> occsBanco;
    private List<OccClassePessoa> occsClassePessoa;
    private List<OCCGrupoBem> occsGrupoBem;
    private List<OCCGrupoMaterial> occsGrupoMaterial;
    private List<OCCNaturezaDividaPublica> occsNaturezaDividaPublica;
    private List<OCCTipoPassivoAtuarial> occsTipoPassivoAtuarial;
    private List<OCCOrigemRecurso> occsOrigemRecurso;
    private List<ConfigDespesaBens> configuracoesDespesaBens;
    private List<ConfigGrupoMaterial> configuracoesGrupoMaterial;
    private List<ConfigTipoViagemContaDespesa> configuracoesTipoViagem;
    private List<ConfigContaDespTipoPessoa> configuracoesTipoPessoa;
    private List<ConfigTipoObjetoCompra> configuracoesTipoObjetoCompra;
    private List<TransporteGrupoOrcamentario> transportesGruposOrcamentarios;

    public TransporteConfiguracaoContabilControlador() {
        super(TransporteConfiguracaoContabil.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transporte-configuracao-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-transporte-configuracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        assistenteTransporteConfiguracaoContabil = new AssistenteTransporteConfiguracaoContabil();
        selecionado.setDataTransporte(sistemaControlador.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        if (selecionado.getDataTransporte() != null) {
            selecionado.setExercicioDestino(facade.getExercicioPorAno(DataUtil.getAno(selecionado.getDataTransporte()) + 1));
        }
        grupo = Grupo.EVENTOS;
        opcao = Opcao.TODOS;
        verificarConfiguracoes();
        instanciarListas();
    }

    private void instanciarListas() {
        itensEvento = Lists.newArrayList();
        unidadesDaContaFinanceira = Lists.newArrayList();
        fontesDaContaFinanceira = Lists.newArrayList();
        unidadesGestoras = Lists.newArrayList();
        obrigacoesAPagar = Lists.newArrayList();
        creditosAReceber = Lists.newArrayList();
        receitasRealizadas = Lists.newArrayList();
        patrimoniosLiquidos = Lists.newArrayList();
        investimentos = Lists.newArrayList();
        dividasAtivas = Lists.newArrayList();
        pagamentosDeResto = Lists.newArrayList();
        liquidacoesDeResto = Lists.newArrayList();
        cancelamentosDeResto = Lists.newArrayList();
        empenhosDeResto = Lists.newArrayList();
        pagamentos = Lists.newArrayList();
        liquidacoes = Lists.newArrayList();
        empenhos = Lists.newArrayList();
        occsConta = Lists.newArrayList();
        occsBanco = Lists.newArrayList();
        occsClassePessoa = Lists.newArrayList();
        occsGrupoBem = Lists.newArrayList();
        occsGrupoMaterial = Lists.newArrayList();
        occsNaturezaDividaPublica = Lists.newArrayList();
        occsTipoPassivoAtuarial = Lists.newArrayList();
        occsOrigemRecurso = Lists.newArrayList();
        configuracoesDespesaBens = Lists.newArrayList();
        configuracoesGrupoMaterial = Lists.newArrayList();
        configuracoesTipoViagem = Lists.newArrayList();
        configuracoesTipoPessoa = Lists.newArrayList();
        configuracoesTipoObjetoCompra = Lists.newArrayList();
        transportesGruposOrcamentarios = Lists.newArrayList();
    }

    public List<Opcao> getOpcoes() {
        return Lists.newArrayList(Opcao.values());
    }

    private void iniciarTipoConfiguracao() {
        tiposConfiguracao = new TipoConfiguracaoContabil[getTiposDeTransportes().size()];
    }

    @URLAction(mappingId = "editar-transporte-configuracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-transporte-configuracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        assistenteTransporteConfiguracaoContabil = new AssistenteTransporteConfiguracaoContabil();
    }

    @URLAction(mappingId = "log-transporte-configuracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        try {
            assistenteTransporteConfiguracaoContabil = (AssistenteTransporteConfiguracaoContabil) Web.pegaDaSessao("TRANSPORTE");
            if (assistenteTransporteConfiguracaoContabil != null) {
                selecionado = assistenteTransporteConfiguracaoContabil.getTransporteConfiguracaoContabil();
                prepararAssistenteTransporte();
                assistenteTransporteConfiguracaoContabil.setQueryOccConta(QueryReprocessamentoService.getService().getQueryOccConta());
                futureProcesso = facade.transportarConfiguracao(assistenteTransporteConfiguracaoContabil);
            }
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void novoTransporte() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
    }

    private void prepararAssistenteTransporte() {
        assistenteTransporteConfiguracaoContabil.setDataTransporte(selecionado.getDataTransporte());
        assistenteTransporteConfiguracaoContabil.setTransporteConfiguracaoContabil(selecionado);
        assistenteTransporteConfiguracaoContabil.setExercicio(selecionado.getExercicioDestino());
        assistenteTransporteConfiguracaoContabil.setUsuarioSistema(selecionado.getUsuarioSistema());
        assistenteTransporteConfiguracaoContabil.setImagem(getCaminhoBrasao());
        assistenteTransporteConfiguracaoContabil.setMapaMensagens(new HashMap<TipoConfiguracaoContabil, HashSet<String>>());
        assistenteTransporteConfiguracaoContabil.setConteudoLog("");
    }

    public void simularConfiguracao() {
        try {
            validarTransporte();
            instanciarListas();
            prepararAssistenteTransporte();
            assistenteTransporteConfiguracaoContabil.setQueryOccConta(QueryReprocessamentoService.getService().getQueryOccConta());
            assistenteTransporteConfiguracaoContabil.getAssistenteBarraProgresso().setCalculando(true);
            selecionado.limparConfiguracoes();
            for (TipoConfiguracaoContabil tipoConfiguracao : tiposConfiguracao) {
                assistenteTransporteConfiguracaoContabil.setTipoConfiguracaoContabil(tipoConfiguracao);
                switch (tipoConfiguracao) {
                    case CLP_LCP:
                        itensEvento = facade.simularTransporteCLPItemEventoCLPAndLCP(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case UNIDADE_FONTE_CONTA_FINANCEIRA:
                        unidadesDaContaFinanceira = facade.simularTransporteUnidadesContaFinanceira(selecionado, assistenteTransporteConfiguracaoContabil);
                        fontesDaContaFinanceira = facade.simularTransporteFontesContaFinanceira(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case UNIDADE_GESTORA:
                        unidadesGestoras = facade.buscarUnidadesGestoras(selecionado.getDataTransporte());
                        break;
                    case CDE_OBRIGACAO_PAGAR:
                        obrigacoesAPagar = facade.simularTransporteCDEObrigacaoAPagar(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_EMPENHO:
                        empenhos = facade.simularTransporteCDEEmpenho(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_EMPENHO_RESTO:
                        empenhosDeResto = facade.simularTransporteCDEEmpenhoResto(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_CANCELAMENTO_RESTO:
                        cancelamentosDeResto = facade.simularTransporteCDECancelamentoResto(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_LIQUIDACAO:
                        liquidacoes = facade.simularTransporteCDELiquidacao(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_LIQUIDACAO_RESTO:
                        liquidacoesDeResto = facade.simularTransporteCDELiquidacaoResto(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_PAGAMENTO:
                        pagamentos = facade.simularTransportarCDEPagamento(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_PAGAMENTO_RESTO:
                        pagamentosDeResto = facade.simularTransporteCDEPagamentoResto(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_RECEITA_REALIZADA:
                        receitasRealizadas = facade.simularTransporteCDEReceitaRealizada(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_CREDITO_RECEBER:
                        creditosAReceber = facade.simularTransporteCDECreditoReceber(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_DIVIDA_ATIVA:
                        dividasAtivas = facade.simularTransporteCDEDividaAtiva(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_INVESTIMENTO:
                        investimentos = facade.simularTransporteCDEInvestimento(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CDE_PATRIMONIO_LIQUIDO:
                        patrimoniosLiquidos = facade.simularTransporteCDEPatrimonioLiquido(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_CONTA:
                        occsConta = facade.simularTransporteConfiguracaoOCCConta(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_BANCO:
                        occsBanco = facade.simularTransporteConfiguracaoOCCBanco(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_CLASSE_CREDOR:
                        occsClassePessoa = facade.simularTransporteConfiguracaoOCCClassePessoa(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_GRUPO_PATRIMONIAL:
                        occsGrupoBem = facade.simularTransporteConfiguracaoOCCGrupoPatrimonial(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_GRUPO_MATERIAL:
                        occsGrupoMaterial = facade.simularTransporteConfiguracaoOCCGrupoMaterial(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_NATUREZA_DIVIDA_PUBLICA:
                        occsNaturezaDividaPublica = facade.simularTransporteConfiguracaoOCCNaturezaDividaPublica(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_TIPO_PASSIVO_ATUARIAL:
                        occsTipoPassivoAtuarial = facade.simularTransporteConfiguracaoOCCTipoPassivoAtuarial(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case OCC_ORIGEM_RECURSO:
                        occsOrigemRecurso = facade.simularTransporteConfiguracaoOCCOrigemRecurso(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CONFIGURACAO_CONTA_DESPESA_GRUPO_PATRIMONIAL:
                        configuracoesDespesaBens = facade.simularTransporteConfiguracaoContaDespesaGrupoPatrimonial(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CONFIGURACAO_CONTA_DESPESA_GRUPO_MATERIAL:
                        configuracoesGrupoMaterial = facade.simularTransporteConfiguracaoContaDespesaGrupoMaterial(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CONFIGURACAO_CONTA_DESPESA_TIPO_VIAGEM:
                        configuracoesTipoViagem = facade.simularTransporteConfiguracaoContaDespesaTipoViagem(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CONFIGURACAO_CONTA_DESPESA_TIPO_PESSOA:
                        configuracoesTipoPessoa = facade.simularTransporteConfiguracaoContaDespesaTipoPessoa(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case CONFIGURACAO_CONTA_DESPESA_TIPO_OBJETO_COMPRA:
                        configuracoesTipoObjetoCompra = facade.simularTransporteConfiguracaoContaDespesaTipoObjetoCompra(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                    case GRUPO_ORCAMENTARIO:
                        transportesGruposOrcamentarios = facade.simularTransporteGruposOrcamentarios(selecionado, assistenteTransporteConfiguracaoContabil);
                        break;
                }
            }
            facade.montarMapaLog(assistenteTransporteConfiguracaoContabil, selecionado);
            assistenteTransporteConfiguracaoContabil.getAssistenteBarraProgresso().setCalculando(false);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void transportarConfiguracao() {
        try {
            validarTransporte();
            adicionarTipoTransporte();
            prepararAssistenteTransporte();
            selecionado.limparConfiguracoes();
            Web.poeNaSessao("TRANSPORTE", assistenteTransporteConfiguracaoContabil);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "acompanhamento/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void adicionarTipoTransporte() {
        selecionado.setTiposConfiguracoesContabeis(new ArrayList<TransporteTipoConfiguracaoContabil>());
        for (TipoConfiguracaoContabil tipoConfiguracao : tiposConfiguracao) {
            TransporteTipoConfiguracaoContabil tipo = new TransporteTipoConfiguracaoContabil();
            tipo.setTransporteConfigContabil(selecionado);
            tipo.setTipoConfiguracaoContabil(tipoConfiguracao);
            Util.adicionarObjetoEmLista(selecionado.getTiposConfiguracoesContabeis(), tipo);
        }
    }

    public List<TipoConfiguracaoContabil> getTiposDeTransportes() {
        if (grupo == null) {
            return Lists.newArrayList();
        }
        List<TipoConfiguracaoContabil> retorno = Lists.newArrayList();
        switch (grupo) {
            case EVENTOS:
                retorno.add(TipoConfiguracaoContabil.CLP_LCP);
                break;
            case CDES:
                retorno.add(TipoConfiguracaoContabil.CDE_OBRIGACAO_PAGAR);
                retorno.add(TipoConfiguracaoContabil.CDE_EMPENHO);
                retorno.add(TipoConfiguracaoContabil.CDE_EMPENHO_RESTO);
                retorno.add(TipoConfiguracaoContabil.CDE_CANCELAMENTO_RESTO);
                retorno.add(TipoConfiguracaoContabil.CDE_LIQUIDACAO);
                retorno.add(TipoConfiguracaoContabil.CDE_LIQUIDACAO_RESTO);
                retorno.add(TipoConfiguracaoContabil.CDE_PAGAMENTO);
                retorno.add(TipoConfiguracaoContabil.CDE_PAGAMENTO_RESTO);
                retorno.add(TipoConfiguracaoContabil.CDE_RECEITA_REALIZADA);
                retorno.add(TipoConfiguracaoContabil.CDE_CREDITO_RECEBER);
                retorno.add(TipoConfiguracaoContabil.CDE_DIVIDA_ATIVA);
                retorno.add(TipoConfiguracaoContabil.CDE_INVESTIMENTO);
                retorno.add(TipoConfiguracaoContabil.CDE_PATRIMONIO_LIQUIDO);
                break;
            case OCCS:
                retorno.add(TipoConfiguracaoContabil.OCC_CONTA);
                retorno.add(TipoConfiguracaoContabil.OCC_BANCO);
                retorno.add(TipoConfiguracaoContabil.OCC_CLASSE_CREDOR);
                retorno.add(TipoConfiguracaoContabil.OCC_GRUPO_PATRIMONIAL);
                retorno.add(TipoConfiguracaoContabil.OCC_GRUPO_MATERIAL);
                retorno.add(TipoConfiguracaoContabil.OCC_NATUREZA_DIVIDA_PUBLICA);
                retorno.add(TipoConfiguracaoContabil.OCC_TIPO_PASSIVO_ATUARIAL);
                retorno.add(TipoConfiguracaoContabil.OCC_ORIGEM_RECURSO);
                break;
            case OUTROS:
                retorno.add(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_GRUPO_PATRIMONIAL);
                retorno.add(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_GRUPO_MATERIAL);
                retorno.add(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_VIAGEM);
                retorno.add(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_PESSOA);
                retorno.add(TipoConfiguracaoContabil.CONFIGURACAO_CONTA_DESPESA_TIPO_OBJETO_COMPRA);
                retorno.add(TipoConfiguracaoContabil.UNIDADE_FONTE_CONTA_FINANCEIRA);
                retorno.add(TipoConfiguracaoContabil.UNIDADE_GESTORA);
                retorno.add(TipoConfiguracaoContabil.GRUPO_ORCAMENTARIO);
                break;
        }
        return retorno;
    }

    private void validarTransporte() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (tiposConfiguracao.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Configuração dever ser informado. Selecione oa menos uma configuração para continuar a operação.");
        }
        facade.validarPlanoContas(selecionado.getExercicioDestino(), ve);
        for (TipoConfiguracaoContabil tipoConfiguracaoContabil : tiposConfiguracao) {
            if (facade.hasTransportePeloTipo(selecionado, tipoConfiguracaoContabil)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um transporte para o exercício " + selecionado.getExercicioDestino().getAno() + " do tipo de configuração " + tipoConfiguracaoContabil.getDescricao() + ".");
            }
        }
        ve.lancarException();
    }

    public void gerarLog() {
        try {
            String conteudo = assistenteTransporteConfiguracaoContabil.getConteudoLog();
            Util.geraPDF("Transporte de Configurações Contábeis - Log-erros", conteudo, FacesContext.getCurrentInstance());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao gerar o log! Detalhe: " + ex.getMessage());
        }
    }

    public void gerarLogVisualizar() {
        try {
            prepararAssistenteTransporte();
            facade.montarMapaLog(assistenteTransporteConfiguracaoContabil, selecionado);
            String conteudo = assistenteTransporteConfiguracaoContabil.getConteudoLog();
            Util.geraPDF("Transporte de Configurações Contábeis - Log-erros", conteudo, FacesContext.getCurrentInstance());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao gerar o log! Detalhe: " + ex.getMessage());
        }
    }

    public void finalizarBarraProgresso() {
        if (!assistenteTransporteConfiguracaoContabil.getAssistenteBarraProgresso().getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.executaJavaScript("poll.stop()");
        }
    }

    public void consultaProcesso() {
        if (futureProcesso != null && futureProcesso.isDone()) {
            selecionado = assistenteTransporteConfiguracaoContabil.getTransporteConfiguracaoContabil();
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public void verificarConfiguracoes() {
        iniciarTipoConfiguracao();
        int pos = 0;
        for (TipoConfiguracaoContabil tipo : getTiposDeTransportes()) {
            tiposConfiguracao[pos] = tipo;
            pos++;
        }
    }

    public void pararProcessamento() {
        futureProcesso.cancel(true);
        assistenteTransporteConfiguracaoContabil.getAssistenteBarraProgresso().setCalculando(Boolean.FALSE);
    }

    public List<SelectItem> getGrupos() {
        return Util.getListSelectItemSemCampoVazio(Grupo.values(), false);
    }

    public TransporteConfiguracaoContabil getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TransporteConfiguracaoContabil selecionado) {
        this.selecionado = selecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }


    public AssistenteTransporteConfiguracaoContabil getAssistenteTransporteConfiguracaoContabil() {
        return assistenteTransporteConfiguracaoContabil;
    }

    public void setAssistenteTransporteConfiguracaoContabil(AssistenteTransporteConfiguracaoContabil assistenteTransporteConfiguracaoContabil) {
        this.assistenteTransporteConfiguracaoContabil = assistenteTransporteConfiguracaoContabil;
    }

    public TipoConfiguracaoContabil[] getTiposConfiguracao() {
        return tiposConfiguracao;
    }

    public void setTiposConfiguracao(TipoConfiguracaoContabil[] tiposConfiguracao) {
        this.tiposConfiguracao = tiposConfiguracao;
    }

    private String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/escudo.png";
        return imagem;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<TransporteConfiguracaoCLPLCP> getItensEvento() {
        List<TransporteConfiguracaoCLPLCP> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (TransporteConfiguracaoCLPLCP transporteConfiguracaoCLPLCP : itensEvento) {
                    if (transporteConfiguracaoCLPLCP.getErroDuranteProcessamento()) {
                        retorno.add(transporteConfiguracaoCLPLCP);
                    }
                }
                return retorno;
            case SUCESSO:
                for (TransporteConfiguracaoCLPLCP transporteConfiguracaoCLPLCP : itensEvento) {
                    if (!transporteConfiguracaoCLPLCP.getErroDuranteProcessamento()) {
                        retorno.add(transporteConfiguracaoCLPLCP);
                    }
                }
                return retorno;
            default:
                return itensEvento;
        }
    }

    public void setItensEvento(List<TransporteConfiguracaoCLPLCP> itensEvento) {
        this.itensEvento = itensEvento;
    }

    public List<TransporteSaldoFinanceiro> getUnidadesDaContaFinanceira() {
        return buscarFontesOrUnidadesDaContaFinanceira(unidadesDaContaFinanceira);
    }

    public void setUnidadesDaContaFinanceira(List<TransporteSaldoFinanceiro> unidadesDaContaFinanceira) {
        this.unidadesDaContaFinanceira = unidadesDaContaFinanceira;
    }

    public List<TransporteSaldoFinanceiro> getFontesDaContaFinanceira() {
        return buscarFontesOrUnidadesDaContaFinanceira(fontesDaContaFinanceira);
    }

    private List<TransporteSaldoFinanceiro> buscarFontesOrUnidadesDaContaFinanceira(List<TransporteSaldoFinanceiro> fontesOrUnidadesDaContaFinanceira) {
        List<TransporteSaldoFinanceiro> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : fontesOrUnidadesDaContaFinanceira) {
                    if (transporteSaldoFinanceiro.getErroDuranteProcessamento()) {
                        retorno.add(transporteSaldoFinanceiro);
                    }
                }
                return retorno;
            case SUCESSO:
                for (TransporteSaldoFinanceiro transporteSaldoFinanceiro : fontesOrUnidadesDaContaFinanceira) {
                    if (!transporteSaldoFinanceiro.getErroDuranteProcessamento()) {
                        retorno.add(transporteSaldoFinanceiro);
                    }
                }
                return retorno;
            default:
                return fontesOrUnidadesDaContaFinanceira;
        }
    }

    public void setFontesDaContaFinanceira(List<TransporteSaldoFinanceiro> fontesDaContaFinanceira) {
        this.fontesDaContaFinanceira = fontesDaContaFinanceira;
    }

    public List<UnidadeGestora> getUnidadesGestoras() {
        List<UnidadeGestora> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (UnidadeGestora unidadeGestora : unidadesGestoras) {
                    if (unidadeGestora.getErroDuranteProcessamento()) {
                        retorno.add(unidadeGestora);
                    }
                }
                return retorno;
            case SUCESSO:
                for (UnidadeGestora unidadeGestora : unidadesGestoras) {
                    if (!unidadeGestora.getErroDuranteProcessamento()) {
                        retorno.add(unidadeGestora);
                    }
                }
                return retorno;
            default:
                return unidadesGestoras;
        }
    }

    public void setUnidadesGestoras(List<UnidadeGestora> unidadesGestoras) {
        this.unidadesGestoras = unidadesGestoras;
    }

    public List<ConfigObrigacaoAPagar> getObrigacoesAPagar() {
        List<ConfigObrigacaoAPagar> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigObrigacaoAPagar configObrigacaoAPagar : obrigacoesAPagar) {
                    if (configObrigacaoAPagar.getErroDuranteProcessamento()) {
                        retorno.add(configObrigacaoAPagar);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigObrigacaoAPagar configObrigacaoAPagar : obrigacoesAPagar) {
                    if (!configObrigacaoAPagar.getErroDuranteProcessamento()) {
                        retorno.add(configObrigacaoAPagar);
                    }
                }
                return retorno;
            default:
                return obrigacoesAPagar;
        }
    }

    public void setObrigacoesAPagar(List<ConfigObrigacaoAPagar> obrigacoesAPagar) {
        this.obrigacoesAPagar = obrigacoesAPagar;
    }

    public List<ConfigCreditoReceber> getCreditosAReceber() {
        List<ConfigCreditoReceber> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigCreditoReceber configCreditoReceber : creditosAReceber) {
                    if (configCreditoReceber.getErroDuranteProcessamento()) {
                        retorno.add(configCreditoReceber);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigCreditoReceber configCreditoReceber : creditosAReceber) {
                    if (!configCreditoReceber.getErroDuranteProcessamento()) {
                        retorno.add(configCreditoReceber);
                    }
                }
                return retorno;
            default:
                return creditosAReceber;
        }
    }

    public void setCreditosAReceber(List<ConfigCreditoReceber> creditosAReceber) {
        this.creditosAReceber = creditosAReceber;
    }

    public List<ConfigReceitaRealizada> getReceitasRealizadas() {
        List<ConfigReceitaRealizada> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigReceitaRealizada configReceitaRealizada : receitasRealizadas) {
                    if (configReceitaRealizada.getErroDuranteProcessamento()) {
                        retorno.add(configReceitaRealizada);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigReceitaRealizada configReceitaRealizada : receitasRealizadas) {
                    if (!configReceitaRealizada.getErroDuranteProcessamento()) {
                        retorno.add(configReceitaRealizada);
                    }
                }
                return retorno;
            default:
                return receitasRealizadas;
        }
    }

    public void setReceitasRealizadas(List<ConfigReceitaRealizada> receitasRealizadas) {
        this.receitasRealizadas = receitasRealizadas;
    }

    public List<ConfigPatrimonioLiquido> getPatrimoniosLiquidos() {
        List<ConfigPatrimonioLiquido> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigPatrimonioLiquido configPatrimonioLiquido : patrimoniosLiquidos) {
                    if (configPatrimonioLiquido.getErroDuranteProcessamento()) {
                        retorno.add(configPatrimonioLiquido);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigPatrimonioLiquido configPatrimonioLiquido : patrimoniosLiquidos) {
                    if (!configPatrimonioLiquido.getErroDuranteProcessamento()) {
                        retorno.add(configPatrimonioLiquido);
                    }
                }
                return retorno;
            default:
                return patrimoniosLiquidos;
        }
    }

    public void setPatrimoniosLiquidos(List<ConfigPatrimonioLiquido> patrimoniosLiquidos) {
        this.patrimoniosLiquidos = patrimoniosLiquidos;
    }

    public List<ConfigInvestimento> getInvestimentos() {
        List<ConfigInvestimento> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigInvestimento configInvestimento : investimentos) {
                    if (configInvestimento.getErroDuranteProcessamento()) {
                        retorno.add(configInvestimento);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigInvestimento configInvestimento : investimentos) {
                    if (!configInvestimento.getErroDuranteProcessamento()) {
                        retorno.add(configInvestimento);
                    }
                }
                return retorno;
            default:
                return investimentos;
        }
    }

    public void setInvestimentos(List<ConfigInvestimento> investimentos) {
        this.investimentos = investimentos;
    }

    public List<ConfigDividaAtivaContabil> getDividasAtivas() {
        List<ConfigDividaAtivaContabil> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigDividaAtivaContabil configDividaAtivaContabil : dividasAtivas) {
                    if (configDividaAtivaContabil.getErroDuranteProcessamento()) {
                        retorno.add(configDividaAtivaContabil);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigDividaAtivaContabil configDividaAtivaContabil : dividasAtivas) {
                    if (!configDividaAtivaContabil.getErroDuranteProcessamento()) {
                        retorno.add(configDividaAtivaContabil);
                    }
                }
                return retorno;
            default:
                return dividasAtivas;
        }
    }

    public void setDividasAtivas(List<ConfigDividaAtivaContabil> dividasAtivas) {
        this.dividasAtivas = dividasAtivas;
    }

    public List<ConfigPagamentoRestoPagar> getPagamentosDeResto() {
        List<ConfigPagamentoRestoPagar> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigPagamentoRestoPagar configPagamentoRestoPagar : pagamentosDeResto) {
                    if (configPagamentoRestoPagar.getErroDuranteProcessamento()) {
                        retorno.add(configPagamentoRestoPagar);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigPagamentoRestoPagar configPagamentoRestoPagar : pagamentosDeResto) {
                    if (!configPagamentoRestoPagar.getErroDuranteProcessamento()) {
                        retorno.add(configPagamentoRestoPagar);
                    }
                }
                return retorno;
            default:
                return pagamentosDeResto;
        }
    }

    public void setPagamentosDeResto(List<ConfigPagamentoRestoPagar> pagamentosDeResto) {
        this.pagamentosDeResto = pagamentosDeResto;
    }

    public List<ConfigLiquidacaoResPagar> getLiquidacoesDeResto() {
        List<ConfigLiquidacaoResPagar> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigLiquidacaoResPagar configLiquidacaoResPagar : liquidacoesDeResto) {
                    if (configLiquidacaoResPagar.getErroDuranteProcessamento()) {
                        retorno.add(configLiquidacaoResPagar);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigLiquidacaoResPagar configLiquidacaoResPagar : liquidacoesDeResto) {
                    if (!configLiquidacaoResPagar.getErroDuranteProcessamento()) {
                        retorno.add(configLiquidacaoResPagar);
                    }
                }
                return retorno;
            default:
                return liquidacoesDeResto;
        }
    }

    public void setLiquidacoesDeResto(List<ConfigLiquidacaoResPagar> liquidacoesDeResto) {
        this.liquidacoesDeResto = liquidacoesDeResto;
    }

    public List<ConfigCancelamentoResto> getCancelamentosDeResto() {
        List<ConfigCancelamentoResto> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigCancelamentoResto configCancelamentoResto : cancelamentosDeResto) {
                    if (configCancelamentoResto.getErroDuranteProcessamento()) {
                        retorno.add(configCancelamentoResto);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigCancelamentoResto configCancelamentoResto : cancelamentosDeResto) {
                    if (!configCancelamentoResto.getErroDuranteProcessamento()) {
                        retorno.add(configCancelamentoResto);
                    }
                }
                return retorno;
            default:
                return cancelamentosDeResto;
        }
    }

    public void setCancelamentosDeResto(List<ConfigCancelamentoResto> cancelamentosDeResto) {
        this.cancelamentosDeResto = cancelamentosDeResto;
    }

    public List<ConfigEmpenhoRestoPagar> getEmpenhosDeResto() {
        List<ConfigEmpenhoRestoPagar> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigEmpenhoRestoPagar configEmpenhoRestoPagar : empenhosDeResto) {
                    if (configEmpenhoRestoPagar.getErroDuranteProcessamento()) {
                        retorno.add(configEmpenhoRestoPagar);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigEmpenhoRestoPagar configEmpenhoRestoPagar : empenhosDeResto) {
                    if (!configEmpenhoRestoPagar.getErroDuranteProcessamento()) {
                        retorno.add(configEmpenhoRestoPagar);
                    }
                }
                return retorno;
            default:
                return empenhosDeResto;
        }
    }

    public void setEmpenhosDeResto(List<ConfigEmpenhoRestoPagar> empenhosDeResto) {
        this.empenhosDeResto = empenhosDeResto;
    }

    public List<ConfigPagamento> getPagamentos() {
        List<ConfigPagamento> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigPagamento configPagamento : pagamentos) {
                    if (configPagamento.getErroDuranteProcessamento()) {
                        retorno.add(configPagamento);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigPagamento configPagamento : pagamentos) {
                    if (!configPagamento.getErroDuranteProcessamento()) {
                        retorno.add(configPagamento);
                    }
                }
                return retorno;
            default:
                return pagamentos;
        }
    }

    public void setPagamentos(List<ConfigPagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<ConfigLiquidacao> getLiquidacoes() {
        List<ConfigLiquidacao> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigLiquidacao configLiquidacao : liquidacoes) {
                    if (configLiquidacao.getErroDuranteProcessamento()) {
                        retorno.add(configLiquidacao);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigLiquidacao configLiquidacao : liquidacoes) {
                    if (!configLiquidacao.getErroDuranteProcessamento()) {
                        retorno.add(configLiquidacao);
                    }
                }
                return retorno;
            default:
                return liquidacoes;
        }
    }

    public void setLiquidacoes(List<ConfigLiquidacao> liquidacoes) {
        this.liquidacoes = liquidacoes;
    }

    public List<ConfigEmpenho> getEmpenhos() {
        List<ConfigEmpenho> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigEmpenho configEmpenho : empenhos) {
                    if (configEmpenho.getErroDuranteProcessamento()) {
                        retorno.add(configEmpenho);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigEmpenho configEmpenho : empenhos) {
                    if (!configEmpenho.getErroDuranteProcessamento()) {
                        retorno.add(configEmpenho);
                    }
                }
                return retorno;
            default:
                return empenhos;
        }
    }

    public void setEmpenhos(List<ConfigEmpenho> empenhos) {
        this.empenhos = empenhos;
    }

    public List<OCCConta> getOccsConta() {
        List<OCCConta> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCConta occConta : occsConta) {
                    if (occConta.getErroDuranteProcessamento()) {
                        retorno.add(occConta);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCConta occConta : occsConta) {
                    if (!occConta.getErroDuranteProcessamento()) {
                        retorno.add(occConta);
                    }
                }
                return retorno;
            default:
                return occsConta;
        }
    }

    public void setOccsConta(List<OCCConta> occsConta) {
        this.occsConta = occsConta;
    }

    public List<OCCBanco> getOccsBanco() {
        List<OCCBanco> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCBanco occBanco : occsBanco) {
                    if (occBanco.getErroDuranteProcessamento()) {
                        retorno.add(occBanco);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCBanco occBanco : occsBanco) {
                    if (!occBanco.getErroDuranteProcessamento()) {
                        retorno.add(occBanco);
                    }
                }
                return retorno;
            default:
                return occsBanco;
        }
    }

    public void setOccsBanco(List<OCCBanco> occsBanco) {
        this.occsBanco = occsBanco;
    }

    public List<OccClassePessoa> getOccsClassePessoa() {
        List<OccClassePessoa> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OccClassePessoa occClassePessoa : occsClassePessoa) {
                    if (occClassePessoa.getErroDuranteProcessamento()) {
                        retorno.add(occClassePessoa);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OccClassePessoa occClassePessoa : occsClassePessoa) {
                    if (!occClassePessoa.getErroDuranteProcessamento()) {
                        retorno.add(occClassePessoa);
                    }
                }
                return retorno;
            default:
                return occsClassePessoa;
        }
    }

    public void setOccsClassePessoa(List<OccClassePessoa> occsClassePessoa) {
        this.occsClassePessoa = occsClassePessoa;
    }

    public List<OCCGrupoBem> getOccsGrupoBem() {
        List<OCCGrupoBem> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCGrupoBem occGrupoBem : occsGrupoBem) {
                    if (occGrupoBem.getErroDuranteProcessamento()) {
                        retorno.add(occGrupoBem);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCGrupoBem occGrupoBem : occsGrupoBem) {
                    if (!occGrupoBem.getErroDuranteProcessamento()) {
                        retorno.add(occGrupoBem);
                    }
                }
                return retorno;
            default:
                return occsGrupoBem;
        }
    }

    public void setOccsGrupoBem(List<OCCGrupoBem> occsGrupoBem) {
        this.occsGrupoBem = occsGrupoBem;
    }

    public List<OCCGrupoMaterial> getOccsGrupoMaterial() {
        List<OCCGrupoMaterial> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCGrupoMaterial occGrupoMaterial : occsGrupoMaterial) {
                    if (occGrupoMaterial.getErroDuranteProcessamento()) {
                        retorno.add(occGrupoMaterial);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCGrupoMaterial occGrupoMaterial : occsGrupoMaterial) {
                    if (!occGrupoMaterial.getErroDuranteProcessamento()) {
                        retorno.add(occGrupoMaterial);
                    }
                }
                return retorno;
            default:
                return occsGrupoMaterial;
        }
    }

    public void setOccsGrupoMaterial(List<OCCGrupoMaterial> occsGrupoMaterial) {
        this.occsGrupoMaterial = occsGrupoMaterial;
    }

    public List<OCCNaturezaDividaPublica> getOccsNaturezaDividaPublica() {
        List<OCCNaturezaDividaPublica> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCNaturezaDividaPublica occNaturezaDividaPublica : occsNaturezaDividaPublica) {
                    if (occNaturezaDividaPublica.getErroDuranteProcessamento()) {
                        retorno.add(occNaturezaDividaPublica);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCNaturezaDividaPublica occNaturezaDividaPublica : occsNaturezaDividaPublica) {
                    if (!occNaturezaDividaPublica.getErroDuranteProcessamento()) {
                        retorno.add(occNaturezaDividaPublica);
                    }
                }
                return retorno;
            default:
                return occsNaturezaDividaPublica;
        }
    }

    public void setOccsNaturezaDividaPublica(List<OCCNaturezaDividaPublica> occsNaturezaDividaPublica) {
        this.occsNaturezaDividaPublica = occsNaturezaDividaPublica;
    }

    public List<OCCTipoPassivoAtuarial> getOccsTipoPassivoAtuarial() {
        List<OCCTipoPassivoAtuarial> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCTipoPassivoAtuarial occTipoPassivoAtuarial : occsTipoPassivoAtuarial) {
                    if (occTipoPassivoAtuarial.getErroDuranteProcessamento()) {
                        retorno.add(occTipoPassivoAtuarial);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCTipoPassivoAtuarial occTipoPassivoAtuarial : occsTipoPassivoAtuarial) {
                    if (!occTipoPassivoAtuarial.getErroDuranteProcessamento()) {
                        retorno.add(occTipoPassivoAtuarial);
                    }
                }
                return retorno;
            default:
                return occsTipoPassivoAtuarial;
        }
    }

    public void setOccsTipoPassivoAtuarial(List<OCCTipoPassivoAtuarial> occsTipoPassivoAtuarial) {
        this.occsTipoPassivoAtuarial = occsTipoPassivoAtuarial;
    }

    public List<OCCOrigemRecurso> getOccsOrigemRecurso() {
        List<OCCOrigemRecurso> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (OCCOrigemRecurso occOrigemRecurso : occsOrigemRecurso) {
                    if (occOrigemRecurso.getErroDuranteProcessamento()) {
                        retorno.add(occOrigemRecurso);
                    }
                }
                return retorno;
            case SUCESSO:
                for (OCCOrigemRecurso occOrigemRecurso : occsOrigemRecurso) {
                    if (!occOrigemRecurso.getErroDuranteProcessamento()) {
                        retorno.add(occOrigemRecurso);
                    }
                }
                return retorno;
            default:
                return occsOrigemRecurso;
        }
    }

    public void setOccsOrigemRecurso(List<OCCOrigemRecurso> occsOrigemRecurso) {
        this.occsOrigemRecurso = occsOrigemRecurso;
    }

    public List<ConfigDespesaBens> getConfiguracoesDespesaBens() {
        List<ConfigDespesaBens> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigDespesaBens configDespesaBens : configuracoesDespesaBens) {
                    if (configDespesaBens.getErroDuranteProcessamento()) {
                        retorno.add(configDespesaBens);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigDespesaBens configDespesaBens : configuracoesDespesaBens) {
                    if (!configDespesaBens.getErroDuranteProcessamento()) {
                        retorno.add(configDespesaBens);
                    }
                }
                return retorno;
            default:
                return configuracoesDespesaBens;
        }
    }

    public void setConfiguracoesDespesaBens(List<ConfigDespesaBens> configuracoesDespesaBens) {
        this.configuracoesDespesaBens = configuracoesDespesaBens;
    }

    public List<ConfigGrupoMaterial> getConfiguracoesGrupoMaterial() {
        List<ConfigGrupoMaterial> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigGrupoMaterial configGrupoMaterial : configuracoesGrupoMaterial) {
                    if (configGrupoMaterial.getErroDuranteProcessamento()) {
                        retorno.add(configGrupoMaterial);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigGrupoMaterial configGrupoMaterial : configuracoesGrupoMaterial) {
                    if (!configGrupoMaterial.getErroDuranteProcessamento()) {
                        retorno.add(configGrupoMaterial);
                    }
                }
                return retorno;
            default:
                return configuracoesGrupoMaterial;
        }
    }

    public void setConfiguracoesGrupoMaterial(List<ConfigGrupoMaterial> configuracoesGrupoMaterial) {
        this.configuracoesGrupoMaterial = configuracoesGrupoMaterial;
    }

    public List<ConfigTipoViagemContaDespesa> getConfiguracoesTipoViagem() {
        List<ConfigTipoViagemContaDespesa> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigTipoViagemContaDespesa configTipoViagemContaDespesa : configuracoesTipoViagem) {
                    if (configTipoViagemContaDespesa.getErroDuranteProcessamento()) {
                        retorno.add(configTipoViagemContaDespesa);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigTipoViagemContaDespesa configTipoViagemContaDespesa : configuracoesTipoViagem) {
                    if (!configTipoViagemContaDespesa.getErroDuranteProcessamento()) {
                        retorno.add(configTipoViagemContaDespesa);
                    }
                }
                return retorno;
            default:
                return configuracoesTipoViagem;
        }
    }

    public List<TipoViagemContaDespesa> getConfiguracoesTipoViagemContaDespesa() {
        List<TipoViagemContaDespesa> retorno = Lists.newArrayList();
        if (configuracoesTipoViagem != null) {
            for (ConfigTipoViagemContaDespesa configTipoViagemContaDespesa : getConfiguracoesTipoViagem()) {
                for (TipoViagemContaDespesa tipoViagemContaDespesa : configTipoViagemContaDespesa.getListaContaDespesa()) {
                    tipoViagemContaDespesa.setErroDuranteProcessamento(configTipoViagemContaDespesa.getErroDuranteProcessamento());
                    tipoViagemContaDespesa.setMensagemDeErro(configTipoViagemContaDespesa.getMensagemDeErro());
                    retorno.add(tipoViagemContaDespesa);
                }
            }
        }
        return retorno;
    }

    public void setConfiguracoesTipoViagem(List<ConfigTipoViagemContaDespesa> configuracoesTipoViagem) {
        this.configuracoesTipoViagem = configuracoesTipoViagem;
    }

    public List<ConfigContaDespTipoPessoa> getConfiguracoesTipoPessoa() {
        List<ConfigContaDespTipoPessoa> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigContaDespTipoPessoa configContaDespTipoPessoa : configuracoesTipoPessoa) {
                    if (configContaDespTipoPessoa.getErroDuranteProcessamento()) {
                        retorno.add(configContaDespTipoPessoa);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigContaDespTipoPessoa configContaDespTipoPessoa : configuracoesTipoPessoa) {
                    if (!configContaDespTipoPessoa.getErroDuranteProcessamento()) {
                        retorno.add(configContaDespTipoPessoa);
                    }
                }
                return retorno;
            default:
                return configuracoesTipoPessoa;
        }
    }

    public void setConfiguracoesTipoPessoa(List<ConfigContaDespTipoPessoa> configuracoesTipoPessoa) {
        this.configuracoesTipoPessoa = configuracoesTipoPessoa;
    }

    public List<ConfigTipoObjetoCompra> getConfiguracoesTipoObjetoCompra() {
        List<ConfigTipoObjetoCompra> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (ConfigTipoObjetoCompra configTipoObjetoCompra : configuracoesTipoObjetoCompra) {
                    if (configTipoObjetoCompra.getErroDuranteProcessamento()) {
                        retorno.add(configTipoObjetoCompra);
                    }
                }
                return retorno;
            case SUCESSO:
                for (ConfigTipoObjetoCompra configTipoObjetoCompra : configuracoesTipoObjetoCompra) {
                    if (!configTipoObjetoCompra.getErroDuranteProcessamento()) {
                        retorno.add(configTipoObjetoCompra);
                    }
                }
                return retorno;
            default:
                return configuracoesTipoObjetoCompra;
        }
    }

    public void setConfiguracoesTipoObjetoCompra(List<ConfigTipoObjetoCompra> configuracoesTipoObjetoCompra) {
        this.configuracoesTipoObjetoCompra = configuracoesTipoObjetoCompra;
    }

    public Opcao getOpcao() {
        return opcao;
    }

    public void setOpcao(Opcao opcao) {
        this.opcao = opcao;
    }

    public List<TransporteGrupoOrcamentario> getTransportesGruposOrcamentarios() {
        List<TransporteGrupoOrcamentario> retorno = Lists.newArrayList();
        switch (opcao) {
            case ERRO:
                for (TransporteGrupoOrcamentario transporteGrupoOrcamentario : transportesGruposOrcamentarios) {
                    if (!Strings.isNullOrEmpty(transporteGrupoOrcamentario.getLog())) {
                        retorno.add(transporteGrupoOrcamentario);
                    }
                }
                return retorno;
            case SUCESSO:
                for (TransporteGrupoOrcamentario transporteGrupoOrcamentario : transportesGruposOrcamentarios) {
                    if (Strings.isNullOrEmpty(transporteGrupoOrcamentario.getLog())) {
                        retorno.add(transporteGrupoOrcamentario);
                    }
                }
                return retorno;
            default:
                return transportesGruposOrcamentarios;
        }
    }

    public void setTransportesGruposOrcamentarios(List<TransporteGrupoOrcamentario> transportesGruposOrcamentarios) {
        this.transportesGruposOrcamentarios = transportesGruposOrcamentarios;
    }

    public enum Grupo {
        EVENTOS("Eventos"),
        OCCS("OCCs"),
        CDES("CDEs"),
        OUTROS("Outros");

        private String descricao;

        Grupo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum Opcao {
        TODOS("Todos"),
        SUCESSO("Sucesso"),
        ERRO("Erro");

        private String descricao;

        Opcao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
