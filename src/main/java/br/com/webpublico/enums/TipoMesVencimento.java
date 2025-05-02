package br.com.webpublico.enums;

/**
 * Created by Tharlyson on 12/04/19.
 */
public enum TipoMesVencimento {

    MES_REFERENCIA("Mês de Referência"),
    MES_PRECEDENTE_REFERENCIA("Mês Precedente à Referência"),
    MES_SUBSEQUENTE_REFERENCIA("Mês Subsequente à Refêrencia");

    TipoMesVencimento(String descricao){ this.descricao = descricao;}

    public String descricao;

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}
