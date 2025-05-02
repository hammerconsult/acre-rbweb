package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoAquisicaoDTO;

public enum SituacaoAquisicao {

    EM_ELABORACAO("Em Elaboração", SituacaoAquisicaoDTO.EM_ELABORACAO),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação", SituacaoAquisicaoDTO.AGUARDANDO_EFETIVACAO),
    FINALIZADO("Finalizado", SituacaoAquisicaoDTO.FINALIZADO),
    ESTORNADO("Estornado", SituacaoAquisicaoDTO.ESTORNADO),
    RECUSADO("Recusado", SituacaoAquisicaoDTO.RECUSADO);


    private String descricao;
    private SituacaoAquisicaoDTO situacaoAquisicaoDTO;

    SituacaoAquisicao(String descricao, SituacaoAquisicaoDTO situacaoAquisicaoDTO) {
        this.descricao = descricao;
        this.situacaoAquisicaoDTO = situacaoAquisicaoDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoAquisicaoDTO getSituacaoAquisicaoDTO() {
        return situacaoAquisicaoDTO;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

}
