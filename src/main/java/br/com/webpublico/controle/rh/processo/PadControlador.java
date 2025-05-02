package br.com.webpublico.controle.rh.processo;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.processo.Pad;
import br.com.webpublico.enums.rh.processo.TipoPad;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.processo.PadFacade;
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
import java.util.Date;
import java.util.List;

@ManagedBean(name = "padControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPad", pattern = "/processo-administrativo-disciplinar/novo/", viewId = "/faces/rh/processo/processoadministrativodisciplinar/edita.xhtml"),
    @URLMapping(id = "editarPad", pattern = "/processo-administrativo-disciplinar/editar/#{padControlador.id}/", viewId = "/faces/rh/processo/processoadministrativodisciplinar/edita.xhtml"),
    @URLMapping(id = "verPad", pattern = "/processo-administrativo-disciplinar/ver/#{padControlador.id}/", viewId = "/faces/rh/processo/processoadministrativodisciplinar/visualizar.xhtml"),
    @URLMapping(id = "listarPad", pattern = "/processo-administrativo-disciplinar/listar/", viewId = "/faces/rh/processo/processoadministrativodisciplinar/lista.xhtml")


})
public class PadControlador extends PrettyControlador<Pad> implements Serializable, CRUD {

    @EJB
    private SistemaFacade sistemaFacade;

    @EJB
    private PadFacade padFacade;

    @EJB
    private ContratoFPFacade contratoFPFacade;

    public PadControlador() {
        super(Pad.class);
    }

    @URLAction(mappingId = "novoPad", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setResponsavel(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataCadastro(new Date());
    }

    @URLAction(mappingId = "editarPad", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verPad", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return padFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-administrativo-disciplinar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTipoPad() {
        return Util.getListSelectItem(TipoPad.values(), false);
    }

    @Override
    public void salvar() {
        try {
            validarInformacoes();
            selecionado.setDataCadastro(new Date());
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    private void validarInformacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Servidor.");
        }
        if (selecionado.getProtocolo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Protocolo.");
        }
        if (selecionado.getTipoPad() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de P.A.D.");
        }
        ve.lancarException();
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        return contratoFPFacade.recuperarContratoDiferentePrefeitoAndSecretario(parte);
    }


    public void cancelarProcessoParaServidor(){
        selecionado.setContratoFP(null);
    }
}
