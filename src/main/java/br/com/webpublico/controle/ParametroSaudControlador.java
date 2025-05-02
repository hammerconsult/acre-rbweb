/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroSaud;
import br.com.webpublico.entidades.ParametroSaudDocumento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ParametroSaudFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarParametroSaud", pattern = "/tributario/saud/documentacao-saud/listar/",
        viewId = "/faces/tributario/saud/parametrosaud/lista.xhtml"),
    @URLMapping(id = "editarParametroSaud", pattern = "/tributario/saud/documentacao-saud/editar/#{parametroSaudControlador.id}/",
        viewId = "/faces/tributario/saud/parametrosaud/edita.xhtml"),
    @URLMapping(id = "verParametroSaud", pattern = "/tributario/saud/documentacao-saud/ver/#{parametroSaudControlador.id}/",
        viewId = "/faces/tributario/saud/parametrosaud/visualizar.xhtml"),
    @URLMapping(id = "novoParametroSaud", pattern = "/tributario/saud/documentacao-saud/novo/",
        viewId = "/faces/tributario/saud/parametrosaud/edita.xhtml")
})
public class ParametroSaudControlador extends PrettyControlador<ParametroSaud> implements Serializable, CRUD {

    @EJB
    private ParametroSaudFacade facade;
    private ParametroSaudDocumento documento;

    public ParametroSaudControlador() {
        super(ParametroSaud.class);
    }

    @Override
    public ParametroSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/documentacao-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ParametroSaudDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(ParametroSaudDocumento documento) {
        this.documento = documento;
    }

    @URLAction(mappingId = "novoParametroSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioRegistro(facade.getSistemaFacade().getUsuarioCorrente());
        criarNovoDocumento();
    }

    @URLAction(mappingId = "editarParametroSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        criarNovoDocumento();
    }

    @URLAction(mappingId = "verParametroSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void editarDocumento(ParametroSaudDocumento documento) {
        this.documento = documento;
        removerDocumento(this.documento);
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            documento.setParametroSaud(selecionado);
            selecionado.getDocumentos().add(documento);
            criarNovoDocumento();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumento() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(documento);
        for (ParametroSaudDocumento parametroSaudDocumento : selecionado.getDocumentos()) {
            if (parametroSaudDocumento.getDescricao().toLowerCase().trim().equals(documento.getDescricao().trim().toLowerCase())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("Já existe um documento adicionado com essa mesma descrição");
            }
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    private void criarNovoDocumento() {
        documento = new ParametroSaudDocumento();
    }

    public void removerDocumento(ParametroSaudDocumento documento) {
        selecionado.getDocumentos().remove(documento);
    }

}
