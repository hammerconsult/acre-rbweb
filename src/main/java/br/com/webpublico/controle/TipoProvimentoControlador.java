/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoProvimentoFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "tipoProvimentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoProvimento", pattern = "/tipoprovimento/novo/", viewId = "/faces/rh/administracaodepagamento/tipoprovimento/edita.xhtml"),
    @URLMapping(id = "editarTipoProvimento", pattern = "/tipoprovimento/editar/#{tipoProvimentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoprovimento/edita.xhtml"),
    @URLMapping(id = "listarTipoProvimento", pattern = "/tipoprovimento/listar/", viewId = "/faces/rh/administracaodepagamento/tipoprovimento/lista.xhtml"),
    @URLMapping(id = "verTipoProvimento", pattern = "/tipoprovimento/ver/#{tipoProvimentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoprovimento/visualizar.xhtml")
})
public class TipoProvimentoControlador extends PrettyControlador<TipoProvimento> implements Serializable, CRUD {

    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;

    public TipoProvimentoControlador() {
        super(TipoProvimento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoProvimentoFacade;
    }

    @URLAction(mappingId = "novoTipoProvimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoProvimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoProvimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        TipoProvimento tipoSelecionado = (TipoProvimento) selecionado;
        if (Util.validaCampos(selecionado)) {
            if (tipoProvimentoFacade.existeCodigo(tipoSelecionado)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Código informado já está cadastrado em outro Tipo de Provimento !");
                FacesContext.getCurrentInstance().addMessage("msg", msg);
                retorno = false;
            }
        } else {
            retorno = false;
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipoprovimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
