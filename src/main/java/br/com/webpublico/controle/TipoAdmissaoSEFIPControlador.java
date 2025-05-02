/*
 * Codigo gerado automaticamente em Tue Feb 07 08:11:08 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoAdmissaoSEFIP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAdmissaoSEFIPFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoAdmissaoSEFIPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoAdmissaoSEFIP", pattern = "/rh/tipo-de-admissao-do-sefip/novo/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaosefip/edita.xhtml"),
        @URLMapping(id = "listaTipoAdmissaoSEFIP", pattern = "/rh/tipo-de-admissao-do-sefip/listar/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaosefip/lista.xhtml"),
        @URLMapping(id = "verTipoAdmissaoSEFIP", pattern = "/rh/tipo-de-admissao-do-sefip/ver/#{tipoAdmissaoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaosefip/visualizar.xhtml"),
        @URLMapping(id = "editarTipoAdmissaoSEFIP", pattern = "/rh/tipo-de-admissao-do-sefip/editar/#{tipoAdmissaoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoadmissaosefip/edita.xhtml"),
})
public class TipoAdmissaoSEFIPControlador extends PrettyControlador<TipoAdmissaoSEFIP> implements Serializable, CRUD {

    @EJB
    private TipoAdmissaoSEFIPFacade tipoAdmissaoSEFIPFacade;

    public TipoAdmissaoSEFIPControlador() {
        super(TipoAdmissaoSEFIP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAdmissaoSEFIPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-admissao-do-sefip/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tipoAdmissaoSEFIPFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Tipo de Admissão SEFIP");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoTipoAdmissaoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoAdmissaoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoAdmissaoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
