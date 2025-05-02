/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author peixe
 */
public enum TipoBase {

    INSS_EMPRESA("Inss - Empresa"),
    PREVIDENCIA_SOCIAL_GERAL_INSS("Previdência Social Geral - Inss"),
    SALARIO_13_PREVIDENCIA_SOCIAL_INSS("13º Previdência Social - Inss"),
    FGTS("FGTS"),
    FGTS_13("13º FGTS"),
    PREVIDENCIA_PRIVADA_RPPS("Previdência Privada - RPPS"),
    PREVIDENCIA_PRIVADA_13("13º Previdência Privada - RPPS"),
    IRRF("IRRF"),
    IRRF_13("13º IRRF"),
    FERIAS("Férias"),
    FERIAS_13("13º Férias");
    private String descricao;

    private TipoBase(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
