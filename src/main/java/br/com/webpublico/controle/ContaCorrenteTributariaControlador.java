package br.com.webpublico.controle;

/**
 * Created by Fabio on 08/08/2018.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImprimeDemonstrativoDebitos;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "contaCorrenteTributariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista-conta-corrente",
        pattern = "/tributario/conta-corrente-tributaria/listar/",
        viewId = "/faces/tributario/contacorrente/contacorrente/lista.xhtml"),
    @URLMapping(id = "ver-conta-corrente",
        pattern = "/tributario/conta-corrente-tributaria/ver/#{contaCorrenteTributariaControlador.id}/",
        viewId = "/faces/tributario/contacorrente/contacorrente/visualizar.xhtml")
})
public class ContaCorrenteTributariaControlador extends PrettyControlador<ContaCorrenteTributaria> implements Serializable, CRUD {

    @EJB
    private ContaCorrenteTributariaFacade contaCorrenteTributariaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ProcessoCreditoContaCorrenteFacade processoCreditoContaCorrenteFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private List<ResultadoParcela> resultadoConsulta;
    private List<ResultadoParcela> parcelasDoResidual;
    private ResultadoParcela[] resultados;
    private BigDecimal valorCreditoUtilizado;
    private BigDecimal valorResiduoUtilizado;
    private Date vencimentoDam;
    private BigDecimal valorDam;
    private DAM damSelecionado;
    private SistemaControlador sistemaControlador;
    private List<ItemLoteBaixa> itemLoteBaixasDoDamSelecionado;
    private List<ItemProcessoDebito> itemProcessoDebitosDoDamSelecionado;
    private List<PagamentoAvulso> pagamentosAvulsosDoDamSelecionado;
    private ParcelaContaCorrenteTributaria parcelaContaCorrenteSelecionada;

    public ContaCorrenteTributariaControlador() {
        super(ContaCorrenteTributaria.class);
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Date getDataOperacao() {
        return sistemaControlador.getDataOperacao();
    }

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
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

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public List<ResultadoParcela> getParcelasDoResidual() {
        return parcelasDoResidual;
    }

    public void setParcelasDoResidual(List<ResultadoParcela> parcelasDoResidual) {
        this.parcelasDoResidual = parcelasDoResidual;
    }

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
        this.resultados = resultados;
    }

    public BigDecimal getValorDam() {
        return valorDam;
    }

    public void setValorDam(BigDecimal valorDam) {
        this.valorDam = valorDam;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public DAM getDamSelecionado() {
        return damSelecionado;
    }

    public void setDamSelecionado(DAM damSelecionado) {
        this.damSelecionado = damSelecionado;
    }

    public List<ItemLoteBaixa> getItemLoteBaixasDoDamSelecionado() {
        return itemLoteBaixasDoDamSelecionado;
    }

    public void setItemLoteBaixasDoDamSelecionado(List<ItemLoteBaixa> itemLoteBaixasDoDamSelecionado) {
        this.itemLoteBaixasDoDamSelecionado = itemLoteBaixasDoDamSelecionado;
    }

    public List<ItemProcessoDebito> getItemProcessoDebitosDoDamSelecionado() {
        return itemProcessoDebitosDoDamSelecionado;
    }

    public void setItemProcessoDebitosDoDamSelecionado(List<ItemProcessoDebito> itemProcessoDebitosDoDamSelecionado) {
        this.itemProcessoDebitosDoDamSelecionado = itemProcessoDebitosDoDamSelecionado;
    }

    public List<PagamentoAvulso> getPagamentosAvulsosDoDamSelecionado() {
        return pagamentosAvulsosDoDamSelecionado;
    }

    public void setPagamentosAvulsosDoDamSelecionado(List<PagamentoAvulso> pagamentosAvulsosDoDamSelecionado) {
        this.pagamentosAvulsosDoDamSelecionado = pagamentosAvulsosDoDamSelecionado;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, processoCreditoContaCorrenteFacade.getPessoaFacade());
        }
        return converterDivida;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, processoCreditoContaCorrenteFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Divida> completarDivida(String parte) {
        return processoCreditoContaCorrenteFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    @Override
    public AbstractFacade getFacede() {
        return contaCorrenteTributariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente-tributaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "lista-conta-corrente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-conta-corrente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        valorCreditoUtilizado = null;
        valorResiduoUtilizado = null;
        carregarInformacoesDasParcelas();
        carregarParcelasDoResidual();
        parcelaContaCorrenteSelecionada = null;
    }

    private void carregarInformacoesDasParcelas() {
        for (ParcelaContaCorrenteTributaria parcela : selecionado.getParcelas()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getParcelaValorDivida().getId());
            consultaParcela.executaConsulta();
            processoCreditoContaCorrenteFacade.buscarValorPagoNaArrecadacao(consultaParcela.getResultados(), parcela.getOrigem());
            parcela.setResultadoParcela(consultaParcela.getResultados().get(0));
            if (!parcela.getCompensada()) {
                parcela.setValorDiferencaAtualizada(contaCorrenteTributariaFacade.getValorDiferencaAtualizada(parcela));
            }
        }
        valorCreditoUtilizado = null;
    }

    public boolean isPessoaFisica() {
        return selecionado.getPessoa() instanceof PessoaFisica;
    }

    public boolean isPessoaJuridica() {
        return selecionado.getPessoa() instanceof PessoaJuridica;
    }

    public BigDecimal getSaldoCreditoDisponivel() {
        return getTotalDoCredito().subtract(getValorCreditoUtilizado());
    }

    public BigDecimal getSaldoResiduoDisponivel() {
        BigDecimal saldo = getTotalDoResiduo().subtract(getValorResiduoUtilizado());
        return saldo.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean hasSaldoResiduoParcelaSelecionada() {
        if (parcelaContaCorrenteSelecionada != null) {
            return parcelaContaCorrenteSelecionada.getValorDiferencaAtualizada().compareTo(BigDecimal.ZERO) > 0 &&
                parcelaContaCorrenteSelecionada.getCalculoContaCorrente() == null;
        }
        return false;
    }

    public boolean isParcelaSelecionadaDeResiduo() {
        if (parcelaContaCorrenteSelecionada != null) {
            return TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO.equals(parcelaContaCorrenteSelecionada.getTipoDiferenca());
        }
        return false;
    }

    public ParcelaContaCorrenteTributaria getParcelaContaCorrenteSelecionada() {
        return parcelaContaCorrenteSelecionada;
    }

    public void setParcelaContaCorrenteSelecionada(ParcelaContaCorrenteTributaria parcelaContaCorrenteSelecionada) {
        this.parcelaContaCorrenteSelecionada = parcelaContaCorrenteSelecionada;
    }

    public BigDecimal getTotalDoCredito() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : selecionado.getParcelas()) {
            if (parcelaContaCorrenteTributaria.getTipoDiferenca().equals(TipoDiferencaContaCorrente.CREDITO_ARRECADACAO)) {
                total = total.add(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
            }
        }
        return total;
    }

    public BigDecimal getTotalDoResiduo() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria : selecionado.getParcelas()) {
            if (parcelaContaCorrenteTributaria.getTipoDiferenca().equals(TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO)) {
                total = total.add(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
            }
        }
        return total;
    }

    public BigDecimal getValorCreditoUtilizado() {
        if (valorCreditoUtilizado == null) {
            valorCreditoUtilizado = BigDecimal.ZERO;
            for (ParcelaContaCorrenteTributaria parcelasCredito : selecionado.getParcelasCreditos()) {
                if (parcelasCredito.getCompensada()) {
                    valorCreditoUtilizado = valorCreditoUtilizado.add(parcelasCredito.getValorDiferencaAtualizada());
                } else if (parcelasCredito.getValorCompesado().compareTo(BigDecimal.ZERO) > 0) {
                    valorCreditoUtilizado = valorCreditoUtilizado.add(parcelasCredito.getValorCompesado());
                } else if (parcelasCredito.getCalculoContaCorrente() != null && parcelasCredito.getCalculoContaCorrente().getRestituicao() != null) {
                    valorCreditoUtilizado = valorCreditoUtilizado.add(parcelasCredito.getValorDiferencaAtualizada());
                }
            }
        }
        return valorCreditoUtilizado;
    }

    public BigDecimal getValorCompensadoRestituido(ParcelaContaCorrenteTributaria parcela) {
        if (parcela.getCalculoContaCorrente() != null && parcela.getCalculoContaCorrente().getRestituicao() != null) {
            return parcela.getValorDiferencaAtualizada();
        }
        if (parcela.getValorCompesado().compareTo(BigDecimal.ZERO) > 0) {
            return parcela.getValorCompesado();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorResiduoUtilizado() {
        if (valorResiduoUtilizado == null) {
            valorResiduoUtilizado = BigDecimal.ZERO;
        }
        return valorResiduoUtilizado;
    }

    public void setValorResiduoUtilizado(BigDecimal valorResiduoUtilizado) {
        this.valorResiduoUtilizado = valorResiduoUtilizado;
    }

    public void setValorCreditoUtilizado(BigDecimal valorCreditoUtilizado) {
        this.valorCreditoUtilizado = valorCreditoUtilizado;
    }

    private void validarCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            if (filtroExercicioFinal.getAno().compareTo(filtroExercicioInicio.getAno()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício final não pode ser posterior ao exercício inicial.");
            }
        }
        if (vencimentoInicial != null && vencimentoFinal != null) {
            if (vencimentoInicial.compareTo(vencimentoFinal) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final não pode ser maior que a Data de Vencimento Inicial.");
            }
        }
        ve.lancarException();
    }

    public void pesquisar() {
        try {
            validarCamposPreenchidos();
            resultadoConsulta.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            adicionarParametro(consulta);
            consulta.executaConsulta();
            resultadoConsulta.addAll(consulta.getResultados());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadrao());
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito Em Aberto!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }

    public void inicializarFiltros() {
        resultadoConsulta = Lists.newArrayList();
        filtroDivida = null;
        filtroExercicioFinal = null;
        filtroExercicioInicio = null;
        dividaAtiva = false;
        dividaAtivaAzuijada = false;
        dividaDoExercicio = false;
    }

    public void adicionarParametro(ConsultaParcela consulta) {
        consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());

        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
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
        } else if (!dividaDoExercicio && !dividaAtiva && dividaAtivaAzuijada) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 1);
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
    }

    public void imprimirConsulta() throws JRException, IOException {
        Pessoa pessoa = selecionado.getPessoa();
        if (pessoa != null && !resultadoConsulta.isEmpty()) {

            Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadrao());
            EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();

            ImprimeDemonstrativoDebitos imprime = new ImprimeDemonstrativoDebitos("RelatorioConsultaDebitos.jasper", new LinkedList<>(resultadoConsulta));
            imprime.montarMapa();

            imprime.adicionarParametro("BRASAO", imprime.getCaminhoImagem());
            imprime.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            imprime.adicionarParametro("PESSOA", pessoa.getNome());
            imprime.adicionarParametro("CPF_CNPJ", pessoa.getCpf_Cnpj());
            if (enderecoCorrespondencia != null) {
                imprime.adicionarParametro("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
            }
            imprime.adicionarParametro("FILTROS", montarStringFiltros());
            imprime.adicionarParametro("TOTALPORSITUACAO", imprime.getTotalPorSituacao());
            imprime.adicionarParametro("SUBREPORT_DIR", imprime.getCaminho());
            imprime.imprimeRelatorio();
        }
    }

    public String montarStringFiltros() {
        StringBuilder sb = new StringBuilder("");
        if (filtroExercicioInicio != null) {
            sb.append("Exercício Inicial: " + filtroExercicioInicio.getAno());
        } else {
            sb.append("Exercício Inicial: 0");
        }
        sb.append(", ");
        if (filtroExercicioFinal != null) {
            sb.append("Exercício Final: " + filtroExercicioFinal.getAno());
        } else {
            sb.append("Exercício Final: 0");
        }
        sb.append(", ");
        if (vencimentoInicial != null) {
            sb.append("Vencimento Inicial: " + Util.formatterDiaMesAno.format(vencimentoInicial));
        } else {
            sb.append("Vencimento Inicial: 0");
        }
        sb.append(", ");
        if (vencimentoFinal != null) {
            sb.append("Vencimento Final: " + Util.formatterDiaMesAno.format(vencimentoFinal));
        } else {
            sb.append("Vencimento Final: 0");
        }
        sb.append(", ");
        sb.append("Situações Selecionadas: ").append(SituacaoParcela.EM_ABERTO.name());

        if (filtroDivida != null) {
            sb.append(", ");
            sb.append("Dívida Selecionada: ");
            sb.append(filtroDivida.getDescricao());
        }
        return sb.toString();
    }

    private void validarParcelasSelecionadas() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (resultados.length <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione as parcelas desejadas para emissão do DAM.");
        }
        ve.lancarException();
    }

    private void validarParcelasSelecionadasParaDamAgrupado() {
        ValidacaoException ve = new ValidacaoException();
        if (resultados.length > 1) {
            List<ConfiguracaoDAM> configuracoesDAM = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : resultados) {
                Divida dividaDam = contaCorrenteTributariaFacade.getDividaFacade().recuperar(resultadoParcela.getIdDivida());
                if (!configuracoesDAM.contains(dividaDam.getConfiguracaoDAM())) {
                    configuracoesDAM.add(dividaDam.getConfiguracaoDAM());
                }
            }
            if (configuracoesDAM.size() > 1) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("As parcelas selecionadas não podem ser agrupadas, pois os Documentos de Arrecadação Municipal – DAM, possuem configurações diferentes!");
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione as parcelas desejadas para emissão do DAM.");
        }
        ve.lancarException();
    }

    public void prepararImpressaoDam() {
        try {
            validarParcelasSelecionadas();
            valorDam = BigDecimal.ZERO;
            for (ResultadoParcela resultado : resultados) {
                valorDam = valorDam.add((resultado.getValorTotal()));
            }
            Calendar c = DataUtil.ultimoDiaMes(new Date());
            c = DataUtil.ultimoDiaUtil(c, contaCorrenteTributariaFacade.getFeriadoFacade());
            vencimentoDam = c.getTime();
            FacesUtil.executaJavaScript("confirmaDam.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public boolean getMaisDeUmaParcela() {
        return resultados.length > 1;
    }

    public void imprimirDamIndividual() {
        try {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(gerarDamIndividual());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível imprimir o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
    }

    public void imprimirDam(ResultadoParcela parcela) {
        try {
            DAM dam = buscarDAM(parcela.getIdParcela());
            if (dam != null) {
                ImprimeDAM imprimeDAM = new ImprimeDAM();
                imprimeDAM.setGeraNoDialog(true);
                List<DAM> dams = Lists.newArrayList(dam);
                imprimeDAM.imprimirDamUnicoViaApi(dams);
            } else {
                if (parcela.isVencido(new Date())) {
                    FacesUtil.addOperacaoNaoPermitida("Não é possível emitir um DAM vencido, vá até a consulta de débitos efetuar a emissão do DAM atualizado dessa parcela!");
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Não foi encontrado o DAM para essa parcela!");
                }
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível imprimir o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
    }

    public void imprimirDamAgrupado() throws Exception {
        try {
            validarParcelasSelecionadasParaDamAgrupado();
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            List<ResultadoParcela> parcelasDoDam = Lists.newArrayList(resultados);
            Collections.sort(parcelasDoDam, ResultadoParcela.getComparatorByValuePadrao());
            imprimeDAM.imprimirDamCompostoViaApi(gerarDamAgrupado(parcelasDoDam), selecionado.getPessoa());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<DAM> gerarDamIndividual() {
        List<DAM> dams = Lists.newArrayList();
        try {
            Calendar c = DataUtil.ultimoDiaMes(new Date());
            for (ResultadoParcela parcela : resultados) {
                DAM dam = contaCorrenteTributariaFacade.getDamFacade()
                        .gerarDAMUnicoViaApi(sistemaControlador.getUsuarioCorrente(), parcela);
                dams.add(dam);
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
        return dams;
    }

    public DAM gerarDamAgrupado(List<ResultadoParcela> resultadoParcelas) {
        List<ParcelaValorDivida> parcelas = Lists.newArrayList();
        try {
            boolean gerarNovoDam = true;
            BigDecimal valorTotalParcelas = BigDecimal.ZERO;
            for (ResultadoParcela parcela : resultadoParcelas) {
                valorTotalParcelas = valorTotalParcelas.add(parcela.getValorTotal());
            }
            List<DAM> damsAgrupados = contaCorrenteTributariaFacade.getDamFacade().buscarDamsAgrupadosDaParcela(resultadoParcelas.get(0).getIdParcela());
            DAM damAgrupado = null;
            for (DAM dam : damsAgrupados) {
                if (dam.getValorTotal().compareTo(valorTotalParcelas) == 0
                    && verificarParcelasDoDam(dam, resultadoParcelas)
                    && !dam.isVencido()) {
                    gerarNovoDam = false;
                    damAgrupado = dam;
                    break;
                }
            }
            if (gerarNovoDam && damAgrupado == null) {
                for (ResultadoParcela parcela : resultadoParcelas) {
                    ParcelaValorDivida p = contaCorrenteTributariaFacade.getConsultaDebitoFacade().recuperaParcela(parcela.getIdParcela());
                    parcelas.add(p);
                }
                return contaCorrenteTributariaFacade.getDamFacade().geraDAM(parcelas, vencimentoDam);
            } else {
                return damAgrupado;
            }
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error(e.getMessage());
        }
        return null;
    }

    private boolean verificarParcelasDoDam(DAM dam, List<ResultadoParcela> resultadoParcelas) {
        for (ItemDAM itemDAM : dam.getItens()) {
            boolean temParcela = false;
            for (ResultadoParcela resultadoParcela : resultadoParcelas) {
                if (resultadoParcela.getIdParcela().equals(itemDAM.getParcela().getId())) {
                    temParcela = true;
                    break;
                }
            }
            if (!temParcela) {
                return false;
            }
        }
        return !dam.getItens().isEmpty();
    }

    public void abrirDetalhesDAM(ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        parcelaContaCorrenteSelecionada = parcelaContaCorrenteTributaria;
        damSelecionado = contaCorrenteTributariaFacade.getDamFacade().buscarDamPagoDaParcela(parcelaContaCorrenteTributaria.getParcelaValorDivida().getId());
        if (damSelecionado != null && !ContaCorrenteTributaria.Origem.COMPENSACAO.equals(parcelaContaCorrenteTributaria.getOrigem())) {
            itemLoteBaixasDoDamSelecionado = contaCorrenteTributariaFacade.getLoteBaixaFacade().itensLoteBaixaPorDam(damSelecionado);

            itemProcessoDebitosDoDamSelecionado = Lists.newArrayList();
            pagamentosAvulsosDoDamSelecionado = Lists.newArrayList();

            if (itemLoteBaixasDoDamSelecionado.isEmpty()) {
                itemProcessoDebitosDoDamSelecionado = contaCorrenteTributariaFacade.getConsultaDAMFacade().itensProcessoDebitoPorDam(damSelecionado);
            }
            if (itemLoteBaixasDoDamSelecionado.isEmpty() && itemProcessoDebitosDoDamSelecionado.isEmpty()) {
                pagamentosAvulsosDoDamSelecionado = contaCorrenteTributariaFacade.getConsultaDAMFacade().pagamentosAvulsosPorDam(damSelecionado);
            }
            RequestContext.getCurrentInstance().update("formDetalhesDAM");
            RequestContext.getCurrentInstance().execute("detalhesDAM.show()");
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi localizado o DAM pago referente a essa parcela ou esse residual é referente a uma compensação!");
        }
    }

    public Date buscarDataPagamentoDAMSelecioado() {
        if (damSelecionado != null && damSelecionado.getId() != null && damSelecionado.getSituacao().equals(DAM.Situacao.PAGO)) {
            return contaCorrenteTributariaFacade.getDamFacade().recuperaDataPagamentoDAM(damSelecionado);
        }
        return null;
    }

    public List<ConsultaDAMControlador.HistoricoImpressaoDAMSemParcela> getHistorico() {
        if (damSelecionado != null) {
            List<HistoricoImpressaoDAM> historicoParcelas = contaCorrenteTributariaFacade.getDamFacade().listaHistoricoImpressaoDam(damSelecionado);
            List<ConsultaDAMControlador.HistoricoImpressaoDAMSemParcela> historicoDAMs = Lists.newArrayList();
            for (HistoricoImpressaoDAM historicoParcela : historicoParcelas) {
                historicoDAMs.add(new ConsultaDAMControlador.HistoricoImpressaoDAMSemParcela(historicoParcela.getDataOperacao(),
                    historicoParcela.getUsuarioSistema(), historicoParcela.getTipoImpressao()));
            }
            Collections.sort(historicoDAMs);
            return historicoDAMs;
        }
        return Lists.newArrayList();
    }

    public void gerarDebitoDoResidualParcelaSelecionada() {
        try {
            if (parcelaContaCorrenteSelecionada != null) {
                BigDecimal valorDoDebito = parcelaContaCorrenteSelecionada.getValorDiferencaAtualizada();
                if (valorDoDebito.compareTo(BigDecimal.ZERO) > 0) {
                    CalculoContaCorrente calculoContaCorrente = contaCorrenteTributariaFacade.criarCalculoContaCorrente(parcelaContaCorrenteSelecionada, valorDoDebito);
                    calculoContaCorrente.setProcessoCalculo(contaCorrenteTributariaFacade.salvarProcessoCalculoContaCorrente(calculoContaCorrente.getProcessoCalculo()));
                    calculoContaCorrente = calculoContaCorrente.getProcessoCalculo().getCalculoContaCorrente().get(0);

                    contaCorrenteTributariaFacade.gerarDebito(calculoContaCorrente);
                    contaCorrenteTributariaFacade.gerarDAM(calculoContaCorrente);
                    atribuirCalculoAsParcelasUtilizadas(calculoContaCorrente, parcelaContaCorrenteSelecionada);
                    carregarParcelasDoResidual();

                    FacesUtil.addOperacaoRealizada("Débito no valor de " + Util.formataValor(valorDoDebito) + " gerado com sucesso!");
                    FacesUtil.executaJavaScript("confirmaDebito.hide()");
                    FacesUtil.executaJavaScript("detalhesDAM.hide()");
                    FacesUtil.atualizarComponente("Formulario");
                } else {
                    FacesUtil.addOperacaoNaoPermitida("Não é possível gerar débito com valor zero!");
                }
            }
        } catch(Exception e){
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro ao gerar o débito: {}", e);
        }
    }

    private void atribuirCalculoAsParcelasUtilizadas(CalculoContaCorrente calculoContaCorrente, ParcelaContaCorrenteTributaria parcelaContaCorrenteTributaria) {
        if (parcelaContaCorrenteTributaria.getTipoDiferenca().equals(TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO)) {
            if (parcelaContaCorrenteTributaria.getCalculoContaCorrente() == null) {
                parcelaContaCorrenteTributaria.setCalculoContaCorrente(calculoContaCorrente);
                parcelaContaCorrenteTributaria.setValorDiferencaUtilizada(parcelaContaCorrenteTributaria.getValorDiferencaAtualizada());
            }
        }
        contaCorrenteTributariaFacade.salvarParcela(parcelaContaCorrenteTributaria);
        ver();
    }

    public DAM buscarDAM(Long parcela) {
        return contaCorrenteTributariaFacade.getDamFacade().recuperaDAMPeloIdParcela(parcela);
    }

    private void carregarParcelasDoResidual() {
        parcelasDoResidual = contaCorrenteTributariaFacade.buscarParcelasDoResidual(selecionado);
        valorResiduoUtilizado = contaCorrenteTributariaFacade.somarValorResiduoUtilizado(selecionado.getParcelasResiduos());
    }

    public boolean podeRemoverParcela(ParcelaContaCorrenteTributaria parcela) {
        return parcela.getCalculoContaCorrente() == null &&
            ContaCorrenteTributaria.Origem.ARRECADACAO.equals(parcela.getOrigem()) && !parcela.getCompensada();
    }

    public void removerParcelaDaContaCorrente(ParcelaContaCorrenteTributaria parcela) {
        if (podeRemoverParcela(parcela)) {
            selecionado.getParcelas().remove(parcela);
            contaCorrenteTributariaFacade.salvar(selecionado);
        }
    }

    private void validarNovoProcessoCompensacao() {
        ValidacaoException ve = new ValidacaoException();
        if (getSaldoCreditoDisponivel().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Corrente não possui valor de crédito para compensar!");
        }
        ve.lancarException();
    }

    public void criarIrProcessoCompensacao() {
        try {
            validarNovoProcessoCompensacao();

            Compensacao compensacao = new Compensacao();
            compensacao.setPessoa(selecionado.getPessoa());
            Web.poeNaSessao(compensacao);
            FacesUtil.redirecionamentoInterno("/tributario/processo-de-compensacao/novo/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void criarProcessoRestituicao() {
        try {
            validarNovoProcessoRestituicao();
            Restituicao restituicao = contaCorrenteTributariaFacade.getRestituicaoFacade().buscarRestituicaoEmAbertoPorIdPessoa(selecionado.getPessoa().getId());
            if(restituicao == null) {
                restituicao = new Restituicao();
                restituicao.setContribuinte(selecionado.getPessoa());
                Web.navegacao(getUrlAtual(), "/tributario/processo-de-restituicao/novo/", restituicao);
            } else {
               Web.navegacao(getUrlAtual(), "/tributario/processo-de-restituicao/ver/" + restituicao.getId() + "/", restituicao);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovoProcessoRestituicao() {
        ValidacaoException ve = new ValidacaoException();
        if (getSaldoCreditoDisponivel().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Corrente não possui valor de crédito para restituir!");
        }
        ve.lancarException();
    }

    public boolean isTodasParcelasCompensadas() {
        for (ParcelaContaCorrenteTributaria parcela : selecionado.getParcelasCreditos()) {
            if (parcela.getCalculoContaCorrente() == null && !parcela.getCompensada()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        Web.limpaNavegacao();
    }
}
