/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.OperacaoFormulaDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.comum.OperacaoFormulaDTO;

/**
 * @author arthur
 */
@GrupoDiagrama(nome = "Componente")
public enum OperacaoFormula {

    ADICAO("Adição", "+", OperacaoFormulaDTO.ADICAO),
    SUBTRACAO("Subtração", "-", OperacaoFormulaDTO.SUBTRACAO);
    private String descricao;
    private String simbolo;
    private OperacaoFormulaDTO toDto;

    OperacaoFormula(String descricao, String simbolo, OperacaoFormulaDTO toDto) {
        this.descricao = descricao;
        this.simbolo = simbolo;
        this.toDto = toDto;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getDescricao() {
        return descricao;
    }

    public OperacaoFormulaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
