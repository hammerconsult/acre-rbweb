package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.SituacaoMovimentoPagamentoDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum SituacaoMovimentoPagamento {
    ABERTO("Aberto", SituacaoMovimentoPagamentoDTO.ABERTO),
    DEFERIDO("Deferido", SituacaoMovimentoPagamentoDTO.DEFERIDO),
    EM_TRANSITO("Em trânsito", SituacaoMovimentoPagamentoDTO.EM_TRANSITO),
    PAGO("Pago", SituacaoMovimentoPagamentoDTO.PAGO);

    SituacaoMovimentoPagamento(String descricao, SituacaoMovimentoPagamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    private String descricao;
    private SituacaoMovimentoPagamentoDTO toDto;

    public static List<SituacaoMovimentoPagamentoDTO> situacoesMovimentosPagamentosToDto(List<SituacaoMovimentoPagamento> situacoes) {
        List<SituacaoMovimentoPagamentoDTO> toReturn = Lists.newArrayList();
        if (situacoes != null && !situacoes.isEmpty()) {
            for (SituacaoMovimentoPagamento situacao : situacoes) {
                toReturn.add(situacao.toDto);
            }
        }
        return toReturn;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoMovimentoPagamentoDTO getToDto() {
        return toDto;
    }

    public List<String> getStatus() {
        List<String> status = Lists.newArrayList();
        switch (this) {
            case ABERTO:
                status.add("ABERTO");
                break;
            case DEFERIDO:
                status.add("DEFERIDO");
                status.add("BORDERO");
                break;
            case EM_TRANSITO:
                status.add("EFETUADO");
                status.add("INDEFERIDO");
                break;
            case PAGO:
                status.add("PAGO");
                break;
            default:
                break;
        }
        return status;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
