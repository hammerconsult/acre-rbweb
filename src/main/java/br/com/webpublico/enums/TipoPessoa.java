package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.rh.TipoPessoaDTO;

public enum TipoPessoa implements EnumComDescricao {

    FISICA("F", "Física", TipoPessoaDTO.FISICA),
    JURIDICA("J", "Jurídica", TipoPessoaDTO.JURIDICA);

    private String sigla;
    private String descricao;
    private TipoPessoaDTO toDto;

    TipoPessoa(String sigla, String descricao, TipoPessoaDTO toDto) {
        this.sigla = sigla;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getSigla() {
        return sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoPessoaDTO getToDto() {
        return toDto;
    }
}
