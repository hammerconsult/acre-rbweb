package br.com.webpublico.enums;

/**
 * Created by romanini on 08/07/15.
 */
public enum TipoIdentificacaoGuia {
    CPF("CPF"),
    CNPJ("CNPJ"),
    PIS_PASEP("PIS/PASEP"),
    IDENTIFICADOR("Identificador"),
    CEI("CEI");

    private String descricao;

    TipoIdentificacaoGuia(String descricao) {
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
