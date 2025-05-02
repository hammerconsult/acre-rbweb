/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author WebPublico07
 */
public enum StatusPeriodoAquisitivo {

    CONCEDIDO("Concedido", "Gozo integral"),
    NAO_CONCEDIDO("Não Concedido", ""),
    SEM_DIREITO("Sem Direito", "Gozo perdido"),
    PARCIAL("Parcial", "Gozo parcial"),
    DESISTENCIA("Desistência", "");

    private String descricao;
    private String descricaoTipoGozoFerias;

    private StatusPeriodoAquisitivo(String descricao, String descricaoTipoGozoFerias) {
        this.descricao = descricao;
        this.descricaoTipoGozoFerias = descricaoTipoGozoFerias;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoTipoGozoFerias() {
        return descricaoTipoGozoFerias;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
