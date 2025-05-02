package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "lancamentoIsencaoAcrescimoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLancamentoIsencaoAcrescimo", pattern = "/tributario/lancamento-isencao-acrescimo/novo/",
        viewId = "/faces/tributario/contacorrente/isencaoacrescimos/edita.xhtml"),
    @URLMapping(id = "editarLancamentoIsencaoAcrescimo", pattern = "/tributario/lancamento-isencao-acrescimo/editar/#{lancamentoIsencaoAcrescimoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/isencaoacrescimos/edita.xhtml"),
    @URLMapping(id = "listarLancamentoIsencaoAcrescimo", pattern = "/tributario/lancamento-isencao-acrescimo/listar/",
        viewId = "/faces/tributario/contacorrente/isencaoacrescimos/lista.xhtml"),
    @URLMapping(id = "verLancamentoIsencaoAcrescimo", pattern = "/tributario/lancamento-isencao-acrescimo/ver/#{lancamentoIsencaoAcrescimoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/isencaoacrescimos/visualizar.xhtml")
})
public class LancamentoIsencaoAcrescimoControlador extends PrettyControlador<ProcessoIsencaoAcrescimos> implements Serializable, CRUD {

    @EJB
    private LancamentoIsencaoAcrescimoFacade facade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private ConverterAutoComplete converterAtoLegal;
    private TipoCadastroTributario tipoCadastroTributario;
    private Cadastro informacoesCadastro;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
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
    private Divida filtroDivida;
    private ConsultaParcela consultaParcela;
    private List<ResultadoParcela> parcelas;
    private ResultadoParcela[] parcelasTable;
    @EJB
    private VWConsultaDeDebitosFacade view;

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


    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LancamentoIsencaoAcrescimoControlador() {
        super(ProcessoIsencaoAcrescimos.class);
    }

    public String getNomeClasse() {
        if (tipoCadastroTributario != null) {
            switch (tipoCadastroTributario) {
                case IMOBILIARIO:
                    return CadastroImobiliario.class.getSimpleName();
                case ECONOMICO:
                    return CadastroEconomico.class.getSimpleName();
                case PESSOA:
                    return Pessoa.class.getSimpleName();
            }
        }
        return CadastroRural.class.getSimpleName();
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        if (tipoCadastroTributario != null) {
            switch (tipoCadastroTributario) {
                case IMOBILIARIO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroImobiliarioControlador");
                case ECONOMICO:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
                case PESSOA:
                    return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pessoaPesquisaGenerico");
            }
        }
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("componentePesquisaGenerico");
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {
            selecionado.setCadastro((Cadastro) obj);
        } else if (obj instanceof Pessoa) {
            selecionado.setPessoa((Pessoa) obj);
        }
    }

    @URLAction(mappingId = "novoLancamentoIsencaoAcrescimo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuario(getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataLancamento(new Date());
        selecionado.setDataInicio(new Date());
        tipoCadastroTributario = null;
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        limparCampos();
    }

    @URLAction(mappingId = "verLancamentoIsencaoAcrescimo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        facade.atualizarProcessoDeducaoAcrescimos(selecionado);
    }

    @URLAction(mappingId = "editarLancamentoIsencaoAcrescimo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        facade.atualizarProcessoDeducaoAcrescimos(selecionado);
        selecionado.setLancouDeducoes(Boolean.TRUE);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/lancamento-isencao-acrescimo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void validarTipoCadastro() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Tipo de Cadastro' deve ser informado.");
        } else if (selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O 'Cadastro' ou 'Contribuinte Geral' deve ser informado.");
        }
        ve.lancarException();
    }

    public void iniciarConsultaDeDebitos() {
        try {
            validarTipoCadastro();
            limparCampos();
            FacesUtil.executaJavaScript("dialogo.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public String buscarInscricaoDoCadastro(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.buscarNumeroTipoCadastroPorParcela(parcela);
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void limparCampos() {
        situacaoParcela = SituacaoParcela.EM_ABERTO;
        filtroDivida = null;
        filtroExercicioInicio = null;
        filtroExercicioFinal = null;
        vencimentoFinal = null;
        vencimentoInicial = null;
        parcelas = Lists.newArrayList();
        parcelasTable = new ResultadoParcela[]{};
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
        List<SituacaoParcela> retorno = new ArrayList<>();
        for (SituacaoParcela situacaoParcela1 : SituacaoParcela.getValues()) {
            retorno.add(situacaoParcela1);
        }
        return retorno;
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
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return consultaDebitoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }


    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return consultaDebitoFacade.getCadastroEconomicoFacade().buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }


    public List<CadastroRural> completaCadastroRural(String parte) {
        return consultaDebitoFacade.getCadastroRuralFacade().listaFiltrandoPorCodigo(parte.trim());
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
        selecionado.setCadastro(null);
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

    public void atualizarExercicio() {
        filtroExercicioFinal = filtroExercicioInicio;
        FacesUtil.atualizarComponente("Formulario:exercicioFinal");
    }

    public void atualizarData() {
        vencimentoFinal = vencimentoInicial;
        FacesUtil.atualizarComponente("Formulario:dataFinal");
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

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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

    public BigDecimal getValorCorrecaoMonetaria() {
        return valorCorrecaoMonetaria;
    }

    public void setValorCorrecaoMonetaria(BigDecimal valorCorrecaoMonetaria) {
        this.valorCorrecaoMonetaria = valorCorrecaoMonetaria;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public ResultadoParcela[] getParcelasTable() {
        return parcelasTable;
    }

    public void setParcelasTable(ResultadoParcela[] parcelasTable) {
        this.parcelasTable = parcelasTable;
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public List<Divida> completaDivida(String parte) {
        if (tipoCadastroTributario != null) {
            return tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) ? consultaDebitoFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao") :
                consultaDebitoFacade.getDividaFacade().listaDividasPorTipoCadastro(parte.trim(), tipoCadastroTributario);
        }
        return Lists.newArrayList();
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null && selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else if ((selecionado.getPessoa() == null || selecionado.getPessoa().getId() == null)
            && (tipoCadastroTributario == null)
            && (selecionado.getCadastro() == null || selecionado.getCadastro().getId() != null)
            && (filtroDivida == null || filtroDivida.getId() == null)
            && (filtroExercicioInicio == null || filtroExercicioInicio.getId() == null)
            && (filtroExercicioFinal == null || filtroExercicioFinal.getId() == null)
            && !dividaAtiva
            && !dividaAtivaAzuijada
            && !dividaDoExercicio) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um filtro.");
        } else if ((selecionado.getCadastro() == null && selecionado.getPessoa() == null) && tipoCadastroTributario != null) {
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
            parcelas.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            if(!consulta.getFiltros().isEmpty()){
                consulta.executaConsulta();
                parcelas.addAll(consulta.getResultados());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        Collections.sort(parcelas, new OrdenaResultadoParcelaPorVencimento());
        try {
            if (parcelas.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }

    public void inicializarFiltros() {
        parcelas = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    private void adicionarParametro(ConsultaParcela consultaParcela) {
        consultaParcela.getFiltros().clear();
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastro().getId());
        }

        if (selecionado.getPessoa() != null && selecionado.getPessoa().getId() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());
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

    private void adicionarParametroVer() {
        consultaParcela = new ConsultaParcela();
        consultaParcela.getFiltros().clear();

        List<Long> idParcelas = Lists.newArrayList();
        for (IsencaoAcrescimoParcela p : selecionado.getIsencoesParcela()) {
            idParcelas.add(p.getParcela().getId());
        }
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idParcelas);
    }

    public boolean isPossivelEditar() {
        return ProcessoIsencaoAcrescimos.Situacao.ATIVO.equals(selecionado.getSituacao());
    }

    public boolean isPossivelEstornar() {
        return Boolean.TRUE.equals(selecionado.getLancouDeducoes()) && ProcessoIsencaoAcrescimos.Situacao.EFETIVADO.equals(selecionado.getSituacao());
    }

    public void iniciarEstorno() {
        selecionado.setUsuarioCancelamento(getSistemaControlador().getUsuarioCorrente());
        selecionado.setDataCancelamento(new Date());
    }

    public void estornarProcessoDeducaoAcrescimos() {
        if (selecionado.getMotivoCancelamento() == null || selecionado.getMotivoCancelamento().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo 'Motivo do Estorno' deve ser informado.");
        } else {
            facade.estornarProcessoDeducaoAcrescimos(selecionado);
            FacesUtil.addOperacaoRealizada("Processo de Dedução de Acrescimos ESTORNADO com sucesso");
            FacesUtil.redirecionamentoInterno("/tributario/lancamento-isencao-acrescimo/ver/" + selecionado.getId() + "/");
        }
    }

    public boolean isPossivelEfetivar() {
        return Boolean.TRUE.equals(selecionado.getLancouDeducoes()) && ProcessoIsencaoAcrescimos.Situacao.ATIVO.equals(selecionado.getSituacao());
    }

    public void efetivarProcessoDeducaoAcrescimos() {
        facade.efetivarProcessoDeducaoAcrescimos(selecionado);
        FacesUtil.addOperacaoRealizada("Processo de Dedução de Acrescimos EFETIVADO com sucesso");
        FacesUtil.redirecionamentoInterno("/tributario/lancamento-isencao-acrescimo/ver/" + selecionado.getId() + "/");
    }

    public List<SelectItem> getTiposDeducao() {
        return Util.getListSelectItem(ProcessoIsencaoAcrescimos.TipoDeducao.values());
    }

    public void processarAlteracaoTipoDeducao() {
        selecionado.setPercentualDeducao(BigDecimal.ZERO);
    }

    public void removerParcela(IsencaoAcrescimoParcela parcela) {
        selecionado.getIsencoesParcela().remove(parcela);
        FacesUtil.addOperacaoRealizada("Parcela removida com sucesso.");
    }

    public boolean desabilitarAlteracaoCadastro() {
        return selecionado.getLancouDeducoes() || (selecionado.getIsencoesParcela() != null && !selecionado.getIsencoesParcela().isEmpty());
    }

    public void calcularDeducaoDebitosSelecionados() {
        try {
            validarConfiguracaoDeducaoAcrescimo();
            validarParcelasSelecionadas();
            facade.calcularDeducaoAcrescimos(selecionado);
            selecionado.setLancouDeducoes(Boolean.TRUE);
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }

    }

    private void validarParcelasSelecionadas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getIsencoesParcela() == null || selecionado.getIsencoesParcela().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma dívida foi selecionada.");
        }
        ve.lancarException();
    }

    private void validarConfiguracaoDeducaoAcrescimo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data Inicial' deve ser informado.");
        }
        if (selecionado.getDataFim() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Data Final' deve ser informado.");
        }
        if (selecionado.getTipoDeducao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Tipo de Deduçao' deve ser informado.");
        } else {
            if (selecionado.isTipoDeducaoPercentual() &&
                (selecionado.getPercentualDeducao() == null ||
                    selecionado.getPercentualDeducao().compareTo(BigDecimal.ZERO) <= 0 ||
                    selecionado.getPercentualDeducao().compareTo(new BigDecimal("100")) > 0)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo 'Percentual de Dedução (%)' deve ser informado, com um valor maior que zero (0) e menor ou igual a cem(100).");
            }
            if (!selecionado.getIsentaMulta() && !selecionado.getIsentaJuros() && !selecionado.getIsentaCorrecao()) {
                ve.adicionarMensagemDeCampoObrigatorio("Ao menos uma isenção deve ser informada, ou Juros ou Multa ou Correção.");
            }
            if (selecionado.getIsentaCorrecao()) {
                if (selecionado.getExercicioCorrecao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo 'Exercício da correção' deve ser informado!");
                } else if (selecionado.getExercicioCorrecao().getAno() > getSistemaControlador().getExercicioCorrenteAsInteger()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'Exercício da correção' não pode ser maior que o exercício atual!!");
                }
            }
        }
        if (selecionado.getDataInicio() != null && selecionado.getDataFim() != null && selecionado.getDataInicio().after(selecionado.getDataFim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A 'Data Inicial' deve ser menor que a 'Data Final'.");
        }
        ve.lancarException();
    }

    public void limparDividasComDeducao() {
        for (IsencaoAcrescimoParcela isencaoAcrescimoParcela : selecionado.getIsencoesParcela()) {
            isencaoAcrescimoParcela.setValorJurosDeduzido(BigDecimal.ZERO);
            isencaoAcrescimoParcela.setValorMultaDeduzido(BigDecimal.ZERO);
            isencaoAcrescimoParcela.setValorCorrecaoDeduzido(BigDecimal.ZERO);
        }
        selecionado.setLancouDeducoes(Boolean.FALSE);
        FacesUtil.atualizarComponente("Formulario");
    }

    public boolean desabilitarConfirmarDividasSelecionadas() {
        return selecionado.getLancouDeducoes() || (selecionado.getIsencoesParcela() == null || selecionado.getIsencoesParcela().isEmpty());
    }

    public boolean desabilitarLimparDividasComDeducao() {
        return !selecionado.getLancouDeducoes() || (selecionado.getIsencoesParcela() == null || selecionado.getIsencoesParcela().isEmpty());
    }

    public void adicionarDebitosSelecionados() {
        if (parcelasTable == null || parcelasTable.length == 0) {
            FacesUtil.addAtencao("Por favor seleciona ao menos uma parcela.");
            return;
        }
        if (selecionado.getIsencoesParcela() == null) {
            selecionado.setIsencoesParcela(new ArrayList());
        }
        boolean adicionado = false;
        for (ResultadoParcela resultadoParcela : parcelasTable) {
            if (!parcelaAdicionada(resultadoParcela.getIdParcela())) {
                selecionado.getIsencoesParcela().add(facade.criarOrAtualizarIsencaoAcrescimoParcela(selecionado, resultadoParcela, null));
                parcelas.remove(resultadoParcela);
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

    private boolean parcelaAdicionada(Long idParcela) {
        if (selecionado.getIsencoesParcela() != null && !selecionado.getIsencoesParcela().isEmpty()) {
            for (IsencaoAcrescimoParcela parcela : selecionado.getIsencoesParcela()) {
                if (parcela.getParcela().getId().equals(idParcela)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void mostrarExercicio() {
        FacesUtil.atualizarComponente("exercicioCorrecao");
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Processo de Dedução de Acréscimos");
            dto.adicionarParametro("USUARIO", consultaDebitoFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA DE FINANÇAS");
            dto.adicionarParametro("TITULO", "Processo de Dedução de Acréscimos");
            dto.adicionarParametro("ID", selecionado.getId());
            dto.setApi("tributario/processo-isencao-acrescimos/");
            ReportService.getInstance().gerarRelatorio(consultaDebitoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

}
