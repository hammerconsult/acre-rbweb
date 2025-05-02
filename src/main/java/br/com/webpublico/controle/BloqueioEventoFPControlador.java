package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioEventoFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "bloqueioEventoFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBloqueioEventoFP", pattern = "/bloqueio-eventofp/novo/", viewId = "/faces/rh/administracaodepagamento/bloqueioeventofp/edita.xhtml"),
    @URLMapping(id = "editarBloqueioEventoFP", pattern = "/bloqueio-eventofp/editar/#{bloqueioEventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/bloqueioeventofp/edita.xhtml"),
    @URLMapping(id = "listarBloqueioEventoFP", pattern = "/bloqueio-eventofp/listar/", viewId = "/faces/rh/administracaodepagamento/bloqueioeventofp/lista.xhtml"),
    @URLMapping(id = "verBloqueioEventoFP", pattern = "/bloqueio-eventofp/ver/#{bloqueioEventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/bloqueioeventofp/visualizar.xhtml")
})
public class BloqueioEventoFPControlador extends PrettyControlador<BloqueioEventoFP> implements Serializable, CRUD {

    @EJB
    private BloqueioEventoFPFacade bloqueioEventoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    private ConverterAutoComplete converterEventoFP;

    public BloqueioEventoFPControlador() {
        super(BloqueioEventoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return bloqueioEventoFPFacade;
    }

    @URLAction(mappingId = "novoBloqueioEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verBloqueioEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBloqueioEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (fichaFinanceiraFPFacade.verificaExistenciaFichaCalculadaPorDataEStatus(selecionado.getVinculoFP(), selecionado.getDataInicial(), selecionado.getDataFinal(), StatusCompetencia.EFETIVADA)) {
            FacesUtil.addAtencao("Já existe cálculo efetivado para este servidor.");
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    @Override
    public void excluir() {
        if (validaExclusao()) {
            super.excluir();
        }
    }

    private boolean validaExclusao() {
        Boolean retorno = Boolean.TRUE;

        if (fichaFinanceiraFPFacade.verificaExistenciaFichaCalculadaPorDataEStatus(selecionado.getVinculoFP(), selecionado.getDataInicial(), selecionado.getDataFinal(), StatusCompetencia.EFETIVADA)) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Já existe cálculo efetivado para este servidor.");
            retorno = Boolean.FALSE;
        }
        if (fichaFinanceiraFPFacade.verificaExistenciaFichaCalculadaPorDataEStatus(selecionado.getVinculoFP(), selecionado.getDataInicial(), selecionado.getDataFinal(), StatusCompetencia.ABERTA)) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Você deve excluir o cálculo deste servidor para excluir.");
            retorno = Boolean.FALSE;
        }

        return retorno;

    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        if (selecionado.getEventoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Evento FP deve ser informado.");
        }
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (selecionado.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getDataInicial().after(selecionado.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser inferior ou igual a Data Final.");
        }
        ve.lancarException();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bloqueio-eventofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<VinculoFP> completaServidor(String parte) {
        return vinculoFPFacade.listaTodosVinculosVigentes(parte.trim());
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }
}
