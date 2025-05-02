package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoRequisicaoMaterialDTO;

/**
 * Created by mga on 29/12/2017.
 */
public enum SituacaoRequisicaoMaterial {
    NAO_AVALIADA("Não Avaliada", SituacaoRequisicaoMaterialDTO.NAO_AVALIADA),
    APROVADA_TOTALMENTE_E_NAO_ATENDIDA("Aprovada Totalmente e Não Atendida", SituacaoRequisicaoMaterialDTO.APROVADA_TOTALMENTE_E_NAO_ATENDIDA),
    APROVADA_PARCIALMENTE_E_NAO_ATENDIDA("Aprovada Parcialmente e Não Atendida", SituacaoRequisicaoMaterialDTO.APROVADA_PARCIALMENTE_E_NAO_ATENDIDA),
    REJEITADA("Rejeitada", SituacaoRequisicaoMaterialDTO.REJEITADA),
    ATENDIDA_TOTALMENTE("Atendida Totalmente", SituacaoRequisicaoMaterialDTO.ATENDIDA_TOTALMENTE),
    ATENDIDA_PARCIALMENTE("Atendida Parcialmente", SituacaoRequisicaoMaterialDTO.ATENDIDA_PARCIALMENTE),
    SAIDA_TOTAL_ENTRADA_NAO_REALIZADA("Saída Total e Entrada não Realizada", SituacaoRequisicaoMaterialDTO.SAIDA_TOTAL_ENTRADA_NAO_REALIZADA),
    SAIDA_PARCIAL_ENTRADA_NAO_REALIZADA("Saída Parcial e Entrada não Realizada", SituacaoRequisicaoMaterialDTO.SAIDA_PARCIAL_ENTRADA_NAO_REALIZADA),
    TRANSFERENCIA_TOTAL_CONCLUIDA("Transferência Total Concluída", SituacaoRequisicaoMaterialDTO.TRANSFERENCIA_TOTAL_CONCLUIDA),
    SAIDA_PARCIAL_ENTRADAS_REALIZADAS("Saída Parcial com Entrada(s) Realizada(s)", SituacaoRequisicaoMaterialDTO.SAIDA_PARCIAL_ENTRADAS_REALIZADAS),
    SAIDA_TOTAL_ENTRADAS_REALIZADAS("Saída Total com Entrada(s) Realizada(s)", SituacaoRequisicaoMaterialDTO.SAIDA_TOTAL_ENTRADAS_REALIZADAS);
    private String descricao;
    private SituacaoRequisicaoMaterialDTO toDto;

    SituacaoRequisicaoMaterial(String descricao, SituacaoRequisicaoMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoRequisicaoMaterialDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
