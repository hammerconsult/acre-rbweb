package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.List;

public class AssistenteMalaDiretaIPTU implements Serializable {

    private Long idMalaDireta;
    private List<Long> idsCadastros;
    private String usuario;

    public Long getIdMalaDireta() {
        return idMalaDireta;
    }

    public void setIdMalaDireta(Long idMalaDireta) {
        this.idMalaDireta = idMalaDireta;
    }

    public List<Long> getIdsCadastros() {
        return idsCadastros;
    }

    public void setIdsCadastros(List<Long> idsCadastros) {
        this.idsCadastros = idsCadastros;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
