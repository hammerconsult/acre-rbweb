package br.com.webpublico.enums.administrativo.frotas;

/**
 * Created by wellington on 13/07/17.
 */
public enum TipoCotaAbastecimento {
    NORMAL("Normal"), DISTRIBUICAO("Distribuição");

    private String descricao;

    TipoCotaAbastecimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
