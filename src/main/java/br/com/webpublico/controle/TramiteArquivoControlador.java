/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Tramite;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SituacaoTramiteFacade;
import br.com.webpublico.negocios.TramiteFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.enums.Operacoes;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean
@SessionScoped
public class TramiteArquivoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TramiteFacade tramiteFacade;
    @EJB
    private SituacaoTramiteFacade situacaoTramiteFacade;
    private List<FileUploadEvent> fileUploadEvents;
    @EJB
    private ArquivoFacade arquivoFacade;

    public TramiteArquivoControlador() {
        metadata = new EntidadeMetaData(Tramite.class);

    }

    public TramiteFacade getFacade() {
        return tramiteFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tramiteFacade;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        //Tramite t = (Tramite) evento.getComponent().getAttributes().get("tramite");
        //selecionado = tramiteFacade.recuperar(t.getId());
        selecionado = (Tramite) evento.getComponent().getAttributes().get("tramite");
        fileUploadEvents = new ArrayList<FileUploadEvent>();
    }

    public List<FileUploadEvent> getFileUploadEvents() {
        return fileUploadEvents;
    }

    public void setFileUploadEvents(List<FileUploadEvent> fileUploadEvents) {
        this.fileUploadEvents = fileUploadEvents;
    }

    public void uploadArquivos(FileUploadEvent event) {
        fileUploadEvents.add(event);
    }

    public void removerArquivo(ActionEvent evento) {
        FileUploadEvent e = (FileUploadEvent) evento.getComponent().getAttributes().get("remove");
        fileUploadEvents.remove(e);
    }

//    @Override
//    public String salvar() {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        ExternalContext externalContext = facesContext.getExternalContext();
//        ServletContext servletContext = (ServletContext) externalContext.getContext();
//        String pathReal = servletContext.getContextPath();
//        try {
//            tramiteFacade.salvar(((Tramite) selecionado), fileUploadEvents);
//            FacesContext.getCurrentInstance().getExternalContext().redirect(pathReal + "/faces/" + caminho() + ".xhtml");
//        } catch (Exception e) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ops!", "Problemas ao Salvar"));
//
//        }
//        return "";
//    }

    public Tramite getTramite() {
        return (Tramite) selecionado;
    }
}
