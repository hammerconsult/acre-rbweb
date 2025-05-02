package br.com.webpublico.entidadesauxiliares.rh.integracaoponto;

public class ExcluirFeriasDTO {

    private String idRbweb;

    public ExcluirFeriasDTO() {}

    public ExcluirFeriasDTO(String idRbweb) {
        this.idRbweb = idRbweb;
    }

    public String getIdRbweb() {
        return idRbweb;
    }

    public void setIdRbweb(String idRbweb) {
        this.idRbweb = idRbweb;
    }
}
