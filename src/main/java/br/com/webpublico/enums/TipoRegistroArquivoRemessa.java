/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroArquivoRemessa {

    HEADER("Header"),
    TIPO_1("Tipo 1"),
    TIPO_2("Tipo 2"),
    TIPO_3("Tipo 3"),
    TIPO_4("Tipo 4"),
    TIPO_5("Tipo 5"),
    TRAILER("Trailer");

    private TipoRegistroArquivoRemessa(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

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
