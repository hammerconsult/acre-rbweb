package br.com.webpublico.enums;

/**
 * @author GhouL
 */
public enum TipoCobrancaParcelamento {
    /*
     AO ADICIONAR MAIS UM TIPO VERIFICAR NO RELATÓRIO 'TABELACONTRATORECONHEICMENTODEBITO'.
     */
    NAO_APLICAVEL("Não Aplicável"),
    DESCONTO("Desconto Percentual"),
    ACRESCIMO("Acréscimo Percentual");

    String descricao;

    private TipoCobrancaParcelamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
