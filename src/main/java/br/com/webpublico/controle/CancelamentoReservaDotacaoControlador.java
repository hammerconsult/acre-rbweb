package br.com.webpublico.controle;

import br.com.webpublico.entidades.CancelamentoReservaDotacao;
import br.com.webpublico.entidades.SolicitacaoCancelamentoReservaDotacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CancelamentoReservaDotacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/06/14
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "cancelamentoReservaDotacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cancelamento-reserva-dotacao", pattern = "/cancelamento-reserva-dotacao/novo/", viewId = "/faces/financeiro/orcamentario/cancelamentoreservadotacao/edita.xhtml"),
    @URLMapping(id = "editar-cancelamento-reserva-dotacao", pattern = "/cancelamento-reserva-dotacao/editar/#{cancelamentoReservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/cancelamentoreservadotacao/edita.xhtml"),
    @URLMapping(id = "ver-cancelamento-reserva-dotacao", pattern = "/cancelamento-reserva-dotacao/ver/#{cancelamentoReservaDotacaoControlador.id}/", viewId = "/faces/financeiro/orcamentario/cancelamentoreservadotacao/visualizar.xhtml"),
    @URLMapping(id = "listar-cancelamento-reserva-dotacao", pattern = "/cancelamento-reserva-dotacao/listar/", viewId = "/faces/financeiro/orcamentario/cancelamentoreservadotacao/lista.xhtml")
})
public class CancelamentoReservaDotacaoControlador extends PrettyControlador<CancelamentoReservaDotacao> implements Serializable, CRUD {

    @EJB
    private CancelamentoReservaDotacaoFacade cancelamentoReservaDotacaoFacade;
    private ConverterAutoComplete converterSolicitacao;


    public CancelamentoReservaDotacaoControlador() {
        super(CancelamentoReservaDotacao.class);
    }

    @URLAction(mappingId = "novo-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(getSistemaControlador().getDataOperacao());
        selecionado.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "ver-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-cancelamento-reserva-dotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cancelamento-reserva-dotacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return cancelamentoReservaDotacaoFacade;
    }


    //CONVERTERS

    public ConverterAutoComplete getConverterSolicitacao() {
        if (converterSolicitacao == null) {
            converterSolicitacao = new ConverterAutoComplete(SolicitacaoCancelamentoReservaDotacao.class, cancelamentoReservaDotacaoFacade.getSolicitacaoCancelamentoReservaDotacaoFacade());
        }
        return converterSolicitacao;
    }

    //COMPLETA
    public List<SolicitacaoCancelamentoReservaDotacao> completaSolicitacao(String parte) {
        return cancelamentoReservaDotacaoFacade.getSolicitacaoCancelamentoReservaDotacaoFacade().listarSolicitacaoAbertaPorNumeroEConta(parte);
    }


    @Override
    public void salvar() {
        try {
            Util.validaCampos(selecionado);
            if (operacao == Operacoes.NOVO) {
                getFacede().salvarNovo(selecionado);
            } else {
                getFacede().salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
        }
    }
}
