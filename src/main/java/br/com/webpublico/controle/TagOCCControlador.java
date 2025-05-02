/*
 * Codigo gerado automaticamente em Wed Jun 20 15:59:49 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.entidades.TagOCC;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TagOccFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "tagOCCControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novotagocc", pattern = "/tagocc/novo/", viewId = "/faces/financeiro/clp/tagocc/edita.xhtml"),
        @URLMapping(id = "editartagocc", pattern = "/tagocc/editar/#{tagOCCControlador.id}/", viewId = "/faces/financeiro/clp/tagocc/edita.xhtml"),
        @URLMapping(id = "vertagocc", pattern = "/tagocc/ver/#{tagOCCControlador.id}/", viewId = "/faces/financeiro/clp/tagocc/visualizar.xhtml"),
        @URLMapping(id = "listartagoccr", pattern = "/tagocc/listar/", viewId = "/faces/financeiro/clp/tagocc/lista.xhtml")
})
public class TagOCCControlador extends PrettyControlador<TagOCC> implements Serializable, CRUD {

    @EJB
    private TagOccFacade tagOccFacade;

    public TagOCCControlador() {
        super(TagOCC.class);
    }

    public TagOccFacade getFacade() {
        return tagOccFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tagOccFacade;
    }

    @URLAction(mappingId = "novotagocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new TagOCC();
    }

    @URLAction(mappingId = "vertagocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editartagocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tagocc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean validaCampos() {
        boolean controle = true;
        if (Util.validaCampos(selecionado)) {
        }
        return controle;
    }

    @Override
    public void salvar() {
        try {
            if (selecionado.getId() == null) {
                selecionado.setCodigo(getNumeroMaiorCLPTag().toString());
            }
            if (Util.validaCampos(selecionado)) {
                super.salvar();
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", " Erro: " + ex.getMessage()));
        }
        if (isSessao()) {
            return;
        }
    }



    public List<SelectItem> getListaTipoContaAux() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EntidadeOCC tpa : EntidadeOCC.values()) {
            toReturn.add(new SelectItem(tpa, tpa.getDescricao()));
        }
        return toReturn;
    }

    public Integer getNumeroMaiorCLPTag() {
        BigDecimal maior = new BigDecimal(tagOccFacade.retornaUltimoNumeroTag());
        maior = maior.add(BigDecimal.ONE);
        return maior.intValue();
    }
}
