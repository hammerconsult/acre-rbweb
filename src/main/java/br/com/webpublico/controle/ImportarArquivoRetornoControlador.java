/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fabio
 */

@ManagedBean(name = "importarArquivoRetornoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "importar-rcb001", pattern = "/importar-arquivo-rcb001/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/importararquivoretorno/edita.xhtml"),
        @URLMapping(id = "lista-rcb001", pattern = "/importar-arquivo-rcb001/listar/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/importararquivoretorno/lista.xhtml")
})

public class ImportarArquivoRetornoControlador extends PrettyControlador<ArquivoRetornoBancario> implements Serializable, CRUD {

    @EJB
    private BorderoFacade borderoFacade;
    private List<FileUploadEvent> fileUploadEvents;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public ImportarArquivoRetornoControlador() {
        super(ArquivoRetornoBancario.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/importar-arquivo-rcb001/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return borderoFacade;
    }

    @URLAction(mappingId = "importar-rcb001", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    public void novoArquivo() {
        fileUploadEvents = new ArrayList<FileUploadEvent>();
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void sobeArquivos(FileUploadEvent event) throws FileNotFoundException, IOException {
        if (fileUploadEvents == null) {
            fileUploadEvents = new ArrayList<FileUploadEvent>();
        }
        fileUploadEvents.add(event);
    }

    public void geraArquivos() {
        try {
            borderoFacade.geraArquivos(fileUploadEvents);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Arquivo importado com sucesso."));
            redireciona();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", ex.getMessage()));
        }
    }
}
