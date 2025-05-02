/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.rh.TipoPeriodoAquisitivoDTO;

/**
 * @author peixe
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@CorEntidade(value = "#9400D3")
public enum TipoPeriodoAquisitivo {

    FERIAS("Férias", TipoPeriodoAquisitivoDTO.FERIAS),
    LICENCA("Licença Prêmio", TipoPeriodoAquisitivoDTO.LICENCA);
    private String descricao;
    private TipoPeriodoAquisitivoDTO toDto;

    TipoPeriodoAquisitivo(String descricao, TipoPeriodoAquisitivoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoPeriodoAquisitivoDTO getToDto() {
        return toDto;
    }
}
