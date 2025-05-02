/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.AssuntoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.DocumentacaoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.DocumentoLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.DocumentacaoLicenciamentoAmbientalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarDocumentacaoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/documentacao/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/documentacao/lista.xhtml"),
    @URLMapping(id = "editarDocumentacaoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/documentacao/editar/#{documentacaoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/documentacao/edita.xhtml"),
    @URLMapping(id = "verDocumentacaoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/documentacao/ver/#{documentacaoLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/documentacao/visualizar.xhtml"),
    @URLMapping(id = "novoDocumentacaoLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/documentacao/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/documentacao/edita.xhtml")
})
public class DocumentacaoLicenciamentoAmbientalControlador extends PrettyControlador<DocumentacaoLicenciamentoAmbiental> implements Serializable, CRUD {

    @EJB
    private DocumentacaoLicenciamentoAmbientalFacade facade;
    private DocumentoLicenciamentoAmbiental documento;
    private List<DocumentacaoLicenciamentoAmbiental> lista;

    public DocumentacaoLicenciamentoAmbientalControlador() {
        super(DocumentacaoLicenciamentoAmbiental.class);
    }

    @Override
    public DocumentacaoLicenciamentoAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/documentacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public DocumentoLicenciamentoAmbiental getDocumento() {
        return documento;
    }

    public void setDocumento(DocumentoLicenciamentoAmbiental documento) {
        this.documento = documento;
    }

    public List<DocumentacaoLicenciamentoAmbiental> getLista() {
        return lista;
    }

    public void setLista(List<DocumentacaoLicenciamentoAmbiental> lista) {
        this.lista = lista;
    }

    @URLAction(mappingId = "novoDocumentacaoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioRegistro(facade.getSistemaFacade().getUsuarioCorrente());
        criarNovoDocumento();
    }

    @URLAction(mappingId = "editarDocumentacaoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        criarNovoDocumento();
    }

    @URLAction(mappingId = "verDocumentacaoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "listarDocumentacaoLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        lista = facade.lista();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDocumentos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um documento!");
        }
        if (ve.temMensagens()) {
            ve.lancarException();
            return false;
        }
        return true;
    }

    public void editarDocumento(DocumentoLicenciamentoAmbiental documento) {
        this.documento = documento;
        removerDocumento(this.documento);
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            documento.setDocumentacaoLicenciamentoAmbiental(selecionado);
            selecionado.getDocumentos().add(documento);
            criarNovoDocumento();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public List<AssuntoLicenciamentoAmbiental> completaAssunto(String parte) {
        return facade.getAssuntoLicenciamentoAmbientalFacade().buscarFiltrando(parte);
    }

    public void validarDocumento() {
        Util.validarCampos(documento);
        if (selecionado.hasDocumento(documento)) {
            throw new ValidacaoException("Já existe um documento adicionado para " +
                (documento.getAssuntoLicenciamentoAmbiental() == null ? " todos os assuntos" :
                    documento.getAssuntoLicenciamentoAmbiental().getDescricao()) + " com essa mesma descrição resumida");
        }
    }

    private void criarNovoDocumento() {
        documento = new DocumentoLicenciamentoAmbiental();
    }

    public void removerDocumento(DocumentoLicenciamentoAmbiental documento) {
        selecionado.getDocumentos().remove(documento);
    }
}
