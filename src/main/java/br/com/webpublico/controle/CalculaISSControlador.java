package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean(name = "calculaISSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaCalculoMensal", pattern = "/tributario/lancamento-de-iss-mensal/listar/", viewId = "/faces/tributario/issqn/calculohomologado/lista.xhtml"),
    @URLMapping(id = "novoCalculoMensal", pattern = "/tributario/lancamento-de-iss-mensal/novo/", viewId = "/faces/tributario/issqn/calculohomologado/geracalculo.xhtml"),
    @URLMapping(id = "verCalculoMensal", pattern = "/tributario/lancamento-de-iss-mensal/ver/#{calculaISSControlador.id}/", viewId = "/faces/tributario/issqn/calculohomologado/visualizar.xhtml"),
    @URLMapping(id = "listaCalculoFixo", pattern = "/tributario/lancamento-de-iss-fixo/listar/", viewId = "/faces/tributario/issqn/calculofixo/lista.xhtml"),
    @URLMapping(id = "novoCalculoFixo", pattern = "/tributario/lancamento-de-iss-fixo/novo/", viewId = "/faces/tributario/issqn/calculofixo/geracalculo.xhtml"),
    @URLMapping(id = "verCalculoFixo", pattern = "/tributario/lancamento-de-iss-fixo/ver/#{calculaISSControlador.id}/", viewId = "/faces/tributario/issqn/calculofixo/visualizar.xhtml"),
    @URLMapping(id = "listaCalculoEstimado", pattern = "/tributario/lancamento-de-iss-estimado/listar/", viewId = "/faces/tributario/issqn/calculoestimado/lista.xhtml"),
    @URLMapping(id = "novoCalculoEstimado", pattern = "/tributario/lancamento-de-iss-estimado/novo/", viewId = "/faces/tributario/issqn/calculoestimado/geracalculo.xhtml"),
    @URLMapping(id = "verCalculoEstimado", pattern = "/tributario/lancamento-de-iss-estimado/ver/#{calculaISSControlador.id}/", viewId = "/faces/tributario/issqn/calculoestimado/visualizar.xhtml"),
})
public class CalculaISSControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CalculaISSControlador.class);
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private DAMFacade DAMFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private GeraValorDividaISS geraDebito;
    @EJB
    private GeraValorDividaMultaAcessoria geraDebitoMultaAcessoria;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private CadastroEconomico cadastroEconomico;
    private BigDecimal valorBaseHomologado;
    private ConverterAutoComplete converterCE;
    private ProcessoCalculoISS processoCalculoISS;
    private List<ProcessoCalculoISS> processosCalculosISS;
    private Integer mesReferencia;
    private Exercicio exercicio;
    private ConverterExercicio converterExercicio;
    private boolean dividaefetivada = false;
    private List<Servico> servicos;
    private ValorDivida valorDivida;
    private ParcelaValorDivida parcelaValorDivida;
    private BigDecimal taxaSobreIss;
    private boolean ausenciaMovimento;
    private boolean habilitaValorTaxaSobreISS;
    private Long subDivida;
    private Long sequenciaLancamento;
    private List<ItemCalculoIss> itensCalculoISS;
    private List lista;
    private Integer quantidadeMaximaRegistros;
    private String filtro;
    private CalculoISS selecionado;
    private CalculoISSEstorno estorno;
    private List<ParcelaValorDivida> parcelas;
    private List<ParcelaValorDivida> parcelasMultaAcessoria;
    private String observacoes;
    private Long id;
    private Boolean imprimeMultaAcessoria;
    private Boolean imprimeDamCalculoISS;
    private Boolean imprimeDeclaracao;
    private ProcessoCalculoMultaAcessoria processoMultaAcessoria;
    private CalculoMultaAcessoria calculoMultaAcessoria;
    private String loginAutorizacao = "";
    private String senhaAutorizacao = "";
    private boolean issPessoaFisica = false;
    private List<CalculoISS> calculos;
    private List<ProcessoCalculoMultaAcessoria> itemProcessoCalculoMultaAcessoria;
    private List<ResultadoParcela> resultadoParcela;
    private List<ResultadoParcela> resultadoParcelaMultaAcessoria;
    private boolean lancarMulta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParcelaValorDivida> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaValorDivida> parcelas) {
        this.parcelas = parcelas;
    }

    public List<ParcelaValorDivida> getParcelasMultaAcessoria() {
        return parcelasMultaAcessoria;
    }

    public void setParcelasMultaAcessoria(List<ParcelaValorDivida> parcelasMultaAcessoria) {
        this.parcelasMultaAcessoria = parcelasMultaAcessoria;
    }

    public CalculoMultaAcessoria getCalculoMultaAcessoria() {
        return calculoMultaAcessoria;
    }

    public void setCalculoMultaAcessoria(CalculoMultaAcessoria calculoMultaAcessoria) {
        this.calculoMultaAcessoria = calculoMultaAcessoria;
    }

    public List<ItemCalculoIss> getItensCalculoISS() {
        return itensCalculoISS;
    }

    public void setItensCalculoISS(List<ItemCalculoIss> itensCalculoISS) {
        this.itensCalculoISS = itensCalculoISS;
    }

    public CalculoISSEstorno getEstorno() {
        return estorno;
    }

    public void setEstorno(CalculoISSEstorno estorno) {
        this.estorno = estorno;
    }

    public Integer getQuantidadeMaximaRegistros() {
        return quantidadeMaximaRegistros;
    }

    public void setQuantidadeMaximaRegistros(Integer quantidadeMaximaRegistros) {
        this.quantidadeMaximaRegistros = quantidadeMaximaRegistros;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Long getSubDivida() {
        return subDivida;
    }

    public void setSubDivida(Long subDivida) {
        this.subDivida = subDivida;
    }

    public Long getSequenciaLancamento() {
        return sequenciaLancamento;
    }

    public void setSequenciaLancamento(Long sequenciaLancamento) {
        this.sequenciaLancamento = sequenciaLancamento;
    }

    public boolean isHabilitaValorTaxaSobreISS() {
        return habilitaValorTaxaSobreISS;
    }

    public void setHabilitaValorTaxaSobreISS(boolean habilitaValorTaxaSobreISS) {
        this.habilitaValorTaxaSobreISS = habilitaValorTaxaSobreISS;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getLoginAutorizacao() {
        return loginAutorizacao;
    }

    public void setLoginAutorizacao(String loginAutorizacao) {
        this.loginAutorizacao = loginAutorizacao;
    }

    public String getSenhaAutorizacao() {
        return senhaAutorizacao;
    }

    public void setSenhaAutorizacao(String senhaAutorizacao) {
        this.senhaAutorizacao = senhaAutorizacao;
    }

    public boolean isIssPessoaFisica() {
        return issPessoaFisica;
    }

    public void setIssPessoaFisica(boolean issPessoaFisica) {
        this.issPessoaFisica = issPessoaFisica;
    }

    public List<CalculoISS> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoISS> calculos) {
        this.calculos = calculos;
    }

    public List<ProcessoCalculoISS> getProcessosCalculosISS() {
        return processosCalculosISS;
    }

    public boolean habilitaValorTaxaSobreISS() {
        boolean retorno = Boolean.FALSE;
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario != null && configuracaoTributario.getTributoTaxaSobreISS() != null) {
            retorno = Boolean.TRUE;
        }
        return retorno;
    }

    public void novo() {
        imprimeMultaAcessoria = new Boolean(false);
        imprimeDamCalculoISS = new Boolean(false);
        imprimeDeclaracao = new Boolean(false);
        cadastroEconomico = null;
        if (exercicio == null) {
            exercicio = sistemaFacade.getExercicioCorrente();
        }
        processoCalculoISS = null;
        valorBaseHomologado = null;
        dividaefetivada = false;
        servicos = new ArrayList<>();
        taxaSobreIss = BigDecimal.ZERO;
        habilitaValorTaxaSobreISS = habilitaValorTaxaSobreISS();
        subDivida = 0l;
        sequenciaLancamento = Long.valueOf(1);
        selecionado = new CalculoISS();
        selecionado.setUsuarioLancamento(sistemaFacade.getUsuarioCorrente());
        ausenciaMovimento = false;
        observacoes = null;
        calculoMultaAcessoria = new CalculoMultaAcessoria();
        processoMultaAcessoria = new ProcessoCalculoMultaAcessoria();
        processosCalculosISS = Lists.newArrayList();
        itemProcessoCalculoMultaAcessoria = Lists.newArrayList();
    }

    @URLAction(mappingId = "novoCalculoMensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoHomologado() {
        Calendar c = Calendar.getInstance();
        mesReferencia = c.get(Calendar.MONTH) + 1;
        novo();
    }

    @URLAction(mappingId = "novoCalculoFixo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoFixo() {
        novo();
    }

    @URLAction(mappingId = "novoCalculoEstimado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstimado() {
        novo();
    }

    public Converter getConverterCE() {
        if (converterCE == null) {
            converterCE = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCE;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    private void selecionarCadastroEconomico() {
        if (cadastroEconomico != null)
            cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
    }

    public void selecionarCadastroEconomicoMensal(SelectEvent e) {
        calculos = null;
        cadastroEconomico = (CadastroEconomico) e.getObject();
        servicos = cadastroEconomicoFacade.listaServicoPorCE(cadastroEconomico);
        recuperarSubDividaESequenciaLancamento();
    }

    public void selecionarCadastroEconomicoFixo(SelectEvent e) {
        cadastroEconomico = (CadastroEconomico) e.getObject();
        verificarTipoDePessoaDoCMC();
        recuperarSubDividaESequenciaLancamento();
    }

    public void selecionarCadastroEconomicoEstimado(SelectEvent e) {
        cadastroEconomico = (CadastroEconomico) e.getObject();
        selecionarCadastroEconomico();
        recuperarSubDividaESequenciaLancamento();
    }

    public List<CadastroEconomico> completaCadastroEconomicoIssHomologado(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPorPessoaTipoIss(parte.trim(), TipoIssqn.MENSAL, TipoIssqn.SUBLIMITE_ULTRAPASSADO, TipoIssqn.SIMPLES_NACIONAL);
    }

    public List<CadastroEconomico> completaCadastroEconomicoIssEstimado(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPorPessoaTipoIss(parte.trim(), TipoIssqn.ESTIMADO);
    }

    public List<CadastroEconomico> completaCadastroEconomicoIssFixo(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPorPessoaTipoIss(parte.trim(), TipoIssqn.FIXO);
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public BigDecimal getValorBaseHomologado() {
        return valorBaseHomologado;
    }

    public void setValorBaseHomologado(BigDecimal valorBaseHomologado) {
        this.valorBaseHomologado = valorBaseHomologado;
    }

    public ProcessoCalculoISS getProcessoCalculoISS() {
        return processoCalculoISS;
    }

    public void setProcessoCalculoISS(ProcessoCalculoISS processoCálculoISS) {
        this.processoCalculoISS = processoCálculoISS;
    }

    public boolean isDividaefetivada() {
        return dividaefetivada;
    }

    public void setDividaefetivada(boolean dividaefetivada) {
        this.dividaefetivada = dividaefetivada;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Integer mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            lista.add(new SelectItem(mes.getNumeroMes(), mes.getDescricao()));
        }
        return lista;
    }

    public void declararAusenciaMovimento() {
        try {
            podeLancarAusencia();
            criarCalculoParaLancamentos();
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCalculosLancamento() {
        ValidacaoException ve = new ValidacaoException();
        if (calculos.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe e Adicione os Calculos de ISS à serem Efetivados");
        }
        ve.lancarException();
    }


    public void prepararCalculoIssHomologado() {
        try {
            lancarMulta = false;
            if (calculos == null) {
                calculos = Lists.newArrayList();
            }
            validarCalculosLancamento();
            for (CalculoISS calculo : calculos) {
                if (calculaISSFacade.isPassivelMultaAcessoria(calculo.getProcessoCalculoISS())) {
                    FacesUtil.executaJavaScript("dlgConfirmaMulta.show()");
                    return;
                }
            }
            calcularIssHomologado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void calcularIssHomologado(Boolean lancarMulta) {
        this.lancarMulta = lancarMulta;
        calcularIssHomologado();
    }

    public void calcularIssHomologado() {
        try {
            processosCalculosISS = Lists.newArrayList();
            for (CalculoISS calculo : calculos) {
                calculo.setUsuarioLancamento(sistemaFacade.getUsuarioCorrente());
                calculo.setObservacao(observacoes);
                ProcessoCalculoISS processo = calculaISSFacade.salva(calculo.getProcessoCalculoISS());
                processo = gerarValorDivida(processo);
                if (calculo.getValorCalculado().compareTo(BigDecimal.ZERO) == 0) {
                    imprimeDeclaracao = true;
                }
                processosCalculosISS.add(processo);
            }
            lancarMultaAcessoria();
            FacesUtil.addOperacaoRealizada("Cálculo efetivado com sucesso!");
            imprimeDamCalculoISS = true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void lancarMultaAcessoria() {
        if (lancarMulta) {
            for (ProcessoCalculoISS processoCalculo : processosCalculosISS) {
                ProcessoCalculoMultaAcessoria processoCalculoMultaAcessoria = calculaISSFacade.processoCalculoMultaAcessoria(processoCalculo.getCalculos().get(0));
                if (processoCalculoMultaAcessoria != null) {
                    itemProcessoCalculoMultaAcessoria.add(processoCalculoMultaAcessoria);
                }
            }
            for (ProcessoCalculoMultaAcessoria processoCalculoMultaAcessoria : itemProcessoCalculoMultaAcessoria) {
                gerarCalculoMultaAcessoria(processoCalculoMultaAcessoria);
            }
            if (!itemProcessoCalculoMultaAcessoria.isEmpty()) {
                FacesUtil.addOperacaoRealizada("Aplicação de Multa Acessória por Não Declaração de ISS!");
            }
        }
    }

    public List<ProcessoCalculoMultaAcessoria> getItemProcessoCalculoMultaAcessoria() {
        return itemProcessoCalculoMultaAcessoria;
    }

    private void gerarCalculoMultaAcessoria(ProcessoCalculoMultaAcessoria processoCalculo) {
        gerarValorDividaMultaAcessoria(processoCalculo);
        imprimeDamCalculoISS = true;
        imprimeMultaAcessoria = true;
    }

    private boolean validarLancamentosISS() {
        boolean retorno = true;
        if (validaAusenciaMovimento()) {
            FacesUtil.executaJavaScript("ausenciaMovimento.show()");
            retorno = false;
        } else if (jahLancado(TipoIssqn.MENSAL)) {
            FacesUtil.executaJavaScript("confirmaCalculo.show()");
            retorno = false;
        }
        return retorno;
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (mesReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o mês de referência!");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício!");
        }
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro Econômico!");
        }
        ve.lancarException();
    }

    private void validarEnquadramentoFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico.getEnquadramentoVigente() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro não possui enquadramento fiscal vigente!");
        } else {
            if (cadastroEconomico.getEnquadramentoVigente().isIsento()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro é isento de ISSQN!");
            }
            if (isDataDoLancamentoPosteriorADataAtual()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar lançamentos futuros. Verifique o Mês de Referência e o Exercício!");
            }
            if (mesAnoReferenciaAnteriorMesAnoDataAbertura()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar lançamentos para a referência informada, pois a mesma é anterior a data de abertura do Cadastro Econômico!");
            }
            if (verificarAcaoFiscalParaCadastroEconomico()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Este C.M.C possui uma ação fiscal em execução na competência informada!");
            }
        }
        ve.lancarException();
    }

    private boolean verificarAcaoFiscalParaCadastroEconomico() {
        Calendar data = Calendar.getInstance();
        data.setTime(new Date());
        data.set(Calendar.DAY_OF_MONTH, 1);
        data.set(Calendar.MONTH, mesReferencia - 1);
        data.set(Calendar.YEAR, exercicio.getAno());
        return calculaISSFacade.hasAcaoFiscalParaCadastroEconomico(cadastroEconomico, data.getTime());
    }

    private boolean isDataDoLancamentoPosteriorADataAtual() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(exercicio.getAno(), mesReferencia - 1, 1);
        Date dataLancamento = calendar.getTime();
        Date dataAtual = SistemaFacade.getDataCorrente();
        if (dataLancamento.after(dataAtual)) {
            return true;
        }
        return false;
    }

    private boolean mesAnoReferenciaAnteriorMesAnoDataAbertura() {
        Calendar dataAberturaCMC = Calendar.getInstance();
        dataAberturaCMC.setTime(cadastroEconomico.getAbertura());
        dataAberturaCMC.set(Calendar.DAY_OF_MONTH, 1);
        Calendar dataLancamento = Calendar.getInstance();
        dataLancamento.set(Calendar.DAY_OF_MONTH, 1);
        dataLancamento.set(Calendar.MONTH, mesReferencia - 1);
        dataLancamento.set(Calendar.YEAR, exercicio.getAno());
        return DataUtil.dataSemHorario(dataAberturaCMC.getTime()).compareTo(DataUtil.dataSemHorario(dataLancamento.getTime())) > 0;
    }

    private boolean mesAnoDataLancamentoAnteriorMesAnoReferencia() {
        Calendar dataLancamento = Calendar.getInstance();
        dataLancamento.setTime(sistemaFacade.getDataCorrente());
        dataLancamento.set(Calendar.DAY_OF_MONTH, 1);
        Calendar dataReferencia = Calendar.getInstance();
        dataReferencia.set(Calendar.DAY_OF_MONTH, 1);
        dataReferencia.set(Calendar.MONTH, mesReferencia - 1);
        dataReferencia.set(Calendar.YEAR, exercicio.getAno());
        return DataUtil.dataSemHorario(dataLancamento.getTime()).compareTo(DataUtil.dataSemHorario(dataReferencia.getTime())) < 0;
    }

    public void calcularIssFixo() {
        mesReferencia = -1;
        try {
            selecionarCadastroEconomico();
            validarCalculoFixo();
            processoCalculoISS = calculaISSFacade.calcularIssFixo(cadastroEconomico, exercicio, selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public boolean cmcPessoaFisica() {
        if (selecionado.getCadastroEconomico().getPessoa() instanceof PessoaFisica) {
            return true;
        }
        return false;
    }

    public void salvaFixo() throws IOException, JRException {
        selecionarCadastroEconomico();
        processoCalculoISS = calculaISSFacade.salva(processoCalculoISS);
        processoCalculoISS = gerarValorDivida(processoCalculoISS);
        FacesUtil.addOperacaoRealizada("Cálculo efetivado com sucesso!");
    }

    public void calcularIssEstimado() {
        try {
            selecionarCadastroEconomico();
            validarCalculoEstimado();
            if (TipoPeriodoValorEstimado.ANUAL.equals(cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado())) {
                mesReferencia = -1;
            }
            if (jahLancado(TipoIssqn.ESTIMADO)) {
                RequestContext.getCurrentInstance().execute("confirmaCalculo.show()");
            } else {
                processoCalculoISS = calculaISSFacade.calcularIssEstimado(cadastroEconomico, exercicio, mesReferencia);
                FacesUtil.addOperacaoRealizada("Cálculo realizado com sucesso! Efetive o cálculo para gerar o DAM!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void salvaEstimado() {
        processoCalculoISS = calculaISSFacade.salva(processoCalculoISS);
        processoCalculoISS = gerarValorDivida(processoCalculoISS);
        FacesUtil.addOperacaoRealizada("Cálculo efetivado com sucesso!");
    }

    public ProcessoCalculoISS gerarValorDivida(ProcessoCalculoISS processo) {
        try {
            geraDebito.geraDebito(processo);
            geraDebito.getDamFacade().geraDAM(processo.getCalculos().get(0));
            processo = calculaISSFacade.salva(processo);
            selecionarCadastroEconomico();
            dividaefetivada = true;
        } catch (ExcecaoNegocioGenerica e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Erro não tratado! " + e.getMessage());
        }
        return processo;
    }

    public void gerarValorDividaMultaAcessoria(ProcessoCalculoMultaAcessoria processo) {
        try {
            processo = calculaISSFacade.salvarProcessoCalculoMultaAcessoria(processo);
            geraDebitoMultaAcessoria.geraDebito(processo);
            geraDebitoMultaAcessoria.getDamFacade().geraDAM(processo.getCalculos().get(0));
            calculoMultaAcessoria = processo.getCalculos().get(0);

        } catch (ExcecaoNegocioGenerica e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoRealizada(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoRealizada("Erro não tratado! " + e.getMessage());
        }
    }

    public boolean podeImprimirDeclaracaoAusenciaMovimentoNoVisualizar() {
        try {
            CalculoISS calculo = processoCalculoISS.getCalculos().get(0);
            if (calculo.getAusenciaMovimento()) {
                return true;
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
        return false;
    }

    private void validarCalculoISSMensal() {
        ValidacaoException ve = new ValidacaoException();
        if (getServicos().isEmpty() || getServicos() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O C.M.C. não foi informado ou nenhum Serviço foi encontrado!");
        } else {
            for (Servico s : getServicos()) {
                if (s.getFaturamento() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do Faturamento Mensal do serviço '" + s.getNome() + "' não foi informado!");
                } else if (s.getFaturamento().compareTo(BigDecimal.ZERO) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do Faturamento Mensal do serviço '" + s.getNome() + "' não pode ser menor que zero!");
                }
                if (s.getValorBase() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da Base de Cálculo do serviço '" + s.getNome() + "' não foi informado!");
                } else if (s.getValorBase().compareTo(BigDecimal.ZERO) < 0 || s.getValorBase().compareTo(s.getFaturamento()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da Base de Cálculo do serviço '" + s.getNome() + "' não pode ser menor que zero e não pode ser maior que o valor do Faturamento Mensal!");
                } else {
                    if (s.getAliquotaISSHomologado() == null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A alíquota de ISS Mensal do serviço '" + s.getNome() + "' não foi informada!");
                    } else if (s.getAliquotaISSHomologado().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A alíquota de ISS Mensal do serviço '" + s.getNome() + "' não pode ser negativa!");
                    }
                }
            }
        }
        if (cadastroEconomico == null || cadastroEconomico.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o C.M.C. antes de continuar!");
        } else if (cadastroEconomico.getEnquadramentoVigente() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro não possui enquadramento fiscal vigente!");
        }
        if (exercicio == null || exercicio.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Exercício antes de continuar!");
        }
        if (habilitaValorTaxaSobreISS) {
            if (taxaSobreIss == null || taxaSobreIss.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da TSA não deve ser menor que zero!");
            }
        }
        if (mesReferencia == null || mesReferencia.intValue() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Mês de Referência antes de continuar!");
        }
        if (configuracaoTributarioFacade.retornaUltimo() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há Configuração do Tributário válida!");
        } else if (configuracaoTributarioFacade.retornaUltimo().getDividaISSHomologado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há configuração de Dívida de ISS Mensal nas Configurações do Tributário!");
        } else if (configuracaoTributarioFacade.retornaUltimo().getDividaISSHomologado().getConfiguracaoDAM() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há Configuração de DAM para a dívida " + configuracaoTributarioFacade.retornaUltimo().getDividaISSHomologado());
        }
        ve.lancarException();
    }

    private void validarCalculoFixo() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null || cadastroEconomico.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o C.M.C. antes de continuar!");
        } else {
            if (cadastroEconomico != null && cadastroEconomico.getPessoa() instanceof PessoaJuridica) {
                if (selecionado.getQuantidadeProfissionais() <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico não possui sócios!");
                }
                if (selecionado.getQtdeUFMProfissionalSocio() == null || selecionado.getQtdeUFMProfissionalSocio().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Verifique os parâmetros de ISS nas configurações!");
                }
            }
        }
        if (exercicio == null || exercicio.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Exercício antes de continuar!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void verificarTipoDePessoaDoCMC() {
        if (cadastroEconomico.getPessoa() instanceof PessoaFisica) {
            issPessoaFisica = true;
            selecionado.setQuantidadeProfissionais(null);
            selecionado.setQtdeUFMProfissionalSocio(null);
        } else {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            selecionado.setQuantidadeProfissionais(cadastroEconomicoFacade.numeroDeSocios(cadastroEconomico));
            selecionado.setQtdeUFMProfissionalSocio(configuracaoTributario.getQtdeUfmIssFixoProfSuperior());
            issPessoaFisica = false;
        }
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        this.setCadastroEconomico((CadastroEconomico) obj);
        servicos = cadastroEconomicoFacade.listaServicoPorCE(this.cadastroEconomico);
        recuperarSubDividaESequenciaLancamento();
    }

    public void selecionarCadastroEconomicoMENSAL(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        this.setCadastroEconomico((CadastroEconomico) obj);
        servicos = cadastroEconomicoFacade.listaServicoPorCE(this.cadastroEconomico);
        recuperarSubDividaESequenciaLancamento();
    }

    public ComponentePesquisaGenerico getTipoISSMENSAL() {
        return componentePesquisa(TipoIssqn.MENSAL);
    }

    public ComponentePesquisaGenerico getTipoISSFIXO() {
        return componentePesquisa(TipoIssqn.FIXO);
    }

    public ComponentePesquisaGenerico getTipoISSESTIMADO() {
        return componentePesquisa(TipoIssqn.ESTIMADO);
    }

    private ComponentePesquisaGenerico componentePesquisa(TipoIssqn tipoISS) {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        componente.setTipoIss(tipoISS);
        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    private void validarCalculoEstimado() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null || cadastroEconomico.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o C.M.C. antes de continuar!");
        } else if (cadastroEconomico.getEnquadramentoVigente() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro não possui enquadramento fiscal vigente!");
        } else {
            if (!TipoIssqn.ESTIMADO.equals(cadastroEconomico.getEnquadramentoVigente().getTipoIssqn())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de ISSQN do C.M.C. informado não é Estimado!");
            }
            if (cadastroEconomico.getEnquadramentoVigente().getIssEstimado() == null || cadastroEconomico.getEnquadramentoVigente().getIssEstimado().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado o valor do ISS Estimado no C.M.C!");
            }
            if (cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado um Tipo de Período para o ISS Estimado no C.M.C. selecionado!");
            }
        }
        if (exercicio == null || exercicio.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Exercício antes de continuar!");
        }
        if (configuracaoTributarioFacade.retornaUltimo() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há Configuração do Tributário válida!");
        } else if (configuracaoTributarioFacade.retornaUltimo().getDividaISSEstimado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há configuração de Dívida de ISS Estimado nas Configurações do Tributário!");
        } else if (configuracaoTributarioFacade.retornaUltimo().getDividaISSEstimado().getConfiguracaoDAM() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há Configuração de DAM para a dívida " + configuracaoTributarioFacade.retornaUltimo().getDividaISSEstimado());
        }
        if (cadastroEconomico != null && cadastroEconomico.getEnquadramentoVigente() != null && cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado() != null
            && TipoPeriodoValorEstimado.ANUAL.equals(cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado())) {
            Calendar abertura = Calendar.getInstance();
            abertura.setTime(cadastroEconomico.getAbertura());
            Calendar dataLancamento = Calendar.getInstance();
            dataLancamento.setTime(sistemaFacade.getDataCorrente());
            if (dataLancamento.get(Calendar.YEAR) < abertura.get(Calendar.YEAR)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O ano da Data de Lançamento é menor que o ano de abertura do Cadastro Econômico!");
            }
            if (exercicio != null && exercicio.getAno() < abertura.get(Calendar.YEAR)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício é menor que o ano de abertura do Cadastro Econômico!");
            }
        }
        if (cadastroEconomico != null && cadastroEconomico.getEnquadramentoVigente() != null && cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado() != null
            && TipoPeriodoValorEstimado.MENSAL.equals(cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado())) {
            if (mesAnoDataLancamentoAnteriorMesAnoReferencia()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar lançamentos futuros!");
            }
            if (mesAnoReferenciaAnteriorMesAnoDataAbertura()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A referência informada é anterior a data de abertura do Cadastro Econômcio!");
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void setaValorServico(Servico servico) {
        servicos.set(servicos.indexOf(servico), servico);
    }

    public void imprimirDAMHomologado() throws Exception {
        List<DAM> dams = Lists.newArrayList();
        for (ProcessoCalculoISS processo : processosCalculosISS) {
            for (CalculoISS calculoISS : processo.getCalculos()) {
                if (calculoISS.getValorCalculado().compareTo(BigDecimal.ZERO) > 0) {
                    DAM dam = DAMFacade.recuperaDAMpeloCalculo(calculoISS);
                    if (dam != null) {
                        dams.add(dam);
                    }
                }
            }
        }
        if (!dams.isEmpty()) {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.adicionarParametro("OBSERVACOES", observacoes);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } else {
            FacesUtil.addWarn("Atenção!", "Os débitos para o cadastro não foram gerados, pois o cálculo foi de Ausência de Movimento.");
        }
    }

    public void imprimirDeclaracaoAusenciaMovimento() {
        List<DAM> dams = Lists.newArrayList();
        for (ProcessoCalculoISS processoCalculoISS : processosCalculosISS) {
            if (processoCalculoISS.getCalculos().get(0).getValorCalculado().compareTo(BigDecimal.ZERO) == 0) {
                dams.add(DAMFacade.recuperaDAMpeloCalculo(processoCalculoISS.getCalculos().get(0)));
            }
        }
        if (dams != null && !dams.isEmpty()) {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.adicionarParametro("OBSERVACOES", processosCalculosISS.get(0).getObservacoes());
            try {
                imprimeDAM.imprimirDamUnicoViaApi(dams);
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            }
        } else {
            FacesUtil.addError("Não é possível imprimir o DAM!", "");
        }
    }

    public void imprimirDAM() throws Exception {
        CalculoISS calculo = processoCalculoISS.getCalculos().get(0);
        DAM dam = DAMFacade.recuperaDAMpeloCalculo(calculo);
        if (dam != null) {
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.adicionarParametro("OBSERVACOES", processoCalculoISS.getObservacoes());
            imprimeDAM.imprimirDamUnicoViaApi(dam);
        } else {
            FacesUtil.addError("Não é possível imprimir o DAM!", "");
        }
    }

    public void imprimirDamMultaAcessoria() {
        try {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            List<DAM> dams = Lists.newArrayList();
            for (ProcessoCalculoISS processos : processosCalculosISS) {
                CalculoMultaAcessoria calculo = calculaISSFacade.recuperarCalculoMultaAcessoria(processos);
                if (calculo != null) {
                    dams.add(DAMFacade.recuperaDAMpeloCalculo(calculo));
                }
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.adicionarParametro("SUBREPORT_DIR", subReport);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public boolean jahLancado(TipoIssqn tipoIssqn) {
        valorDivida = null;
        List<ValorDivida> valores = null;
        if ((cadastroEconomico != null && cadastroEconomico.getId() != null)
            && (exercicio != null && exercicio.getId() != null)) {
            valores = calculaISSFacade.jaLancado(cadastroEconomico, mesReferencia, exercicio, tipoIssqn);
            if (valores != null && valores.size() > 0) {
                valorDivida = calculaISSFacade.recuperarValorDivida(valores.get(0));
                if (valorDivida.getParcelaValorDividas() == null || valorDivida.getParcelaValorDividas().isEmpty()) {
                    return false;
                }
                parcelaValorDivida = valorDivida.getParcelaValorDividas().get(0);
            }
        }
        return valorDivida != null;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public String getVencimentoDaDivida() {
        if (parcelaValorDivida != null && parcelaValorDivida.getVencimento() != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(parcelaValorDivida.getVencimento());
        }
        return "";
    }

    public String getValorDaDivida() {
        if (valorDivida != null && valorDivida.getValor() != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return df.format(valorDivida.getValor());
        }
        return "";
    }

    public List<ResultadoParcela> getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(List<ResultadoParcela> resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public List<ResultadoParcela> getResultadoParcelaMultaAcessoria() {
        return resultadoParcelaMultaAcessoria;
    }

    public void setResultadoParcelaMultaAcessoria(List<ResultadoParcela> resultadoParcelaMultaAcessoria) {
        this.resultadoParcelaMultaAcessoria = resultadoParcelaMultaAcessoria;
    }

    public boolean isBoqueiaMesRefencia() {
        try {
            return cadastroEconomico != null &&
                cadastroEconomico.getEnquadramentoVigente() != null &&
                cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado() != null &&
                TipoPeriodoValorEstimado.ANUAL.equals(cadastroEconomico.getEnquadramentoVigente().getTipoPeriodoValorEstimado());
        } catch (Exception e) {
            return false;
        }
    }

    public void calcularValorBase(Servico servico, int index) {
        if (servico.getAliquotaISSHomologado().compareTo(BigDecimal.ZERO) < 0) {
            servico.setAliquotaISSHomologado(servico.getAliquotaISSHomologado().multiply(new BigDecimal("-1")));
        }
        if (servico.getPermiteDeducao() != null && servico.getPermiteDeducao()) {
            servico.setValorBase(servico.getFaturamento().subtract(servico.getFaturamento().multiply(servico.getPercentualDeducao().divide(new BigDecimal(100)))));
        } else {
            servico.setValorBase(servico.getFaturamento());
        }
        if (servico.getValorBase() != null) {
            servico.setValorCalculado(servico.getValorBase().multiply(servico.getAliquotaISSHomologado().divide(new BigDecimal(100))));

        }
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:" + index + ":valorCalculado");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:" + index + ":valorBase");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalValorCalculado");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalBaseCalculo");
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalFaturamento");
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabelaServicos:" + index + ":valorBase')");
    }

    public void atualizarCalculoValorBase(Servico servico, int index) {
        if (servico.getValorBase() != null) {
            servico.setValorCalculado(servico.getValorBase().multiply(servico.getAliquotaISSHomologado().divide(new BigDecimal(100))));
        }
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:" + index + ":valorCalculado");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:" + index + ":valorCalculado");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:" + index + ":valorBase");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalValorCalculado");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalBaseCalculo");
        FacesUtil.atualizarComponente("Formulario:tabelaServicos:totalFaturamento");
    }

    public String setaFoco(Servico servico) {
        return "setaFoco(Formulario:tabelaServicos:" + (servicos.indexOf(servico) + 1) + ":valorfaturamento)";
    }

    public BigDecimal getTaxaSobreIss() {
        return taxaSobreIss;
    }

    public void setTaxaSobreIss(BigDecimal taxaSobreIss) {
        this.taxaSobreIss = taxaSobreIss;
    }

    public boolean validaAusenciaMovimento() {
        boolean retorno = false;
        BigDecimal totalValorBase = BigDecimal.ZERO;
        BigDecimal totalFaturamento = BigDecimal.ZERO;
        for (Servico s : getServicos()) {
            totalValorBase = totalValorBase.add(s.getValorBase());
            totalFaturamento = totalFaturamento.add(s.getFaturamento());
        }
        if (totalValorBase.compareTo(BigDecimal.ZERO) <= 0 && totalFaturamento.compareTo(BigDecimal.ZERO) <= 0) {
            retorno = true;
        }
        return retorno;
    }

    private void podeLancarAusencia() {
        ValidacaoException ve = new ValidacaoException();
        if (calculaISSFacade.temLancamentoNaReferencia(mesReferencia, exercicio, cadastroEconomico)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe lançamento para esta competência. A declaração de ausência de movimento não pode ser realizada!");
        }
        ve.lancarException();
    }

    public List<CalculoISS> getLista() {
        if (lista != null) {
            Collections.sort(lista, new Comparator<CalculoISS>() {
                @Override
                public int compare(CalculoISS o1, CalculoISS o2) {
                    Integer x = o1.getCadastroEconomico().getPessoa().compareTo(o2.getCadastroEconomico().getPessoa());
                    if (x == 0) {
                        x = o1.getProcessoCalculoISS().getMesReferencia().compareTo(o2.getProcessoCalculoISS().getMesReferencia());
                    }
                    return x;
                }
            });
        }
        return lista;
    }

    public void recuperarSubDividaESequenciaLancamento() {
        if (exercicio != null && mesReferencia != null) {
            subDivida = calculaISSFacade.buscarSubDividaCalculo(cadastroEconomico, exercicio, mesReferencia);
            sequenciaLancamento = calculaISSFacade.buscarSequenciaDeLancamentoDoCalculoIssEstimadoMensalOuHomologado(cadastroEconomico, exercicio, mesReferencia, TipoCalculoISS.MENSAL);
        }
    }

    public List<ItemCalculoIss> itensDoCalculo(CalculoISS calculo) {
        return calculaISSFacade.itensCalculoISS(calculo);
    }

    public void pesquisar() {
        lista = calculaISSFacade.lista(filtro, quantidadeMaximaRegistros);
    }

    public List<CalculoISS> lista() {
        return calculaISSFacade.lista();
    }

    public void editar() {
        try {
            parcelas = new ArrayList<>();
            selecionado = (CalculoISS) Web.pegaDaSessao(CalculoISS.class);
            if (selecionado == null) {
                selecionado = calculaISSFacade.recuperarID(id);
            }
            this.parcelas = calculaISSFacade.buscarParcelasCalculo(selecionado);
            this.processoCalculoISS = calculaISSFacade.recarregar(selecionado.getProcessoCalculoISS());
            this.exercicio = this.processoCalculoISS.getExercicio();
            this.observacoes = processoCalculoISS.getObservacoes();
            this.valorDivida = new ValorDivida();
            this.parcelaValorDivida = new ParcelaValorDivida();
            this.lista = new ArrayList<>();
            servicos = cadastroEconomicoFacade.listaServicoPorCE(selecionado.getCadastroEconomico());
            getRecarregarProcessoCalculoMultaAcessoria();
            processosCalculosISS = Lists.newArrayList(calculaISSFacade.recarregar(selecionado.getProcessoCalculoISS()));
            recuperarParcelasGeradas();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void recuperarParcelasGeradas() {
        resultadoParcela = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, selecionado.getId()).executaConsulta().getResultados();
    }

    public void editarCalculoFixo() {
        try {
            parcelas = new ArrayList<>();
            selecionado = (CalculoISS) Web.pegaDaSessao(CalculoISS.class);
            if (selecionado == null) {
                selecionado = calculaISSFacade.recuperarID(id);
            }
            this.parcelas = calculaISSFacade.buscarParcelasCalculo(selecionado);
            this.processoCalculoISS = calculaISSFacade.recarregarSemAListaDeCalculos(selecionado.getProcessoCalculoISS());
            this.processoCalculoISS.setCalculos(new ArrayList<CalculoISS>());
            this.processoCalculoISS.getCalculos().add(selecionado);
            this.exercicio = this.processoCalculoISS.getExercicio();
            this.observacoes = processoCalculoISS.getObservacoes();
            this.valorDivida = new ValorDivida();
            this.parcelaValorDivida = new ParcelaValorDivida();
            this.lista = new ArrayList<>();
            servicos = cadastroEconomicoFacade.listaServicoPorCE(selecionado.getCadastroEconomico());
            recuperarParcelasGeradas();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @URLAction(mappingId = "verCalculoMensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCalculoHomologado() {
        editar();
    }

    @URLAction(mappingId = "verCalculoFixo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCalculoFixo() {
        editarCalculoFixo();
    }

    @URLAction(mappingId = "verCalculoEstimado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCalculoEstimado() {
        editar();
    }

    public Object getSelecionado() {
        return selecionado;
    }

    public BigDecimal getTotalFaturamentoMensal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Servico serv : getServicos()) {
            total = total.add(serv.getValorDiferenca());
        }
        return total;
    }

    public BigDecimal totalFaturamento() {
        BigDecimal total = BigDecimal.ZERO;
        if (getServicos() != null) {
            for (Servico serv : getServicos()) {
                if (serv.getFaturamento() != null) {
                    total = total.add(serv.getFaturamento());
                }
            }
        }
        return total;

    }

    public BigDecimal recuperarFaturamentoDoCalculo(CalculoISS calculoISS) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : calculoISS.getItemCalculoIsss()) {
            if (item.getFaturamento() != null) {
                total = total.add(item.getFaturamento());
            }
        }
        return total;
    }

    public BigDecimal recuperarBaseCalculoDoCalculo(CalculoISS calculoISS) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCalculoIss item : calculoISS.getItemCalculoIsss()) {
            if (item.getFaturamento() != null) {
                total = total.add(item.getBaseCalculo());
            }
        }
        return total;
    }

    public BigDecimal totalValorBase() {
        BigDecimal total = BigDecimal.ZERO;
        if (getServicos() != null) {
            for (Servico serv : getServicos()) {
                if (serv.getValorBase() != null) {
                    total = total.add(serv.getValorBase());
                }
            }
        }
        return total;
    }

    public BigDecimal totalValorCalculado() {
        BigDecimal total = BigDecimal.ZERO;
        if (getServicos() != null) {
            for (Servico serv : getServicos()) {
                if (serv.getValorCalculado() != null) {
                    total = total.add(serv.getValorCalculado());
                }
            }
        }
        return total;
    }

    public boolean getCmc() {
        return selecionado.getCadastroEconomico() != null;
    }

    public Mes retornaMes(Integer mesReferencia) {
        if (mesReferencia != null && mesReferencia > 0) {
            return Mes.getMesToInt(mesReferencia);
        }

        return Mes.JANEIRO;
    }

    public List<Tributo> listaTributos(CalculoISS calculo) {
        if (calculo != null) {
            return new ArrayList<>(calculaISSFacade.recuperar(calculo).getItemValor().keySet());
        }
        return null;
    }

    public String actionNovo() {
        return "geracalculo";
    }

    public BigDecimal getValorCalculadoEmUfm() throws UFMException {
        BigDecimal valor = BigDecimal.ZERO;

        try {
            valor = processoCalculoISS.getCalculos().get(0).getValorCalculado();
        } catch (NullPointerException ex) {
            logger.error(ex.getMessage());
        }

        return moedaFacade.converterToUFMParaExercicio(valor, exercicio);
    }

    public boolean getImprimeDeclaracao() {
        if (imprimeDeclaracao != null) {
            return imprimeDeclaracao.booleanValue();
        }
        return false;
    }

    public void setImprimeDeclaracao(Boolean imprimeDeclaracao) {
        this.imprimeDeclaracao = imprimeDeclaracao;
    }

    public boolean getImprimeDamCalculoISS() {
        if (imprimeDamCalculoISS != null) {
            return imprimeDamCalculoISS.booleanValue();
        }
        return false;
    }

    public void setImprimeDamCalculoISS(Boolean imprimeDamCalculoISS) {
        this.imprimeDamCalculoISS = imprimeDamCalculoISS;
    }

    public boolean getImprimeMultaAcessoria() {
        if (imprimeMultaAcessoria != null) {
            return imprimeMultaAcessoria.booleanValue();
        }
        return false;
    }

    public void setImprimeMultaAcessoria(Boolean imprimeMultaAcessoria) {
        this.imprimeMultaAcessoria = imprimeMultaAcessoria;
    }

    public Boolean getRecarregarProcessoCalculoMultaAcessoria() {
        if (selecionado != null && selecionado.getProcessoMultaAcessoria() != null && selecionado.getProcessoMultaAcessoria().getId() != null) {
            calculoMultaAcessoria = calculaISSFacade.recuperarCalculoMultaAcessoria(selecionado.getProcessoCalculoISS());
            if (calculoMultaAcessoria != null) {
                resultadoParcelaMultaAcessoria = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, calculoMultaAcessoria).executaConsulta().getResultados();
            }
            return true;
        }
        return false;
    }

    public ProcessoCalculoMultaAcessoria getProcessoMultaAcessoria() {
        return processoMultaAcessoria;
    }

    public void setProcessoMultaAcessoria(ProcessoCalculoMultaAcessoria processoMultaAcessoria) {
        this.processoMultaAcessoria = processoMultaAcessoria;
    }

    public void logar() {
        if (selecionado.getMotivoLancamentoValorMenor() == null || "".equals(selecionado.getMotivoLancamentoValorMenor())) {
            FacesUtil.addCampoObrigatorio("Informe o motivo do lançamento de ISS com base de cálculo menor que a dedução!");
        } else if (calculaISSFacade.getUsuarioSistemaFacade().validaLoginSenha(loginAutorizacao, senhaAutorizacao)) {
            UsuarioSistema usuario = calculaISSFacade.getUsuarioSistemaFacade().findOneByLogin(loginAutorizacao);
            if (calculaISSFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(usuario, AutorizacaoTributario.INFORMAR_VALOR_ISSQN_MENSAL_MENOR)) {
                FacesUtil.executaJavaScript("login.hide()");
//                processarCalculo();
            } else {
                FacesUtil.executaJavaScript("login.hide()");
                FacesUtil.addOperacaoNaoRealizada("O usuário informado não possui autorização para realizar este procedimento.");
                setaNuloLoginSenha();
            }
        } else {
            FacesUtil.executaJavaScript("login.hide()");
            FacesUtil.addOperacaoRealizada("Usuário ou senha incorretos.");
            setaNuloLoginSenha();
        }
    }

    public void setaNuloLoginSenha() {
        this.loginAutorizacao = "";
        this.senhaAutorizacao = "";
        selecionado.setMotivoLancamentoValorMenor("");
    }

    public String getCaminhoPadrao() {
        String caminho = "";
        if (TipoCalculoISS.MENSAL.equals(selecionado.getTipoCalculoISS())) {
            caminho = "mensal";
        } else if (TipoCalculoISS.ESTIMADO.equals(selecionado.getTipoCalculoISS())) {
            caminho = "estimado";
        } else if (TipoCalculoISS.FIXO.equals(selecionado.getTipoCalculoISS())) {
            caminho = "fixo";
        }
        return "/tributario/lancamento-de-iss-" + caminho + "/";
    }

    public void cancelar() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public String nomeUsuario() {
        if (selecionado.getUsuarioLancamento() != null && !selecionado.getNotaEletronica()) {
            return selecionado.getUsuarioLancamento().getLogin() + " - " + selecionado.getUsuarioLancamento().getPessoaFisica().getNome();
        } else if (selecionado.getNotaEletronica()) {
            return "Nota Fiscal Eletrônica";
        } else if (selecionado.getGeradoPortalContribuinte()) {
            return "Portal do Contribuinte";
        }
        return "";
    }

    public void adicionarIssMensal() {
        try {
            validarCamposObrigatorios();
            cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
            validarCalculoISSMensal();
            validarEnquadramentoFiscal();
            if (validarLancamentosISS()) {
                criarCalculoParaLancamentos();
                FacesUtil.atualizarComponente("Formulario");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void criarCalculoParaLancamentos() {
        if (calculos == null) {
            calculos = Lists.newArrayList();
        }
        CadastroEconomico ce = cadastroEconomico;
        String observacao = observacoes;
        processoCalculoISS = calculaISSFacade.calcularIssHomologado(cadastroEconomico, exercicio, mesReferencia, observacoes, taxaSobreIss, ausenciaMovimento, false);
        CalculoISS calculo = processoCalculoISS.getCalculos().get(0);
        calculo.setMesdeReferencia(Mes.getMesToInt(mesReferencia));
        calculaISSFacade.criarItensCalculoISSMensal(calculo, processoCalculoISS, servicos, selecionado.getMotivoLancamentoValorMenor(), false);
        calculos.add(calculo);
        novo();
        cadastroEconomico = ce;
        selecionado.setObservacao(observacao);
        observacoes = observacao;
        servicos = cadastroEconomicoFacade.listaServicoPorCE(cadastroEconomico);
        recuperarSubDividaESequenciaLancamento();
    }

    public void removerCalculo(CalculoISS calculoISS) {
        calculos.remove(calculoISS);
    }

    public void limparCampos() {
        novo();
        calculos = Lists.newArrayList();
    }

    public String retornarNomeContribuinte() {
        if (selecionado.getGeradoPortalContribuinte()) {
            String cpfCnpjContribuinte = resultadoParcela.get(0).getCadastro();
            if (!Strings.isNullOrEmpty(cpfCnpjContribuinte)) {
                return cadastroEconomicoFacade.getPessoaFacade().buscarPessoaAtivasPorCpfOrCnpj(cpfCnpjContribuinte).toString();
            }
        }
        return "";
    }
}
