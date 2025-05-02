package br.com.webpublico.enums;

/**
 * Created by Edi on 05/04/2016.
 */
public enum TipoBancoArquivoObn {

    CAIXA_ECONOMICA("Caixa Econômica", "104"),
    BANCO_DO_BRASIL("Banco do Brasil", "001");
    private String descricao;
    private String codigo;

    TipoBancoArquivoObn(String descricao, String codigo) {
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
