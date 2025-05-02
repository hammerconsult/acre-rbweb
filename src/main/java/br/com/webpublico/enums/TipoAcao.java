/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author juggernaut
 */
public enum TipoAcao {

    PROJETO("Projeto"),
    ATIVIDADE("Atividade"),
    RESERVA_CONTIGENTE("Reserva de Contingência");
    private String descricao;

    private TipoAcao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
