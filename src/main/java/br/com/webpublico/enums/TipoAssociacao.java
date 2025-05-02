/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author boy
 */
public enum TipoAssociacao {

    CONTRIBUICAO_ANUAL("Contribuição Anual", 1),
    CONTRIBUICAO_MENSAL("Contribuição Mensal", 2);
    private String descricao;
    private Integer codigo;

    private TipoAssociacao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return codigo + " - " + descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoAssociacao find(Integer codigo) {
        for (TipoAssociacao tipo : TipoAssociacao.values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new RuntimeException("Tipo não encontrado para o código:" + codigo);

    }
}
