/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum TipoItemSindicato {

    CONTRIBUICAO_SINDICAL("Contribuição Sindical", 1),
    ASSOCIACAO_SINDICAL("Associação Sindical", 2),
    CONTRIBUICAO_ASSISTENCIAL("Contribuição Assistencial", 3),
    CONTRIBUICAO_CONFEDERATIVA("Contribuição Confederativa", 4);
    private String descricao;
    private Integer codigo;

    private TipoItemSindicato(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return codigo + " - " + descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoItemSindicato find(Integer codigo) {
        for (TipoItemSindicato tipo : TipoItemSindicato.values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new RuntimeException("Tipo não encontrado para o código:" + codigo);

    }
}
