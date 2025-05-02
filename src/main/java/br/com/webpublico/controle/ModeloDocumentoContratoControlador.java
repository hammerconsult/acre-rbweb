/*
 * Codigo gerado automaticamente em Wed Dec 14 17:32:26 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ModeloDocumentoContrato;
import br.com.webpublico.entidades.TipoModeloDoctoOficial;
import br.com.webpublico.enums.TagsModeloDocumentoContrato;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ModeloDocumentoContratoFacade;
import br.com.webpublico.util.Util;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "modeloDocumentoContratoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-modelo-documento-contrato", pattern = "/modelo-documento-contrato/novo/", viewId = "/faces/administrativo/contrato/modelo-documento/edita.xhtml"),
        @URLMapping(id = "editar-modelo-documento-contrato", pattern = "/modelo-documento-contrato/editar/#{modeloDocumentoContratoControlador.id}/", viewId = "/faces/administrativo/contrato/modelo-documento/edita.xhtml"),
        @URLMapping(id = "ver-modelo-documento-contrato", pattern = "/modelo-documento-contrato/ver/#{modeloDocumentoContratoControlador.id}/", viewId = "/faces/administrativo/contrato/modelo-documento/visualizar.xhtml"),
        @URLMapping(id = "listar-modelo-documento-contrato", pattern = "/modelo-documento-contrato/listar/", viewId = "/faces/administrativo/contrato/modelo-documento/lista.xhtml")
})
public class ModeloDocumentoContratoControlador extends PrettyControlador<ModeloDocumentoContrato> implements Serializable, CRUD {

    @EJB
    private ModeloDocumentoContratoFacade modeloDocumentoContratoFacade;

    public ModeloDocumentoContratoControlador() {
        super(ModeloDocumentoContrato.class);
    }

    public ModeloDocumentoContratoFacade getFacade() {
        return modeloDocumentoContratoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return modeloDocumentoContratoFacade;
    }

    @Override
    public void excluir() {
        super.excluir();
    }


    @URLAction(mappingId = "novo-modelo-documento-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-modelo-documento-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-modelo-documento-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/modelo-documento-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (!Util.validaCampos(selecionado)) {
            return;
        }
        super.salvar();
    }

    public List<SelectItem> getRecuperarTags() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TagsModeloDocumentoContrato object : TagsModeloDocumentoContrato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
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

    public void adicionarDataHojeAno() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_ANO.name() + "')");
    }

    public void adicionarDataHojeMes() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES.name() + "')");
    }

    public void adicionarDataHojeMesExtenso() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_MES_EXTENSO.name() + "')");
    }

    public void adicionarDataHojeDia() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsDataHoje.DATA_HOJE_DIA.name() + "')");
    }

    public void adicionarQuebraPagina() {
        RequestContext.getCurrentInstance().execute("insertAtCursor(' $" + TipoModeloDoctoOficial.TagsConfiguracaoDocumento.QUEBRA_PAGINA.name() + "')");
    }
}
