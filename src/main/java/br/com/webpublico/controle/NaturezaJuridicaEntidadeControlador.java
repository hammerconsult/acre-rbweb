/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.NaturezaJuridicaEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NaturezaJuridicaEntidadeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean(name = "naturezaJuridicaEntidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoNaturezaJuridicaEntidade", pattern = "/natureza-juridica-entidade/novo/", viewId = "/faces/rh/administracaodepagamento/naturezajuridicaentidade/edita.xhtml"),
        @URLMapping(id = "editarNaturezaJuridicaEntidade", pattern = "/natureza-juridica-entidade/editar/#{naturezaJuridicaEntidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezajuridicaentidade/edita.xhtml"),
        @URLMapping(id = "listarNaturezaJuridicaEntidade", pattern = "/natureza-juridica-entidade/listar/", viewId = "/faces/rh/administracaodepagamento/naturezajuridicaentidade/lista.xhtml"),
        @URLMapping(id = "verNaturezaJuridicaEntidade", pattern = "/natureza-juridica-entidade/ver/#{naturezaJuridicaEntidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezajuridicaentidade/visualizar.xhtml")
})
public class NaturezaJuridicaEntidadeControlador extends PrettyControlador<NaturezaJuridicaEntidade> implements Serializable, CRUD {

    @EJB
    private NaturezaJuridicaEntidadeFacade naturezaJuridicaEntidadeFacade;

    @Override
    public AbstractFacade getFacede() {
        return this.naturezaJuridicaEntidadeFacade;
    }

    public NaturezaJuridicaEntidadeControlador() {
        super(NaturezaJuridicaEntidade.class);
    }

    public Boolean validaCampos() {
        Boolean retorno = Util.validaCampos(selecionado);

        if (naturezaJuridicaEntidadeFacade.validaCodigo((NaturezaJuridicaEntidade) selecionado)) {
            FacesUtil.addError("Atenção!", "O código já foi cadastrado.");
            retorno = false;
        }

        return retorno;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/natureza-juridica-entidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoNaturezaJuridicaEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarNaturezaJuridicaEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verNaturezaJuridicaEntidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
