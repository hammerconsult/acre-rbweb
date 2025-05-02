/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum TipoProvisaoSalario {

    DECIMO_TERCEIRO_SALARIO("13º Salário"),
    FERIAS("Férias"),
    LICENCA_PREMIO("Licença Prêmio");
    private String descricao;

    private TipoProvisaoSalario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
