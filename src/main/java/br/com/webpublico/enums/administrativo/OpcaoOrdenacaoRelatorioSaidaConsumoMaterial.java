package br.com.webpublico.enums.administrativo;

public enum OpcaoOrdenacaoRelatorioSaidaConsumoMaterial {

    ITEM_SAIDA("Item Saída"),
    CODIGO_MATERIAL("Código do Material"),
    DESCRICAO_MATERIAL("Descrição do Material");

    OpcaoOrdenacaoRelatorioSaidaConsumoMaterial(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;


    public String getDescricao() {
        return descricao;
    }


    @Override
    public String toString() {
        return this.descricao;
    }
}
