/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.RecolhimentoSEFIP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RecolhimentoSEFIPFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "recolhimentoSEFIPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRecolhimentoSEFIP", pattern = "/rh/codigos-de-recolhimento-do-sefip/novo/", viewId = "/faces/rh/administracaodepagamento/recolhimentosefip/edita.xhtml"),
        @URLMapping(id = "listaRecolhimentoSEFIP", pattern = "/rh/codigos-de-recolhimento-do-sefip/listar/", viewId = "/faces/rh/administracaodepagamento/recolhimentosefip/lista.xhtml"),
        @URLMapping(id = "verRecolhimentoSEFIP", pattern = "/rh/codigos-de-recolhimento-do-sefip/ver/#{recolhimentoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recolhimentosefip/visualizar.xhtml"),
        @URLMapping(id = "editarRecolhimentoSEFIP", pattern = "/rh/codigos-de-recolhimento-do-sefip/editar/#{recolhimentoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recolhimentosefip/edita.xhtml"),
})
public class RecolhimentoSEFIPControlador extends PrettyControlador<RecolhimentoSEFIP> implements Serializable, CRUD {

    @EJB
    private RecolhimentoSEFIPFacade recolhimentoSEFIPFacade;

    public RecolhimentoSEFIPControlador() {
        super(RecolhimentoSEFIP.class);
    }

    public RecolhimentoSEFIPFacade getFacade() {
        return recolhimentoSEFIPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return recolhimentoSEFIPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/codigos-de-recolhimento-do-sefip/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (recolhimentoSEFIPFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado !");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoRecolhimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verRecolhimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRecolhimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
