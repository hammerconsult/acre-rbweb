/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum RacaCor {

    INDIGENA("1", "Indígena", 5),
    BRANCA("2", "Branca", 1),
    NEGRO("4", "Negro", 2),
    AMARELA("6", "Amarela", 4),
    PARDA("8", "Parda", 3),
    NAO_INFORMADA("9", "Não Informado", 6);

    private String codigo;
    private String descricao;
    private Integer codigoEsocial;

    RacaCor(String codigo, String descricao, Integer codigoEsocial) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.codigoEsocial = codigoEsocial;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigoEsocial() {
        return codigoEsocial;
    }

    public void setCodigoEsocial(Integer codigoEsocial) {
        this.codigoEsocial = codigoEsocial;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
