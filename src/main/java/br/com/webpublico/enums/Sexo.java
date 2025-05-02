/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.enums.SexoDTO;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum Sexo {

    MASCULINO("1", "Masculino", "M", SexoDTO.MASCULINO, "2"),
    FEMININO("2", "Feminino", "F", SexoDTO.FEMININO, "1");

    private String codigo;
    private String descricao;
    private String sigla;
    private String codigoEstudoAtuarial;

    private SexoDTO sexoDTO;

    Sexo(String codigo, String descricao, String sigla, SexoDTO sexoDTO, String codigoEstudoAtuarial) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.sigla = sigla;
        this.sexoDTO = sexoDTO;
        this.codigoEstudoAtuarial = codigoEstudoAtuarial;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public SexoDTO getSexoDTO() {
        return sexoDTO;
    }

    public String getCodigoEstudoAtuarial() {
        return codigoEstudoAtuarial;
    }

    public void setCodigoEstudoAtuarial(String codigoEstudoAtuarial) {
        this.codigoEstudoAtuarial = codigoEstudoAtuarial;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static Sexo getEnumByDTO(SexoDTO dto) {
        for (Sexo value : values()) {
            if (value.sexoDTO.equals(dto)) {
                return value;
            }
        }
        return null;
    }
}
