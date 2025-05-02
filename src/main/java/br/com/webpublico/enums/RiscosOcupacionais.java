package br.com.webpublico.enums;

/**
 * Created by carlos on 24/06/15.
 */
public enum RiscosOcupacionais {

    NAO_HA_RISCO("Não há risco ocupacional"),
    HA_RISCO("Há risco ocupacional");
    private String descricao;

    private RiscosOcupacionais(String descricao) {
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
