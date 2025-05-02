/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoAdmissaoFGTS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAdmissaoFGTSFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoAdmissaoFGTSControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoAdmissaoFGTS", pattern = "/rh/tipo-de-admissao-do-fgts/novo/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaofgts/edita.xhtml"),
        @URLMapping(id = "listaTipoAdmissaoFGTS", pattern = "/rh/tipo-de-admissao-do-fgts/listar/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaofgts/lista.xhtml"),
        @URLMapping(id = "verTipoAdmissaoFGTS", pattern = "/rh/tipo-de-admissao-do-fgts/ver/#{tipoAdmissaoFGTSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaofgts/visualizar.xhtml"),
        @URLMapping(id = "editarTipoAdmissaoFGTS", pattern = "/rh/tipo-de-admissao-do-fgts/editar/#{tipoAdmissaoFGTSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaofgts/edita.xhtml"),
})
public class TipoAdmissaoFGTSControlador extends PrettyControlador<TipoAdmissaoFGTS> implements Serializable, CRUD {

    @EJB
    private TipoAdmissaoFGTSFacade tipoAdmissaoFGTSFacade;

    public TipoAdmissaoFGTSControlador() {
        super(TipoAdmissaoFGTS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAdmissaoFGTSFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-admissao-do-fgts/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tipoAdmissaoFGTSFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção!", "O Código informado já está cadastrado em outro Tipo de Admissão FGTS");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoTipoAdmissaoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoAdmissaoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoAdmissaoFGTS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
