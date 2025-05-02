package br.com.webpublico.enums;

/**
 * Created by israeleriston on 06/11/15.
 */
public enum PercursoAcidente {
    RESIDENCIA_PRO_TRABALHO("Residência para Trabalho"),
    TRABALHO_PRA_RESIDENCIA("Trabalho para Residência"),
    TRABALHO_PRO_ALMOCO("Saida para Almoço"),
    ALMOCO_PRO_TRABALHO("Volta do Almoço");


    private String descricao;

    PercursoAcidente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
