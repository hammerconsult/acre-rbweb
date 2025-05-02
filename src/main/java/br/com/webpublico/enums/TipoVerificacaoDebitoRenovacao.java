package br.com.webpublico.enums;

/**
 * Created by William on 28/04/2016.
 */
public enum TipoVerificacaoDebitoRenovacao {
    PENDENTE("Pendente"),
    VERIFICADO("Verificado");;

    private String descricao;

    TipoVerificacaoDebitoRenovacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
