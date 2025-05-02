package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoLoteCessaoDTO;

/**
 * Created by mga on 12/01/2018.
 */
public enum SituacaoLoteCessao {

    AGUARDANDO_EFETIVACAO("Aguardando Efetivação", SituacaoLoteCessaoDTO.AGUARDANDO_EFETIVACAO),
    ACEITA("Aceita", SituacaoLoteCessaoDTO.ACEITA),
    EM_ELABORACAO("Em Elaboração", SituacaoLoteCessaoDTO.EM_ELABORACAO),
    RECUSADA("Recusada", SituacaoLoteCessaoDTO.RECUSADA);

    private String descricao;
    private SituacaoLoteCessaoDTO toDto;

    SituacaoLoteCessao(String descricao, SituacaoLoteCessaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoLoteCessaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
