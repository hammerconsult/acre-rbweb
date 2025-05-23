package br.com.webpublico.controle;

/**
 * Created by William on 30/03/2017.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "processoDebitoAtualizacaoMonetariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "lista-processo-atualizacao-monetaria",
        pattern = "/tributario/conta-corrente/processo-de-debitos/atualizacao-monetaria/listar/",
        viewId = "/faces/tributario/contacorrente/processodebitosatualizacaomonetaria/lista.xhtml"),
    @URLMapping(id = "novo-processo-atualizacao-monetaria",
        pattern = "/tributario/conta-corrente/processo-de-debitos/atualizacao-monetaria/novo/",
        viewId = "/faces/tributario/contacorrente/processodebitosatualizacaomonetaria/edita.xhtml"),
    @URLMapping(id = "ver-processo-atualizacao-monetaria",
        pattern = "/tributario/conta-corrente/processo-de-debitos/atualizacao-monetaria/ver/#{processoDebitoAtualizacaoMonetariaControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processodebitosatualizacaomonetaria/visualizar.xhtml"),
    @URLMapping(id = "editar-processo-atualizacao-monetaria",
        pattern = "/tributario/conta-corrente/processo-de-debitos/atualizacao-monetaria/editar/#{processoDebitoAtualizacaoMonetariaControlador.id}/",
        viewId = "/faces/tributario/contacorrente/processodebitosatualizacaomonetaria/edita.xhtml"),
})
public class ProcessoDebitoAtualizacaoMonetariaControlador extends PrettyControlador<ProcessoDebito> implements Serializable, CRUD {
    @EJB
    private ProcessoDebitoAtualizacaoMonetariaFacade processoDebitoAtualizacaoMonetariaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterExercicio;
    //FILTROS PARA CONSULTA
    private String filtroCodigoBarras;
    private String filtroNumeroDAM;
    private TipoCadastroTributario tipoCadastroTributario;
    private Divida filtroDivida;
    private Exercicio filtroExercicioInicio;
    private Exercicio filtroExercicioFinal;
    private boolean dividaDoExercicio;
    private boolean dividaAtiva;
    private boolean dividaAtivaAzuijada;
    private ConverterAutoComplete converterCadastroImobiliario;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private LoteFacade loteFacade;
    private List<ResultadoParcela> resultadoConsulta;
    private SituacaoParcela situacaoParcela;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private ResultadoParcela[] resultados;


    public ProcessoDebitoAtualizacaoMonetariaControlador() {
        super(ProcessoDebito.class);
        resultadoConsulta = Lists.newArrayList();
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

    public ResultadoParcela[] getResultados() {
        return resultados;
    }

    public void setResultados(ResultadoParcela[] resultados) {
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

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
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

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, processoDebitoAtualizacaoMonetariaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, processoDebitoAtualizacaoMonetariaFacade.getPessoaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completarDivida(String parte) {
        return processoDebitoAtualizacaoMonetariaFacade.getDividaFacade().listaFiltrandoDividas(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, processoDebitoAtualizacaoMonetariaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    @Override
    public AbstractFacade getFacede() {
        return processoDebitoAtualizacaoMonetariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente/processo-de-debitos/atualizacao-monetaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void inicializarProcesso() {
        operacao = Operacoes.NOVO;
        try {
            selecionado.setUsuarioIncluiu(processoDebitoAtualizacaoMonetariaFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacao(SituacaoProcessoDebito.EM_ABERTO);
            selecionado.setLancamento(processoDebitoAtualizacaoMonetariaFacade.getSistemaFacade().getDataOperacao());
            selecionado.setExercicio(processoDebitoAtualizacaoMonetariaFacade.getSistemaFacade().getExercicioCorrente());
            selecionado.setCodigo(processoDebitoAtualizacaoMonetariaFacade.getProcessoDebitoFacade().recuperarProximoCodigoPorExercicioTipo(selecionado.getExercicio(), selecionado.getTipo()));
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro inesperado durante o processo! " + ex.getMessage());
        }
    }

    @URLAction(mappingId = "novo-processo-atualizacao-monetaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setTipo(TipoProcessoDebito.ATUALIZACAO_MONETARIA);
        inicializarProcesso();
    }


    @URLAction(mappingId = "ver-processo-atualizacao-monetaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verProcessoDebito() {
        super.ver();
        carregarInformacoesProcesso();
        recuperarParcelaDoProcessoDebito();

    }

    private void recuperarParcelaDoProcessoDebito() {
        for (ItemProcessoDebito processoDebito : selecionado.getItens()) {
            processoDebito.setParcela(processoDebitoAtualizacaoMonetariaFacade.recuperarParcelaValorDivida(processoDebito.getParcela().getId()));
        }
    }


    @Override
    @URLAction(mappingId = "editar-processo-atualizacao-monetaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarInformacoesProcesso();
        recuperarParcelaDoProcessoDebito();
    }

    private void carregarInformacoesProcesso() {
        tipoCadastroTributario = selecionado.getTipoCadastro();
        selecionado.setDataEstorno(processoDebitoAtualizacaoMonetariaFacade.getSistemaFacade().getDataOperacao());
    }

    @Override
    public void salvar() {
        try {
            validarProcesso();
            atribuirValoresInformadosDeParcelamento();

            selecionado.setTipoCadastro(tipoCadastroTributario);
            if (operacao == Operacoes.NOVO) {
                processoDebitoAtualizacaoMonetariaFacade.salvarNovo(selecionado);
            } else {
                processoDebitoAtualizacaoMonetariaFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro Salvo com sucesso!");
            navegarParaVisualizar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Ocorreu um erro ao salvar: " + e.getMessage());
        }
    }

    private void atribuirValoresInformadosDeParcelamento() {
        for (ItemProcessoDebito itemProcessoDebito : selecionado.getItens()) {
            if (!itemProcessoDebito.getItemProcessoDebitoParcelamento().isEmpty()) {
                BigDecimal valorImposto = BigDecimal.ZERO;
                BigDecimal valorTaxa = BigDecimal.ZERO;
                BigDecimal valorJuros = BigDecimal.ZERO;
                BigDecimal valorMulta = BigDecimal.ZERO;
                BigDecimal valorCorrecao = BigDecimal.ZERO;
                BigDecimal valorHonorarios = BigDecimal.ZERO;

                for (ItemProcessoDebitoParcelamento itemProcessoDebitoParcelamento : itemProcessoDebito.getItemProcessoDebitoParcelamento()) {
                    valorImposto = valorImposto.add(itemProcessoDebitoParcelamento.getImposto());
                    valorTaxa = valorTaxa.add(itemProcessoDebitoParcelamento.getTaxa());
                    valorJuros = valorJuros.add(itemProcessoDebitoParcelamento.getJuros());
                    valorMulta = valorMulta.add(itemProcessoDebitoParcelamento.getMulta());
                    valorCorrecao = valorCorrecao.add(itemProcessoDebitoParcelamento.getCorrecao());
                    valorHonorarios = valorHonorarios.add(itemProcessoDebitoParcelamento.getHonorarios());
                }

                for (ItemProcessoDebitoParcela itemProcessoDebitoParcela : itemProcessoDebito.getItemProcessoDebitoParcela()) {
                    if (valorImposto.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.IMPOSTO.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorImposto);
                    }
                    if (valorTaxa.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.TAXA.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorTaxa);
                    }
                    if (valorJuros.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.JUROS.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorJuros);
                    }
                    if (valorMulta.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.MULTA.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorMulta);
                    }
                    if (valorCorrecao.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.CORRECAO.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorCorrecao);
                    }
                    if (valorHonorarios.compareTo(BigDecimal.ZERO) > 0 &&
                        Tributo.TipoTributo.HONORARIOS.equals(itemProcessoDebitoParcela.getTributo().getTipoTributo())) {
                        itemProcessoDebitoParcela.setValor(valorHonorarios);
                    }
                }
            }
            if (selecionado.getId() != null) {
                itemProcessoDebito = processoDebitoAtualizacaoMonetariaFacade.salvarItem(itemProcessoDebito);
            }
        }
    }

    public void validarProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Código deve ser preenchido!");
        } else if (selecionado.getCodigo() <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser maio que zero!");
        } else if (processoDebitoAtualizacaoMonetariaFacade.getProcessoDebitoFacade().codigoEmUso(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Código informado em uso! Foi gerado um novo código.");
        }
        if (selecionado.getTipo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Processo de Débitos deve ser informado!");
        }
        if (selecionado.getLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Lançamento deve ser preenchida!");
        }
        if (selecionado.getMotivo() == null || selecionado.getMotivo().trim().length() <= 0) {
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
        } else if (selecionado.getId() != null && selecionado.getSituacao() != SituacaoProcessoDebito.EM_ABERTO) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O processo selecionado encontra-se " + selecionado.getSituacao().getDescricao() + ". Somente processos EM ABERTO podem ser alterados.");
        }

        boolean todosValoresZerados = true;
        for (ItemProcessoDebito itemProcessoDebito : selecionado.getItens()) {
            if (itemProcessoDebito.getItemProcessoDebitoParcela() != null) {
                for (ItemProcessoDebitoParcela itemProcessoDebitoParcela : itemProcessoDebito.getItemProcessoDebitoParcela()) {
                    if (itemProcessoDebitoParcela.getValor().compareTo(BigDecimal.ZERO) > 0) {
                        todosValoresZerados = false;
                        break;
                    }
                }
            }
        }
        if (todosValoresZerados) {
            ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor para um dos Tributos!");
        }
        itensProcessoDebito:
        for (ItemProcessoDebito itemProcessoDebito : selecionado.getItens()) {
            if (!itemProcessoDebito.getItemProcessoDebitoParcelamento().isEmpty()) {
                for (ItemProcessoDebitoParcelamento itemProcessoDebitoParcelamento : itemProcessoDebito.getItemProcessoDebitoParcelamento()) {
                    if (itemProcessoDebitoParcelamento.getImposto() == null || itemProcessoDebitoParcelamento.getImposto().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor do Imposto para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
                    if (itemProcessoDebitoParcelamento.getTaxa() == null || itemProcessoDebitoParcelamento.getTaxa().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor da Taxa para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
                    if (itemProcessoDebitoParcelamento.getJuros() == null || itemProcessoDebitoParcelamento.getJuros().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor do Juros para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
                    if (itemProcessoDebitoParcelamento.getMulta() == null || itemProcessoDebitoParcelamento.getMulta().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor da Multa para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
                    if (itemProcessoDebitoParcelamento.getCorrecao() == null || itemProcessoDebitoParcelamento.getCorrecao().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor da Correção para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
                    if (itemProcessoDebitoParcelamento.getHonorarios() == null || itemProcessoDebitoParcelamento.getHonorarios().compareTo(BigDecimal.ZERO) < 0) {
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o valor do Honorários para todos itens do Parcelamento!");
                        break itensProcessoDebito;
                    }
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
            processoDebitoAtualizacaoMonetariaFacade.encerrarProcesso(selecionado);
            FacesUtil.addOperacaoRealizada("Processo de Débitos processado com sucesso!");
            navegarParaVisualizar();
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
            processoDebitoAtualizacaoMonetariaFacade.estornarProcesso(selecionado, SituacaoProcessoDebito.ESTORNADO);
            FacesUtil.addOperacaoRealizada("Processo estornado com sucesso!");
            navegarParaVisualizar();
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
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo do estorno!");
        }
        if (selecionado.getDataEstorno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data do estorno!");
        }
        for (ItemProcessoDebito itemProcessoDebito : selecionado.getItens()) {
            ResultadoParcela parcela = buscarResultadoParcelaDaParcela(itemProcessoDebito.getParcela());
            if (!itemProcessoDebito.getSituacaoProxima().equals(parcela.getSituacaoEnumValue())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível estornar, uma ou mais parcelas estão com situação diferente à original da efetivação do processo!");
            }
        }
        ve.lancarException();
    }

    private ResultadoParcela buscarResultadoParcelaDaParcela(ParcelaValorDivida parcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, parcela.getId());
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados().get(0);
    }


    @Override
    public void excluir() {
        if (selecionado.getSituacao().ESTORNADO.equals(selecionado.getSituacao()) || (selecionado.getSituacao().FINALIZADO.equals(selecionado.getSituacao()))) {
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
        if (resultados.length == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe uma parcela para adicionar ao processo.");
        }
        ve.lancarException();
    }

    public void adicionarParcelas() {
        try {
            validarParcelasProcesso();
            for (ResultadoParcela resultadoParcela : resultados) {
                processoDebitoAtualizacaoMonetariaFacade.adicionarItem(resultadoParcela, selecionado, resultadoConsulta);
            }
            FacesUtil.addOperacaoRealizada("Parcelas adicionadas no Processo de Débitos!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerItem(ItemProcessoDebito item) {
        selecionado.getItens().remove(item);
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
        Collections.sort(resultadoConsulta, new OrdenaResultadoParcelaPorVencimento());
        try {
            if (resultadoConsulta.isEmpty()) {
                FacesUtil.addError("A pesquisa não encontrou nenhum Débito!", "");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Impossível continuar", e.getMessage());
        }
    }


    public void adicionarParametro(ConsultaParcela consulta) {
        if (filtroExercicioInicio != null && filtroExercicioFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroExercicioInicio.getAno());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, filtroExercicioFinal.getAno());
        }
        if (selecionado.getCadastro() != null && selecionado.getCadastro().getId() != null) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getCadastro().getId());
        }

        if (selecionado.getPessoa() != null && tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, selecionado.getPessoa().getId());
        }

        if (filtroDivida != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, filtroDivida.getId());
        }

        if (selecionado.getTipo().equals(TipoProcessoDebito.PRESCRICAO)) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.DIFERENTE, 1);

        } else {
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
        }

        if (vencimentoInicial != null && vencimentoFinal != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, vencimentoFinal);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, vencimentoInicial);
        }
        consulta.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC).addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.ISOLAMENTO);

    }

    public void inicializarFiltros() {
        filtroCodigoBarras = null;
        filtroNumeroDAM = null;
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
        if (tipoCadastroTributario == null && selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro.");
        } else if ((selecionado.getPessoa() == null || selecionado.getPessoa().getId() == null)
            && (tipoCadastroTributario == null)
            && (selecionado.getCadastro() == null || selecionado.getCadastro().getId() != null)
            && (filtroCodigoBarras == null || filtroCodigoBarras.trim().isEmpty())
            && (filtroNumeroDAM == null || filtroNumeroDAM.trim().isEmpty())
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
        return selecionado.getSituacao() == null || selecionado.isAberto();
    }

    public boolean naoProcessado() {
        return selecionado == null || selecionado.getSituacao() != null && selecionado.getSituacao().equals(SituacaoProcessoDebito.EM_ABERTO) && selecionado.getId() != null;
    }

    public boolean isHabilitarBotaoEstornar() {
        return selecionado.getSituacao() != null
            && selecionado.getSituacao().equals(SituacaoProcessoDebito.FINALIZADO)
            && selecionado.getId() != null
            && selecionado.getValido();
    }

    public void limparFiltroCadastro() {
    }

    public String retornarSituacaoDaDivida(ParcelaValorDivida parcela) {
        if (parcela == null) {
            return " - ";
        }
        if (parcela.isDividaAtiva()) {
            return "DÍVIDA ATIVA";
        }
        if (parcela.isDividaAtivaAjuizada()) {
            return "DÍVIDA ATIVA AJUIZADA";
        }
        return "DO EXERCÍCIO";
    }


    public String recuperarUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoDebitoAtualizacaoMonetariaFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public BigDecimal recuperarSaldoUltimaSituacao(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = processoDebitoAtualizacaoMonetariaFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSaldo();
    }


    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (selecionado.getCadastro() != null) {
            return cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) selecionado.getCadastro());
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public Testada getTestadaPrincipal() {
        if (selecionado.getCadastro() != null) {
            return loteFacade.recuperarTestadaPrincipal(((CadastroImobiliario) selecionado.getCadastro()).getLote());
        } else {
            return new Testada();
        }
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }


    public List<Pessoa> recuperarPessoasCadastro() {
        List<Pessoa> retorno = new ArrayList<>();
        if (selecionado.getCadastro() instanceof CadastroImobiliario) {
            CadastroImobiliario cadastroIm = cadastroImobiliarioFacade.recuperar(selecionado.getCadastro().getId());
            for (Propriedade p : cadastroIm.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (selecionado.getCadastro() instanceof CadastroRural) {
            CadastroRural cadastroRu = cadastroRuralFacade.recuperar(selecionado.getCadastro().getId());
            for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (selecionado.getCadastro() instanceof CadastroEconomico) {
            CadastroEconomico cadastroEco = cadastroEconomicoFacade.recuperar(selecionado.getCadastro().getId());
            for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                retorno.add(sociedadeCadastroEconomico.getSocio());
            }
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, processoDebitoAtualizacaoMonetariaFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return processoDebitoAtualizacaoMonetariaFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void alterarSituacaoParcela() {
        if (!selecionado.getTipo().equals(TipoProcessoDebito.REATIVACAO)) {
            situacaoParcela = SituacaoParcela.EM_ABERTO;
        } else {
            situacaoParcela = SituacaoParcela.SUSPENSAO;
        }
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setCadastro((CadastroEconomico) obj);
    }
}
