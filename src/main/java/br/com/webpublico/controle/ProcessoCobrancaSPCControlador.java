package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SelecaoResultadoParcela;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoCobrancaSPCFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoProcessoCobrancaSPC",
            pattern = "/tributario/processo-cobranca-spc/novo/",
            viewId = "/faces/tributario/processocobrancaspc/edita.xhtml"),
        @URLMapping(id = "editarProcessoCobrancaSPC",
            pattern = "/tributario/processo-cobranca-spc/editar/#{processoCobrancaSPCControlador.id}/",
            viewId = "/faces/tributario/processocobrancaspc/edita.xhtml"),
        @URLMapping(id = "verProcessoCobrancaSPC",
            pattern = "/tributario/processo-cobranca-spc/ver/#{processoCobrancaSPCControlador.id}/",
            viewId = "/faces/tributario/processocobrancaspc/visualizar.xhtml"),
        @URLMapping(id = "listarProcessoCobrancaSPC",
            pattern = "/tributario/processo-cobranca-spc/listar/",
            viewId = "/faces/tributario/processocobrancaspc/lista.xhtml")
})
public class ProcessoCobrancaSPCControlador extends PrettyControlador<ProcessoCobrancaSPC> implements Serializable, CRUD {

    @EJB
    private ProcessoCobrancaSPCFacade facade;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private List<SelecaoResultadoParcela> parcelas;
    private ParametroCobrancaSPC parametroCobrancaSPC;
    private List<LogEnvioSPC> logs;

    public ProcessoCobrancaSPCControlador() {
        super(ProcessoCobrancaSPC.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public List<SelecaoResultadoParcela> getParcelas() {
        return parcelas;
    }

    public ParametroCobrancaSPC getParametroCobrancaSPC() {
        if (parametroCobrancaSPC == null) {
            parametroCobrancaSPC = facade.getParametroCobrancaSPCFacade().recuperarUltimo();
        }
        return parametroCobrancaSPC;
    }

    public List<LogEnvioSPC> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEnvioSPC> logs) {
        this.logs = logs;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/processo-cobranca-spc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoProcessoCobrancaSPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataLancamento(new Date());
        selecionado.setSituacao(ProcessoCobrancaSPC.Situacao.ABERTO);
        selecionado.setUsuarioLancamento(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "editarProcessoCobrancaSPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verProcessoCobrancaSPC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public void abrirBuscaDebitos() {
        if (selecionado.getContribuinte() == null) {
            FacesUtil.addCampoObrigatorio("O campo Contribuinte deve ser informado.");
        } else {
            parcelas = Lists.newArrayList();
            valorInicial = BigDecimal.ZERO;
            valorFinal = BigDecimal.valueOf(3000);
            FacesUtil.executaJavaScript("dlgDebitos.show()");
        }
    }

    private void validarPesquisaDebitos() {
        if (valorInicial == null) {
            throw new ValidacaoException("O campo Valor Inicial deve ser informado.");
        }
        if (valorFinal == null) {
            throw new ValidacaoException("O campo Valor Final deve ser informado.");
        }
    }

    public void pesquisarDebitos() {
        try {
            validarPesquisaDebitos();
            ConsultaParcela consultaParcela = montarConsultaParcela();
            List<ResultadoParcela> resultados = consultaParcela.executaConsulta().getResultados();
            parcelas = resultados.stream()
                .filter(parc -> parc.getValorTotal().compareTo(valorInicial) >= 0 && parc.getValorTotal().compareTo(valorFinal) <= 0)
                .map(parc -> {
                    SelecaoResultadoParcela selecaoResultadoParcela = new SelecaoResultadoParcela();
                    selecaoResultadoParcela.setResultadoParcela(parc);
                    return selecaoResultadoParcela;
                })
                .collect(Collectors.toList());
            if (parcelas.isEmpty()) {
                FacesUtil.addAtencao("Nenhum débito encontrado.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private ConsultaParcela montarConsultaParcela() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL,
            selecionado.getContribuinte().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL,
            SituacaoParcela.EM_ABERTO.name());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR,
            new Date());
        ParametroCobrancaSPC parametro = getParametroCobrancaSPC();
        if (parametro != null) {
            if (!parametro.getDebitoExercicio()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
            }
            if (!parametro.getDebitoDividaAtiva()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 0);
            }
            if (!parametro.getDebitoDividaAtivaProtestada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DEBITO_PROTESTADO, ConsultaParcela.Operador.IGUAL, 0);
            }
            if (!parametro.getDebitoDividaAtivaAjuizada()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_AJUIZADA, ConsultaParcela.Operador.IGUAL, 0);
            }
        }
        return consultaParcela;
    }

    public void adicionarDebitos() {
        try {
            List<SelecaoResultadoParcela> parcelasSelecionadas = parcelas
                .stream()
                .filter(SelecaoResultadoParcela::isSelecionado)
                .collect(Collectors.toList());
            if (parcelasSelecionadas.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Nenhum débito selecionado para ser adicionado.");
            } else {
                for (SelecaoResultadoParcela parcelaSelecionada : parcelasSelecionadas) {
                    ItemProcessoCobrancaSPC itemProcessoCobrancaSPC = new ItemProcessoCobrancaSPC();
                    itemProcessoCobrancaSPC.setProcessoCobrancaSPC(selecionado);
                    itemProcessoCobrancaSPC.setParcelaValorDivida(new ParcelaValorDivida(parcelaSelecionada.getResultadoParcela().getIdParcela()));
                    itemProcessoCobrancaSPC.setValorImposto(parcelaSelecionada.getResultadoParcela().getValorImposto());
                    itemProcessoCobrancaSPC.setValorTaxa(parcelaSelecionada.getResultadoParcela().getValorTaxa());
                    itemProcessoCobrancaSPC.setValorDesconto(parcelaSelecionada.getResultadoParcela().getValorDesconto());
                    itemProcessoCobrancaSPC.setValorJuros(parcelaSelecionada.getResultadoParcela().getValorJuros());
                    itemProcessoCobrancaSPC.setValorMulta(parcelaSelecionada.getResultadoParcela().getValorMulta());
                    itemProcessoCobrancaSPC.setValorCorrecao(parcelaSelecionada.getResultadoParcela().getValorCorrecao());
                    itemProcessoCobrancaSPC.setValorHonorarios(parcelaSelecionada.getResultadoParcela().getValorHonorarios());
                    itemProcessoCobrancaSPC.setValorTotal(parcelaSelecionada.getResultadoParcela().getValorTotal());
                    itemProcessoCobrancaSPC.setResultadoParcela(parcelaSelecionada.getResultadoParcela());
                    selecionado.addItem(itemProcessoCobrancaSPC);
                }
                FacesUtil.executaJavaScript("dlgDebitos.hide()");
                FacesUtil.atualizarComponente("formulario");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void alterouContribuinte() {
        selecionado.setItens(Lists.newArrayList());
    }

    public boolean isPermitidoSelecionar(SelecaoResultadoParcela selecaoResultadoParcela) {
        return selecionado.getItens()
            .stream()
            .noneMatch(i -> i.getParcelaValorDivida().getId().equals(selecaoResultadoParcela.getResultadoParcela().getIdParcela()));
    }

    public void excluirItem(ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        selecionado.excluirItem(itemProcessoCobrancaSPC);
    }

    public void processar() {
        try {
            selecionado = facade.processar(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            if (ProcessoCobrancaSPC.Situacao.PROCESSADO.equals(selecionado.getSituacao())) {
                FacesUtil.addOperacaoRealizada("Cobrança pelo SPC processada com sucesso.");
            } else if (ProcessoCobrancaSPC.Situacao.PROCESSADO_INCONSISTENCIA.equals(selecionado.getSituacao())) {
                FacesUtil.addAtencao("Cobrança pelo SPC processada com inconsistência.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }


    @Override
    public void salvar() {
        salvar(Redirecionar.VER);
    }

    public Boolean permitePessoaFisica() {
        ParametroCobrancaSPC parametro = getParametroCobrancaSPC();
        return parametro == null || parametro.getPessoaFisica();
    }

    public Boolean permitePessoaJuridica() {
        ParametroCobrancaSPC parametro = getParametroCobrancaSPC();
        return parametro == null || parametro.getPessoaJuridica();

    }

    public void buscarLogsEnvioSPC(ItemProcessoCobrancaSPC item) {
        logs = facade.buscarLogsEnvioSPC(item);
    }

    public Boolean podeProcessar() {
        return selecionado.getItens().stream()
            .anyMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.AGUARDANDO_ENVIO)
                || i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.FALHA_INCLUSAO));
    }

    public Boolean podeEstornar() {
        return selecionado.getItens().stream()
            .anyMatch(i -> i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.FALHA_EXCLUSAO)
                    || i.getStatusSPC().equals(ItemProcessoCobrancaSPC.StatusSPC.INCLUIDO));
    }

    public void estornar() {
        try {
            selecionado = facade.estornar(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            if (ProcessoCobrancaSPC.Situacao.ESTORNADO.equals(selecionado.getSituacao())) {
                FacesUtil.addOperacaoRealizada("Cobrança pelo SPC estornada com sucesso.");
            } else if (ProcessoCobrancaSPC.Situacao.ESTORNADO_INCONSISTENCIA.equals(selecionado.getSituacao())) {
                FacesUtil.addAtencao("Cobrança pelo SPC estornada com inconsistência.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }
}
