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
public enum EstadoCivil {

    SOLTEIRO("Solteiro(a)", "1", "1", 1, br.com.webpublico.pessoa.enumeration.EstadoCivil.SOLTEIRO),
    CASADO("Casado(a)", "2", "2", 2, br.com.webpublico.pessoa.enumeration.EstadoCivil.CASADO),
    SEPARADO("Separado(a) Judicialmente","4", "4", 4, br.com.webpublico.pessoa.enumeration.EstadoCivil.DIVORCIADO),
    DIVORCIADO("Divorciado(a)", "5", "5", 3, br.com.webpublico.pessoa.enumeration.EstadoCivil.DIVORCIADO),
    VIUVO("Viuvo(a)", "3", "3", 5, br.com.webpublico.pessoa.enumeration.EstadoCivil.VIUVO),
    UNIAO_ESTAVEL("União Estável","6", "6",  1, br.com.webpublico.pessoa.enumeration.EstadoCivil.UNIAO_ESTAVEL);
    /* OUTROS("Outros","9", "0");*/
    private String descricao;
    private String codigoSiprev;
    private String codigoRPPS;
    private Integer codigoESocial;
    private br.com.webpublico.pessoa.enumeration.EstadoCivil dto;

    EstadoCivil(String descricao, String codigoSiprev, String codigoRPPS, Integer codigoESocial, br.com.webpublico.pessoa.enumeration.EstadoCivil dto) {
        this.descricao = descricao;
        this.codigoSiprev = codigoSiprev;
        this.codigoRPPS = codigoRPPS;
        this.codigoESocial = codigoESocial;
        this.dto = dto;
    }


    public String getDescricao() {
        return descricao;
    }

    public String getCodigoSiprev() {
        return codigoSiprev;
    }

    public String getCodigoRPPS() {
        return codigoRPPS;
    }

    public Integer getCodigoESocial() {
        return codigoESocial;
    }

    public void setCodigoESocial(Integer codigoESocial) {
        this.codigoESocial = codigoESocial;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
