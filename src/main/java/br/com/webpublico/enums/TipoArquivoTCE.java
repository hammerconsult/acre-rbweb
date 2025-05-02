package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 17/04/14
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public enum TipoArquivoTCE {
    BEM_ESTOQUE("Bens de Estoque"),
    BEM_MOVEL("Bens Móveis"),
    BEM_IMOVEL("Bens Imóveis"),
    COMPENSADO("Diárias/Suprimento de Fundo/Contribuição"),
    CONTA_EXYTA("Conta Extra"),
    CONTA_FINANCEIRA("Conta Financeira"),
    CREDITO_DIVIDA("Crédito a receber/Dívida Ativa"),
    DIVIDA_PUBLICA("Dívida Pública"),
    RESTO_PAGAR("Resto a Pagar");
    private String descricao;

    private TipoArquivoTCE(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
