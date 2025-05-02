package br.com.webpublico.enums.administrativo;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoDocumentoFiscalEntradaMaterialDTO;

/**
 * Created by mga on 11/01/2018.
 */
public enum SituacaoDocumentoFiscalEntradaMaterial {
    AGUARDANDO_LIQUIDACAO("Aguardando Liquidação", SituacaoDocumentoFiscalEntradaMaterialDTO.AGUARDANDO_LIQUIDACAO),
    LIQUIDADO("Liquidado", SituacaoDocumentoFiscalEntradaMaterialDTO.LIQUIDADO),
    ESTORNADO("Estornado", SituacaoDocumentoFiscalEntradaMaterialDTO.ESTORNADO),
    LIQUIDADO_PARCIALMENTE("Liquidado Parcialmente", SituacaoDocumentoFiscalEntradaMaterialDTO.LIQUIDADO_PARCIALMENTE);

    private String descricao;
    private SituacaoDocumentoFiscalEntradaMaterialDTO toDto;

    SituacaoDocumentoFiscalEntradaMaterial(String descricao, SituacaoDocumentoFiscalEntradaMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public boolean isLiquidado(){
        return LIQUIDADO.equals(this);
    }

    public boolean isLiquidadoParcialmente(){
        return LIQUIDADO_PARCIALMENTE.equals(this);
    }

    public boolean isEstornado(){
        return ESTORNADO.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoDocumentoFiscalEntradaMaterialDTO getToDto() {
        return toDto;
    }
}
