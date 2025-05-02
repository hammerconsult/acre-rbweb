/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * Define os tipos possíveis de complemento
 *
 * @author arthur
 */
public enum TipoComplementoContabil {

    CONTA_BANCARIA("Conta bancária"),
    BEM("Bem");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoComplementoContabil(String descricao) {
        this.descricao = descricao;
    }
}
