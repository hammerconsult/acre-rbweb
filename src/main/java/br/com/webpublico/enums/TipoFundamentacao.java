/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author boy
 */
public enum TipoFundamentacao {

    ART_40_CF_I("art. 40,§7°, CF, Inciso I", "0601", "Pensão por morte (art. 40, § 7º, da CF/1988)"),
    ART_40_CF_II("art. 40,§7°, CF, Inciso II", "0601", "Pensão por morte (art. 40, § 7º, da CF/1988)"),
    ART_40_CF_NR("art. 40,§7°, c/c Art. 3, CF, NR EC 47/2005", "0603", "Pensão por morte com paridade, decorrente do art. 3º da EC 47/2005");
//    ART_6_41_2003("Pensão por morte com paridade, decorrente do art. 6º-A da EC 41/2003", "0602", "Pensão por morte com paridade, decorrente do art. 6º-A da EC 41/2003");
//    PENSAO_MORTE_MILITAR("Pensão por morte militar", "0604", "Pensão por morte militar");

    private String descricao;
    private String codigoEsocial;
    private String descricaoEsocial;

    TipoFundamentacao(String descricao, String codigoEsocial, String descricaoEsocial) {
        this.descricao = descricao;
        this.codigoEsocial = codigoEsocial;
        this.descricaoEsocial = descricaoEsocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoEsocial() {
        return codigoEsocial;
    }

    public String getDescricaoEsocial() {
        return descricaoEsocial;
    }

}
