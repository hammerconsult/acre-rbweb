package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
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

@ManagedBean(name = "tipoBaseFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoBaseFP", pattern = "/tipo-basefp/novo/", viewId = "/faces/rh/administracaodepagamento/tipobasefp/edita.xhtml"),
        @URLMapping(id = "editarTipoBaseFP", pattern = "/tipo-basefp/editar/#{tipoBaseFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipobasefp/edita.xhtml"),
        @URLMapping(id = "listarTipoBaseFP", pattern = "/tipo-basefp/listar/", viewId = "/faces/rh/administracaodepagamento/tipobasefp/lista.xhtml"),
        @URLMapping(id = "verTipoBaseFP", pattern = "/tipo-basefp/ver/#{tipoBaseFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipobasefp/visualizar.xhtml")
})
public class TipoBaseFPControlador extends PrettyControlador<TipoBaseFP> implements Serializable, CRUD {

    @EJB
    private TipoBaseFPFacade tipoBaseFPFacade;

    public TipoBaseFPControlador() {
        super(TipoBaseFP.class);
    }

    @URLAction(mappingId = "novoTipoBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    private boolean validaCampos() {
        boolean valido = Util.validaCampos(this.selecionado);

        if (tipoBaseFPFacade.isCodigoExistente(selecionado)) {
            FacesUtil.addWarn("Atenção!", "O código informado já existe cadastrado no sistema.");
            valido = false;
        }

        return valido;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-basefp/";
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoBaseFPFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
