package br.com.webpublico.controle;

import br.com.webpublico.entidades.DocumentosNecessariosAnexo;
import br.com.webpublico.entidades.ItemDocumentoAnexo;
import br.com.webpublico.entidades.TipoDocumentoAnexo;
import br.com.webpublico.enums.TipoFuncionalidadeParaAnexo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DocumentosNecessariosAnexoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 19/02/2016.
 */
@ManagedBean(name = "documentosNecessariosAnexoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDocumentoNecessario", pattern = "/upload-documentos-obrigatorios/novo/", viewId = "/faces/administrativo/licitacao/documentosnecessariosanexo/edita.xhtml"),
    @URLMapping(id = "editarDocumentoNecessario", pattern = "/upload-documentos-obrigatorios/editar/#{documentosNecessariosAnexoControlador.id}/", viewId = "/faces/administrativo/licitacao/documentosnecessariosanexo/edita.xhtml"),
    @URLMapping(id = "listarDocumentoNecessario", pattern = "/upload-documentos-obrigatorios/listar/", viewId = "/faces/administrativo/licitacao/documentosnecessariosanexo/lista.xhtml"),
    @URLMapping(id = "verDocumentoNecessario", pattern = "/upload-documentos-obrigatorios/ver/#{documentosNecessariosAnexoControlador.id}/", viewId = "/faces/administrativo/licitacao/documentosnecessariosanexo/visualizar.xhtml")
})
public class DocumentosNecessariosAnexoControlador extends PrettyControlador<DocumentosNecessariosAnexo> implements Serializable, CRUD {

    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    private ItemDocumentoAnexo itemDocumentoAnexo;


    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public ItemDocumentoAnexo getItemDocumentoAnexo() {
        return itemDocumentoAnexo;
    }

    public void setItemDocumentoAnexo(ItemDocumentoAnexo itemDocumentoAnexo) {
        this.itemDocumentoAnexo = itemDocumentoAnexo;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/upload-documentos-obrigatorios/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return documentosNecessariosAnexoFacade;
    }

    @URLAction(mappingId = "novoDocumentoNecessario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    public DocumentosNecessariosAnexoControlador() {
        super(DocumentosNecessariosAnexo.class);
        tipoDocumentoAnexo = new TipoDocumentoAnexo();
        itemDocumentoAnexo = new ItemDocumentoAnexo();
    }

    @URLAction(mappingId = "editarDocumentoNecessario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verDocumentoNecessario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTipoFuncionalidadeParaAnexo() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFuncionalidadeParaAnexo tipo : TipoFuncionalidadeParaAnexo.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void adicionarItemDocumentoEFuncionalidade() {
        if (validarDocumentoEFuncionalidade()) {
            itemDocumentoAnexo.setDocumentosNecessariosAnexo(selecionado);
            selecionado.getItens().add(itemDocumentoAnexo);
            itemDocumentoAnexo = new ItemDocumentoAnexo();
        }
    }

    public boolean validarDocumentoEFuncionalidade() {
        boolean validou = true;
        if (itemDocumentoAnexo.getTipoDocumentoAnexo() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Tipo Documento Anexo");
            validou = false;
        }
        if (itemDocumentoAnexo.getTipoFuncionalidadeParaAnexo() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Funcionalidade");
            validou = false;
        }
        for (ItemDocumentoAnexo item : selecionado.getItens()) {
            if (item.getTipoDocumentoAnexo().equals(itemDocumentoAnexo.getTipoDocumentoAnexo())
                && item.getTipoFuncionalidadeParaAnexo().equals(itemDocumentoAnexo.getTipoFuncionalidadeParaAnexo())) {
                FacesUtil.addOperacaoNaoPermitida("Essa informação já foi adicionada à lista");
                validou = false;
            }
        }
        return validou;
    }

    public boolean isPossuiMaisDeUmRegistroCadastrado() {
        return documentosNecessariosAnexoFacade.isPossuiMaisDeUmRegistroCadastrado();
    }

    public void excluirItemDocumentoEFuncionalidade(ItemDocumentoAnexo item) {
        selecionado.getItens().remove(item);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (selecionado.getDataInicio() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Data de Inicio");
            validou = false;
        }
        if (selecionado.getDataFim() == null) {
            FacesUtil.addCampoObrigatorio("Data de Término");
            validou = false;
        }
        if (selecionado.getDataInicio() != null && selecionado.getDataFim() != null) {
            if (selecionado.getDataInicio().after(selecionado.getDataFim())) {
                FacesUtil.addOperacaoNaoPermitida("A Data de Inicio não pode ser maior que a Data de Término");
                validou = false;
            }
        }
        if (selecionado.getItens().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Adicione um Tipo de Documento e Funcionalidade");
            validou = false;
        }
        return validou;
    }
}
