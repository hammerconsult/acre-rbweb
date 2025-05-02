/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums.rh.dirf;

/**
 * @author Mailson Peixe
 */
public enum DirfReg35 {

    ASSALARIADO("Trabalhador Assalariado", "0561"),
    APOSENTADO_PENSIONIATA("Aposentado/Pensionista", "3533");

    private String descricao;
    private String codigo;

    DirfReg35(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private DirfReg35() {
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }
}
