package br.com.webpublico.enums.tributario.regularizacaoconstrucao;


import br.com.webpublico.webreportdto.dto.tributario.TipoConstrucaoDTO;

public enum TipoConstrucao {

    CONSTRUCAO(1, "Construção", TipoConstrucaoDTO.CONSTRUCAO),
    DEMOLICAO(2, "Demolição", TipoConstrucaoDTO.DEMOLICAO),
    REFORMA(3, "Reforma", TipoConstrucaoDTO.REFORMA),
    OUTRO(4, "Outro", TipoConstrucaoDTO.OUTRO);

    private final Integer codigo;
    private final String descricao;
    private final TipoConstrucaoDTO toDto;

    TipoConstrucao(Integer codigo, String descricao, TipoConstrucaoDTO toDto) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public TipoConstrucaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
