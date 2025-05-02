/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoAdmissaoRAIS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAdmissaoRAISFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoAdmissaoRAISControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoAdmissaoRAIS", pattern = "/rh/tipo-de-admissao-da-rais/novo/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaorais/edita.xhtml"),
        @URLMapping(id = "listaTipoAdmissaoRAIS", pattern = "/rh/tipo-de-admissao-da-rais/listar/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaorais/lista.xhtml"),
        @URLMapping(id = "verTipoAdmissaoRAIS", pattern = "/rh/tipo-de-admissao-da-rais/ver/#{tipoAdmissaoRAISControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaorais/visualizar.xhtml"),
        @URLMapping(id = "editarTipoAdmissaoRAIS", pattern = "/rh/tipo-de-admissao-da-rais/editar/#{tipoAdmissaoRAISControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaorais/edita.xhtml"),
})
public class TipoAdmissaoRAISControlador extends PrettyControlador<TipoAdmissaoRAIS> implements Serializable, CRUD {

    @EJB
    private TipoAdmissaoRAISFacade tipoAdmissaoRAISFacade;

    public TipoAdmissaoRAISControlador() {
        super(TipoAdmissaoRAIS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAdmissaoRAISFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-admissao-da-rais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tipoAdmissaoRAISFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção!", "O Código informado já está cadastrado em outro Tipo de Admissão RAIS");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoTipoAdmissaoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoAdmissaoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoAdmissaoRAIS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
}
