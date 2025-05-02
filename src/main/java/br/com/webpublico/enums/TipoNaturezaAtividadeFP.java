/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoNaturezaAtividadeFP {

    PENOSIDADE("Penosidade", 1),
    INSALUBRIDADE("Insalubridade", 2),
    PERICULOSIDADE("Periculosidade", 3);

    private String descricao;
    private Integer codigo;

    private TipoNaturezaAtividadeFP(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private TipoNaturezaAtividadeFP() {
    }

    public String getDescricao() {
        return codigo + "-" + descricao;
    }

    public static TipoNaturezaAtividadeFP find(Integer codigo) {
        for (TipoNaturezaAtividadeFP tipo : TipoNaturezaAtividadeFP.values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new RuntimeException("Tipo não encontrado para o código:" + codigo);
    }

}
