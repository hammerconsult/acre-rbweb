package br.com.webpublico.enums;

/**
 * Created by venom on 03/12/14.
 */
public enum TipoLocalConcurso {

    INSCRICAO("Inscrição"),
    PAGAMENTO("Pagamento");

    private String descricao;

    TipoLocalConcurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
