package br.com.webpublico.nfse.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.TipoRpsNfseDTO;

public enum TipoRps implements NfseEnum, EnumComDescricao {
    RPS(1, "RPS", TipoRpsNfseDTO.RPS),
    NOTA_FISCAL_CONJULGADA(2, "Nota Fiscal Conjulgada (Mista)", TipoRpsNfseDTO.NOTA_FISCAL_CONJULGADA),
    CUPOM(3, "Cupom", TipoRpsNfseDTO.CUPOM),
    NOTA_FISCAL_SERIE_UNICA(4, "Nota Fiscal Série Única", TipoRpsNfseDTO.NOTA_FISCAL_SERIE_UNICA);

    private int codigo;
    private String descricao;
    private TipoRpsNfseDTO tipoRpsNfseDTO;

    TipoRps(int codigo, String descricao, TipoRpsNfseDTO tipoRpsNfseDTO) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoRpsNfseDTO = tipoRpsNfseDTO;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public TipoRpsNfseDTO toDto() {
        return tipoRpsNfseDTO;
    }

    public static TipoRps fromDto(TipoRpsNfseDTO dto) {
        for (TipoRps value : values()) {
            if (value.tipoRpsNfseDTO.equals(dto)) {
                return value;
            }
        }
        return null;
    }
}
