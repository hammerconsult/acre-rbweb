package br.com.webpublico.entidadesauxiliares;

import java.util.List;

public class AssistenteMalaDiretaGeral {

    private Long idMala;
    private List<Long> idsItens;
    private String usuario;

    public AssistenteMalaDiretaGeral() {
    }

    public Long getIdMala() {
        return idMala;
    }

    public void setIdMala(Long idMala) {
        this.idMala = idMala;
    }

    public List<Long> getIdsItens() {
        return idsItens;
    }

    public void setIdsItens(List<Long> idsItens) {
        this.idsItens = idsItens;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
