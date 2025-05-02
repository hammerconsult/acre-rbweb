package br.com.webpublico.entidadesauxiliares;

public class AceiteTermoUsoDTO {
    private Long termoUsoId;
    private String usuario;

    public AceiteTermoUsoDTO() {
    }

    public Long getTermoUsoId() {
        return termoUsoId;
    }

    public void setTermoUsoId(Long termoUsoId) {
        this.termoUsoId = termoUsoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
