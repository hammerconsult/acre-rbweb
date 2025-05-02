package br.com.webpublico.enums;

/**
 * Created by romanini on 06/02/2020.
 */
public enum TipoIntegradorRHContabil {

    EMPENHO("Empenho"),
    LIQUIDACAO("Liquidação"),
    PAGAMENTO("Pagamento"),
    OBRIGACAO_A_PAGAR("Obrigação a Pagar");

    private String descricao;

    TipoIntegradorRHContabil(String descricao) {
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
