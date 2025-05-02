package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoMovimentoRelatorioRestoAPagarDTO;
/**
 * Created by carlos on 27/03/17.
 */
public enum TipoMovimentoRelatorioRestoAPagar {
    INSCRITOS("Inscritos", TipoMovimentoRelatorioRestoAPagarDTO.INSCRITOS),
    PAGOS("Pagos", TipoMovimentoRelatorioRestoAPagarDTO.PAGOS),
    CANCELADOS("Cancelados", TipoMovimentoRelatorioRestoAPagarDTO.CANCELADOS);

    private String descricao;
    private TipoMovimentoRelatorioRestoAPagarDTO toDto;

    TipoMovimentoRelatorioRestoAPagar(String descricao, TipoMovimentoRelatorioRestoAPagarDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMovimentoRelatorioRestoAPagarDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
