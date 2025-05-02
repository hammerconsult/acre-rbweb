package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoControleLicitacaoDTO;

/**
 * Created by wellington on 12/09/17.
 */
public enum TipoControleLicitacao {
    QUANTIDADE("Quantidade", TipoControleLicitacaoDTO.QUANTIDADE),
    VALOR("Valor", TipoControleLicitacaoDTO.VALOR);
    private String descricao;
    private TipoControleLicitacaoDTO toDto;

    TipoControleLicitacao(String descricao, TipoControleLicitacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public static TipoControleLicitacao[] values(TipoSolicitacao tipoSolicitacao) {
        if (tipoSolicitacao != null) {
            switch (tipoSolicitacao) {
                case COMPRA_SERVICO: {
                    return new TipoControleLicitacao[]{QUANTIDADE, VALOR};
                }
                case OBRA_SERVICO_DE_ENGENHARIA: {
                    return new TipoControleLicitacao[]{QUANTIDADE};
                }
            }
        }
        return new TipoControleLicitacao[]{};
    }

    public boolean isTipoControlePorValor() {
        return TipoControleLicitacao.VALOR.equals(this);
    }

    public boolean isTipoControlePorQuantidade() {
        return TipoControleLicitacao.QUANTIDADE.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoControleLicitacaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
