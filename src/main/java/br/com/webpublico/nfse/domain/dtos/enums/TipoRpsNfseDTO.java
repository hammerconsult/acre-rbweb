package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoRpsNfseDTO implements NfseEnumDTO {
    RPS(1, "RPS"),
    NOTA_FISCAL_CONJULGADA(2, "Nota Fiscal Conjulgada (Mista)"),
    CUPOM(3, "Cupom"),
    NOTA_FISCAL_SERIE_UNICA(4, "Nota Fiscal Série Única");

    private int codigo;
    private String descricao;

    TipoRpsNfseDTO(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoRpsNfseDTO findByCodigo(int codigo) {
        for (TipoRpsNfseDTO tipoRpsNfseDTO : values()) {
            if (tipoRpsNfseDTO.getCodigo() == codigo) {
                return tipoRpsNfseDTO;
            }
        }
        return null;
    }
}
