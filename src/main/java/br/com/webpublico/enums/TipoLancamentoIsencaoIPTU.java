package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.tributario.TipoLancamentoIsencaoIPTUDTO;

/**
 * @author Fabio
 */
public enum TipoLancamentoIsencaoIPTU {

    //    NORMAL("0-Normal"),
    ISENTO_IPTU_TSU("2-Isento IPTU+TSU", "IPTU+TSU", TipoLancamentoIsencaoIPTUDTO.ISENTO_IPTU_TSU),
    ISENTO_IPTU("3-Isento IPTU", "IPTU", TipoLancamentoIsencaoIPTUDTO.ISENTO_IPTU),
    ISENTO_TSU("4-Isento TSU", "TSU", TipoLancamentoIsencaoIPTUDTO.ISENTO_TSU);

    private String descricao;
    private String descricaoCurta;
    private TipoLancamentoIsencaoIPTUDTO toDto;

    TipoLancamentoIsencaoIPTU(String descricao, String descricaoCurta, TipoLancamentoIsencaoIPTUDTO toDto) {
        this.descricao = descricao;
        this.descricaoCurta = descricaoCurta;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public TipoLancamentoIsencaoIPTUDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
