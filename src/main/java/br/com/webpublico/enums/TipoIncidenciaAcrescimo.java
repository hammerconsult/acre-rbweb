/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.TipoIncidenciaAcrescimoDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoIncidenciaAcrescimo implements EnumConsultaDebitos {
    IMPOSTO("Imposto", TipoIncidenciaAcrescimoDTO.IMPOSTO),
    TAXA("Taxa", TipoIncidenciaAcrescimoDTO.TAXA),
    MULTA_ORIGINAL("Multa do Original", TipoIncidenciaAcrescimoDTO.MULTA_ORIGINAL),
    JUROS_ORIGINAL("Juros do Original", TipoIncidenciaAcrescimoDTO.JUROS_ORIGINAL),
    CORRECAO_ORIGINAL("Correção do Original", TipoIncidenciaAcrescimoDTO.CORRECAO_ORIGINAL),
    HONORARIOS_ORIGINAL("Honorários do Original", TipoIncidenciaAcrescimoDTO.HONORARIOS_ORIGINAL),
    MULTA("Multa de Mora", TipoIncidenciaAcrescimoDTO.MULTA),
    JUROS("Juros de Mora", TipoIncidenciaAcrescimoDTO.JUROS),
    CORRECAO_ATUAL("Correção Atual", TipoIncidenciaAcrescimoDTO.CORRECAO_ATUAL),
    CORRECAO("Correção", TipoIncidenciaAcrescimoDTO.CORRECAO);
    private String descricao;
    private TipoIncidenciaAcrescimoDTO dto;

    private TipoIncidenciaAcrescimo(String descricao, TipoIncidenciaAcrescimoDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public static List<TipoIncidenciaAcrescimo> getAplicaveisSobreJuros() {
        return getTipoIncidenciaAcrescimos();
    }

    public static List<TipoIncidenciaAcrescimo> getAplicaveisSobreMulta() {
        return getTipoIncidenciaAcrescimos();
    }

    private static List<TipoIncidenciaAcrescimo> getTipoIncidenciaAcrescimos() {
        List<TipoIncidenciaAcrescimo> lista = Lists.newArrayList();
        lista.add(IMPOSTO);
        lista.add(TAXA);
        lista.add(CORRECAO);
        lista.add(MULTA_ORIGINAL);
        lista.add(JUROS_ORIGINAL);
        lista.add(CORRECAO_ORIGINAL);
        lista.add(HONORARIOS_ORIGINAL);
        lista.add(CORRECAO_ATUAL);
        return lista;
    }

    public static List<TipoIncidenciaAcrescimo> getAplicaveisSobreCorrecao() {
        List<TipoIncidenciaAcrescimo> lista = Lists.newArrayList();
        lista.add(IMPOSTO);
        lista.add(TAXA);
        lista.add(MULTA_ORIGINAL);
        lista.add(JUROS_ORIGINAL);
        lista.add(CORRECAO_ORIGINAL);
        lista.add(HONORARIOS_ORIGINAL);
        return lista;
    }

    public static List<TipoIncidenciaAcrescimo> getAplicaveisSobre(TipoIncidenciaAcrescimo tipo) {
        List<TipoIncidenciaAcrescimo> lista = Lists.newArrayList();
        for (TipoIncidenciaAcrescimo tipoAp : values()) {
            if (!tipoAp.equals(tipo)) {
                lista.add(tipoAp);
            }
        }
        return lista;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public TipoIncidenciaAcrescimoDTO toDto() {
        return dto;
    }
}
