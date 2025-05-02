package br.com.webpublico.controle.rh.administracaodepagamento.eventofpsimplificado;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.TipoLancamentoFPSimplificado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.EventoFPSimplificadoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 07/02/2019.
 */
@ManagedBean(name = "eventoFPSimplificadoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-eventofp", pattern = "/eventofp-simplificado/novo/", viewId = "/faces/rh/administracaodepagamento/eventofp-simplificado/edita.xhtml"),
    @URLMapping(id = "editar-eventofp", pattern = "/eventofp-simplificado/editar/#{eventoFPSimplificadoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/eventofp-simplificado/edita.xhtml"),
    @URLMapping(id = "listar-eventofp", pattern = "/eventofp-simplificado/listar/", viewId = "/faces/rh/administracaodepagamento/eventofp-simplificado/lista.xhtml"),
    @URLMapping(id = "ver-eventofp", pattern = "/eventofp-simplificado/ver/#{eventoFPSimplificadoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/eventofp-simplificado/visualizar.xhtml")
})
public class EventoFPSimplificadoControlador extends PrettyControlador<EventoFP> implements Serializable, CRUD {

    private EventoFPTipoFolha eventoFPTipoFolha;
    @EJB
    private EventoFPFacade eventoFPFacade;

    @EJB
    private EventoFPSimplificadoFacade eventoFPSimplificadoFacade;

    public EventoFPSimplificadoControlador() {
        super(EventoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return eventoFPSimplificadoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/eventofp-simplificado/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public EventoFPTipoFolha getEventoFPTipoFolha() {
        return eventoFPTipoFolha;
    }

    public void setEventoFPTipoFolha(EventoFPTipoFolha eventoFPTipoFolha) {
        this.eventoFPTipoFolha = eventoFPTipoFolha;
    }

    public List<SelectItem> getTipoFolhaDePagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    @URLAction(mappingId = "novo-eventofp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        eventoFPTipoFolha = new EventoFPTipoFolha();
        selecionado.setUltimoAcumuladoEmDezembro(false);
    }

    @URLAction(mappingId = "ver-eventofp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-eventofp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        eventoFPTipoFolha = new EventoFPTipoFolha();
    }

    public List<SelectItem> getTipoLancamento() {
        return Util.getListSelectItem(TipoLancamentoFPSimplificado.values());
    }

    public boolean isLancamentoReferencia() {
        return selecionado.getTipoLancamentoFPSimplificado() != null && TipoLancamentoFPSimplificado.REFERENCIA.equals(selecionado.getTipoLancamentoFPSimplificado());
    }

    public boolean isLancamentoValor() {
        return selecionado.getTipoLancamentoFPSimplificado() != null && TipoLancamentoFPSimplificado.VALOR.equals(selecionado.getTipoLancamentoFPSimplificado());
    }

    private Boolean validaCodigoAlfaNumerico(String codigo) {
        return codigo.matches("[a-zA-Z0-9]*");
    }

    private void validarInformacoes() {
        ValidacaoException ve = new ValidacaoException();
        String codigo = selecionado.getCodigo();
        if (!validaCodigoAlfaNumerico(codigo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Código deve ser Alfanumérico(O Código deve possuir apenas letras e números)");
        }
        if (codigo != null && isOperacaoNovo()) {
            if (eventoFPFacade.verificaCodigoSalvo(codigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um código com este número.");
            }
        }
        if (codigo != null && isOperacaoEditar()) {
            if (!eventoFPFacade.verificaCodigoEditar(selecionado) && eventoFPFacade.verificaCodigoSalvo(codigo)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Edição não permitida, já existe um código com este número.");
            }
        }
        if (selecionado.getTipoLancamentoFPSimplificado() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Lançamento do evento.");
        }
        if (selecionado.getTipoLancamentoFPSimplificado() != null && TipoLancamentoFPSimplificado.REFERENCIA.equals(selecionado.getTipoLancamentoFPSimplificado())) {
            if (selecionado.getBaseFP() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Para o lançamento por referência deve ser informado a Base FP.");
            }
        }
        if (isLancamentoValor() && selecionado.getValorMaximoLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Valor Máximo Permitido em Lançamento FP deve ser informado.");
        } else if (isLancamentoValor() && selecionado.getValorMaximoLancamento().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O Valor Máximo Permitido em Lançamento FP deve ser maior que zero.");
        }
        ve.lancarException();
    }

    public void adicionarEventoFPTipoFolha() {
        try {
            validarCamposObrigatoriosTipoFolha();
            associarEAdicionarEventoTipoFolha(eventoFPTipoFolha);
            this.eventoFPTipoFolha = new EventoFPTipoFolha();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar um novo Tipo de Folha, erro:" + e.getMessage());
        }
    }

    private void validarCamposObrigatoriosTipoFolha() {
        ValidacaoException ve = new ValidacaoException();
        if (eventoFPTipoFolha != null) {
            if (eventoFPTipoFolha.getTipoFolhaDePagamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione o Tipo de Folha.");
            }
        }
        if (selecionado.getTiposFolha() != null) {
            for (EventoFPTipoFolha fpTipoFolha : selecionado.getTiposFolha()) {
                if (eventoFPTipoFolha.getTipoFolhaDePagamento().equals(fpTipoFolha.getTipoFolhaDePagamento())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Tipo de folha já adicionado.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private void associarEAdicionarEventoTipoFolha(EventoFPTipoFolha eventoFPTipoFolha) {
        eventoFPTipoFolha.setEventoFP(selecionado);
        selecionado.getTiposFolha().add(eventoFPTipoFolha);
    }

    public void limparValoresTipoLancamento() {
        selecionado.setBaseFP(null);
        selecionado.setValorMaximoLancamento(BigDecimal.ZERO);
    }

    public void removerEvento(EventoFPTipoFolha evento) {
        if (selecionado.getTiposFolha().contains(evento)) {
            selecionado.getTiposFolha().remove(evento);
        }
    }

    @Override
    public void salvar() {
        try {
            validarInformacoes();
            if (TipoLancamentoFPSimplificado.REFERENCIA.equals(selecionado.getTipoLancamentoFPSimplificado())) {
                selecionado.setFormula(selecionado.getTipoLancamentoFPSimplificado().getFormula().replace("baseParaCalculo", selecionado.getBaseFP().getCodigo()));
                selecionado.setFormulaValorIntegral(selecionado.getFormula());
            }
            if (TipoLancamentoFPSimplificado.VENCIMENTO_BASE.equals(selecionado.getTipoLancamentoFPSimplificado())) {
                selecionado.setFormula(selecionado.getTipoLancamentoFPSimplificado().getFormula());
                selecionado.setFormulaValorIntegral(selecionado.getFormula());
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
