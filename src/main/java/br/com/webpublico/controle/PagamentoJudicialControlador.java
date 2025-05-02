/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaPagamentoJudicialParcelaPorVencimento;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.MostrarParcelas;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.PagamentoJudicialFacade;
import br.com.webpublico.negocios.VWConsultaDeDebitosFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "pagamentoJudicialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaProcessoPagamentoJudicial", pattern = "/tributario/conta-corrente/processo-de-debitos/compensacao/listar/", viewId = "/faces/tributario/contacorrente/pagamentojudicial/lista.xhtml"),
    @URLMapping(id = "novoProcessoPagamentoJudicial", pattern = "/tributario/conta-corrente/processo-de-debitos/compensacao/novo/", viewId = "/faces/tributario/contacorrente/pagamentojudicial/edita.xhtml"),
    @URLMapping(id = "verProcessoPagamentoJudicial", pattern = "/tributario/conta-corrente/processo-de-debitos/compensacao/ver/#{pagamentoJudicialControlador.id}/", viewId = "/faces/tributario/contacorrente/pagamentojudicial/visualizar.xhtml"),
    @URLMapping(id = "editarProcessoPagamentoJudicial", pattern = "/tributario/conta-corrente/processo-de-debitos/compensacao/editar/#{pagamentoJudicialControlador.id}/", viewId = "/faces/tributario/contacorrente/pagamentojudicial/edita.xhtml")
})

public class PagamentoJudicialControlador extends PrettyControlador<PagamentoJudicial> implements Serializable, CRUD, MostrarParcelas {

    @EJB
    private PagamentoJudicialFacade pagamentoJudicialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private VWConsultaDeDebitosFacade view;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRural;
    private List<ResultadoParcela> resultadoConsulta;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private ConverterGenerico converterDivida;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private List<ResultadoParcela> parcelas;


    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }


    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public List<Divida> completaDivida(String parte) {
        return pagamentoJudicialFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
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

    public boolean isDividaDoExercicio() {
        return dividaDoExercicio;
    }

    public void setDividaDoExercicio(boolean dividaDoExercicio) {
        this.dividaDoExercicio = dividaDoExercicio;
    }


    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }


    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
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

    public PagamentoJudicialControlador() {
        super(PagamentoJudicial.class);
//        parcelas = Lists.newArrayList();
        resultadoConsulta = Lists.newArrayList();
    }

    public ConverterGenerico getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, pagamentoJudicialFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pagamentoJudicialFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, pagamentoJudicialFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }


    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, pagamentoJudicialFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, pagamentoJudicialFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public List<SituacaoCadastralPessoa> getSituacoesDisponiveis() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pagamentoJudicialFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }


    @Override
    public AbstractFacade getFacede() {
        return pagamentoJudicialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente/processo-de-debitos/compensacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoProcessoPagamentoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuario(pagamentoJudicialFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setCodigo(pagamentoJudicialFacade.recuperaProximoCodigoPorExercicio(selecionado.getExercicio()));
        resultadoConsulta = Lists.newArrayList();
    }

    @URLAction(mappingId = "verProcessoPagamentoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarParcelasGeradas(selecionado);
    }

    private void recuperarParcelasGeradas(PagamentoJudicial selecionado) {
        List<CalculoPagamentoJudicial> calculo = pagamentoJudicialFacade.recuperarCalculo(selecionado);
        List<Long> ids = Lists.newArrayList();
        if (calculo != null) {
            for (CalculoPagamentoJudicial calculoPagamentoJudicial : calculo) {
                ids.add(calculoPagamentoJudicial.getId());
            }
            parcelas = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, ids).executaConsulta().getResultados();
        }
    }

    @URLAction(mappingId = "editarProcessoPagamentoJudicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return pagamentoJudicialFacade.getCadastroEconomicoFacade().listaCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJPorSituacao(parte, SituacaoCadastralCadastroEconomico.ATIVA_REGULAR, SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return pagamentoJudicialFacade.getCadastroRuralFacade().completaCodigoNomeLocalizacaoIncra(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return pagamentoJudicialFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        if (obj instanceof Cadastro) {
            selecionado.setCadastro((Cadastro) obj);
        } else if (obj instanceof Pessoa) {
            selecionado.setPessoa((Pessoa) obj);
        }
    }

    public String getNomeClasse() {
        if (selecionado.getTipoCadastroTributario() != null) {
            switch (selecionado.getTipoCadastroTributario()) {
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
        if (selecionado.getTipoCadastroTributario() != null) {
            switch (selecionado.getTipoCadastroTributario()) {
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

    public void limpaCadastro() {
        selecionado.setPessoa(null);
        selecionado.setCadastro(null);
    }

    public void dialogBuscaDeDebitos() {
        if (podeBuscarDebitos()) {
            inicializaFiltros();
            resultadoConsulta.clear();
            FacesUtil.executaJavaScript("dialogParcelas.show()");
        }
    }

    public void consultarParcelas() {
        resultadoConsulta = Lists.newArrayList();
        ConsultaParcela consulta = new ConsultaParcela(selecionado.getDataBloqueio());
        adicionaParametro(consulta);
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
        if (!resultadoConsulta.isEmpty()) {
            Collections.sort(resultadoConsulta, new OrdenaResultadoParcelaPorVencimento());
        }
    }

    private void adicionaParametro(ConsultaParcela consulta) {
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastro().getId());
        }
        if (selecionado.getPessoa() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());
        }
        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (dividaDoExercicio && !dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!dividaDoExercicio && dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        } else if (!dividaDoExercicio && dividaAtiva && !dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
        } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }
        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
    }


    private boolean podeBuscarDebitos() {
        boolean retorno = true;
        if (selecionado.getValorACompensar().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("O valor a compensar deve ser maior que zero.");
        }
        if (selecionado.getTipoCadastroTributario() == null) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o Tipo de Cadastro.");
        }
        if (selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            retorno = false;
            FacesUtil.addOperacaoNaoPermitida("Informe o Cadastro.");

        }
        return retorno;
    }

    public boolean habilitaBotaoEstornar() {
        selecionado.setDataEstorno(getSistemaControlador().getDataOperacao());
        return selecionado.getSituacao() != null && selecionado.getSituacao().equals(SituacaoProcessoDebito.FINALIZADO);
    }


    public void removerParcela(PagamentoJudicialParcela p) {
        selecionado.setSaldo(selecionado.getSaldo().add(getValorCompensado(p)));
        selecionado.setValorCompensado(selecionado.getValorCompensado().subtract(p.getValorCompensado()));
        selecionado.getParcelas().remove(p);

    }

    public void setaSaldoInicial() {
        selecionado.setSaldo(selecionado.getValorACompensar());
    }

    public void adicionarParcelas(ResultadoParcela resultadoParcela) {
        if (!parcelaJaAdicionada(resultadoParcela)) {
            PagamentoJudicialParcela parcela = new PagamentoJudicialParcela();
            ParcelaValorDivida pvd = pagamentoJudicialFacade.recuperaParcelaValorDivida(resultadoParcela.getId());
            calcularSaldo(resultadoParcela, parcela);
            parcela.setPagamentoJudicial(selecionado);
            parcela.setExercicio(exercicioFacade.recuperarExercicioPeloAno(resultadoParcela.getExercicio()));
            parcela.setVencimento(resultadoParcela.getVencimento());
            parcela.setParcelaValorDivida(pvd);
            parcela.setCorrecao(resultadoParcela.getValorCorrecao());
            parcela.setDesconto(resultadoParcela.getValorDesconto());
            parcela.setJuros(resultadoParcela.getValorJuros());
            parcela.setMulta(resultadoParcela.getValorMulta());
            parcela.setImposto(resultadoParcela.getValorImposto());
            parcela.setReferencia(resultadoParcela.getReferencia());
            parcela.setSd(resultadoParcela.getSd());
            parcela.setTaxa(resultadoParcela.getValorTaxa());
            parcela.setValorOriginal(resultadoParcela.getValorOriginal());
            parcela.setTotal(resultadoParcela.getValorTotal());
            parcela.setValorHonorarios(resultadoParcela.getValorHonorarios());
            parcela.setTipoCadastro(resultadoParcela.getTipoCadastro());
            parcela.setTipoDeDebito(resultadoParcela.getTipoDeDebito());
            parcela.setParcela(resultadoParcela.getParcela());
            parcela.setSituacao(SituacaoParcela.fromDto(resultadoParcela.getSituacaoEnumValue()));
            parcela.setTipoCalculo(Calculo.TipoCalculo.fromDto(resultadoParcela.getTipoCalculoEnumValue()));
            parcela.setSequencia(pvd.getSequenciaParcela());
            parcela.setQuantidadeParcela(pagamentoJudicialFacade.recuperaQuantidadeParcela(pvd).longValue());
            parcela.setDividaAtiva(resultadoParcela.getDividaAtiva());
            parcela.setDividaAtivaAjuizada(resultadoParcela.getDividaAtivaAjuizada());
            resultadoConsulta.remove(resultadoParcela);
            if (selecionado.getValorCompensado().compareTo(BigDecimal.ZERO) == 0) {
                selecionado.setValorCompensado(parcela.getValorCompensado());
            } else {
                selecionado.setValorCompensado(selecionado.getValorCompensado().add(parcela.getValorCompensado()));
            }
            selecionado.getParcelas().add(parcela);
            Collections.sort(selecionado.getParcelas(), new OrdenaPagamentoJudicialParcelaPorVencimento());
        }
    }

    private boolean parcelaJaAdicionada(ResultadoParcela resultadoParcela) {
        for (PagamentoJudicialParcela pagamentoJudicialParcela : selecionado.getParcelas()) {
            if (resultadoParcela.getIdParcela().equals(pagamentoJudicialParcela.getParcelaValorDivida().getId())) {
                FacesUtil.addOperacaoNaoPermitida("Parcela já adicionada no Processo de Compensação de Débitos.");
                return true;
            }
        }
        return false;
    }

    private void calcularSaldo(ResultadoParcela resultadoParcela, PagamentoJudicialParcela p) {
        BigDecimal saldoAdicionandoParcela = selecionado.getSaldo().subtract(resultadoParcela.getValorTotal());
        if (saldoAdicionandoParcela.compareTo(BigDecimal.ZERO) >= 0) {
            selecionado.setSaldo(selecionado.getSaldo().subtract(resultadoParcela.getValorTotal()));
            p.setValorCompensado(resultadoParcela.getValorTotal());
            p.setValorResidual(BigDecimal.ZERO);
        } else {
            ajustarValorCompensado(resultadoParcela, selecionado.getSaldo(), p);
            if (resultadoParcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                p.setValorResidual((resultadoParcela.getValorTotal().subtract(resultadoParcela.getValorHonorarios())).subtract(p.getValorCompensado()));
            } else {
                p.setValorResidual(resultadoParcela.getValorTotal().subtract(p.getValorCompensado()));
            }
            selecionado.setSaldo(BigDecimal.ZERO);
            FacesUtil.executaJavaScript("mensagemSemSaldo.show()");
        }
    }

    private BigDecimal percentualAbate(BigDecimal valorParcela, BigDecimal saldo) {
        BigDecimal saldoXcem = (saldo.multiply(new BigDecimal(100)));
        return saldoXcem.divide(valorParcela, 8, RoundingMode.HALF_UP);
    }

    private void ajustarValorCompensado(ResultadoParcela resultadoParcela, BigDecimal saldo, PagamentoJudicialParcela parcela) {
        BigDecimal percentualAbate = percentualAbate(resultadoParcela.getValorTotal(), saldo);

        BigDecimal totalMenosHonorarios = resultadoParcela.getValorTotal()
            .subtract(resultadoParcela.getValorHonorarios());

        BigDecimal valorMultiplicadoPorPecentual = totalMenosHonorarios
            .multiply(percentualAbate);
        valorMultiplicadoPorPecentual.setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorFinal = valorMultiplicadoPorPecentual
            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        parcela.setValorCompensado(valorFinal);
    }

    public boolean possuiSaldo() {
        return selecionado.getSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    public void inicializaFiltros() {
        resultadoConsulta = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            selecionado.setDataCompensacao(new Date());
            selecionado = pagamentoJudicialFacade.salvarProcesso(selecionado);
            FacesUtil.addOperacaoRealizada("Salvo com sucesso");
            Web.navegacao(getUrlAtual(), new StringBuilder("/tributario/conta-corrente/processo-de-debitos/compensacao/ver/").append(selecionado.getId()).append("/").toString());
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getMotivo().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("Informe o Motivo ou Fundamentação Legal");
            retorno = false;
        }
        if (selecionado.getParcelas().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("O Processo de Compensação não possui parcelas para compensar");
            retorno = false;
        }
        return retorno;
    }

    public void encerraProcesso() {
        try {
            pagamentoJudicialFacade.encerraProcesso(selecionado);
            recuperarParcelasGeradas(selecionado);
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.addOperacaoRealizada("Processado com Sucesso");
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao processar: " + e.getMessage());
        }
    }

    public void estornaProcesso() {
        if (podeEstornar()) {
            try {
                pagamentoJudicialFacade.estornar(selecionado, parcelas);
                recuperarParcelasGeradas(selecionado);
                FacesUtil.addOperacaoRealizada("Processo de Compensação estornado");
            } catch (Exception e) {
                logger.error(e.getMessage());
                FacesUtil.addOperacaoNaoPermitida("Ocorreu um erro ao salvar: " + e.getMessage());
            }
        }
    }

    public boolean podeEstornar() {
        boolean retorno = true;
        if (selecionado.getMotivoEstorno().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("Informe o motivo do estorno.");
            retorno = false;
        }
        for (ResultadoParcela parcela : parcelas) {
            if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                FacesUtil.addOperacaoNaoPermitida("Não é possível estornar parcela com situação diferente de EM ABERTO.");
                retorno = false;
            }
        }
        return retorno;
    }

    public List<SelectItem> getDivida() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Divida d : pagamentoJudicialFacade.getDividaFacade().listaDividasPorTipoCadastro(selecionado.getTipoCadastroTributario())) {
            retorno.add(new SelectItem(d, d.getDescricao()));
        }
        return retorno;
    }

    @Override
    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public BigDecimal getValorResidual(PagamentoJudicialParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal honorariosDoValorResidual = parcela.getValorResidual().multiply(BigDecimal.valueOf(0.05));
            return parcela.getValorResidual().add(honorariosDoValorResidual);
        } else {
            return parcela.getValorResidual();
        }
    }

    public BigDecimal getValorCompensado(PagamentoJudicialParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            return parcela.getValorCompensado().add(getValorHonorariosDoTotalSubvencionado(parcela));
        } else {
            return parcela.getValorCompensado();
        }
    }

    public BigDecimal getValorCompensadoTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (selecionado.getSaldo().compareTo(BigDecimal.ZERO) == 0 && !selecionado.getParcelas().isEmpty()) {
            return selecionado.getValorACompensar();
        }
        for (PagamentoJudicialParcela pagamentoJudicialParcela : selecionado.getParcelas()) {
            if (pagamentoJudicialParcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && pagamentoJudicialParcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
                valor = valor.add(pagamentoJudicialParcela.getValorCompensado().add(getValorHonorariosDoTotalSubvencionado(pagamentoJudicialParcela)));
            } else {
                valor = valor.add(pagamentoJudicialParcela.getValorCompensado());
            }
        }
        return valor;
    }

    public BigDecimal getValorHonorariosDoTotalSubvencionado(PagamentoJudicialParcela parcela) {
        if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0 && parcela.getValorResidual().compareTo(BigDecimal.ZERO) > 0) {
            return parcela.getValorCompensado().multiply(BigDecimal.valueOf(0.05));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Date definirDataDePesquisaDosAcrescimosDaParcela() {
        if (selecionado.getDataBloqueio() != null) {
            return selecionado.getDataBloqueio();
        }
        return getSistemaControlador().getDataOperacao();
    }
}
