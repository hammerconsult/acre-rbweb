package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DataTableConsultaDebito;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.LancamentoDescontoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "lancamentoDescontoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLancamento", pattern = "/tributario/lancamento-de-desconto/novo/", viewId = "/faces/tributario/arrecadacao/lancamentodesconto/edita.xhtml"),
    @URLMapping(id = "editarLancamento", pattern = "/tributario/lancamento-de-desconto/editar/#{lancamentoDescontoControlador.id}/", viewId = "/faces/tributario/arrecadacao/lancamentodesconto/edita.xhtml"),
    @URLMapping(id = "listarLancamento", pattern = "/tributario/lancamento-de-desconto/listar/", viewId = "/faces/tributario/arrecadacao/lancamentodesconto/lista.xhtml"),
    @URLMapping(id = "verLancamento", pattern = "/tributario/lancamento-de-desconto/ver/#{lancamentoDescontoControlador.id}/", viewId = "/faces/tributario/arrecadacao/lancamentodesconto/visualizar.xhtml")
})
public class LancamentoDescontoControlador extends PrettyControlador<LancamentoDesconto> implements Serializable, CRUD {

    private static final String DIVIDA_ATIVA = "Dívida Ativa";
    private static final String DIVIDA_ATIVA_AJUIZADA = "Dívida Ativa Ajuizada";
    private static final String DO_EXERCICIO = "Do Exercício";
    @EJB
    private LancamentoDescontoFacade facade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private ConverterAutoComplete converterAtoLegal;
    private TipoCadastroTributario tipoCadastroTributario;
    private Cadastro cadastro;
    private Cadastro informacoesCadastro;
    private Pessoa filtroContribuinte;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private Boolean incluiSocios;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterExercicio converterExercicio;
    private SituacaoParcela[] situacoes;
    private List<DataTableConsultaDebito> listaParcela;
    private DataTableConsultaDebito[] parcelas;
    private BigDecimal valorTotal;
    private BigDecimal valorCorrecaoMonetaria;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorParcela;
    private BigDecimal valorSaldo;
    private BigDecimal valorUFM;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private SituacaoParcela situacaoParcela;
    private List<ResultadoParcela> resultadoParcelas;
    private ResultadoParcela[] parcelasTable;
    private Divida filtroDivida;
    private ConverterGenerico converterDivida;


    public LancamentoDescontoControlador() {
        super(LancamentoDesconto.class);
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    public List<DataTableConsultaDebito> getListaParcela() {
        return listaParcela;
    }

    public SituacaoParcela[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoParcela[] situacoes) {
        this.situacoes = situacoes;
    }

    public Boolean getIncluiSocios() {
        return incluiSocios;
    }

    public void setIncluiSocios(Boolean incluiSocios) {
        this.incluiSocios = incluiSocios;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public DataTableConsultaDebito[] getParcelas() {
        return parcelas;
    }

    public void setParcelas(DataTableConsultaDebito[] parcelas) {
        this.parcelas = parcelas;
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public ResultadoParcela[] getParcelasTable() {
        return parcelasTable;
    }

    public void setParcelasTable(ResultadoParcela[] parcelasTable) {
        this.parcelasTable = parcelasTable;
    }

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public boolean isDividaAtivaAzuijada() {
        return dividaAtivaAzuijada;
    }

    public void setDividaAtivaAzuijada(boolean dividaAtivaAzuijada) {
        this.dividaAtivaAzuijada = dividaAtivaAzuijada;
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public List<Divida> completaDivida(String parte) {
        if (tipoCadastroTributario != null) {
            return tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) ? consultaDebitoFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao") :
                consultaDebitoFacade.getDividaFacade().listaDividasPorTipoCadastro(parte.trim(), tipoCadastroTributario);
        }
        return Lists.newArrayList();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ConverterGenerico getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, facade.getDividaFacade());
        }
        return converterDivida;
    }

    @URLAction(mappingId = "novoLancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuario(getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataLancamento(new Date());
        selecionado.setSituacao(SituacaoLancamentoDesconto.ABERTO);
        limparCampos();
    }

    @URLAction(mappingId = "verLancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarParcelasAdicionadas();
        atualizarValoresLancamentoDesconto();
    }

    private void atualizarValoresLancamentoDesconto() {
        if (selecionado.getSituacao().equals(SituacaoLancamentoDesconto.ABERTO) &&
            selecionado.getParcelas() != null && !selecionado.getParcelas().isEmpty()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();

            for (LancamentoDescontoParcela lancamentoDescontoParcela : selecionado.getParcelas()) {
                consultaParcela.limpaConsulta();
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, lancamentoDescontoParcela.getParcela().getId());
                ResultadoParcela resultadoParcela = consultaParcela.executaConsulta().getResultados().get(0);
                lancamentoDescontoParcela.setImposto(resultadoParcela.getValorImposto());
                lancamentoDescontoParcela.setTaxa(resultadoParcela.getValorTaxa());
                lancamentoDescontoParcela.setJuros(resultadoParcela.getValorJuros());
                lancamentoDescontoParcela.setMulta(resultadoParcela.getValorMulta());
                lancamentoDescontoParcela.setCorrecao(resultadoParcela.getValorCorrecao());
                lancamentoDescontoParcela.setHonorarios(resultadoParcela.getValorHonorarios());
                lancamentoDescontoParcela.setDesconto(resultadoParcela.getValorDesconto());
            }

            facade.calcularDescontosDebitos(selecionado);
        }
        if (selecionado.getDataCancelamento() == null) {
            selecionado.setDataCancelamento(getSistemaControlador().getDataOperacao());
        }
    }

    @URLAction(mappingId = "editarLancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atualizarValoresLancamentoDesconto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/lancamento-de-desconto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null && getCadastro() == null && getFiltroContribuinte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else if ((getFiltroContribuinte() == null || getFiltroContribuinte().getId() == null)
            && (tipoCadastroTributario == null)
            && (getCadastro() == null || getCadastro().getId() != null)
            && (filtroDivida == null || filtroDivida.getId() == null)
            && (filtroExercicioInicio == null || filtroExercicioInicio.getId() == null)
            && (filtroExercicioFinal == null || filtroExercicioFinal.getId() == null)
            && !dividaAtiva
            && !dividaAtivaAzuijada
            && !dividaDoExercicio) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro.");
        } else if ((getCadastro() == null && getFiltroContribuinte() == null) && tipoCadastroTributario != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quando informado o tipo de cadastro, é necessário que seja informado também o cadastro.");
        } else if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final não pode ser posterior ao exercício inicial.");
            }
        } else if (filtroExercicioInicio != null && filtroExercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício final deve ser informado.");
        } else if (filtroExercicioInicio == null && filtroExercicioFinal != null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Exercício inicial deve ser informado.");
        } else if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            resultadoParcelas = Lists.newArrayList();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            if (!consulta.getFiltros().isEmpty()) {
                consulta.executaConsulta();
                resultadoParcelas = consulta.getResultados();
                Collections.sort(resultadoParcelas, new OrdenaResultadoParcelaPorVencimento());

                if (resultadoParcelas.isEmpty()) {
                    FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
                } else {
                    somarValores();
                }
            } else {
                FacesUtil.addOperacaoNaoPermitida("Para efetuar a consulta de débitos informe ao menos um filtro");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarParametro(ConsultaParcela consultaParcela) {
        consultaParcela.getFiltros().clear();
        if (getCadastro() != null && getCadastro().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, getCadastro().getId());
        }

        if (getFiltroContribuinte() != null && getFiltroContribuinte().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, getFiltroContribuinte().getId());
        }

        if (filtroExercicioInicio != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
        }

        if (filtroExercicioFinal != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }

        if (vencimentoInicial != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }

        if (vencimentoFinal != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
        }

        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);

        if (filtroDivida != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }
    }

    public void recuperarParcelasAdicionadas() {
        resultadoParcelas = Lists.newArrayList();
        ConsultaParcela consulta = new ConsultaParcela();
        adicionaParametroTst(consulta);
        consulta.executaConsulta();
        resultadoParcelas = consulta.getResultados();
    }

    public void adicionaParametroTst(ConsultaParcela consulta) {
        List<Long> idParcela = Lists.newArrayList();
        for (LancamentoDescontoParcela lancamento : selecionado.getParcelas()) {
            idParcela.add(lancamento.getParcela().getId());

        }
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idParcela);
    }


    public void limparCampos() {
        listaParcela = new ArrayList<>();
        situacaoParcela = SituacaoParcela.EM_ABERTO;
        filtroDivida = null;
        parcelas = new DataTableConsultaDebito[0];
        filtroExercicioInicio = null;
        filtroExercicioFinal = null;
        vencimentoFinal = null;
        vencimentoInicial = null;
        tipoCadastroTributario = null;
        incluiSocios = false;
        situacoes = new SituacaoParcela[0];
        resultadoParcelas = Lists.newArrayList();
        parcelasTable = null;

        inicializaValoreBigDecimalZero();
    }

    private void inicializaValoreBigDecimalZero() {
        valorCorrecaoMonetaria = BigDecimal.ZERO;
        valorJuros = BigDecimal.ZERO;
        valorMulta = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorParcela = BigDecimal.ZERO;
        valorSaldo = BigDecimal.ZERO;
        valorUFM = BigDecimal.ZERO;
        valorImposto = BigDecimal.ZERO;
        valorTaxa = BigDecimal.ZERO;
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        List<SituacaoParcela> retorno = new ArrayList<SituacaoParcela>();
        for (SituacaoParcela situacaoParcela1 : SituacaoParcela.getValues()) {
            retorno.add(situacaoParcela1);
        }
        return retorno;
    }

    public boolean habilitaConsultaSocios() {
        return TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return facade.getAtoLegalFacade().listaFiltrandoNomeNumero(parte.trim());
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, facade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return consultaDebitoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, consultaDebitoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return consultaDebitoFacade.getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, consultaDebitoFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return consultaDebitoFacade.getCadastroRuralFacade().listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, consultaDebitoFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return consultaDebitoFacade.getContratoRendasPatrimoniaisFacade().listaFiltrando(parte.trim(), "numeroContrato");
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        return consultaDebitoFacade.getPessoaFacade().recuperaPessoasDoCadastro(informacoesCadastro);
    }

    public Testada getTestadaPrincipal() {
        if (informacoesCadastro instanceof CadastroImobiliario) {
            if (informacoesCadastro != null) {
                return consultaDebitoFacade.getLoteFacade().recuperarTestadaPrincipal(((CadastroImobiliario) informacoesCadastro).getLote());
            }
        }
        return new Testada();
    }

    public void limparCadastro() {
        cadastro = null;
        filtroContribuinte = null;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastroInicial) {
        this.cadastro = cadastroInicial;
    }

    public void setaCadastro(Cadastro cadastro) {
        informacoesCadastro = cadastro;
    }

    public Pessoa getFiltroContribuinte() {
        return filtroContribuinte;
    }

    public void setFiltroContribuinte(Pessoa filtroContribuinte) {
        this.filtroContribuinte = filtroContribuinte;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(consultaDebitoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Cadastro getInformacoesCadastro() {
        return informacoesCadastro;
    }

    public void setInformacoesCadastro(Cadastro informacoesCadastro) {
        this.informacoesCadastro = informacoesCadastro;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getDataAtual() {
        return new Date();
    }

    public void somarValores() {
        try {
            inicializaValoreBigDecimalZero();
            for (ResultadoParcela parcela : resultadoParcelas) {
                if (!parcela.getCotaUnica()) {
                    valorJuros = valorJuros.add(parcela.getValorJuros().setScale(2, RoundingMode.HALF_EVEN));
                    valorMulta = valorMulta.add(parcela.getValorMulta().setScale(2, RoundingMode.HALF_EVEN));
                    valorImposto = valorImposto.add(parcela.getValorImposto().setScale(2, RoundingMode.HALF_EVEN));
                    valorTaxa = valorTaxa.add(parcela.getValorTaxa().setScale(2, RoundingMode.HALF_EVEN));
                    valorTotal = valorTotal.add(parcela.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorCorrecaoMonetaria() {
        return valorCorrecaoMonetaria;
    }

    public void setValorCorrecaoMonetaria(BigDecimal valorCorrecaoMonetaria) {
        this.valorCorrecaoMonetaria = valorCorrecaoMonetaria;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public void adicionarDebitosSelecionados() {
        if (parcelasTable == null || parcelasTable.length == 0) {
            FacesUtil.addAtencao("Por favor selecione ao menos uma parcela.");
            return;
        }
        if (selecionado.getParcelas() == null) {
            selecionado.setParcelas(new ArrayList());
        }
        boolean adicionado = false;
        for (ResultadoParcela resultadoParcela : parcelasTable) {
            if (!parcelaAdicionada(resultadoParcela.getIdParcela())) {
                selecionado.getParcelas().add(criarLancamentoDescontoParcela(selecionado, resultadoParcela));
                resultadoParcelas.remove(resultadoParcela);
                adicionado = true;
            } else {
                FacesUtil.addOperacaoNaoRealizada("A parcela selecionada: " + resultadoParcela.getReferencia() + " - " +
                    resultadoParcela.getDivida() + " - " + resultadoParcela.getParcela() + " já foi adicionada ao processo.");
            }
        }

        parcelasTable = new ResultadoParcela[]{};
        if (adicionado) {
            FacesUtil.addOperacaoRealizada("Parcelas adicionadas com sucesso.");
        }
    }

    private LancamentoDescontoParcela criarLancamentoDescontoParcela(LancamentoDesconto lancamentoDesconto, ResultadoParcela resultadoParcela) {
        LancamentoDescontoParcela lancamentoDescontoParcela = new LancamentoDescontoParcela();
        lancamentoDescontoParcela.setLancamentoDesconto(lancamentoDesconto);
        lancamentoDescontoParcela.setParcela(consultaDebitoFacade.recuperaParcela(resultadoParcela.getIdParcela()));
        lancamentoDescontoParcela.setImposto(resultadoParcela.getValorImposto());
        lancamentoDescontoParcela.setTaxa(resultadoParcela.getValorTaxa());
        lancamentoDescontoParcela.setJuros(resultadoParcela.getValorJuros());
        lancamentoDescontoParcela.setMulta(resultadoParcela.getValorMulta());
        lancamentoDescontoParcela.setCorrecao(resultadoParcela.getValorCorrecao());
        lancamentoDescontoParcela.setHonorarios(resultadoParcela.getValorHonorarios());
        lancamentoDescontoParcela.setDesconto(resultadoParcela.getValorDesconto());
        return lancamentoDescontoParcela;
    }

    private boolean parcelaAdicionada(Long idParcela) {
        if (selecionado.getParcelas() != null && !selecionado.getParcelas().isEmpty()) {
            for (LancamentoDescontoParcela parcela : selecionado.getParcelas()) {
                if (parcela.getParcela().getId().equals(idParcela)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public String retornarSituacaoDaDivida(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return DIVIDA_ATIVA;
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return DIVIDA_ATIVA_AJUIZADA;
        }
        return DO_EXERCICIO;
    }

    public boolean desabilitarAlteracaoCadastro() {
        return selecionado.getLancouDescontos() || (selecionado.getParcelas() != null && !selecionado.getParcelas().isEmpty());
    }

    public boolean desabilitarConsultaDebitos() {
        return selecionado.getLancouDescontos() || (cadastro == null && filtroContribuinte == null);
    }

    public void removerParcela(LancamentoDescontoParcela parcela) {
        selecionado.getParcelas().remove(parcela);
        FacesUtil.addOperacaoRealizada("Parcela removida com sucesso.");
    }

    public void iniciarConsultaDebitos() {
        if (cadastro != null || filtroContribuinte != null) {
            resultadoParcelas = Lists.newArrayList();
            parcelasTable = null;

            FacesUtil.executaJavaScript("dialogo.show()");
        } else {
            FacesUtil.addCampoObrigatorio("Informe o cadastro!");
        }
    }

    public boolean desabilitarConfirmacaoDividasSelecionadas() {
        return selecionado.getLancouDescontos() || (selecionado.getParcelas() == null || selecionado.getParcelas().isEmpty());
    }

    public boolean desabilitarLimparDividasComDesconto() {
        return Boolean.FALSE.equals(selecionado.getLancouDescontos());
    }

    public void lancarDescontosDebitosSelecionados() {
        try {
            validarConfiguracaoDesconto();
            validarValoresDescontoFixo();
            validarValoresDescontoPercentual();
            facade.calcularDescontosDebitos(selecionado);
            selecionado.setLancouDescontos(Boolean.TRUE);
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void limparDividasComDescontos() {
        selecionado.setLancouDescontos(Boolean.FALSE);
        for (LancamentoDescontoParcela lancamentoDescontoParcela : selecionado.getParcelas()) {
            lancamentoDescontoParcela.setDescontos(new ArrayList());
        }
        FacesUtil.atualizarComponente("Formulario");
    }


    private void validarConfiguracaoDesconto() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data Inicial' deve ser informado.");
        }
        if (selecionado.getFim() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data Final' deve ser informado.");
        }
        if (selecionado.getTipoDesconto() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Tipo de Desconto' deve ser informado.");
        } else if (selecionado.isTipoDescontoFixo() && (selecionado.getValorDesconto() == null || selecionado.getValorDesconto().compareTo(BigDecimal.ZERO) <= 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Desconto Fixo' deve ser informado, com um valor maior que zero(0).");
        } else if (selecionado.isTipoDescontoPercentual() && !temPercetualDescontoInformadoCorretamento()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Desconto Percentual' deve ser informado para alguma coluna com um valor maior que zero(0) e menor igual a cem(100).");
        }
        if (selecionado.getInicio() != null && selecionado.getFim() != null && selecionado.getInicio().after(selecionado.getFim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor que a Data Final.");
        }
        if (selecionado.getParcelas() == null || selecionado.getParcelas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum débito foi selecionado.");
        }
        ve.lancarException();
    }

    private void validarValoresDescontoFixo() {
        if (selecionado.isTipoDescontoFixo()) {
            ValidacaoException ve = new ValidacaoException();

            boolean validaImposto = true;
            boolean validaTaxa = true;
            boolean validaJuros = true;
            boolean validaMulta = true;
            boolean validaCorrecao = true;
            boolean validaHonorarios = true;
            for (LancamentoDescontoParcela parcela : selecionado.getParcelas()) {
                validaImposto = !validaImposto ? validaImposto : parcela.getImposto().compareTo(selecionado.getValorDescontoImposto()) >= 0;
                validaTaxa = !validaTaxa ? validaTaxa : parcela.getTaxa().compareTo(selecionado.getValorDescontoTaxa()) >= 0;
                validaJuros = !validaJuros ? validaJuros : parcela.getJuros().compareTo(selecionado.getValorDescontoJuros()) >= 0;
                validaMulta = !validaMulta ? validaMulta : parcela.getMulta().compareTo(selecionado.getValorDescontoMulta()) >= 0;
                validaCorrecao = !validaCorrecao ? validaCorrecao : parcela.getCorrecao().compareTo(selecionado.getValorDescontoCorrecao()) >= 0;
                validaHonorarios = !validaHonorarios ? validaHonorarios : parcela.getHonorarios().compareTo(selecionado.getValorDescontoHonorarios()) >= 0;
            }
            if (!validaImposto) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de imposto maior que o valor de imposto da parcela selecionada!");
            }
            if (!validaTaxa) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de taxa maior que o valor de taxa da parcela selecionada!");
            }
            if (!validaJuros) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de juros maior que o valor de juros da parcela selecionada!");
            }
            if (!validaMulta) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de multa maior que o valor de multa da parcela selecionada!");
            }
            if (!validaCorrecao) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de correção maior que o valor de correção da parcela selecionada!");
            }
            if (!validaHonorarios) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de honorários maior que o valor de honorários da parcela selecionada!");
            }

            ve.lancarException();
        }
    }

    private void validarValoresDescontoPercentual() {
        if (selecionado.isTipoDescontoPercentual()) {
            ValidacaoException ve = new ValidacaoException();

            boolean validaImposto = true;
            boolean validaTaxa = true;
            boolean validaJuros = true;
            boolean validaMulta = true;
            boolean validaCorrecao = true;
            boolean validaHonorarios = true;
            for (LancamentoDescontoParcela parcela : selecionado.getParcelas()) {
                validaImposto = !validaImposto ? validaImposto : (parcela.getImposto().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoImposto().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoImposto().compareTo(BigDecimal.ZERO) == 0;
                validaTaxa = !validaTaxa ? validaTaxa : (parcela.getTaxa().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoTaxa().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoTaxa().compareTo(BigDecimal.ZERO) == 0;
                validaJuros = !validaJuros ? validaJuros : (parcela.getJuros().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoJuros().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoJuros().compareTo(BigDecimal.ZERO) == 0;
                validaMulta = !validaMulta ? validaMulta : (parcela.getMulta().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoMulta().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoMulta().compareTo(BigDecimal.ZERO) == 0;
                validaCorrecao = !validaCorrecao ? validaCorrecao : (parcela.getCorrecao().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoCorrecao().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoCorrecao().compareTo(BigDecimal.ZERO) == 0;
                validaHonorarios = !validaHonorarios ? validaHonorarios : (parcela.getHonorarios().compareTo(BigDecimal.ZERO) > 0 && selecionado.getPercentualDescontoHonorarios().compareTo(BigDecimal.ZERO) > 0) || selecionado.getPercentualDescontoHonorarios().compareTo(BigDecimal.ZERO) == 0;
            }
            if (!validaImposto) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de imposto pois o valor de imposto da parcela selecionada é zero!");
            }
            if (!validaTaxa) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de taxa pois o valor de taxa da parcela selecionada é zero!");
            }
            if (!validaJuros) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de juros pois o valor de juros da parcela selecionada é zero!");
            }
            if (!validaMulta) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de multa pois o valor de multa da parcela selecionada é zero!");
            }
            if (!validaCorrecao) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de correção pois o valor de correção da parcela selecionada é zero!");
            }
            if (!validaHonorarios) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível aplicar desconto de honorários pois o valor de honorários da parcela selecionada é zero!");
            }

            ve.lancarException();
        }
    }

    private boolean temPercetualDescontoInformadoCorretamento() {
        boolean valida = true;
        if (selecionado.getPercentualDescontoImposto().compareTo(BigDecimal.ZERO) <= 0 &&
            selecionado.getPercentualDescontoTaxa().compareTo(BigDecimal.ZERO) <= 0 &&
            selecionado.getPercentualDescontoJuros().compareTo(BigDecimal.ZERO) <= 0 &&
            selecionado.getPercentualDescontoMulta().compareTo(BigDecimal.ZERO) <= 0 &&
            selecionado.getPercentualDescontoCorrecao().compareTo(BigDecimal.ZERO) <= 0 &&
            selecionado.getPercentualDescontoHonorarios().compareTo(BigDecimal.ZERO) <= 0) {
            valida = false;
        } else if (selecionado.getPercentualDescontoImposto().compareTo(CEM) > 0 &&
            selecionado.getPercentualDescontoTaxa().compareTo(CEM) > 0 &&
            selecionado.getPercentualDescontoJuros().compareTo(CEM) > 0 &&
            selecionado.getPercentualDescontoMulta().compareTo(CEM) > 0 &&
            selecionado.getPercentualDescontoCorrecao().compareTo(CEM) > 0 &&
            selecionado.getPercentualDescontoHonorarios().compareTo(CEM) > 0) {
            valida = false;
        }
        return valida;
    }

    public void efetivarLancamento() {
        try {
            facade.efetivarLancamento(selecionado);
            FacesUtil.addOperacaoRealizada("Processo de desconto efetivado com sucesso.");
            FacesUtil.redirecionamentoInterno("/tributario/lancamento-de-desconto/ver/" + selecionado.getId() + "/");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void estornarLancamento() {
        try {
            validarEstorno();
            List<DescontoItemParcela> descontosItemParcela = facade.desvincularDescontoItemParcela(selecionado);
            facade.alterarSituacaoParaEstornado(selecionado);
            facade.excluirDescontosItemParcela(descontosItemParcela);
            FacesUtil.addOperacaoRealizada("Processo de desconto estornado com sucesso.");
            FacesUtil.redirecionamentoInterno("/tributario/lancamento-de-desconto/ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void limparEstorno() {
        selecionado.setMotivoCancelamento(null);
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getMotivoCancelamento() == null || selecionado.getMotivoCancelamento().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno!");
        } else {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            for (LancamentoDescontoParcela lancamentoDescontoParcela : selecionado.getParcelas()) {
                consultaParcela.limpaConsulta();
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, lancamentoDescontoParcela.getParcela().getId());
                ResultadoParcela resultadoParcela = consultaParcela.executaConsulta().getResultados().get(0);
                if (resultadoParcela.isPago()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível estornar o lançamento que possui parcelas pagas!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public boolean isPossivelEfetivar() {
        return selecionado.getSituacao().equals(SituacaoLancamentoDesconto.ABERTO) &&
            Boolean.TRUE.equals(selecionado.getLancouDescontos());
    }

    public boolean isPossivelEstornar() {
        return selecionado.getSituacao().equals(SituacaoLancamentoDesconto.EFETIVADO) &&
            Boolean.TRUE.equals(selecionado.getLancouDescontos());
    }

    public boolean isPossivelEditar() {
        return selecionado.getSituacao().equals(SituacaoLancamentoDesconto.ABERTO);
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Divida d : facade.getDividaFacade().listaDividasPorTipoCadastro(tipoCadastroTributario)) {
            retorno.add(new SelectItem(d, d.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposDesconto() {
        return Util.getListSelectItem(LancamentoDesconto.TipoDesconto.values());
    }

    public void processarAlteracaoTipoDesconto() {
        selecionado.setValorDescontoImposto(BigDecimal.ZERO);
        selecionado.setPercentualDescontoImposto(BigDecimal.ZERO);

        selecionado.setValorDescontoTaxa(BigDecimal.ZERO);
        selecionado.setPercentualDescontoTaxa(BigDecimal.ZERO);

        selecionado.setValorDescontoJuros(BigDecimal.ZERO);
        selecionado.setPercentualDescontoJuros(BigDecimal.ZERO);

        selecionado.setValorDescontoMulta(BigDecimal.ZERO);
        selecionado.setPercentualDescontoMulta(BigDecimal.ZERO);

        selecionado.setValorDescontoCorrecao(BigDecimal.ZERO);
        selecionado.setPercentualDescontoCorrecao(BigDecimal.ZERO);

        selecionado.setValorDescontoHonorarios(BigDecimal.ZERO);
        selecionado.setPercentualDescontoHonorarios(BigDecimal.ZERO);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivo() == null || "".equals(selecionado.getMotivo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Motivo!");
        }
        if (selecionado.getParcelas().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor adicionar as parcelas do processo!");
        }
        if (selecionado.getParcelasComDescontos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor confirmar as parcelas selecionadas!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (Operacoes.NOVO.equals(operacao)) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(LancamentoDesconto.class, "codigo"));
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public String montarNumeroParcela(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.montarNumeroParcela(parcela);
    }
}
