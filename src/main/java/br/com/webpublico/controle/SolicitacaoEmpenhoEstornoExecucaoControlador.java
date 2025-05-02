package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoEmpenhoEstornoExecucaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.jetbrains.annotations.NotNull;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolEmpExec", pattern = "/solicitacao-empenho-estorno/novo/", viewId = "/faces/administrativo/contrato/solicitacao-empenho-estorno-execucao/edita.xhtml"),
    @URLMapping(id = "listaSolEmpExec", pattern = "/solicitacao-empenho-estorno/listar/", viewId = "/faces/administrativo/contrato/solicitacao-empenho-estorno-execucao/lista.xhtml"),
    @URLMapping(id = "verSolEmpExec", pattern = "/solicitacao-empenho-estorno/ver/#{solicitacaoEmpenhoEstornoExecucaoControlador.id}/", viewId = "/faces/administrativo/contrato/solicitacao-empenho-estorno-execucao/visualizar.xhtml")
})
public class SolicitacaoEmpenhoEstornoExecucaoControlador extends PrettyControlador<SolicitacaoEmpenhoEstornoExecucao> implements Serializable, CRUD {

    @EJB
    SolicitacaoEmpenhoEstornoExecucaoFacade facade;

    private List<Empenho> empenhos;

    private Empenho empenho;

    private ExecucaoContrato execucaoContrato;


    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;

    private ExecucaoContratoEstorno execucaoContratoEstorno;

    public SolicitacaoEmpenhoEstornoExecucaoControlador() {
        super(SolicitacaoEmpenhoEstornoExecucao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-empenho-estorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public void limparEmpenhos() {
        empenhos = new ArrayList<>();
    }

    public ExecucaoContratoEstorno getExecucaoContratoEstorno() {
        return execucaoContratoEstorno;
    }

    public void setExecucaoContratoEstorno(ExecucaoContratoEstorno execucaoContratoEstorno) {
        this.execucaoContratoEstorno = execucaoContratoEstorno;
    }

    private void inicializar() {
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        empenhos = new ArrayList<>();
    }

    @Override
    @URLAction(mappingId = "novaSolEmpExec", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializar();
    }

    @Override
    @URLAction(mappingId = "verSolEmpExec", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar(Redirecionar.VER);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void buscarEmpenhosExecucao() {
        limparEmpenhos();
        if (selecionado.getExecucaoContrato() != null) {
            ExecucaoContrato execucaoContrato = facade.getExecucaoContratoFacade().recuperarComDependenciasExecucaoEmpenho(selecionado.getExecucaoContrato().getId());
            for (ExecucaoContratoEmpenho empenho : execucaoContrato.getEmpenhos()) {
                if (empenho.getEmpenho() != null) {
                    empenhos.add(empenho.getEmpenho());
                    empenhos.addAll(facade.getEmpenhoFacade().buscarEmpenhoRestoPagarExercicio(empenho.getEmpenho(), facade.getSistemaFacade().getExercicioCorrente()));
                }
            }
        }
    }

    public List<ExecucaoContrato> completarExecucao(String parte) {
        return facade.getExecucaoContratoFacade().buscarExecucaoContratoComSaldoAEstornar(parte.trim());
    }

    public void criarSolicitacaoEmpenho() {
        try {
            if (empenho != null) {
                SolicitacaoEmpenho solEmp = empenho.isEmpenhoNormal() ? empenho.getSolicitacaoEmpenho() : empenho.getEmpenho().getSolicitacaoEmpenho();
                validarSolicitacaoEmpenho(solEmp);
                SolicitacaoEmpenhoEstorno novaSolEmpEstorno = new SolicitacaoEmpenhoEstorno(solEmp, empenho, OrigemSolicitacaoEmpenho.CONTRATO);
                novaSolEmpEstorno.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
                novaSolEmpEstorno.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                novaSolEmpEstorno.setValor(empenho.getSaldo());

                novaSolEmpEstorno.setHistorico(facade.getSolicitacaoEmpenhoEstornoFacade().getHistoricoSolicitacaoEmpenho(solEmp.getClasseCredor(), solEmp.getFonteDespesaORC(), selecionado.getExecucaoContrato()));
                selecionado.setSolicitacaoEmpenhoEstorno(novaSolEmpEstorno);
            }
        } catch (ValidacaoException ve) {
            setEmpenho(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarSolicitacaoEmpenho(SolicitacaoEmpenho solEmp) {
        ValidacaoException ve = new ValidacaoException();
        if (solEmp == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O empenho selecionado não possui solicitação de empenho.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getEmpenhosExecucao() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        if (!empenhos.isEmpty()) {
            for (Empenho emp : empenhos) {
                list.add(new SelectItem(emp, emp.toString()));
            }
        }
        return list;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCamposObrigatorios(selecionado, ve);

        if (selecionado.getExecucaoContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Execução de Contrato deve ser informado.");
        }
        if (empenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado.");
        }
        if (selecionado.getExecucaoContrato() != null && empenho != null) {
            if (selecionado.getSolicitacaoEmpenhoEstorno().getValor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Valor deve ser informado.");
            }
            if (selecionado.getSolicitacaoEmpenhoEstorno().getHistorico() == null || selecionado.getSolicitacaoEmpenhoEstorno().getHistorico().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Histórico deve ser preenchido.");
            }
            if (selecionado.getSolicitacaoEmpenhoEstorno().getValor().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero.");
            }
            if (selecionado.getSolicitacaoEmpenhoEstorno().getValor().compareTo(empenho.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor não deve ser maior que o saldo do empenho.");
            }
        }
        ve.lancarException();
    }
}
