/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cor;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CorFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "corControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaCor", pattern = "/cor/novo/", viewId = "/faces/tributario/cadastromunicipal/cor/edita.xhtml"),
        @URLMapping(id = "editarCor", pattern = "/cor/editar/#{corControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cor/edita.xhtml"),
        @URLMapping(id = "listarCor", pattern = "/cor/listar/", viewId = "/faces/tributario/cadastromunicipal/cor/lista.xhtml"),
        @URLMapping(id = "verCor", pattern = "/cor/ver/#{corControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cor/visualizar.xhtml")
})
public class CorControlador extends PrettyControlador<Cor> implements Serializable, CRUD {

    @EJB
    private CorFacade corFacade;

    public CorControlador() {
        super(Cor.class);
    }

    @URLAction(mappingId = "novaCor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verCor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cor/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return corFacade;
    }


    @Override
    protected String getMensagemSucessoAoSalvar() {
        return "A cor " + selecionado.getDescricao() + " foi salva com sucesso.";
    }

    @Override
    protected String getMensagemSucessoAoExcluir() {
        return "A cor " + selecionado.getDescricao() + " foi excluída com sucesso.";
    }

    public List<Cor> cores() {
        return corFacade.listarOrdenandoPorDescricaoDescrescente();
    }
}
