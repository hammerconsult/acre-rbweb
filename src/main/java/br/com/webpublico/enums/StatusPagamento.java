/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.StatusPagamentoDTO;
import com.google.common.collect.Lists;

import java.util.List;

import java.util.Arrays;
import java.util.List;

/**
 * @author venon
 */
public enum StatusPagamento implements EnumComDescricao {
    ABERTO("Aguardando processamento", StatusPagamentoDTO.ABERTO),
    DEFERIDO("Deferido", StatusPagamentoDTO.DEFERIDO),
    INDEFERIDO("Indeferido", StatusPagamentoDTO.INDEFERIDO),
    EFETUADO("Efetuado", StatusPagamentoDTO.EFETUADO),
    BORDERO("Ordem Bancária", StatusPagamentoDTO.BORDERO),
    PAGO("Pago", StatusPagamentoDTO.PAGO),
    ESTORNADO("Estornado", StatusPagamentoDTO.ESTORNADO),
    CANCELADO("Cancelado (Migração)", StatusPagamentoDTO.CANCELADO);
    private String descricao;
    private StatusPagamentoDTO toDto;

    StatusPagamento(String descricao, StatusPagamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static List<String> getStatusPagamentoAsString(List<StatusPagamento> status) {
        List<String> toReturn = Lists.newArrayList();
        for (StatusPagamento sp : status) {
            toReturn.add(sp.name());
        }
        return toReturn;
    }

    public static List<StatusPagamento> getStatusDiferenteAberto() {
        List toReturn = Lists.newArrayList();
        toReturn.add(StatusPagamento.DEFERIDO);
        toReturn.add(StatusPagamento.INDEFERIDO);
        toReturn.add(StatusPagamento.EFETUADO);
        toReturn.add(StatusPagamento.BORDERO);
        toReturn.add(StatusPagamento.PAGO);
        toReturn.add(StatusPagamento.ESTORNADO);
        return toReturn;
    }


    @Override
    public String getDescricao() {
        return descricao;
    }


    public List<StatusPagamento> getStatusMovimentosNaoFinanceiros() {
        return Arrays.asList(ABERTO, DEFERIDO, INDEFERIDO);
    }

    public StatusPagamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
