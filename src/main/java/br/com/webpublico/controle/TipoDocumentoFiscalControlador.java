/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoDocumentoFiscal;
import br.com.webpublico.enums.Situacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDocumentoFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author claudio
 */
@ManagedBean(name = "tipoDocumentoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-documento-fiscal", pattern = "/tipo-documento-fiscal/novo/", viewId = "/faces/tributario/cadastromunicipal/tipodocumentofiscal/edita.xhtml"),
    @URLMapping(id = "editar-documento-fiscal", pattern = "/tipo-documento-fiscal/editar/#{tipoDocumentoFiscalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tipodocumentofiscal/edita.xhtml"),
    @URLMapping(id = "ver-documento-fiscal", pattern = "/tipo-documento-fiscal/ver/#{tipoDocumentoFiscalControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tipodocumentofiscal/visualizar.xhtml"),
    @URLMapping(id = "listar-documento-fiscal", pattern = "/tipo-documento-fiscal/listar/", viewId = "/faces/tributario/cadastromunicipal/tipodocumentofiscal/lista.xhtml")
})
public class TipoDocumentoFiscalControlador extends PrettyControlador<TipoDocumentoFiscal> implements Serializable, CRUD {

    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;

    public TipoDocumentoFiscalControlador() {
        super(TipoDocumentoFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDocumentoFiscalFacade;
    }

    public void selecionar(ActionEvent evento) {
        selecionado = (TipoDocumentoFiscal) evento.getComponent().getAttributes().get("objeto");
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            tipoDocumentoFiscalFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novo-documento-fiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-documento-fiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-documento-fiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-documento-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItemSemCampoVazio(Situacao.values());
    }

    public List<TipoDocumentoFiscal> completarTipoDocumentoFiscal(String parte) {
        return tipoDocumentoFiscalFacade.buscarTiposDeDocumentosAtivos(parte.trim());
    }
}
