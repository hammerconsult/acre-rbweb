/*
 * Codigo gerado automaticamente em Wed Dec 28 16:25:57 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoHabilitacao;
import br.com.webpublico.entidades.TipoDoctoHabilitacao;
import br.com.webpublico.enums.TipoDocumentoHabilitacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDoctoHabilitacaoFacade;
import br.com.webpublico.util.Util;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "tipoDoctoHabilitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-documento-habilitacao", pattern = "/tipo-documento-habilitacao/novo/", viewId = "/faces/administrativo/licitacao/tipodoctohabilitacao/edita.xhtml"),
    @URLMapping(id = "editar-tipo-documento-habilitacao", pattern = "/tipo-documento-habilitacao/editar/#{tipoDoctoHabilitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/tipodoctohabilitacao/edita.xhtml"),
    @URLMapping(id = "ver-tipo-documento-habilitacao", pattern = "/tipo-documento-habilitacao/ver/#{tipoDoctoHabilitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/tipodoctohabilitacao/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-documento-habilitacao", pattern = "/tipo-documento-habilitacao/listar/", viewId = "/faces/administrativo/licitacao/tipodoctohabilitacao/lista.xhtml")
})
public class TipoDoctoHabilitacaoControlador extends PrettyControlador<TipoDoctoHabilitacao> implements Serializable, CRUD {

    @EJB
    private TipoDoctoHabilitacaoFacade tipoDoctoHabilitacaoFacade;
    private Boolean mostraTabelaDoc;

    public TipoDoctoHabilitacaoControlador() {
        super(TipoDoctoHabilitacao.class);
    }

    public TipoDoctoHabilitacaoFacade getFacade() {
        return tipoDoctoHabilitacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDoctoHabilitacaoFacade;
    }

    public Boolean getMostraTabelaDoc() {
        return mostraTabelaDoc;
    }

    public void setMostraTabelaDoc(Boolean mostraTabelaDoc) {
        this.mostraTabelaDoc = mostraTabelaDoc;
    }

    @Override
    public void salvar() {
        if (validaDescricaoDocto()) {
            super.salvar();
        }
    }

    @Override
    public void excluir() {
        if (!tipoDoctoPodeExcluir()) {
            return;
        }
        super.excluir();

    }

    private boolean tipoDoctoPodeExcluir() {
        if (!tipoDoctoHabilitacaoFacade.validaExclusao((TipoDoctoHabilitacao) selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ((TipoDoctoHabilitacao) selecionado).getDescricao() + " está associado com algum (ns) documento (s) !", null));
            return false;
        }
        return true;
    }

    private boolean validaDescricaoDocto() {
        if (tipoDoctoHabilitacaoFacade.validaDescricaoUnica(((TipoDoctoHabilitacao) selecionado))) {
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ((TipoDoctoHabilitacao) selecionado).getDescricao() + " Já existe um documento com este nome!", null));
            return false;
        }
    }

    @URLAction(mappingId = "novo-tipo-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        mostraTabelaDoc = false;
        ((TipoDoctoHabilitacao) selecionado).setListaDoctosHab(new ArrayList<DoctoHabilitacao>());
    }

    @URLAction(mappingId = "ver-tipo-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarVerEditar();
    }

    @URLAction(mappingId = "editar-tipo-documento-habilitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarVerEditar();
    }

    private void recuperarVerEditar() {
        mostraTabelaDoc = true;
        ((TipoDoctoHabilitacao) selecionado).setListaDoctosHab(new ArrayList<DoctoHabilitacao>());
        ((TipoDoctoHabilitacao) selecionado).setListaDoctosHab(tipoDoctoHabilitacaoFacade.recuperaDoctosPeloTipo((TipoDoctoHabilitacao) selecionado));
    }

    public List<SelectItem> getTipoDocumentoHabilitacao() {
        return Util.getListSelectItem(Arrays.asList(TipoDocumentoHabilitacao.values()));
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-documento-habilitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
