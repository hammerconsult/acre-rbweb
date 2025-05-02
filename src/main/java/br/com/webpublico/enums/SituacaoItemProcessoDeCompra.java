package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoItemProcessoDeCompraDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 07/07/2016
 * Time: 08:50
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoItemProcessoDeCompra {
    ADJUDICADO("Adjudicado", SituacaoItemProcessoDeCompraDTO.ADJUDICADO),
    HOMOLOGADO("Homologado", SituacaoItemProcessoDeCompraDTO.HOMOLOGADO),
    AGUARDANDO("Aguardando", SituacaoItemProcessoDeCompraDTO.AGUARDANDO),
    FRUSTRADO("Frustrado", SituacaoItemProcessoDeCompraDTO.FRUSTRADO);
    private String descricao;
    private SituacaoItemProcessoDeCompraDTO toDto;

    SituacaoItemProcessoDeCompra(String descricao, SituacaoItemProcessoDeCompraDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoItemProcessoDeCompraDTO getToDto() {
        return toDto;
    }

    public static SituacaoItemProcessoDeCompra getSituacaoPorStatusFornecedor(TipoSituacaoFornecedorLicitacao tipo) {
        if (TipoSituacaoFornecedorLicitacao.ADJUDICADA.equals(tipo)) {
            return SituacaoItemProcessoDeCompra.ADJUDICADO;
        }
        if (TipoSituacaoFornecedorLicitacao.HOMOLOGADA.equals(tipo)) {
            return SituacaoItemProcessoDeCompra.HOMOLOGADO;
        }
        return SituacaoItemProcessoDeCompra.AGUARDANDO;
    }

    public boolean isAndamento(){
        return ADJUDICADO.equals(this);
    }

    public boolean isAdjudicado(){
        return ADJUDICADO.equals(this);
    }

    public boolean isHomologado(){
        return HOMOLOGADO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
