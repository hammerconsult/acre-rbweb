/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AnalistaResponsavelAlvaraImediato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AnalistaResponsavelAlvaraImediatoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarAnalistaResponsavelAlvaraImediato",
        pattern = "/tributario/alvara-imediato/analista-responsavel/listar/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/lista.xhtml"),
    @URLMapping(id = "novoAnalistaResponsavelAlvaraImediato",
        pattern = "/tributario/alvara-imediato/analista-responsavel/novo/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/edita.xhtml"),
    @URLMapping(id = "editarAnalistaResponsavelAlvaraImediato",
        pattern = "/tributario/alvara-imediato/analista-responsavel/editar/#{analistaResponsavelAlvaraImediatoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/edita.xhtml"),
    @URLMapping(id = "verAnalistaResponsavelAlvaraImediato",
        pattern = "/tributario/alvara-imediato/analista-responsavel/ver/#{analistaResponsavelAlvaraImediatoControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/analistaresponsavel/visualizar.xhtml")
})
public class AnalistaResponsavelAlvaraImediatoControlador extends PrettyControlador<AnalistaResponsavelAlvaraImediato> implements Serializable, CRUD {

    @EJB
    private AnalistaResponsavelAlvaraImediatoFacade facade;

    public AnalistaResponsavelAlvaraImediatoControlador() {
        super(AnalistaResponsavelAlvaraImediato.class);
    }

    @Override
    public AnalistaResponsavelAlvaraImediatoFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/alvara-imediato/analista-responsavel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAnalistaResponsavelAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarAnalistaResponsavelAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verAnalistaResponsavelAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<AnalistaResponsavelAlvaraImediato> completarAnalistasAtivos(String parte) {
        return facade.completarAnalistasAtivos(parte);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (facade.buscarAnalistaPeloIdUsuarioSistema(selecionado.getId(), selecionado.getUsuarioSistema().getId()) != null) {
            new ValidacaoException("O Analista Responsável informado ja está cadastrado!").lancarException();
            return false;
        }
        return true;
    }
}
