package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoMovimentoPagamentoDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoMovimentoPagamento {
    PAGAMENTO("Pagamento", TipoMovimentoPagamentoDTO.PAGAMENTO),
    DESPESA_EXTRA("Despesa Extra", TipoMovimentoPagamentoDTO.DESPESA_EXTRA),
    LIBERACAO_FINANCEIRA("Liberação Financeira", TipoMovimentoPagamentoDTO.LIBERACAO_FINANCEIRA),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira", TipoMovimentoPagamentoDTO.TRANSFERENCIA_FINANCEIRA);

    TipoMovimentoPagamento(String descricao, TipoMovimentoPagamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    private String descricao;
    private TipoMovimentoPagamentoDTO toDto;

    public static List<TipoMovimentoPagamentoDTO> tiposMovimentosToDto(List<TipoMovimentoPagamento> tiposMovimentos) {
        List<TipoMovimentoPagamentoDTO> toReturn = Lists.newArrayList();
        if (tiposMovimentos != null && !tiposMovimentos.isEmpty()) {
            for (TipoMovimentoPagamento tipoMovimento : tiposMovimentos) {
                toReturn.add(tipoMovimento.toDto);
            }
        }
        return toReturn;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMovimentoPagamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
