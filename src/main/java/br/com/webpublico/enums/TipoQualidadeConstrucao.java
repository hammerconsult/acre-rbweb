/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Daniel
 */
public enum TipoQualidadeConstrucao {

    SIMPLES(5, "5-Simples"),
    REGULAR(4, "4-Regular"),
    MEDIO(3, "3-Médio"),
    ELEVADO(2, "2-Elevado"),
    ESPECIAL(1, "1-Especial");

    private TipoQualidadeConstrucao(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    private String nome;
    private int codigo;

    public String getNome() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }


}
