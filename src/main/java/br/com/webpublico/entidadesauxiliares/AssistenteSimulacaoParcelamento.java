package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.ProcessoParcelamentoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.ProcessoParcelamentoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorHonorarios;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AssistenteSimulacaoParcelamento extends AssistenteBarraProgresso {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final Logger logger = LoggerFactory.getLogger(AssistenteSimulacaoParcelamento.class);

    private ProcessoParcelamentoFacade facade;
    private UsuarioSistema usuarioSistema;
    private Date dataOperacao;
    private ProcessoParcelamento processoParcelamento;
    private ConfiguracaoAcrescimos configuracaoAcrescimos;
    private boolean permiteLancarEntradaMenorQueParamentro;
    private boolean permissaoMudarDataVencimentoPrimeiraParcela;
    private BigDecimal valorMinimoParcela;
    private BigDecimal valorEntradaInformado;
    private BigDecimal valorEntradaInicial;
    private BigDecimal valorMinimoEntrada;
    private List<ResultadoParcela> parcelas;
    private ProcessoParcelamentoControlador.Valores valores;
    private List<ResultadoParcela> parcelasGeradas;

    public AssistenteSimulacaoParcelamento(ProcessoParcelamentoFacade facade,
                                           UsuarioSistema usuarioSistema,
                                           ProcessoParcelamento processoParcelamento,
                                           ConfiguracaoAcrescimos configuracaoAcrescimos,
                                           Date dataOperacao) {
        super();
        this.facade = facade;
        this.usuarioSistema = usuarioSistema;
        this.processoParcelamento = processoParcelamento;
        this.configuracaoAcrescimos = configuracaoAcrescimos;
        this.permiteLancarEntradaMenorQueParamentro = facade.getUsuarioSistemaFacade()
            .validaAutorizacaoUsuario(usuarioSistema, AutorizacaoTributario.PERMITIR_LANCAR_VALOR_ENTRADA_MENOR_QUE_PARAMETRO);
        this.permissaoMudarDataVencimentoPrimeiraParcela = permiteLancarEntradaMenorQueParamentro;
        this.valorMinimoParcela = BigDecimal.ZERO;
        this.valorEntradaInformado = BigDecimal.ZERO;
        this.valorEntradaInicial = BigDecimal.ZERO;
        this.valorMinimoEntrada = BigDecimal.ZERO;
        this.parcelas = Lists.newArrayList();
        this.valores = new ProcessoParcelamentoControlador.Valores();
        this.parcelasGeradas = Lists.newArrayList();
        this.facade = facade;
        this.dataOperacao = dataOperacao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public ConfiguracaoAcrescimos getConfiguracaoAcrescimos() {
        return configuracaoAcrescimos;
    }

    public void setConfiguracaoAcrescimos(ConfiguracaoAcrescimos configuracaoAcrescimos) {
        this.configuracaoAcrescimos = configuracaoAcrescimos;
    }

    public boolean isPermiteLancarEntradaMenorQueParamentro() {
        return permiteLancarEntradaMenorQueParamentro;
    }

    public boolean isPermissaoMudarDataVencimentoPrimeiraParcela() {
        return permissaoMudarDataVencimentoPrimeiraParcela;
    }

    public BigDecimal getValorMinimoParcela() {
        return valorMinimoParcela;
    }

    public void setValorMinimoParcela(BigDecimal valorMinimoParcela) {
        this.valorMinimoParcela = valorMinimoParcela;
    }

    public BigDecimal getValorEntradaInformado() {
        return valorEntradaInformado;
    }

    public void setValorEntradaInformado(BigDecimal valorEntradaInformado) {
        this.valorEntradaInformado = valorEntradaInformado;
    }

    public BigDecimal getValorEntradaInicial() {
        return valorEntradaInicial;
    }

    public void setValorEntradaInicial(BigDecimal valorEntradaInicial) {
        this.valorEntradaInicial = valorEntradaInicial;
    }

    public BigDecimal getValorMinimoEntrada() {
        return valorMinimoEntrada;
    }

    public void setValorMinimoEntrada(BigDecimal valorMinimoEntrada) {
        this.valorMinimoEntrada = valorMinimoEntrada;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public List<ResultadoParcela> getParcelasGeradas() {
        return parcelasGeradas;
    }

    public void setParcelasGeradas(List<ResultadoParcela> parcelasGeradas) {
        this.parcelasGeradas = parcelasGeradas;
    }

    public ProcessoParcelamentoControlador.Valores getValores() {
        return valores;
    }

    public void setValores(ProcessoParcelamentoControlador.Valores valores) {
        this.valores = valores;
    }

    public boolean isValorParcelarMaiorMinimo() {
        return getValorMinimoParcela().compareTo(processoParcelamento.getTotalGeral().subtract(processoParcelamento.getValorTotalDesconto())) < 0;
    }

    public boolean isValorParcelarMaiorQueDobroDoMinimo() {
        BigDecimal totalComDesconto = processoParcelamento.getTotalGeral()
            .subtract(processoParcelamento.getValorTotalDesconto());
        return getValorMinimoParcela().multiply(BigDecimal.valueOf(2)).compareTo(totalComDesconto) < 0;
    }

    public BigDecimal getValorTotalOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (getParcelasGeradas() != null) {
            for (ResultadoParcela originada : getParcelasGeradas()) {
                total = total.add(originada.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalHonorariosOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorHonorarios());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalImpostoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorImposto());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalTaxaOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorTaxa());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalJurosOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorJuros());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalMultaOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorMulta());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalCorrecaoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorCorrecao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalSemDescontoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorTotalSemDesconto());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDescontoOriginada() {
        BigDecimal total = BigDecimal.ZERO;
        if (parcelasGeradas != null) {
            for (ResultadoParcela originada : parcelasGeradas) {
                total = total.add(originada.getValorDesconto());
            }
        }
        return total;
    }

    private void mostraMensagemRedefinicao() {
        FacesUtil.addAtencao("O Valor de Entrada e a Qtde de Parcelas foram definidos com o máximo possível!");
    }

    private void mostrarMensagemEntradaNaoPermitidaMaiorQueTotal(BigDecimal valorInformado) {
        FacesUtil.addOperacaoNaoPermitida("A entrada informada (R$ " + Util.formataNumero(valorInformado) +
            ") não pode ser maior que o valor do parcelamento (R$ " + Util.formataNumero(processoParcelamento.getTotalGeral().subtract(processoParcelamento.getValorTotalDesconto())) +
            ")!");
        mostraMensagemRedefinicao();
    }

    private void mostrarMensagemEntradaNaoPermitida(BigDecimal valorInformado) {
        FacesUtil.addOperacaoNaoPermitida("A entrada informada (R$ " + Util.formataNumero(valorInformado) +
            ") não é permitida, pois com o valor do parcelamento (R$ " +
            Util.formataNumero(getValorTotalOriginada()) +
            ") as demais parcelas ficarão abaixo de meia unidade fiscal!");
        mostraMensagemRedefinicao();
    }

    public String montarDescricaoValorParametroEntrada() {
        return Util.formataNumero(processoParcelamento.getParamParcelamento().getValorMinimoParcelaUfm());
    }

    private void mostrarMensagemEntradaMenorParametro() {
        FacesUtil.addError("Valor da entrada inválido!",
            "O valor não pode ser menor que o percentual de entrada"
                + (processoParcelamento.getParamParcelamento().getExigePercentualEntrada() ? " (" +
                processoParcelamento.getParamParcelamento().getValorPercentualEntrada() + "%) " : " ") +
                "ou menor que " + montarDescricaoValorParametroEntrada() + " Unidade Fiscal (" +
                Util.formataValor(getValorMinimoParcela()) + ").");
    }

    public boolean isValorMenorQueEntrada() {
        return (isPermiteLancarEntradaMenorQueParamentro() && getValorMinimoEntrada() != null
            && processoParcelamento.getValorEntrada().compareTo(getValorMinimoEntrada()) < 0);
    }

    public void definirParaMaximoPossivel() {
        setValorEntradaInformado(BigDecimal.ZERO);
        setValorEntradaInicial(BigDecimal.ZERO);
        getProcessoParcelamento().setValorEntrada(BigDecimal.ZERO);
        setValorMinimoEntrada(getValorMinimoParcela());
        getProcessoParcelamento().setNumeroParcelas(1);

        ajustarParcelasAposBuscar();
    }

    public ProcessoParcelamentoFacade.ValoresParcela aplicarDesconto(ProcessoParcelamentoFacade.ValoresParcela valores, BigDecimal valorDesconto, BigDecimal valorParcela, BigDecimal valorAplicar) {
        if (valorAplicar.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal porcentagem = (valorParcela.multiply(CEM).divide(valorAplicar, 12, RoundingMode.HALF_UP))
                .divide(CEM, 6, RoundingMode.HALF_UP);
            valores.desconto = valorDesconto.multiply(porcentagem).setScale(2, RoundingMode.HALF_UP);
        }
        return valores;
    }

    private Date ajustarVencimentoUltimoDiaMes(Date primeiroVencimento, int numeroParcela, boolean primeiraParcela) {
        Calendar retorno = Calendar.getInstance();
        retorno.setTime(primeiroVencimento);
        if (!primeiraParcela) {
            retorno.add(Calendar.MONTH, (numeroParcela - 1));
            retorno.setTime(DataUtil.ultimoDiaMes(retorno.getTime()).getTime());

            if (retorno.get(Calendar.DAY_OF_MONTH) == 31 && retorno.get(Calendar.MONTH) == Calendar.DECEMBER) {
                retorno.add(Calendar.DAY_OF_MONTH, -1);
                while (DataUtil.ehDiaNaoUtil(retorno.getTime(), facade.getFeriadoFacade())) {
                    retorno.add(Calendar.DAY_OF_MONTH, -1);
                }
            } else {
                while (DataUtil.ehDiaNaoUtil(retorno.getTime(), facade.getFeriadoFacade())) {
                    retorno.add(Calendar.DAY_OF_MONTH, -1);
                }
            }
        }
        return retorno.getTime();
    }

    private ResultadoParcela gerarParcela(ProcessoParcelamento parcelamento, Calendar vencimento, int numeroParcela, ProcessoParcelamentoFacade.ValoresParcela valores) {
        vencimento.setTime(ajustarVencimentoUltimoDiaMes(parcelamento.getVencimentoPrimeiraParcela(), numeroParcela, numeroParcela == 1));

        int parcelas = parcelamento.getNumeroParcelas();
        ResultadoParcela parcela = new ResultadoParcela();
        parcela.setVencimento(vencimento.getTime());
        parcela.setParcela(numeroParcela + "/" + parcelas);
        parcela.setValorImposto(valores.imposto);
        parcela.setValorTaxa(valores.taxa);
        parcela.setValorJuros(valores.juros);
        parcela.setValorMulta(valores.multa);
        parcela.setValorCorrecao(valores.correcao);
        parcela.setValorHonorarios(valores.honorarios);
        parcela.setValorDesconto(valores.desconto);
        parcela.setSituacao(SituacaoParcela.EM_ABERTO.name());
        parcela.setCadastro(TipoCadastroTributario.IMOBILIARIO.name());
        parcela.setTipoCalculo(Calculo.TipoCalculo.PARCELAMENTO.name());
        return parcela;
    }

    private void ajutarValoresDepoisDaEntrada(ProcessoParcelamentoControlador.Valores valores, ResultadoParcela parcela) {
        valores.imposto = valores.imposto.subtract(parcela.getValorImposto());
        valores.taxa = valores.taxa.subtract(parcela.getValorTaxa());
        valores.multa = valores.multa.subtract(parcela.getValorMulta());
        valores.juros = valores.juros.subtract(parcela.getValorJuros());
        valores.correcao = valores.correcao.subtract(parcela.getValorCorrecao());
        valores.honorarios = valores.honorarios.subtract(parcela.getValorHonorarios());
        valores.desconto = valores.desconto.subtract(parcela.getValorDesconto());
        valores.total = valores.juros.add(valores.multa.add(valores.correcao.add(valores.honorarios.add(valores.imposto.add(valores.taxa)))));
    }

    private List<ResultadoParcela> gerarParcelasIniciais() {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        int quantidadeParcelas = getProcessoParcelamento().getNumeroParcelas();
        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(getProcessoParcelamento().getVencimentoPrimeiraParcela());
        int numeroParcela = 1;
        ProcessoParcelamentoControlador.Valores valoresParcelar = new
            ProcessoParcelamentoControlador.Valores(getProcessoParcelamento());

        if (getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) > 0) {
            quantidadeParcelas = quantidadeParcelas - 1;
            ProcessoParcelamentoFacade.ValoresParcela valores = ProcessoParcelamentoFacade.ValoresParcela
                .calcularValores(getProcessoParcelamento(), getProcessoParcelamento().getValorEntrada(), getConfiguracaoAcrescimos());
            valores = aplicarDesconto(valores, getProcessoParcelamento().getValorDesconto(), getProcessoParcelamento().getValorEntrada(),
                getProcessoParcelamento().getTotalGeral());
            ResultadoParcela parcela = gerarParcela(getProcessoParcelamento(), vencimento, numeroParcela, valores);
            parcelas.add(parcela);
            numeroParcela++;
            ajutarValoresDepoisDaEntrada(valoresParcelar, parcela);
        }

        for (int i = 0; i < quantidadeParcelas; i++) {
            ProcessoParcelamentoFacade.ValoresParcela valores = ProcessoParcelamentoFacade.ValoresParcela.calcularValoresParcela(valoresParcelar,
                getProcessoParcelamento().getValorParcela(),
                getConfiguracaoAcrescimos());
            valores = aplicarDesconto(valores, valoresParcelar.desconto,
                getProcessoParcelamento().getValorParcela(), valoresParcelar.total);
            parcelas.add(gerarParcela(getProcessoParcelamento(), vencimento, numeroParcela, valores));
            numeroParcela++;
        }
        return parcelas;
    }


    public List<ResultadoParcela> gerarParcelasParaSimulacao() {
        return gerarParcelasIniciais();
    }

    private ParamParcelamentoTributo atribuirParametroDesconto() {
        ParamParcelamentoTributo paramDesconto = null;
        if (getProcessoParcelamento().getParamParcelamento() != null) {
            if (getProcessoParcelamento().getParamParcelamento().getTipoLancamentoDesconto() != null
                && ParamParcelamento.TipoLancamentoDesconto.NUMERO_PARCELA
                .equals(getProcessoParcelamento().getParamParcelamento().getTipoLancamentoDesconto())) {
                for (ParamParcelamentoTributo paramTributo : getProcessoParcelamento().getParamParcelamento().getTributos()) {
                    if (processoParcelamento.getNumeroParcelas() >= paramTributo.getNumeroParcelaInicial()
                        && processoParcelamento.getNumeroParcelas() <= paramTributo.getNumeroParcelaFinal()) {
                        paramDesconto = paramTributo;
                        break;
                    }
                }
                return paramDesconto;
            } else {
                Calendar vencimento = Calendar.getInstance();
                List<ResultadoParcela> parcelas = gerarParcelasParaSimulacao();
                ResultadoParcela ultima = parcelas != null && !parcelas.isEmpty() ? parcelas.get(parcelas.size() - 1) : null;
                if (ultima != null) {
                    vencimento.setTime(ultima.getVencimento());
                } else {
                    vencimento.setTime(getProcessoParcelamento().getVencimentoPrimeiraParcela());
                    vencimento.add(Calendar.MONTH, processoParcelamento.getNumeroParcelas());
                }

                Collections.sort(getProcessoParcelamento().getParamParcelamento().getTributos(),
                    new ProcessoParcelamentoControlador.ComparaDescontoPorData());
                for (ParamParcelamentoTributo paramTributo : getProcessoParcelamento().getParamParcelamento().getTributos()) {
                    if (vencimento.getTime().compareTo(paramTributo.getVencimentoFinalParcela()) <= 0) {
                        paramDesconto = paramTributo;
                        break;
                    }
                }
                return paramDesconto;
            }
        }
        return null;
    }

    private void definirValorMinimoEntrada(boolean validarEntradaInformado) {
        if (validarEntradaInformado) {
            if ((getValorEntradaInformado() != null
                && getValorEntradaInformado().compareTo(BigDecimal.ZERO) > 0
                && getValorEntradaInformado().compareTo(getValorEntradaInicial()) != 0)) {
                return;
            }
        }

        BigDecimal valorParcelar = getValorTotalOriginada();
        BigDecimal indice = getProcessoParcelamento().getPercentualEntrada().divide(CEM, 8, RoundingMode.HALF_UP);
        BigDecimal projecaoEntrada = (valorParcelar.multiply(indice)).setScale(2, RoundingMode.HALF_UP);
        if (projecaoEntrada.compareTo(BigDecimal.ZERO) > 0 && projecaoEntrada.compareTo(getValorMinimoParcela()) < 0) {
            projecaoEntrada = getValorMinimoParcela();
        }

        if (getValorMinimoEntrada() == null || projecaoEntrada.compareTo(getValorMinimoEntrada()) != 0) {
            setValorMinimoEntrada(projecaoEntrada);
            if (getProcessoParcelamento().getNumeroParcelas() > 1) {
                getProcessoParcelamento().setValorEntrada(getValorMinimoEntrada());
            }
        } else if (getProcessoParcelamento().getValorEntrada().compareTo(valorParcelar) > 0) {
            getProcessoParcelamento().setValorEntrada(valorParcelar);
        }
        setValorEntradaInicial(getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) > 0 ?
            getProcessoParcelamento().getValorEntrada() : projecaoEntrada);
    }

    private void definirValores() {
        setValores(new ProcessoParcelamentoControlador.Valores());
        for (ResultadoParcela parcela : getParcelas()) {
            getValores().calcular(parcela,
                getConfiguracaoAcrescimos(),
                getProcessoParcelamento().getParamParcelamento());
        }
        getProcessoParcelamento().calcularValores(getValores());
        getProcessoParcelamento().setFaixaDesconto(atribuirParametroDesconto());
        if (getProcessoParcelamento().getFaixaDesconto() != null) {
            getValores().aplicarDescontos(getProcessoParcelamento().getFaixaDesconto());
        }
        getProcessoParcelamento().calcularValores(getValores());
        if (getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) <= 0
            && getProcessoParcelamento().getNumeroParcelas() > 1) {
            definirValorMinimoEntrada( true);
        }
        getProcessoParcelamento().calcularValores(getValores());
    }

    private void definirNumeroParcelas() {
        for (ParamParcelamentoFaixa faixa : getProcessoParcelamento().getParamParcelamento().getFaixas()) {
            if (faixa.getValorInicial().compareTo(getValores().getTotal()) <= 0
                && faixa.getValorFinal().compareTo(getValores().getTotal()) >= 0) {
                getProcessoParcelamento().setQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
                getProcessoParcelamento().setNumeroParcelas(getProcessoParcelamento().getQuantidadeMaximaParcelas());
                break;
            }
        }
    }

    public void calcularValorPrimeiraParcela() {
        if (getProcessoParcelamento().getParamParcelamento().getExigePercentualEntrada()) {
            if (getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) < 0) {
                getProcessoParcelamento().setValorEntrada(BigDecimal.ZERO);
            } else if (getProcessoParcelamento().hasEntradaInformada()
                && getProcessoParcelamento().getValorEntrada().compareTo(getValorMinimoEntrada()) < 0
                && !isPermiteLancarEntradaMenorQueParamentro()) {
                getProcessoParcelamento().setValorEntrada(getValorMinimoEntrada());
            }
        }
        if (getProcessoParcelamento().getValorEntrada()
            .compareTo(getProcessoParcelamento().getTotalGeral()) > 0) {
            getProcessoParcelamento().setValorEntrada(getValorMinimoParcela());
        }
    }

    public int calcularNumeroParcelaParaDivisao() {
        return getProcessoParcelamento().getNumeroParcelas() > 1 ?
            getProcessoParcelamento().getNumeroParcelas() - 1 :
            getProcessoParcelamento().getNumeroParcelas();
    }

    private void calcularValorDemaisParcelas() {
        if (getProcessoParcelamento().getNumeroParcelas() == null
            || getProcessoParcelamento().getNumeroParcelas() <= 0) {
            getProcessoParcelamento().setNumeroParcelas(1);
        }
        if (getProcessoParcelamento().hasEntradaInformada()
            && getProcessoParcelamento().getNumeroParcelas() > 1) {
            BigDecimal valor = getProcessoParcelamento().getTotalGeral()
                .subtract(getProcessoParcelamento().getValorEntrada());
            BigDecimal dividido = valor.divide(new BigDecimal(getProcessoParcelamento()
                .getNumeroParcelas() - 1), 2, RoundingMode.HALF_UP);
            if (valor.compareTo(getValorMinimoParcela()) < 0 && getProcessoParcelamento().getValorEntrada()
                .compareTo(getValorMinimoParcela()) >= 0) {
                dividido = getValorMinimoParcela();
                getProcessoParcelamento()
                    .setValorEntrada(getProcessoParcelamento().getTotalGeral()
                        .subtract(getValorMinimoParcela()));
            }
            getProcessoParcelamento().setValorParcela(dividido);
        } else {
            if (getProcessoParcelamento().getNumeroParcelas() > 1) {
                if (isPermiteLancarEntradaMenorQueParamentro()) {
                    if (!isValorParcelarMaiorQueDobroDoMinimo()) {
                        getProcessoParcelamento().setValorPrimeiraParcela(getValorMinimoParcela());
                        getProcessoParcelamento().setValorParcela((getProcessoParcelamento().getTotalGeral()
                            .subtract(getValorMinimoParcela()))
                            .divide(BigDecimal.valueOf(calcularNumeroParcelaParaDivisao()), 2, RoundingMode.HALF_UP));
                    } else {
                        getProcessoParcelamento()
                            .setValorParcela(getProcessoParcelamento().getTotalGeral()
                                .divide(BigDecimal.valueOf(getProcessoParcelamento()
                                    .getNumeroParcelas()), 2, RoundingMode.HALF_UP));
                    }
                } else {
                    getProcessoParcelamento().setValorParcela((getProcessoParcelamento().getTotalGeral()
                        .subtract(getValorMinimoParcela()))
                        .divide(BigDecimal.valueOf(getProcessoParcelamento()
                            .getNumeroParcelas()), 2, RoundingMode.HALF_UP));
                }
            } else {
                getProcessoParcelamento().setValorPrimeiraParcela(BigDecimal.ZERO);
                getProcessoParcelamento().setValorParcela(getProcessoParcelamento().getTotalGeral());
            }
        }
        if (getProcessoParcelamento().getValorParcela().compareTo(BigDecimal.ZERO) <= 0) {
            getProcessoParcelamento().setNumeroParcelas(1);
        }
    }

    public void calcularValorParcelas() {
        calcularValorPrimeiraParcela();
        calcularValorDemaisParcelas();
    }

    private void ajustarQuantidaDeParcelasConformeMinimoDoParamentro() {
        if (getProcessoParcelamento().getParamParcelamento().getValorMinimoParcelaUfm() != null &&
            getProcessoParcelamento().getParamParcelamento().getValorMinimoParcelaUfm().compareTo(BigDecimal.ZERO) > 0) {
            while (getProcessoParcelamento().getValorParcela()
                .compareTo(getValorMinimoParcela()) < 0 && getProcessoParcelamento().getNumeroParcelas() > 1) {
                if (!isValorParcelarMaiorQueDobroDoMinimo()
                    && getProcessoParcelamento().getQuantidadeMaximaParcelas() > 2
                    && isPermiteLancarEntradaMenorQueParamentro()) {
                    getProcessoParcelamento().setQuantidadeMaximaParcelas(getProcessoParcelamento().getQuantidadeMaximaParcelas() - 1);
                }
                getProcessoParcelamento().setNumeroParcelas(getProcessoParcelamento().getNumeroParcelas() - 1);
                definirValores();
                calcularValorParcelas();
                definirValorMinimoEntrada(false);
            }
        }
    }

    private ParamParcelamentoFaixa buscarFaixaPorValorParcelamento() {
        ParamParcelamento paramParcelamento = processoParcelamento.getParamParcelamento();
        if (!Hibernate.isInitialized(paramParcelamento.getFaixas())) {
            processoParcelamento.setParamParcelamento(facade.getParametroParcelamentoFacade().recuperar(paramParcelamento.getId()));
        }
        for (ParamParcelamentoFaixa faixa : processoParcelamento.getParamParcelamento().getFaixas()) {
            if (faixa.getValorInicial().compareTo(getProcessoParcelamento().getTotalGeral()) <= 0
                && faixa.getValorFinal().compareTo(getProcessoParcelamento().getTotalGeral()) >= 0) {
                return faixa;
            }
        }
        return null;
    }

    private void definirValorEntradaQuandoNaoPossui() {
        if (getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) <= 0 &&
            getProcessoParcelamento().getNumeroParcelas() > 1) {
            if (!getParcelasGeradas().isEmpty()) {
                ResultadoParcela resultadoParcela = getParcelasGeradas().get(0);
                getProcessoParcelamento().setValorEntrada(resultadoParcela.getValorTotalSemDesconto());
            }
        }
    }

    private void calcularHonorariosDasParcelas(List<ResultadoParcela> parcelas,
                                               ProcessoParcelamento parcelamento,
                                               ConfiguracaoAcrescimos configuracaoAcrescimos) {
        for (ResultadoParcela parcela : parcelas) {
            BigDecimal calcularHonorariosSobre = parcela.getValorImposto().add(parcela.getValorTaxa()).add(parcela.getValorJuros()).add(parcela.getValorMulta()).add(parcela.getValorCorrecao()).subtract(parcela.getValorDesconto());
            parcela.setValorHonorarios(CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(), calcularHonorariosSobre));
        }
    }

    private boolean calcularNovaEntradaAposCalculoHonorarios(List<ResultadoParcela> parcelas, ProcessoParcelamento parcelamento, BigDecimal valorMinimoParcela) {
        if (parcelamento.getValorEntrada().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal percetualEntrada = parcelamento.getParamParcelamento().getValorPercentualEntrada();

            if (percetualEntrada != null && percetualEntrada.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorTotal = BigDecimal.ZERO;
                for (ResultadoParcela parcela : parcelas) {
                    valorTotal = valorTotal.add(parcela.getValorTotal());
                }
                BigDecimal indice = percetualEntrada.divide(CEM, 8, RoundingMode.HALF_UP);
                BigDecimal projecaoEntrada = (valorTotal.multiply(indice)).setScale(2, RoundingMode.HALF_UP);
                if (projecaoEntrada.compareTo(valorMinimoParcela) < 0) {
                    projecaoEntrada = valorMinimoParcela;
                }
                BigDecimal diferenca = parcelamento.getValorEntrada().subtract(projecaoEntrada);
                if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                    parcelamento.setValorEntrada(projecaoEntrada);
                    return true;
                }
            }
        }
        return false;
    }

    private ProcessoParcelamentoFacade.ValoresParcela buscarTotalParcelamentoCalculadoDasParcelasGeradas(List<ResultadoParcela> parcelas) {
        ProcessoParcelamentoFacade.ValoresParcela valores = new ProcessoParcelamentoFacade.ValoresParcela();
        for (ResultadoParcela parcela : parcelas) {
            adicionarValoresAParcela(valores, parcela);
        }
        return valores;
    }

    private void arredondarValorDaPrimeiraParcelaComValorDeEntrada(List<ResultadoParcela> parcelas, ProcessoParcelamento parcelamento) {
        ProcessoParcelamentoFacade.ValoresParcela valores = buscarTotalParcelamentoCalculadoDasParcelasGeradas(parcelas);
        ProcessoParcelamentoFacade.ValoresParcela valoresDiferenca = new ProcessoParcelamentoFacade.ValoresParcela();

        for (ResultadoParcela parcela : parcelas) {

            if (parcelamento.getNumeroParcelas() > 1
                && parcelamento.getValorEntrada().compareTo(BigDecimal.ZERO) > 0 &&
                parcela.getSequenciaParcela() == 1) {

                BigDecimal percentual = parcelamento.getValorEntrada().multiply(CEM).divide(valores.getTotalComDesconto(), 8, RoundingMode.HALF_UP);

                if (parcela.getValorImposto().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal imposto = valores.imposto.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.imposto = imposto.subtract(parcela.getValorImposto());
                    parcela.setValorImposto(imposto);
                }
                if (parcela.getValorTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal taxa = valores.taxa.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.taxa = taxa.subtract(parcela.getValorTaxa());
                    parcela.setValorTaxa(taxa);
                }
                if (parcela.getValorJuros().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal juros = valores.juros.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.juros = juros.subtract(parcela.getValorJuros());
                    parcela.setValorJuros(juros);
                }
                if (parcela.getValorMulta().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal multa = valores.multa.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.multa = multa.subtract(parcela.getValorMulta());
                    parcela.setValorMulta(multa);
                }
                if (parcela.getValorCorrecao().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal correcao = valores.correcao.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.correcao = correcao.subtract(parcela.getValorCorrecao());
                    parcela.setValorCorrecao(correcao);
                }
                if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal honorarios = valores.honorarios.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.honorarios = honorarios.subtract(parcela.getValorHonorarios());
                    parcela.setValorHonorarios(honorarios);
                }
                if (parcela.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal desconto = valores.desconto.multiply(percentual).divide(CEM, 2, RoundingMode.HALF_UP);
                    valoresDiferenca.desconto = desconto.subtract(parcela.getValorDesconto());
                    parcela.setValorDesconto(desconto);
                }

                BigDecimal diferenca = parcela.getValorTotal().subtract(parcelamento.getValorEntrada());
                if (parcela.getValorImposto().compareTo(BigDecimal.ZERO) > 0) {
                    if (diferenca.compareTo(parcela.getValorImposto()) < 0) {
                        parcela.setValorImposto(parcela.getValorImposto().subtract(diferenca));
                    }
                } else {
                    if (diferenca.compareTo(parcela.getValorTaxa()) < 0) {
                        parcela.setValorTaxa(parcela.getValorTaxa().subtract(diferenca));
                    }
                }
            }
        }
        for (ResultadoParcela parcela : parcelas) {
            if (parcelamento.getNumeroParcelas() > 1 &&
                parcela.getSequenciaParcela() > 1 && valoresDiferenca.getTotalComDesconto().compareTo(BigDecimal.ZERO) > 0) {
                if (parcela.getValorImposto().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorImposto(parcela.getValorImposto().subtract(valoresDiferenca.imposto));
                }
                if (parcela.getValorTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorTaxa(parcela.getValorTaxa().subtract(valoresDiferenca.taxa));
                }
                if (parcela.getValorJuros().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorJuros(parcela.getValorJuros().subtract(valoresDiferenca.juros));
                }
                if (parcela.getValorMulta().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorMulta(parcela.getValorMulta().subtract(valoresDiferenca.multa));
                }
                if (parcela.getValorCorrecao().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorCorrecao(parcela.getValorCorrecao().subtract(valoresDiferenca.correcao));
                }
                if (parcela.getValorHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorHonorarios(parcela.getValorHonorarios().subtract(valoresDiferenca.honorarios));
                }
                if (parcela.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
                    parcela.setValorDesconto(parcela.getValorDesconto().subtract(valoresDiferenca.desconto));
                }
                break;
            }
        }
    }

    private BigDecimal calcularNovoValorParcela(BigDecimal valorColunaParcelamento, BigDecimal valorColunaPrimeiraParcela, int quantidadeParcelas) {
        BigDecimal valor = valorColunaParcelamento.subtract(valorColunaPrimeiraParcela);
        return valor.divide(BigDecimal.valueOf(quantidadeParcelas), 2, RoundingMode.HALF_UP);
    }

    private void ajustarValoresDasParcelasDepoisDeGeradas(List<ResultadoParcela> parcelas, ProcessoParcelamento parcelamento, ConfiguracaoAcrescimos configuracaoAcrescimos) {
        ResultadoParcela primeiraParcela = parcelas.get(0);

        int quantidadeParcelas = parcelas.size() - 1;
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getSequenciaParcela() > 1) {
                parcela.setValorImposto(calcularNovoValorParcela(parcelamento.getValorTotalImposto(), primeiraParcela.getValorImposto(), quantidadeParcelas));
                parcela.setValorTaxa(calcularNovoValorParcela(parcelamento.getValorTotalTaxa(), primeiraParcela.getValorTaxa(), quantidadeParcelas));
                parcela.setValorJuros(calcularNovoValorParcela(parcelamento.getValorTotalJuros(), primeiraParcela.getValorJuros(), quantidadeParcelas));
                parcela.setValorMulta(calcularNovoValorParcela(parcelamento.getValorTotalMulta(), primeiraParcela.getValorMulta(), quantidadeParcelas));
                parcela.setValorCorrecao(calcularNovoValorParcela(parcelamento.getValorTotalCorrecao(), primeiraParcela.getValorCorrecao(), quantidadeParcelas));
                parcela.setValorDesconto(calcularNovoValorParcela(parcelamento.getValorTotalDesconto(), primeiraParcela.getValorDesconto(), quantidadeParcelas));

                if (parcelamento.getValorTotalHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal calcularHonorariosSobre = parcela.getValorImposto().add(parcela.getValorTaxa()).add(parcela.getValorJuros()).add(parcela.getValorMulta()).add(parcela.getValorCorrecao()).subtract(parcela.getValorDesconto());
                    parcela.setValorHonorarios(CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(), calcularHonorariosSobre));
                }
            }
        }
    }

    private BigDecimal processarDiferencaDaParcela(BigDecimal somaColuna, BigDecimal valorTotal, BigDecimal valorColuna) {
        if (somaColuna.compareTo(valorTotal) != 0) {
            BigDecimal diferenca = valorTotal.subtract(somaColuna);
            valorColuna = valorColuna.add(diferenca);
        }
        return valorColuna;
    }

    private void ajustarValoresUltimaParcelaParcelamento(List<ResultadoParcela> parcelas, ProcessoParcelamento parcelamento, ConfiguracaoAcrescimos configuracaoAcrescimos) {
        ProcessoParcelamentoFacade.ValoresParcela valores = buscarTotalParcelamentoCalculadoDasParcelasGeradas(parcelas);

        ResultadoParcela parcela = parcelas.get(parcelas.size() - 1);
        parcela.setValorImposto(processarDiferencaDaParcela(valores.imposto, parcelamento.getValorTotalImposto(), parcela.getValorImposto()));
        parcela.setValorTaxa(processarDiferencaDaParcela(valores.taxa, parcelamento.getValorTotalTaxa(), parcela.getValorTaxa()));
        parcela.setValorJuros(processarDiferencaDaParcela(valores.juros, parcelamento.getValorTotalJuros(), parcela.getValorJuros()));
        parcela.setValorMulta(processarDiferencaDaParcela(valores.multa, parcelamento.getValorTotalMulta(), parcela.getValorMulta()));
        parcela.setValorCorrecao(processarDiferencaDaParcela(valores.correcao, parcelamento.getValorTotalCorrecao(), parcela.getValorCorrecao()));
        parcela.setValorDesconto(processarDiferencaDaParcela(valores.desconto, parcelamento.getValorTotalDesconto(), parcela.getValorDesconto()));

        valores = buscarTotalParcelamentoCalculadoDasParcelasGeradas(parcelas);

        if (parcelamento.getValorTotalHonorarios().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal calcularHonorariosSobre = valores.getTotalComDescontoSemHonorarios();
            BigDecimal honorariosCalculado = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(), calcularHonorariosSobre);
            parcela.setValorHonorarios(processarDiferencaDaParcela(valores.honorarios, honorariosCalculado, parcela.getValorHonorarios()));
        }
    }

    private void adicionarValoresAParcela(ProcessoParcelamentoFacade.ValoresParcela valores, ResultadoParcela parcela) {
        valores.imposto = valores.imposto.add(parcela.getValorImposto());
        valores.taxa = valores.taxa.add(parcela.getValorTaxa());
        valores.juros = valores.juros.add(parcela.getValorJuros());
        valores.multa = valores.multa.add(parcela.getValorMulta());
        valores.correcao = valores.correcao.add(parcela.getValorCorrecao());
        valores.honorarios = valores.honorarios.add(parcela.getValorHonorarios());
        valores.desconto = valores.desconto.add(parcela.getValorDesconto());
    }

    private ProcessoParcelamentoFacade.ValoresParcela buscarTotalParcelamentoCalculadoDasParcelasGeradasSemEntrada(List<ResultadoParcela> parcelas) {
        ProcessoParcelamentoFacade.ValoresParcela valores = new ProcessoParcelamentoFacade.ValoresParcela();
        for (ResultadoParcela parcela : parcelas) {
            if (parcela.getSequenciaParcela() > 1) {
                adicionarValoresAParcela(valores, parcela);
            }
        }
        return valores;
    }

    private BigDecimal calcularValorParaComparacaoDeQuantidadeMaxima(ProcessoParcelamento parcelamento, BigDecimal valorTotal) {
        if (parcelamento.getQuantidadeMaximaParcelas() != null && parcelamento.getQuantidadeMaximaParcelas() > 0) {
            return valorTotal.divide(BigDecimal.valueOf(parcelamento.getQuantidadeMaximaParcelas()), 2, RoundingMode.HALF_UP);
        }
        return valorTotal;
    }

    private void calcularQuantidadeMaximaParcelasDepoisDeCalcularTudo(List<ResultadoParcela> parcelas, ProcessoParcelamento parcelamento, BigDecimal valorMinimoParcela) {
        ProcessoParcelamentoFacade.ValoresParcela valores;
        if (parcelas.size() > 1) {
            valores = buscarTotalParcelamentoCalculadoDasParcelasGeradasSemEntrada(parcelas);
        } else {
            valores = buscarTotalParcelamentoCalculadoDasParcelasGeradas(parcelas);
        }

        BigDecimal valorParaComparacao = calcularValorParaComparacaoDeQuantidadeMaxima(parcelamento, valores.getTotalComDesconto());
        while (valorParaComparacao.compareTo(valorMinimoParcela) < 0
            && parcelamento.getQuantidadeMaximaParcelas() > 1) {
            parcelamento.setQuantidadeMaximaParcelas(parcelamento.getQuantidadeMaximaParcelas() - 1);
            valorParaComparacao = calcularValorParaComparacaoDeQuantidadeMaxima(parcelamento, valores.getTotalComDesconto());
        }
        if (parcelamento.getValorEntrada().compareTo(BigDecimal.ZERO) > 0) {
            parcelamento.setQuantidadeMaximaParcelas(parcelamento.getQuantidadeMaximaParcelas() + 1);
        }

        valores = buscarTotalParcelamentoCalculadoDasParcelasGeradas(parcelas);
        ParamParcelamento paramParcelamento = facade.getParametroParcelamentoFacade().recuperar(parcelamento.getParamParcelamento().getId());
        for (ParamParcelamentoFaixa faixa : paramParcelamento.getFaixas()) {
            if (faixa.getValorInicial().compareTo(valores.getTotalComDesconto()) <= 0 && faixa.getValorFinal().compareTo(valores.getTotalComDesconto()) >= 0) {
                if (parcelamento.getQuantidadeMaximaParcelas() > faixa.getQuantidadeMaximaParcelas()) {
                    parcelamento.setQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
                }
                break;
            }
        }
    }

    public List<ResultadoParcela> gerarParcelas(int contador) {
        contador++;
        List<ResultadoParcela> parcelas = gerarParcelasIniciais();
        if (getProcessoParcelamento().getValorTotalHonorarios() != null
            && getProcessoParcelamento().getValorTotalHonorarios().compareTo(BigDecimal.ZERO) > 0) {
            calcularHonorariosDasParcelas(parcelas, getProcessoParcelamento(), getConfiguracaoAcrescimos());

            if (getValorEntradaInformado() == null || getValorEntradaInformado().compareTo(BigDecimal.ZERO) <= 0) {
                if (calcularNovaEntradaAposCalculoHonorarios(parcelas, getProcessoParcelamento(), getValorMinimoParcela())) {
                    if (contador < 10)
                        parcelas = gerarParcelas(contador);
                }
            }
        }
        arredondarValorDaPrimeiraParcelaComValorDeEntrada(parcelas, getProcessoParcelamento());
        if (getValorEntradaInformado() == null || getValorEntradaInformado().compareTo(BigDecimal.ZERO) <= 0) {
            calcularNovaEntradaAposCalculoHonorarios(parcelas, getProcessoParcelamento(),
                getValorMinimoParcela());
        }
        arredondarValorDaPrimeiraParcelaComValorDeEntrada(parcelas, getProcessoParcelamento());
        ajustarValoresDasParcelasDepoisDeGeradas(parcelas, getProcessoParcelamento(),
            getConfiguracaoAcrescimos());
        ajustarValoresUltimaParcelaParcelamento(parcelas, getProcessoParcelamento(),
            getConfiguracaoAcrescimos());
        calcularQuantidadeMaximaParcelasDepoisDeCalcularTudo(parcelas, getProcessoParcelamento(),
            getValorMinimoParcela());
        return parcelas;
    }

    public boolean verificarValorDasParcelasMenorQueMinimo(List<ResultadoParcela> parcelas, BigDecimal valorMinimoParcela) {
        for (ResultadoParcela parcela : parcelas) {
            if (valorMinimoParcela.compareTo(BigDecimal.ZERO) > 0 && parcela.getValorTotal().compareTo(valorMinimoParcela) < 0) {
                return true;
            }
        }
        return false;
    }

    public void gerarParcelasDefinitivo() {
        setParcelasGeradas(gerarParcelas(0));
        if (verificarValorDasParcelasMenorQueMinimo(getParcelasGeradas(), getValorMinimoParcela())
            && getProcessoParcelamento().getNumeroParcelas() > 1) {
            getProcessoParcelamento().setNumeroParcelas(getProcessoParcelamento().getNumeroParcelas() - 1);
            getProcessoParcelamento().setQuantidadeMaximaParcelas(getProcessoParcelamento().getNumeroParcelas());
            definirValores();
            calcularValorDemaisParcelas();
            gerarParcelasDefinitivo();
        }
    }

    public void calcularValores() {
        if (getProcessoParcelamento().getNumeroParcelas() <= 0) {
            getProcessoParcelamento().setNumeroParcelas(1);
        } else if (getProcessoParcelamento().getNumeroParcelas() >
            getProcessoParcelamento().getQuantidadeMaximaParcelas()
            && isValorParcelarMaiorQueDobroDoMinimo()) {
            calcularQuantidadeMaximaParcelas();
        }
        definirValores();
        if (getProcessoParcelamento().getValorEntrada().compareTo(BigDecimal.ZERO) <= 0
            && isValorParcelarMaiorQueDobroDoMinimo()) {
            calcularQuantidadeMaximaParcelas();
        }
        calcularValorParcelas();
        definirValores();
        gerarParcelasDefinitivo();
    }

    public void calcularValoresAposAlteracaoQuantidadeParcelas() {
        if (getProcessoParcelamento().getNumeroParcelas() == 1) {
            setValorEntradaInformado(null);
            getProcessoParcelamento().setValorEntrada(BigDecimal.ZERO);
        }
        if ((getValorEntradaInformado() == null || getValorEntradaInformado().compareTo(BigDecimal.ZERO) <= 0)
            && getValorMinimoEntrada() != null &&
            isValorParcelarMaiorQueDobroDoMinimo() && (getProcessoParcelamento().getNumeroParcelas() > 1)) {
            getProcessoParcelamento().setValorEntrada(getValorMinimoEntrada());
        }
        definirValores();
        calcularValores();
        definirValorMinimoEntrada( false);

        calcularQuantidadeMaximaParcelas();
        calcularValores();
        definirValorEntradaQuandoNaoPossui();
        if ((getValorEntradaInformado() == null || getValorEntradaInformado().compareTo(BigDecimal.ZERO) <= 0)) {
            gerarParcelasDefinitivo();
        }
        validarValoresDasParcelasComDesconto();
    }

    public void ajustarParcelasAposBuscar() {
        definirValores();
        definirNumeroParcelas();
        calcularValorParcelas();
        definirValores();
        ajustarQuantidaDeParcelasConformeMinimoDoParamentro();
        calcularQuantidadeMaximaParcelas();
        if (isValorParcelarMaiorQueDobroDoMinimo()) {
            ParamParcelamentoFaixa faixa = buscarFaixaPorValorParcelamento();
            getProcessoParcelamento().setQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
            if (getProcessoParcelamento().getNumeroParcelas() > 1) {
                getProcessoParcelamento().setValorEntrada(getValorMinimoEntrada());
            }
        }
        calcularValores();
        if (isValorParcelarMaiorQueDobroDoMinimo() &&
            getProcessoParcelamento().getNumeroParcelas() < getProcessoParcelamento().getQuantidadeMaximaParcelas()) {
            getProcessoParcelamento().setNumeroParcelas(getProcessoParcelamento().getQuantidadeMaximaParcelas());
            calcularValoresAposAlteracaoQuantidadeParcelas();
        }
        if (!isValorParcelarMaiorQueDobroDoMinimo()) {
            getProcessoParcelamento().setNumeroParcelas(1);
            getProcessoParcelamento().setValorEntrada(BigDecimal.ZERO);
        } else {
            definirValorEntradaQuandoNaoPossui();
        }
        gerarParcelasDefinitivo();
    }

    private boolean isValorParcelarMenosEntradaMaiorQueDobroDoMinimo() {
        BigDecimal totalComDesconto = getProcessoParcelamento().getTotalGeral()
            .subtract(getProcessoParcelamento().getValorTotalDesconto());
        return getValorMinimoParcela().multiply(BigDecimal.valueOf(2)).compareTo(totalComDesconto
            .subtract(getProcessoParcelamento().getValorEntrada())) < 0;
    }

    public void atribuirQuantidadeMaximaParcelas(Integer quantidadeMaximaParcelas) {
        getProcessoParcelamento().setQuantidadeMaximaParcelas(quantidadeMaximaParcelas);
        if (getProcessoParcelamento().getNumeroParcelas() > quantidadeMaximaParcelas) {
            getProcessoParcelamento().setNumeroParcelas(quantidadeMaximaParcelas);
        }
    }

    public void calcularQuantidadeMaximaParcelas() {
        ParamParcelamentoFaixa faixa = buscarFaixaPorValorParcelamento();
        if (faixa != null && isValorParcelarMenosEntradaMaiorQueDobroDoMinimo()) {
            atribuirQuantidadeMaximaParcelas(faixa.getQuantidadeMaximaParcelas());
        } else {
            atribuirQuantidadeMaximaParcelas((isPermiteLancarEntradaMenorQueParamentro()
                && isValorParcelarMaiorMinimo()) || isValorParcelarMaiorQueDobroDoMinimo() ? 2 : 1);
        }
    }

    private static BigDecimal descobrirPercentual(BigDecimal total, BigDecimal descobrir) {
        return (descobrir.multiply(CEM).divide(total, 12, RoundingMode.HALF_UP)).divide(CEM, 8, RoundingMode.HALF_UP);
    }

    private static BigDecimal multiplicaPercentual(BigDecimal valorParcela, BigDecimal percentualImposto) {
        return valorParcela.multiply(percentualImposto).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal percentualImpostoAcrescidoDe(ProcessoParcelamento selecionado, BigDecimal somar) {
        return descobrirPercentual(selecionado.getTotalGeral(),
            selecionado.getValorTotalImposto().add(somar));
    }

    private static BigDecimal calcularProporcaoDiferencaParaAbater(BigDecimal valorParcela, BigDecimal valor, BigDecimal diferencaParcela) {
        return (valor.multiply(diferencaParcela)).divide(valorParcela, 8, RoundingMode.HALF_UP);
    }

    private static void ajustarDiferencaItensParcela(BigDecimal valorParcela, ProcessoParcelamentoFacade.ValoresParcela valores) {
        BigDecimal total = valores.imposto
            .add(valores.taxa)
            .add(valores.juros)
            .add(valores.multa)
            .add(valores.correcao)
            .add(valores.honorarios)
            .subtract(valores.desconto);
        if (total.compareTo(valorParcela) != 0) {
            BigDecimal diferenca = valorParcela.subtract(total);
            valorParcela = valorParcela.compareTo(BigDecimal.ZERO) != 0 ? valorParcela : total;
            try {
                BigDecimal diferencaAbatida = BigDecimal.ZERO;
                if (valores.juros != null && valores.juros.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaJuros = calcularProporcaoDiferencaParaAbater(valorParcela, valores.juros, diferenca);
                    valores.juros = valores.juros.add(diferencaJuros);
                    diferencaAbatida = diferencaAbatida.add(diferencaJuros);
                }
                if (diferencaAbatida.compareTo(diferenca) != 0 && valores.multa != null && valores.multa.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaMulta = calcularProporcaoDiferencaParaAbater(valorParcela, valores.multa, diferenca);
                    valores.multa = valores.multa.add(diferencaMulta);
                    diferencaAbatida = diferencaAbatida.add(diferencaMulta);
                }
                if (diferencaAbatida.compareTo(diferenca) != 0 && valores.correcao != null && valores.correcao.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaCorrecao = calcularProporcaoDiferencaParaAbater(valorParcela, valores.correcao, diferenca);
                    valores.correcao = valores.correcao.add(diferencaCorrecao);
                    diferencaAbatida = diferencaAbatida.add(diferencaCorrecao);
                }
                if (diferencaAbatida.compareTo(diferenca) != 0 && valores.honorarios != null && valores.honorarios.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaHonorarios = calcularProporcaoDiferencaParaAbater(valorParcela, valores.honorarios, diferenca);
                    valores.honorarios = valores.honorarios.add(diferencaHonorarios);
                    diferencaAbatida = diferencaAbatida.add(diferencaHonorarios);
                }
                if (diferencaAbatida.compareTo(diferenca) != 0 && valores.imposto != null && valores.imposto.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaImposto = calcularProporcaoDiferencaParaAbater(valorParcela, valores.imposto, diferenca);
                    valores.imposto = valores.imposto.add(diferencaImposto);
                    diferencaAbatida = diferencaAbatida.add(diferencaImposto);
                }
                if (diferencaAbatida.compareTo(diferenca) != 0 && valores.taxa != null && valores.taxa.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal diferencaTaxa = calcularProporcaoDiferencaParaAbater(valorParcela, valores.taxa, diferenca);
                    valores.taxa = valores.taxa.add(diferencaTaxa);
                }
            } catch (Exception e) {
                logger.error("Não foi possível arredondar a parcela! ", e);
            }
        }
    }

    public static ProcessoParcelamentoFacade.ValoresParcela calcularValores(ProcessoParcelamento parcelamento,
                                                                            BigDecimal valorParcela, ConfiguracaoAcrescimos configuracaoAcrescimos) {
        ProcessoParcelamentoFacade.ValoresParcela valores = new ProcessoParcelamentoFacade.ValoresParcela();
        BigDecimal percentualImposto = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalImposto());
        if (percentualImposto.compareTo(BigDecimal.ZERO) > 0) {
            valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
            if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                percentualImposto = percentualImpostoAcrescidoDe(parcelamento, parcelamento.getValorTotalTaxa());
                valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                    percentualImposto = percentualImpostoAcrescidoDe(parcelamento, parcelamento.getValorTotalTaxa().add(parcelamento.getValorTotalJuros()));
                    valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                }
                if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                    percentualImposto = percentualImpostoAcrescidoDe(parcelamento,
                        parcelamento.getValorTotalTaxa().add(parcelamento.getValorTotalJuros().add(parcelamento.getValorTotalMulta())));
                    valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                }
                if (valores.imposto.compareTo(BigDecimal.ZERO) <= 0) {
                    percentualImposto = percentualImpostoAcrescidoDe(parcelamento,
                        parcelamento.getValorTotalTaxa()
                            .add(parcelamento.getValorTotalJuros().add(parcelamento.getValorTotalMulta().add(parcelamento.getValorTotalCorrecao()))));
                    valores.imposto = multiplicaPercentual(valorParcela, percentualImposto);
                }
            }
        }
        BigDecimal percentualTaxa = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalTaxa());
        if (percentualTaxa.compareTo(BigDecimal.ZERO) > 0) {
            valores.taxa = multiplicaPercentual(valorParcela, percentualTaxa);
        }
        BigDecimal percentualJuros = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalJuros());
        if (percentualJuros.compareTo(BigDecimal.ZERO) > 0) {
            valores.juros = multiplicaPercentual(valorParcela, percentualJuros);
        }
        BigDecimal percentualMulta = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalMulta());
        if (percentualMulta.compareTo(BigDecimal.ZERO) > 0) {
            valores.multa = multiplicaPercentual(valorParcela, percentualMulta);
        }
        BigDecimal percentualCorrecao = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalCorrecao());
        if (percentualCorrecao.compareTo(BigDecimal.ZERO) > 0) {
            valores.correcao = multiplicaPercentual(valorParcela, percentualCorrecao);
        }
        BigDecimal percentualDesconto = descobrirPercentual(parcelamento.getTotalGeral(), parcelamento.getValorTotalDesconto());
        if (percentualDesconto.compareTo(BigDecimal.ZERO) > 0) {
            valores.desconto = multiplicaPercentual(valorParcela, percentualDesconto);
        }
        if (parcelamento.getValorTotalHonorarios() != null && parcelamento.getValorTotalHonorarios().compareTo(BigDecimal.ZERO) > 0) {
            valores.honorarios = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), parcelamento.getDataProcessoParcelamento(),
                valores.juros.add(valores.multa.add(valores.correcao.add(valores.imposto.add(valores.taxa)))).subtract(valores.desconto));
        }
        ajustarDiferencaItensParcela(valorParcela, valores);
        return valores;
    }

    private boolean validarValorDeDescontoDasParcelas() {
        for (ResultadoParcela resultadoParcela : getParcelasGeradas()) {
            if (resultadoParcela.getValorTotalSemDesconto().compareTo(resultadoParcela.getValorDesconto()) < 0) {
                return false;
            }
        }
        return true;
    }

    public void validarValoresDasParcelasComDesconto() {
        if (!validarValorDeDescontoDasParcelas()) {
            mostrarMensagemEntradaNaoPermitida(valorEntradaInformado);
            definirParaMaximoPossivel();
        }
    }

    private void validarValorDeEntradaInformadoComParcelasGeradas() {
        if (processoParcelamento.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            boolean descontoZerado = false;
            for (ResultadoParcela parcelasGerada : getParcelasGeradas()) {
                if (parcelasGerada.getValorDesconto().compareTo(BigDecimal.ZERO) <= 0) {
                    descontoZerado = true;
                    break;
                }
            }

            if (descontoZerado) {
                mostrarMensagemEntradaNaoPermitida(getValorEntradaInformado());
                processoParcelamento.setValorEntrada(getValorMinimoParcela());
                calcularValores();
                calcularQuantidadeMaximaParcelas();
            }
        }
    }

    private boolean hasEntradaInformada() {
        return processoParcelamento.getValorEntrada().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean validarValorDasDemaisParcelas() {
        for (ResultadoParcela resultadoParcela : getParcelasGeradas()) {
            if (resultadoParcela.getSequenciaParcela() > 1) {
                if (resultadoParcela.getValorTotal().compareTo(getValorMinimoParcela()) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void validarValorDeEntrada() {
        if (processoParcelamento.getTotalGeral().subtract(processoParcelamento.getValorTotalDesconto()).compareTo(processoParcelamento.getValorEntrada()) <= 0) {
            mostrarMensagemEntradaNaoPermitidaMaiorQueTotal(processoParcelamento.getValorEntrada());
            definirParaMaximoPossivel();
            return;
        }
        if ((processoParcelamento.getTotalGeral().subtract(processoParcelamento.getValorTotalDesconto())
            .subtract(processoParcelamento.getValorEntrada())).compareTo(getValorMinimoParcela()) <= 0) {
            mostrarMensagemEntradaNaoPermitida(processoParcelamento.getValorEntrada());
            definirParaMaximoPossivel();
            return;
        }
        if (processoParcelamento.getTotalGeral().compareTo(getValorMinimoEntrada()) <= 0) {
            mostrarMensagemEntradaNaoPermitida(processoParcelamento.getValorEntrada());
            definirParaMaximoPossivel();
            return;
        }
        if (processoParcelamento.getValorEntrada().compareTo(BigDecimal.ZERO) == 0) {
            processoParcelamento.setNumeroParcelas(1);
        }
        if (hasEntradaInformada()
            && processoParcelamento.getValorEntrada().compareTo(getValorMinimoParcela()) < 0
            && !isPermiteLancarEntradaMenorQueParamentro()) {
            processoParcelamento.setValorEntrada(getValorMinimoParcela());
        }
        if (!isValorParcelarMaiorQueDobroDoMinimo()
            && !isPermiteLancarEntradaMenorQueParamentro()) {
            processoParcelamento.setValorEntrada(BigDecimal.ZERO);
        }
        valorEntradaInformado = processoParcelamento.getValorEntrada();
        calcularQuantidadeMaximaParcelas();
        if (processoParcelamento.getNumeroParcelas() > processoParcelamento.getQuantidadeMaximaParcelas()) {
            processoParcelamento.setNumeroParcelas(processoParcelamento.getQuantidadeMaximaParcelas());
        }
        calcularValores();
        if (getValorEntradaInformado().compareTo(processoParcelamento.getValorEntrada()) != 0
            && processoParcelamento.getValorEntrada().compareTo(getValorMinimoEntrada()) <= 0
            && !isPermiteLancarEntradaMenorQueParamentro()) {
            setValorEntradaInformado(null);
            mostrarMensagemEntradaMenorParametro();
            calcularQuantidadeMaximaParcelas();
            return;
        }
        if (processoParcelamento.getValorParcela().compareTo(getValorMinimoParcela()) < 0
            && hasEntradaInformada()
            && !isPermiteLancarEntradaMenorQueParamentro()) {
            mostrarMensagemEntradaNaoPermitida(getValorEntradaInformado());
            processoParcelamento.setValorEntrada(getValorMinimoParcela());
            calcularValores();
            calcularQuantidadeMaximaParcelas();
            return;
        }
        if (hasEntradaInformada() && processoParcelamento.getNumeroParcelas() <= 1) {
            processoParcelamento.setNumeroParcelas(processoParcelamento.getNumeroParcelas() + 1);
            calcularValores();
            if (processoParcelamento.getNumeroParcelas() == 1 && hasEntradaInformada()) {
                mostrarMensagemEntradaNaoPermitida(processoParcelamento.getValorEntrada());
                processoParcelamento.setValorEntrada(BigDecimal.ZERO);
                definirParaMaximoPossivel();
            } else {
                calcularQuantidadeMaximaParcelas();
            }
        }
        if (!validarValorDasDemaisParcelas()) {
            mostrarMensagemEntradaNaoPermitida(getValorEntradaInformado());
            processoParcelamento.setValorEntrada(getValorMinimoParcela());
            calcularValores();
            calcularQuantidadeMaximaParcelas();
            return;
        }
        validarValoresDasParcelasComDesconto();
        validarValorDeEntradaInformadoComParcelasGeradas();
    }

    public boolean isDiaNaoUtil(Date data) {
        return DataUtil.ehDiaNaoUtil(data, facade.getFeriadoFacade());
    }

    public void atribuirPrimeiroVencimento() {
        Integer dias = 1;
        if (getProcessoParcelamento().getParamParcelamento() != null &&
            getProcessoParcelamento().getParamParcelamento().getDiasVencimentoEntrada() != null) {
            dias = getProcessoParcelamento().getParamParcelamento().getDiasVencimentoEntrada();
        }
        Date dataOperacao = getDataOperacao();
        Calendar cal = Calendar.getInstance();
        if (isPermissaoMudarDataVencimentoPrimeiraParcela()) {
            cal.setTime(dataOperacao);
            cal.add(Calendar.DATE, dias > 0 ? dias : 1);
            while (isDiaNaoUtil(cal.getTime())) {
                cal.add(Calendar.DATE, 1);
            }
        } else {
            cal = DataUtil.ultimoDiaMes(dataOperacao);
            if (DataUtil.getDia(dataOperacao) == cal.get(Calendar.DAY_OF_MONTH)
                || DataUtil.getDia(dataOperacao) >= DataUtil.ultimoDiaUtil(cal, facade.getFeriadoFacade()).get(Calendar.DAY_OF_MONTH)) {
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.DAY_OF_MONTH, DataUtil.ultimoDiaUtil(cal, facade.getFeriadoFacade()).get(Calendar.DAY_OF_MONTH));
            }
        }
        getProcessoParcelamento().setVencimentoPrimeiraParcela(cal.getTime());
    }
}
