/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author everton
 */
public enum GrauInstrucaoCAGED {

    ANALFABETO("Analfabeto inclusive o que, embora tenha recebido instrução, não se alfabetizou.", "1", "01"),
    QUINTO_ANO_INCOMPLETO("Até o 5º ano incompleto do Ensino Fundamental (antigo 1º grau ou primário) que se tenha alfabetizado sem ter freqüentado escola regular.", "2", "02"),
    QUINTO_ANO_COMPLETO("5º ano completo do Ensino Fundamental (antigo 1º grau ou primário).", "3", "03"),
    SEXTO_AO_NONO_ANO("Do 6º ao 9º ano de Ensino Fundamental (antigo 1º grau ou ginásio).", "4", "04"),
    FUNDAMENTAL_COMPLETO("Ensino Fundamental completo (antigo 1º grau ou primário e ginasial).", "5", "05"),
    MEDIO_INCOMPLETO("Ensino Médio incompleto (antigo 2º grau, secundário ou colegial).", "6", "06"),
    MEDIO_COMPLETO("Ensino Médio completo (antigo 2º grau, secundário ou colegial).", "7", "07"),
    SUPERIOR_INCOMPLETO("Educação Superior incompleta.", "8", "08"),
    SUPERIOR_COMPLETO("Educação Superior completa.", "9", "09"),
    POS_GRADUACAO_COMPLETA("Pós Graduação completa.", "9", "10"),
    MESTRADO("Mestrado", "10", "11"),
    DOUTORADO("Doutorado", "11", "12");

    private String codigo;
    private String descricao;
    private String codigoESocial;

    private GrauInstrucaoCAGED(String descricao, String codigo, String codigoESocial) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.codigoESocial = codigoESocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoESocial() {
        return codigoESocial;
    }

    public void setCodigoESocial(String codigoESocial) {
        this.codigoESocial = codigoESocial;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
