package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoDaSolicitacaoDTO;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 27/12/13
 * Time: 09:18
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoDaSolicitacao {
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação", SituacaoDaSolicitacaoDTO.AGUARDANDO_EFETIVACAO),
    AGUARDANDO_APROVACAO("Aguardando Aprovação", SituacaoDaSolicitacaoDTO.AGUARDANDO_APROVACAO),
    FINALIZADA("Finalizada", SituacaoDaSolicitacaoDTO.FINALIZADA),
    EM_ELABORACAO("Em Elaboração", SituacaoDaSolicitacaoDTO.EM_ELABORACAO),
    ACEITA("Aceita", SituacaoDaSolicitacaoDTO.ACEITA),
    RECUSADA("Recusada", SituacaoDaSolicitacaoDTO.RECUSADA);

    private String descricao;
    private SituacaoDaSolicitacaoDTO toDto;

    SituacaoDaSolicitacao(String descricao, SituacaoDaSolicitacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoDaSolicitacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
