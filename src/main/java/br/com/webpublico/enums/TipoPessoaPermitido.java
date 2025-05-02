package br.com.webpublico.enums;

/**
 * @author Andre
 */
public enum TipoPessoaPermitido {

    PESSOAFISICA("Pessoa Física"),
    PESSOAJURIDICA("Pessoa Jurídica"),
    AMBOS("Ambos");

    private String descricao;

    private TipoPessoaPermitido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
