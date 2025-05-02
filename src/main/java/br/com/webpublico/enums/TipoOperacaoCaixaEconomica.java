package br.com.webpublico.enums;

/**
 * Created by Edi on 31/03/2016.
 */
public enum TipoOperacaoCaixaEconomica {

    CONTA_CORRENTE_PESSOA_FISICA("Conta Corrente - Pessoa Física", "01"),
    CONTA_SIMPLES_PESSOA_FISICA("Conta Simples - Pessoa Física", "02"),
    CONTA_CORRENTE_PESSOA_JURIDICA("Conta Corrente - Pessoa Jurídica", "03"),
    ENTIDADES_PUBLICAS("Entidades Públicas", "06"),
    POUPANCA("Poupança", "13"),
    POUPANCA_PESSOA_JURIDICA("Poupança - Pessoa Jurídica", "22"),
    CONTA_CAIXA_FACIL("Conta Caixa Fácil", "23"),
    POUPANCA_CREDITO_IMOBILIARIO("Poupança Crédito Imobiliário", "028");
    private String descricao;
    private String codigo;

    TipoOperacaoCaixaEconomica(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
