/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum Simples {

    NAO_OPTANTE("1", "Não Optante"),
    OPTANTE("2", "Optante"),
    OPTANTE_FATURAMENTO_ANUAL("3", "Optante - Faturamento anual superior a R$ 1.200.000,00"),
    NAO_OPTANTE_PRODUTOR_RURAL("4", "Não Optante - Produtor Rural Pessoa Física (CEI E PAS 604) com faturamento anual superior a R$ 1.200.000,00"),
    NAO_OPTANTE_EMPRESA_COM_LIMINAR("5", "Não Optante - Empresa com Liminar para não recolhimento da Contribuição Social - Lei Complementar 110/01, de 26/06/2001"),
    OPTANTE_FATURAMENTO_ANUAL_EMPRESA_COM_LIMINAR("6", "Optante - faturamento anual superior a 1.200.000,00 - Empresa com Liminar para não recolhimento da Contribuição Social - Lei Complementar 110/01, de 26/06/2001");
    private String codigo;
    private String descricao;

    private Simples(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
