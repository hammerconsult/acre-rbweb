package br.com.webpublico.entidadesauxiliares;

import org.primefaces.event.FileUploadEvent;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 29/11/13
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 * Para não dar problema na migraçao!!!!
 */
public class FileUploadPrimeFaces implements Serializable {
    private FileUploadEvent fileUploadEvent;

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public FileUploadPrimeFaces(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public FileUploadPrimeFaces() {
    }
}
