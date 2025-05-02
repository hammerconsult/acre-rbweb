package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoSituacaoFornecedorLicitacaoDTO;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/02/14
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public enum TipoSituacaoFornecedorLicitacao {
    ADJUDICADA("Adjudicada", "Adjudicação", TipoSituacaoFornecedorLicitacaoDTO.ADJUDICADA),
    HOMOLOGADA("Homologada", "Homologação", TipoSituacaoFornecedorLicitacaoDTO.HOMOLOGADA);

    private String descricao;
    private String descricaoFuncional;
    private TipoSituacaoFornecedorLicitacaoDTO toDto;

    TipoSituacaoFornecedorLicitacao(String descricao, String descricaoFuncional, TipoSituacaoFornecedorLicitacaoDTO toDto) {
        this.descricao = descricao;
        this.descricaoFuncional = descricaoFuncional;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoFuncional() {
        return descricaoFuncional;
    }

    public TipoSituacaoFornecedorLicitacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
