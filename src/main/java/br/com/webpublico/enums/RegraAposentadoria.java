/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum RegraAposentadoria {

    VOLUNTARIA_INTEGRAL_COMNUM_ART_2005("Voluntária Integral Comum (Art. 3º, CF, NR EC 47/2005)"),
    VOLUNTARIA_INTEGRAL_COMNUM_TRANSICAO_ART6_2003("Voluntária Integral Comum(transição - Art.6º, EC 41/2003)"),
    VOLUNTARIA_INTEGRAL_ESPECIAL_MAGISTERIO_ART6_2003("Voluntária Integral Especial(transição - Art.6º, EC 41/2003)"),
    VOLUNTARIA_INTEGRAL_COMUM_TRANSICAO_ART2_2003("Voluntária Integral Comum(transição - Art.2º, EC 41/2003)"),
    VOLUNTARIA_INTEGRAL_ESPECIAL_TRANSICAO_ART2_2003("Voluntária Integral Especial(transição - Art.2º, EC 41/2003)"),
    INVALIDEZ("Invalidez(Art.40, CF, NR, EC 41/2003)"),
    INVALIDEZ_ART40_2012("Invalidez(Art.40, EC, NR, EC 70/2012)"),
    INVALIDEZ_ART23_2009("Invalidez (Art. 23, 5º LM 1793/09)"),
    COMPULSORIA("Compulsória(Art.40, CF, NR, EC 20/1998)"),
    VOLUNTARIA_INTEGRAL("Voluntária Integral (Art. 40 CF NR, EC 20/1998, EC 70/2012)"),//paridade
    VOLUNTARIA_POR_IDADE("Voluntária Por Idade(Art.40, CF, NR, EC 20/1998)"),
    VOLUNTARIA_ESPECIAL_MAGISTERIO("Voluntária do Magistério (Art.40, CF, NR, EC 20/1998)"),//Média
    VOLUNTARIA_PROPORCIONAL("Voluntária Proporcional (Art.40 CF)"),//Média
    VOLUNTARIA_PROPORCIONAL_ART4_2019("Voluntária Proporcional (EC nº 103/2019, Art. 4º, §9º)");
    private String descricao;

    private RegraAposentadoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
