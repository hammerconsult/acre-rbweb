package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.dte.facades.ModeloDocumentoDteFacade;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarModeloDocumentoDte", pattern = "/tributario/dte/modelo-documento/listar/",
        viewId = "/faces/tributario/dte/modelodocumento/lista.xhtml"),
    @URLMapping(id = "verModeloDocumentoDte", pattern = "/tributario/dte/modelo-documento/ver/#{modeloDocumentoDteControlador.id}/",
        viewId = "/faces/tributario/dte/modelodocumento/visualizar.xhtml"),
    @URLMapping(id = "novoModeloDocumentoDte", pattern = "/tributario/dte/modelo-documento/novo/",
        viewId = "/faces/tributario/dte/modelodocumento/edita.xhtml"),
    @URLMapping(id = "editarModeloDocumentoDte", pattern = "/tributario/dte/modelo-documento/editar/#{modeloDocumentoDteControlador.id}/",
        viewId = "/faces/tributario/dte/modelodocumento/edita.xhtml")
})
public class ModeloDocumentoDteControlador extends PrettyControlador<ModeloDocumentoDte> implements CRUD {

    @EJB
    private ModeloDocumentoDteFacade facade;

    public ModeloDocumentoDteControlador() {
        super(ModeloDocumentoDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/dte/modelo-documento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ModeloDocumentoDte> completarModeloDocumento(String parte) {
        if (parte == null)
            parte = "";
        return facade.buscarModeloPorDescricao(parte);
    }

    @URLAction(mappingId = "novoModeloDocumentoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarModeloDocumentoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verModeloDocumentoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    public void adicionarCabecalho() {
        String caminhoDaImagem = geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        String conteudo =
            "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td style=\"text-align: left;\" align=\"right\"><img src=\"../../../../" + caminhoDaImagem + "\" alt=\"PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO\" /></td>"
                + "<td><span style=\"font-size: small;\">PREFEITURA DO MUNIC&Iacute;PIO DE RIO BRANCO</span><br /><span style=\"font-size: small;\">Secretaria Municipal de Finan&ccedil;as</span> <strong><br /></strong></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<br>&nbsp;<br><br>"
                + "<br><br><br> *** INSIRA SEU TEXTO AQUI *** ";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public void adicionarRodape() {
        String conteudo =
            "<div id=\"footer\">"
                + "<table style=\"width: 100%;\">"
                + "<tbody>"
                + "<tr>"
                + "<td>*** INSIRA SEU TEXTO AQUI ***</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<div>";
        RequestContext.getCurrentInstance().execute("insertAtCursor('" + conteudo + "')");
    }

    public String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    public void adicionarTagDtaHoje(String tag) {
        ModeloDocumentoDte.TagsDataHoje tagsDataHoje = ModeloDocumentoDte.TagsDataHoje.valueOf(tag);
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + tagsDataHoje.name() + "')");
    }

    public List<SelectItem> getTags() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ModeloDocumentoDte.TagsCadastroEconomico value : ModeloDocumentoDte.TagsCadastroEconomico.values()) {
            toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }
}
