/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

public enum SituacaoCertidaoDA {

    ABERTA("Aberta", 1, true),
    QUITADA("Quitada", 2, true),
    CANCELADA("Cancelada", 3, true),
    COMPENSADA("Compensada", 4, false),
    SUSPENSA("Suspensa", 5, false),
    SUBSTITUIDA("Substituída", 6, true),
    OUTROS("Outros", 7, false),
    PETICIONADA("Peticionada", 8, false),
    PENHORA_REALIZADA("Penhora Realizada", 10, false),
    JUNCAO_CDALEGADA("Junção de CDAs pelo Número Legado", 11, false);
    private String descricao;
    private Integer codigo;
    private boolean visibled;

    private SituacaoCertidaoDA(String descricao, Integer codigo, boolean visibled) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.visibled = visibled;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public boolean isVisibled() {
        return visibled;
    }

    public boolean isPeticionada() {
        return PETICIONADA.equals(this);
    }
}
