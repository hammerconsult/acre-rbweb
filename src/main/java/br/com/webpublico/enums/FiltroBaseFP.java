package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 15/09/14
 * Time: 15:18
 */
public enum FiltroBaseFP {
    NORMAL("Normal"),
    PENSAO_ALIMENTICIA("Pensão Alimentícia"),
    LANCAMENTOFP("Lançamento"),
    CARGO_COMISSAO("Cargo Comissão");

    private String descricao;

    FiltroBaseFP(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
