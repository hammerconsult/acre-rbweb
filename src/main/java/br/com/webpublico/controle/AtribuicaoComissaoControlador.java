package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtribuicaoComissaoExtraCurricular;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtribuicaoComissaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 01/06/2017.
 */
@ManagedBean(name = "atribuicaoComissaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAtribuicaoComissao", pattern = "/atribuicao-comissao/novo/", viewId = "/faces/rh/concursos/atribuicaocomissao/edita.xhtml"),
    @URLMapping(id = "editarAtribuicaoComissao", pattern = "/atribuicao-comissao/editar/#{atribuicaoComissaoControlador.id}/", viewId = "/faces/rh/concursos/atribuicaocomissao/edita.xhtml"),
    @URLMapping(id = "verAtribuicaoComissao", pattern = "/atribuicao-comissao/ver/#{atribuicaoComissaoControlador.id}/", viewId = "/faces/rh/concursos/atribuicaocomissao/visualizar.xhtml"),
    @URLMapping(id = "listarAtribuicaoComissao", pattern = "/atribuicao-comissao/listar/", viewId = "/faces/rh/concursos/atribuicaocomissao/lista.xhtml")
})
public class AtribuicaoComissaoControlador extends PrettyControlador<AtribuicaoComissaoExtraCurricular> implements Serializable, CRUD {

    @EJB
    private AtribuicaoComissaoFacade facade;

    public AtribuicaoComissaoControlador() {
        super(AtribuicaoComissaoExtraCurricular.class);
    }

    @URLAction(mappingId = "novaAtribuicaoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAtribuicaoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAtribuicaoComissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/atribuicao-comissao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }
}
