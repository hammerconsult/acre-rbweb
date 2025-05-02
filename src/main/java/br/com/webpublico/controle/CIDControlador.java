/*
 * Codigo gerado automaticamente em Mon Sep 05 08:40:28 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CID;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CIDFacade;
import br.com.webpublico.util.EntidadeMetaData;
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

@ManagedBean(name = "cIDControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCid", pattern = "/cid/novo/", viewId = "/faces/rh/administracaodepagamento/cid/edita.xhtml"),
        @URLMapping(id = "editarCid", pattern = "/cid/editar/#{cIDControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cid/edita.xhtml"),
        @URLMapping(id = "listarCid", pattern = "/cid/listar/", viewId = "/faces/rh/administracaodepagamento/cid/lista.xhtml"),
        @URLMapping(id = "verCid", pattern = "/cid/ver/#{cIDControlador.id}/", viewId = "/faces/rh/administracaodepagamento/cid/visualizar.xhtml")
})
public class CIDControlador extends PrettyControlador<CID> implements Serializable, CRUD {

    @EJB
    private CIDFacade cIDFacade;

    public CIDControlador() {
        metadata = new EntidadeMetaData(CID.class);
    }

    @URLAction(mappingId = "novoCid", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verCid", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarCid", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public CIDFacade getFacade() {
        return cIDFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return cIDFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cid/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado) && validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        if (cIDFacade.existeCodigo(selecionado)) {
            FacesUtil.addError("", "Já existe um CID cadastrado com o código informado. <b>Informe um código diferente e tente novamente.</b>");
            return false;
        }
        return true;
    }
}

