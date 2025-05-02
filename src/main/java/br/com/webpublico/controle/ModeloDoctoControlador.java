/*
 * Codigo gerado automaticamente em Wed Dec 14 17:32:26 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DoctoLicitacao;
import br.com.webpublico.entidades.ModeloDocto;
import br.com.webpublico.entidades.TipoModeloDocto;
import br.com.webpublico.entidades.TipoModeloDocto.TipoModeloDocumento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModeloDoctoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "modeloDoctoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-modelo-documento-licitacao", pattern = "/modelo-documento-licitacao/novo/", viewId = "/faces/administrativo/licitacao/modelodocto/edita.xhtml"),
    @URLMapping(id = "editar-modelo-documento-licitacao", pattern = "/modelo-documento-licitacao/editar/#{modeloDoctoControlador.id}/", viewId = "/faces/administrativo/licitacao/modelodocto/edita.xhtml"),
    @URLMapping(id = "ver-modelo-documento-licitacao", pattern = "/modelo-documento-licitacao/ver/#{modeloDoctoControlador.id}/", viewId = "/faces/administrativo/licitacao/modelodocto/visualizar.xhtml"),
    @URLMapping(id = "listar-modelo-documento-licitacao", pattern = "/modelo-documento-licitacao/listar/", viewId = "/faces/administrativo/licitacao/modelodocto/lista.xhtml")
})
public class ModeloDoctoControlador extends PrettyControlador<ModeloDocto> implements Serializable, CRUD {

    @EJB
    private ModeloDoctoFacade modeloDoctoFacade;

    public ModeloDoctoControlador() {
        super(ModeloDocto.class);
    }

    public ModeloDoctoFacade getFacade() {
        return modeloDoctoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return modeloDoctoFacade;
    }

    @Override
    public void excluir() {
        if (!modeloDoctoPodeExcluir((ModeloDocto) selecionado)) {
            return;
        }
        super.excluir();
    }

    private boolean modeloDoctoPodeExcluir(ModeloDocto modeloDocto) {
        DoctoLicitacao doctLicitacao = modeloDoctoFacade.modeloDoctoPodeSerExcluir(modeloDocto);
        if (doctLicitacao != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, modeloDocto.getNome() + " está associado ao documento " + doctLicitacao + " na Licitação com número : " + doctLicitacao.getLicitacao().getNumeroLicitacao() + " - " + doctLicitacao.getLicitacao().getExercicio(), null));
            return false;
        }
        return true;
    }

    public List<SelectItem> getRecuperaTipoModelo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoModeloDocumento object : TipoModeloDocto.TipoModeloDocumento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsLicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDocto.TagsLicitacao object : TipoModeloDocto.TagsLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsAvisoDeLicitacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoModeloDocto.TagsAvisoDeLicitacao object : TipoModeloDocto.TagsAvisoDeLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperarTagRecebimentoTermoObra() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoModeloDocto.TagRecebimentoTermoObra object : TipoModeloDocto.TagRecebimentoTermoObra.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecuperaTagsDaAta() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoModeloDocto.TagsAtasDaLicitacao object : TipoModeloDocto.TagsAtasDaLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> recuperaTagsPeloTipo() {
        if (((ModeloDocto) selecionado).getTipoModeloDocto() != null) {
            return recuperarTagsDoModelo(((ModeloDocto) selecionado));
        }
        return null;
    }

    public List<SelectItem> recuperarTagsDoModelo(ModeloDocto modelo) {
        if (TipoModeloDocumento.TAGSLICITACAO.equals(modelo.getTipoModeloDocto())) {
            return getRecuperaTagsLicitacao();
        }
        if (TipoModeloDocumento.TAGSAVISODELICITACAO.equals(modelo.getTipoModeloDocto())) {
            return getRecuperaTagsAvisoDeLicitacao();
        }
        if (TipoModeloDocumento.TAGS_ATA_LICITACAO.equals(modelo.getTipoModeloDocto())) {
            return getRecuperaTagsDaAta();
        }
        if (TipoModeloDocumento.TAGSRECEBIMENTOPROVISORIOOBRA.equals(modelo.getTipoModeloDocto()) ||
            TipoModeloDocumento.TAGSRECEBIMENTODEFINITIVOOBRA.equals(modelo.getTipoModeloDocto())) {
            return getRecuperarTagRecebimentoTermoObra();
        }
        return null;
    }

    @URLAction(mappingId = "novo-modelo-documento-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-modelo-documento-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-modelo-documento-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modelo-documento-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaModelo()) {
            super.salvar();
        } else {
            FacesUtil.addError("ERRO", "O campo Modelo é obrigatório.");
        }
    }

    private Boolean validaModelo() {
        if (selecionado.getModelo().isEmpty() || selecionado.getModelo().equals("")) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
