package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoPublicacaoLicitacaoDTO;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 13/01/15
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public enum TipoPublicacaoLicitacao {

    EDITAL("Edital", TipoPublicacaoLicitacaoDTO.EDITAL),
    AVISO("Aviso", TipoPublicacaoLicitacaoDTO.AVISO),
    RESULTADODERECURSOS("Resultado de Recursos", TipoPublicacaoLicitacaoDTO.RESULTADODERECURSOS),
    ADJUDICACAO("Adjudicação", TipoPublicacaoLicitacaoDTO.ADJUDICACAO),
    HOMOLOGACAO("Homologação", TipoPublicacaoLicitacaoDTO.HOMOLOGACAO),
    OUTROS("Outros", TipoPublicacaoLicitacaoDTO.OUTROS),
    TERMO_RECONHECIMENTO_DIVIDA("Termo de Reconhecimento de Dívida", TipoPublicacaoLicitacaoDTO.TERMO_RECONHECIMENTO_DIVIDA);

    private String descricao;
    private TipoPublicacaoLicitacaoDTO toDto;

    TipoPublicacaoLicitacao(String descricao, TipoPublicacaoLicitacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoPublicacaoLicitacaoDTO getToDto() {
        return toDto;
    }
}
