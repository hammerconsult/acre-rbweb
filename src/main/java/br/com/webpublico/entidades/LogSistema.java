package br.com.webpublico.entidades;

import br.com.webpublico.util.Util;

import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
public class LogSistema implements Serializable {
    private Date ocorridoEm;
    private transient FacesMessage mensagem;
    private Boolean foiVisualizada;

    public LogSistema() {
    }

    public LogSistema(Date ocorridoEm, FacesMessage mensagem) {
        this.ocorridoEm = ocorridoEm;
        this.mensagem = mensagem;
    }

    public LogSistema(Date ocorridoEm, FacesMessage mensagem, Boolean foiVisualizada) {
        this.ocorridoEm = ocorridoEm;
        this.mensagem = mensagem;
        this.foiVisualizada = foiVisualizada;
    }

    public FacesMessage getMensagem() {
        return mensagem;
    }

    public void setMensagem(FacesMessage mensagem) {
        this.mensagem = mensagem;
    }

    public Date getOcorridoEm() {
        return ocorridoEm;
    }

    public String getOcorridoEmString() {
        return Util.dateHourToString(getOcorridoEm());
    }

    public void setOcorridoEm(Date ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }

    public Boolean getFoiVisualizada() {
        return foiVisualizada;
    }

    public void setFoiVisualizada(Boolean foiVisualizada) {
        this.foiVisualizada = foiVisualizada;
    }

    public String getSeverity() {
        if (mensagem == null) {
            return "";
        }

        switch (mensagem.getSeverity().getOrdinal()) {
            case 0:
                return "info";
            case 1:
                return "warn";
            case 2:
                return "error";
            case 3:
                return "fatal";
            default:
                return "info";
        }
    }
}
