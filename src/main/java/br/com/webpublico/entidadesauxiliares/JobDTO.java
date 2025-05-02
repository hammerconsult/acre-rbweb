package br.com.webpublico.entidadesauxiliares;

public class JobDTO {
    private String descricao;

    public JobDTO() {

    }

    public JobDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
