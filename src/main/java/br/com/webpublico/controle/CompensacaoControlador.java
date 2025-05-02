package br.com.webpublico.controle;

/**
 * Created by Fabio on 06/08/2018.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarQueue;
import br.com.webpublico.negocios.tributario.arrecadacao.DepoisDePagarResquest;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "compensacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista-processo-de-compensacao",
        pattern = "/tributario/processo-de-compensacao/listar/",
        viewId = "/faces/tributario/contacorrente/processocompensacao/lista.xhtml"),
    @URLMapping(id = "novo-processo-de-compensacao",
        pattern = "/tributario/processo-de-compensacao/novo/",
        viewId = "/faces/tributario/contacorrente/processocompensacao/edita.xhtml"),
    @URLMapping(id = "ver-processo-de-compensacao",
        pattern = "/tributario/processo-de-compensacao/ver/#{compensacaoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processocompensacao/visualizar.xhtml"),
    @URLMapping(id = "editar-processo-de-compensacao",
        pattern = "/tributario/processo-de-compensacao/editar/#{compensacaoControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processocompensacao/edita.xhtml"),
})
public class CompensacaoControlador extends PrettyControlador<Compensacao> implements Serializable, CRUD {

    @EJB
    private CompensacaoFacade compensacaoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private DepoisDePagarQueue depoisDePagarQueue;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    //FILTROS PARA CONSULTA
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private ConverterAutoComplete converterCadastroImobiliario;
    private List<ResultadoParcela> resultadoConsulta;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private List<ResultadoParcela> resultados;
    private ContaCorrenteTributaria contaCorrenteTributaria;
    private List<ParcelaContaCorrenteTributaria> parcelasContaCorrente;
    private BigDecimal valorSelecionado;
    private ResultadoParcela parcelaResidual;

    public CompensacaoControlador() {
        super(Compensacao.class);
        resultadoConsulta = Lists.newArrayList();
        parcelaResidual = null;
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void setResultadoConsulta(List<ResultadoParcela> resultadoConsulta) {
        this.resultadoConsulta = resultadoConsulta;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public List<ResultadoParcela> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoParcela> resultados) {
        this.resultados = resultados;
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

    public Divida getFiltroDivida() {
        return filtroDivida;
    }

    public void setFiltroDivida(Divida filtroDivida) {
        this.filtroDivida = filtroDivida;
    }

    public Exercicio getFiltroExercicioFinal() {
        return filtroExercicioFinal;
    }

    public void setFiltroExercicioFinal(Exercicio filtroExercicioFinal) {
        this.filtroExercicioFinal = filtroExercicioFinal;
    }

    public Exercicio getFiltroExercicioInicio() {
        return filtroExercicioInicio;
    }

    public void setFiltroExercicioInicio(Exercicio filtroExercicioInicio) {
        this.filtroExercicioInicio = filtroExercicioInicio;
    }

    public List<ParcelaContaCorrenteTributaria> getParcelasContaCorrente() {
        return parcelasContaCorrente;
    }

    public void setParcelasContaCorrente(List<ParcelaContaCorrenteTributaria> parcelasContaCorrente) {
        this.parcelasContaCorrente = parcelasContaCorrente;
    }

    public BigDecimal getValorSelecionado() {
        return valorSelecionado;
    }

    public void setValorSelecionado(BigDecimal valorSelecionado) {
        this.valorSelecionado = valorSelecionado;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, compensacaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, compensacaoFacade.getPessoaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completarDivida(String parte) {
        return compensacaoFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public List<Pessoa> completarPessoasComContaCorrente(String filtro) {
        return compensacaoFacade.getContaCorrenteTributariaFacade().buscarPessoasComContaCorrente(filtro);
    }

    public ResultadoParcela getParcelaResidual() {
        return parcelaResidual;
    }

    public void setParcelaResidual(ResultadoParcela parcelaResidual) {
        this.parcelaResidual = parcelaResidual;
    }

    public void buscarContaCorrenteTributaria() {
        if (selecionado.getPessoa() != null) {
            carregarContaCorrente();
            if (contaCorrenteTributaria != null) {
                carregarInformacoesDasParcelas();
                carregarValorParaCompensacao();
            }
            if (parcelasContaCorrente.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("A conta corrente do contribuinte " + selecionado.getPessoa().getCpf_Cnpj() + " não possui parcelas de 'Crédito da Arrecadação'!");
                limparDados();
            }
        }
    }

    private void carregarValorParaCompensacao() {
        selecionado.setValorCreditoCompensar(getTotalACompensar());
        selecionado.setValorCompensado(getValorCompensado());
        selecionado.setValorCompensar(getValorResidual());
    }

    private void carregarInformacoesDasParcelas() {
        selecionado.getCreditos().clear();
        carregarInformacoesDasParcelasEditar();
    }

    private void carregarInformacoesDasParcelasEditar() {
        parcelasContaCorrente = Lists.newArrayList();
        for (ParcelaContaCorrenteTributaria parcela : contaCorrenteTributaria.getParcelas()) {
            if ((!parcela.getCompensada() && parcela.getCalculoContaCorrente() == null) && TipoDiferencaContaCorrente.CREDITO_ARRECADACAO.equals(parcela.getTipoDiferenca())) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                if (selecionado.getDataBloqueio() != null) {
                    consultaParcela.setBloqueio(selecionado.getDataBloqueio());
                }
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getParcelaValorDivida().getId());
                consultaParcela.executaConsulta();
                compensacaoFacade.getProcessoCreditoContaCorrenteFacade().buscarValorPagoNaArrecadacaoCompensacao(selecionado, consultaParcela.getResultados());
                parcela.setResultadoParcela(consultaParcela.getResultados().get(0));
                parcela.setValorDiferencaAtualizada(compensacaoFacade.getContaCorrenteTributariaFacade().getValorDiferencaAtualizada(parcela).subtract(parcela.getValorCompesado()));

                parcelasContaCorrente.add(parcela);
            }
        }
    }

    public boolean containsParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        for (CreditoCompensacao credito : selecionado.getCreditos()) {
            if (credito.getParcela().equals(parcela.getParcelaValorDivida())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTodosParcelaCredito() {
        if (parcelasContaCorrente != null) {
            for (ParcelaContaCorrenteTributaria parcela : parcelasContaCorrente) {
                boolean temItem = false;
                for (CreditoCompensacao credito : selecionado.getCreditos()) {
                    if (credito.getParcela().equals(parcela.getParcelaValorDivida())) {
                        temItem = true;
                    }
                }
                if (!temItem) {
                    return false;
                }
            }
        }
        return !selecionado.getCreditos().isEmpty();
    }

    public void removerTodosParcelaCredito() {
        selecionado.getCreditos().clear();
        carregarValorParaCompensacao();
    }

    public void removerParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        CreditoCompensacao aRemover = null;
        for (CreditoCompensacao credito : selecionado.getCreditos()) {
            if (credito.getParcela().equals(parcela.getParcelaValorDivida())) {
                aRemover = credito;
            }
        }
        selecionado.getCreditos().remove(aRemover);
        carregarValorParaCompensacao();
    }

    public void adicionarTosoParcelaCredito() {
        for (ParcelaContaCorrenteTributaria parcela : parcelasContaCorrente) {
            adicionarParcelaCredito(parcela);
        }
        carregarValorParaCompensacao();
    }

    public void adicionarParcelaCredito(ParcelaContaCorrenteTributaria parcela) {
        CreditoCompensacao credito = new CreditoCompensacao();
        credito.setCompensacao(selecionado);
        credito.setParcela(parcela.getParcelaValorDivida());

        credito.setResultadoParcela(parcela.getResultadoParcela());
        credito.setReferencia(credito.getResultadoParcela().getReferencia());
        credito.setDiferenca(parcela.getValorDiferenca());
        credito.setValor(credito.getResultadoParcela().getValorTotal());
        credito.setValorPago(credito.getResultadoParcela().getValorPago());
        credito.setDiferencaAtualizada(parcela.getValorDiferencaAtualizada());
        selecionado.getCreditos().add(credito);

        carregarValorParaCompensacao();
    }


    private BigDecimal getTotalACompensar() {
        BigDecimal total = BigDecimal.ZERO;
        if (contaCorrenteTributaria != null) {
            for (CreditoCompensacao credito : selecionado.getCreditos()) {
                total = total.add(credito.getDiferencaAtualizada());
            }
        }
        return total;
    }

    private BigDecimal getValorCompensado() {
        BigDecimal retorno = BigDecimal.ZERO;
        if (!selecionado.getItens().isEmpty()) {
            for (ItemCompensacao itemCompensacao : selecionado.getItens()) {
                retorno = retorno.add(itemCompensacao.getValorCompensado());
            }
        }
        return retorno;
    }

    private BigDecimal getValorResidual() {
        return getTotalACompensar().subtract(getValorCompensado());
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, compensacaoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    @Override
    public AbstractFacade getFacede() {
        return compensacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/processo-de-compensacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void inicializarProcesso() {
        operacao = Operacoes.NOVO;
        try {
            selecionado.setUsuarioIncluiu(compensacaoFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(compensacaoFacade.getSistemaFacade().getDataOperacao());
            selecionado.setDataCompensacao(compensacaoFacade.getSistemaFacade().getDataOperacao());
            selecionado.setExercicio(compensacaoFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCodigo(compensacaoFacade.recuperarProximoCodigoPorExercicio(selecionado.getExercicio()));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado durante o processo! " + ex.getMessage());
        }
    }

    @URLAction(mappingId = "novo-processo-de-compensacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = (Compensacao) Web.pegaDaSessao(Compensacao.class);
        if (selecionado == null) {
            super.novo();
        }
        parcelasContaCorrente = Lists.newArrayList();
        inicializarProcesso();
        buscarContaCorrenteTributaria();
        resultados = Lists.newArrayList();
    }


    @URLAction(mappingId = "ver-processo-de-compensacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verCompensacao() {
        super.ver();
        carregarInformacoesProcessoVer();
    }

    @Override
    @URLAction(mappingId = "editar-processo-de-compensacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarInformacoesProcesso();
        carregarContaCorrente();
        carregarInformacoesDasParcelasEditar();
        resultados = Lists.newArrayList();
    }

    private void carregarContaCorrente() {
        contaCorrenteTributaria = compensacaoFacade.getContaCorrenteTributariaFacade().buscarContaCorrentePorPessoa(selecionado.getPessoa());
        if (contaCorrenteTributaria != null) {
            contaCorrenteTributaria = compensacaoFacade.getContaCorrenteTributariaFacade().recuperar(contaCorrenteTributaria.getId());
        }
    }

    private void carregarInformacoesProcesso() {
        selecionado.setDataEstorno(compensacaoFacade.getSistemaFacade().getDataOperacao());
        for (ItemCompensacao item : selecionado.getItens()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            if (selecionado.getDataBloqueio() != null) {
                consultaParcela.setBloqueio(selecionado.getDataBloqueio());
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, item.getParcela().getId());
            consultaParcela.executaConsulta();
            item.setResultadoParcela(consultaParcela.getResultados().get(0));
        }
    }

    private void carregarInformacoesProcessoVer() {
        for (CreditoCompensacao credito : selecionado.getCreditos()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            if (selecionado.getDataBloqueio() != null) {
                consultaParcela.setBloqueio(selecionado.getDataBloqueio());
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, credito.getParcela().getId());
            consultaParcela.executaConsulta();
            List<ResultadoParcela> resultados = consultaParcela.getResultados();
            compensacaoFacade.getProcessoCreditoContaCorrenteFacade().buscarValorPagoNaArrecadacaoCompensacao(selecionado, resultados);

            credito.setResultadoParcela(resultados.get(0));
        }

        for (ItemCompensacao itemCompensacao : selecionado.getItens()) {
            if (itemCompensacao.getCalculoCompensacao() != null) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, itemCompensacao.getCalculoCompensacao().getId());
                consultaParcela.executaConsulta();
                parcelaResidual = !consultaParcela.getResultados().isEmpty() ? consultaParcela.getResultados().get(0) : null;
                break;
            }
        }
    }

    @Override
    public void salvar() {
        try {
            validarProcesso();
            if (isOperacaoNovo()) {
                compensacaoFacade.salvarNovo(selecionado);
            } else {
                compensacaoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro Salvo com sucesso!");
            navegarParaVisualizar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    public void validarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Código deve ser preenchido!");
        } else if (selecionado.getCodigo() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser maior que zero!");
        } else if (compensacaoFacade.verificarCodigoEmUso(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Código informado em uso! Foi gerado um novo código.");
        }
        if (selecionado.getLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Lançamento deve ser preenchida!");
        }
        if (selecionado.getMotivo() == null || selecionado.getMotivo().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo ou Fundamentação Legal deve ser preenchido!");
        }
        if (selecionado.getItens() == null || selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo deve conter ao menos uma parcela!");
        }
        if (selecionado.getUsuarioIncluiu() == null || selecionado.getUsuarioIncluiu().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O usuário que incluiu o processo deve ser informado!");
        }
        if (selecionado.getSituacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Situação do Processo deve ser preenchida!");
        } else if (selecionado.getId() != null && !SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + selecionado.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser alterados.");
        }
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Contribuinte deve ser informado!");
        } else {
            if (selecionado.getPessoa() instanceof PessoaFisica) {
                if (!Util.valida_CpfCnpj(((PessoaFisica) selecionado.getPessoa()).getCpf())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte selecionado não possui um CPF válido!");
                } else if (compensacaoFacade.getPessoaFacade().hasOutraPessoaComMesmoCpf((PessoaFisica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF do contribuinte selecionado pertence a mais de uma pessoa física!");
                }
            } else {
                if (!Util.valida_CpfCnpj(((PessoaJuridica) selecionado.getPessoa()).getCnpj())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Contribuinte selecionado não possui um CNPJ válido!");
                } else if (compensacaoFacade.getPessoaFacade().hasOutraPessoaComMesmoCnpj((PessoaJuridica) selecionado.getPessoa(), true)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ do contribuinte selecionado pertence a mais de uma pessoa jurídica!");
                }
            }
        }

        ve.lancarException();
    }

    private void navegarParaVisualizar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void encerrarProcesso() {
        try {
            ResultadoValidacao resultado = compensacaoFacade.encerrarProcesso(selecionado);
            if (resultado.isValidado()) {
                gerarDebitoDoResidualCompensacao();
                FacesUtil.addOperacaoRealizada("Processo de Débitos processado com sucesso!");
                navegarParaVisualizar();
                depoisDePagarQueue.enqueueProcess(DepoisDePagarResquest.build()
                    .nome("Processo de Compensação: " + selecionado.getNumeroProtocolo())
                    .parcelas(selecionado.getItens().stream().map(ItemCompensacao::getParcela).collect(Collectors.toList())));
            } else {
                FacesUtil.printAllMessages(resultado.getMensagens());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao processar: " + e.getMessage());
        }
    }

    public void limparEstorno() {
        selecionado.setMotivoEstorno(null);
    }

    public void estornarProcesso() {
        try {
            validarEstorno();
            compensacaoFacade.estornarProcesso(selecionado, SituacaoProcessoDebito.ESTORNADO);
            FacesUtil.addOperacaoRealizada("Processo estornado com sucesso!");
            FacesUtil.atualizarComponente("Formulario");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    private void validarEstorno() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoEstorno() == null || selecionado.getMotivoEstorno().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno");
        }
        if (selecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data do estorno");
        }
        ve.lancarException();
    }


    @Override
    public void excluir() {
        if (SituacaoProcessoDebito.ESTORNADO.equals(selecionado.getSituacao()) || (SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao()))) {
            FacesUtil.addError("O registro selecionado está finalizado e não pode ser excluido!", "");
        } else {
            try {
                getFacede().remover(selecionado);
                FacesUtil.addInfo("Registro excluído com sucesso!", "");
                cancelar();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível realizar a exclusão!", "");
            }
        }
    }

    private void validarParcelasProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (resultados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe uma parcela para adicionar ao processo.");
        }
        ve.lancarException();
    }

    public void adicionarParcelas() {
        try {
            validarParcelasProcesso();
            boolean adicionou = false;
            for (ResultadoParcela resultadoParcela : resultados) {
                if (compensacaoFacade.validarItemProcesso(selecionado, resultadoParcela)) {
                    BigDecimal valorCompensado = getValorResidual().compareTo(resultadoParcela.getValorTotal()) >= 0 ? resultadoParcela.getValorTotal() : getValorResidual();
                    if (valorCompensado.compareTo(BigDecimal.ZERO) > 0) {
                        compensacaoFacade.adicionarItem(resultadoParcela, selecionado, resultadoConsulta, valorCompensado);
                        adicionou = true;
                    } else {
                        FacesUtil.addOperacaoNaoRealizada("A parcela no valor de " + Util.formataValor(resultadoParcela.getValorTotal()) + " não pode ser adicionada ao processo!");
                    }
                } else {
                    FacesUtil.addError("Não foi possível continuar!", "A parcela no valor de " + Util.formataValor(resultadoParcela.getValorTotal()) + " já foi adicionada ao processo!");
                }
            }
            if (adicionou) {
                carregarValorParaCompensacao();
                resultados = Lists.newArrayList();
                valorSelecionado = BigDecimal.ZERO;
                FacesUtil.addOperacaoRealizada("Parcela(s) adicionada(s) ao Processo de Compensação de Débitos!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerItem(ItemCompensacao item) {
        selecionado.getItens().remove(item);
        carregarValorParaCompensacao();
    }

    public void pesquisar() {
        try {
            resultados = Lists.newArrayList();
            validarCamposPreenchidos();
            resultadoConsulta.clear();
            ConsultaParcela consulta = new ConsultaParcela();
            if (selecionado.getDataBloqueio() != null) {
                consulta.setBloqueio(selecionado.getDataBloqueio());
            }
            List<BigDecimal> cadastros = cadastroFacade.buscarIdsCadastrosAtivosDaPessoa(selecionado.getPessoa(), false);
            if (!cadastros.isEmpty()) {
                consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, cadastros);
                adicionarParametro(consulta);
                consulta.executaConsulta();
                resultadoConsulta.addAll(consulta.getResultados());
            }
            consulta = new ConsultaParcela();
            if (selecionado.getDataBloqueio() != null) {
                consulta.setBloqueio(selecionado.getDataBloqueio());
            }
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());
            adicionarParametro(consulta);
            consulta.addComplementoDoWhere(" and vw.cadastro_id is null");
            consulta.executaConsulta();
            resultadoConsulta.addAll(consulta.getResultados());

            removerParcelasJaAdicionadasAoProcesso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        Collections.sort(resultadoConsulta, ResultadoParcela.getComparatorByValuePadrao());
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhuma Parcela!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }

    private void removerParcelasJaAdicionadasAoProcesso() {
        List<ResultadoParcela> adicionadas = Lists.newArrayList();
        for (ItemCompensacao itemCompensacao : selecionado.getItens()) {
            adicionadas.add(itemCompensacao.getResultadoParcela());
        }
        for (ResultadoParcela adicionada : adicionadas) {
            if (resultadoConsulta.contains(adicionada)) {
                resultadoConsulta.remove(adicionada);
            }
        }
    }

    public void adicionarParametro(ConsultaParcela consulta) {
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

        if (!selecionado.getItens().isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.NOT_IN, buscarIdsParcelasAdicionadas(selecionado.getItens()));
        }
        String complementoDoWhere = " and VW.PARCELA_ID not in (select iccc.parcela_id from ItemCompensacao iccc " +
            " inner join Compensacao ccc on iccc.compensacao_id = ccc.id  " +
            " where ccc.situacao in ('EM_ABERTO','FINALIZADO') ";
        if (selecionado.getId() != null) {
            complementoDoWhere += " and ccc.id <> " + selecionado.getId();
        }
        complementoDoWhere += " )";
        consulta.addComplementoDoWhere(complementoDoWhere);
    }

    private List<Long> buscarIdsParcelasAdicionadas(List<ItemCompensacao> itens) {
        List<Long> listaIdsParcelas = Lists.newArrayList();
        for (ItemCompensacao itemCompensacao : itens) {
            listaIdsParcelas.add(itemCompensacao.getParcela().getId());
        }
        return listaIdsParcelas;
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


    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }


    public boolean habilitarBotaoSalvar() {
        inicializarFiltros();
        return selecionado.getSituacao() == null || SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao());
    }

    public boolean naoProcessado() {
        return selecionado == null || selecionado.getSituacao() != null && SituacaoProcessoDebito.EM_ABERTO.equals(selecionado.getSituacao()) && selecionado.getId() != null;
    }

    public boolean isHabilitarBotaoEstornar() {
        return selecionado.getSituacao() != null
            && SituacaoProcessoDebito.FINALIZADO.equals(selecionado.getSituacao())
            && selecionado.getId() != null;
    }

    public String retornarSituacaoDaDivida(ResultadoParcela resultadoParcela) {
        if (resultadoParcela == null) {
            return "-";
        }
        if (resultadoParcela.getDividaIsDividaAtiva()) {
            return "Dívida Ativa";
        }
        if (resultadoParcela.getDividaAtivaAjuizada()) {
            return "Dívida Ativa Ajuizada";
        }
        return "Do Exercício";
    }


    public String recuperarUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = compensacaoFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, compensacaoFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, compensacaoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return compensacaoFacade.getAtoLegalFacade().listaFiltrando(parte.trim());
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(compensacaoFacade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            CompensacaoArquivo proArq = new CompensacaoArquivo();
            proArq.setArquivo(compensacaoFacade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            proArq.setCompensacao(selecionado);

            selecionado.getArquivos().add(proArq);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void removerArquivo(CompensacaoArquivo arquivo) {
        selecionado.getArquivos().remove(arquivo);
    }

    public void redirecionarParaContaCorrente() {
        ContaCorrenteTributaria contaCorrente = compensacaoFacade.getContaCorrenteTributariaFacade().buscarContaCorrentePorPessoa(selecionado.getPessoa());
        if (contaCorrente != null) {
            FacesUtil.redirecionamentoInterno("/tributario/conta-corrente-tributaria/ver/" + contaCorrente.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi possível localizar a conta corrente desse contribuinte!");
        }
    }

    public ContaCorrenteTributaria getContaCorrenteTributaria() {
        return contaCorrenteTributaria;
    }

    public void setContaCorrenteTributaria(ContaCorrenteTributaria contaCorrenteTributaria) {
        this.contaCorrenteTributaria = contaCorrenteTributaria;
    }

    public void inicializarConsultaDeDebitos() {
        resultados = Lists.newArrayList();
        atualizarValorCompensadoFiltro();
    }

    public void atualizarValorCompensadoFiltro() {
        valorSelecionado = BigDecimal.ZERO;
        for (ResultadoParcela resultado : resultados) {
            valorSelecionado = valorSelecionado.add(resultado.getValorTotal());
        }
    }

    public boolean containsParcelaFiltro(ResultadoParcela resultadoParcela) {
        return resultados.contains(resultadoParcela);
    }

    public void removerParcelaFiltro(ResultadoParcela resultadoParcela) {
        resultados.remove(resultadoParcela);
        atualizarValorCompensadoFiltro();
    }

    public void adicionarParcelaFiltro(ResultadoParcela resultadoParcela) {
        resultados.add(resultadoParcela);
        atualizarValorCompensadoFiltro();
    }

    public void limparDados() {
        selecionado.setPessoa(null);
        parcelasContaCorrente = Lists.newArrayList();
        selecionado.getItens().clear();
        removerTodosParcelaCredito();
    }

    public void gerarDebitoDoResidualCompensacao() {
        try {
            if (contaCorrenteTributaria != null) {
                selecionado = compensacaoFacade.atualizarContaCorrente(selecionado);

                for (ItemCompensacao itemCompensacao : selecionado.getItens()) {
                    BigDecimal valorDoDebito = itemCompensacao.getValorResidual();
                    if (valorDoDebito.compareTo(BigDecimal.ZERO) > 0) {
                        itemCompensacao.setValorCompensado(valorDoDebito);

                        ParcelaContaCorrenteTributaria parcela = criarParcelaContaCorrente(itemCompensacao, valorDoDebito);
                        parcela = compensacaoFacade.getContaCorrenteTributariaFacade().salvarParcela(parcela);

                        CalculoContaCorrente calculoContaCorrente = compensacaoFacade.getContaCorrenteTributariaFacade().criarCalculoContaCorrente(parcela, valorDoDebito);
                        calculoContaCorrente.setCompensacao(selecionado);
                        calculoContaCorrente.setProcessoCalculo(compensacaoFacade.getContaCorrenteTributariaFacade().salvarProcessoCalculoContaCorrente(calculoContaCorrente.getProcessoCalculo()));
                        calculoContaCorrente = (CalculoContaCorrente) calculoContaCorrente.getProcessoCalculo().getCalculos().get(0);
                        compensacaoFacade.getContaCorrenteTributariaFacade().gerarDebito(calculoContaCorrente);
                        compensacaoFacade.getContaCorrenteTributariaFacade().gerarDAM(calculoContaCorrente);

                        parcela.setCalculoContaCorrente(calculoContaCorrente);
                        compensacaoFacade.getContaCorrenteTributariaFacade().salvarParcela(parcela);

                        FacesUtil.addOperacaoRealizada("Débito no valor de " + Util.formataValor(valorDoDebito) + " gerado com sucesso!");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            logger.error("Erro ao gerar o débito: {}", e);
        }
    }

    private ParcelaContaCorrenteTributaria criarParcelaContaCorrente(ItemCompensacao itemCompensacao, BigDecimal valorDoDebito) {
        ParcelaContaCorrenteTributaria parcela = new ParcelaContaCorrenteTributaria();
        parcela.setContaCorrente(contaCorrenteTributaria);
        parcela.setParcelaValorDivida(itemCompensacao.getParcela());
        parcela.setValorDiferenca(valorDoDebito);
        parcela.setCompensada(false);
        parcela.setTipoDiferenca(TipoDiferencaContaCorrente.RESIDUO_ARRECADACAO);
        parcela.setValorDiferencaUtilizada(valorDoDebito);
        parcela.setOrigem(ContaCorrenteTributaria.Origem.COMPENSACAO);
        return parcela;
    }

    public void imprimir() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Processo de Compensação de Débitos");
            dto.adicionarParametro("USUARIO", compensacaoFacade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA DE FINANÇAS");
            dto.adicionarParametro("TITULO", "Processo de Compensação de Débitos");
            dto.adicionarParametro("ID", selecionado.getId());
            dto.setApi("tributario/compensacao/");
            ReportService.getInstance().gerarRelatorio(compensacaoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
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
