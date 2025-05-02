/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Trailer {

    BRANCOS("Brancos"),
    NOVES("Noves"),
    SOMATORIO_VALORES_TODAS_OBS_TIPO_2("Somatório dos valores de todas as OB’s tipo 2"),
    SOMATORIO_SEQ_TODOS_REG_EXCETO_TRAILER("Somatório das seqüências de todos os registros exceto o registro trailer");

    private TipoRegistroOBN600Trailer(String descricao) {
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
