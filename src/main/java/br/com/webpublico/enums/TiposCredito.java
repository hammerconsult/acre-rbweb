/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TiposCreditoDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum TiposCredito implements EnumComDescricao {

    CREDITOS_TRIBUTARIOS_A_RECEBER("Créditos Tributários a Receber", "23", 1, TiposCreditoDTO.CREDITOS_TRIBUTARIOS_A_RECEBER),
    CREDITOS_CLIENTES_A_RECEBER("Créditos de Clientes a Receber", "21", 2, TiposCreditoDTO.CREDITOS_CLIENTES_A_RECEBER),
    CREDITOS_TRANSFERENCIAS_A_RECEBER("Créditos de Transferências a Receber", "25", 3, TiposCreditoDTO.CREDITOS_TRANSFERENCIAS_A_RECEBER),
    CREDITOS_NAO_TRIBUTARIOS_A_RECEBER("Créditos não Tributários a Receber", "27", 4, TiposCreditoDTO.CREDITOS_NAO_TRIBUTARIOS_A_RECEBER),
    CREDITOS_DIVERSOS_A_RECEBER("Créditos Diversos a Receber", "29", 5, TiposCreditoDTO.CREDITOS_DIVERSOS_A_RECEBER),

    DIVIDA_ATIVA_TRIBUTARIA("Dívida Ativa Tributária", "41", 6, TiposCreditoDTO.DIVIDA_ATIVA_TRIBUTARIA),
    DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES("Dívida Ativa não Tributária de Clientes", "44", 7, TiposCreditoDTO.DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES),
    DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS("Dívida Ativa não Tributária demais Créditos", "47", 8, TiposCreditoDTO.DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS),

    NAO_APLICAVEL("Não Aplicável", "00", 9, TiposCreditoDTO.NAO_APLICAVEL),
    VARIACAO_PATRIMONIAL_AUMENTATIVA("Variação Patrimonial Aumentativa", "11", 10, TiposCreditoDTO.VARIACAO_PATRIMONIAL_AUMENTATIVA),
    CONVENIO_E_CONGENERE("Convênio e Congênere", "51", 11, TiposCreditoDTO.CONVENIO_E_CONGENERE),
    OPERACAO_DE_CREDITO("Operação de Crédito", "61", 12, TiposCreditoDTO.OPERACAO_DE_CREDITO),
    ALIENACAO_DE_BENS("Alienação de Bens", "71", 13, TiposCreditoDTO.ALIENACAO_DE_BENS),
    DEDUCAO_DA_RECEITA("Dedução da Receita", "90", 14, TiposCreditoDTO.DEDUCAO_DA_RECEITA);

    private String descricao;
    private String codigo;
    private Integer ordemApresentacao;
    private TiposCreditoDTO toDto;

    TiposCredito(String descricao, String codigo, Integer ordemApresentacao, TiposCreditoDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.ordemApresentacao = ordemApresentacao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public Integer getOrdemApresentacao() {
        return ordemApresentacao;
    }

    public TiposCreditoDTO getToDto() {
        return toDto;
    }

    public boolean isDividaAtiva() {
        return Lists.newArrayList(DIVIDA_ATIVA_TRIBUTARIA,
            DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES,
            DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS)
            .contains(this);
    }

    public static List<TiposCredito> buscarTiposAgrupadorRelatorioCredito() {
        return Lists.newArrayList(CREDITOS_TRIBUTARIOS_A_RECEBER,
            CREDITOS_CLIENTES_A_RECEBER,
            CREDITOS_TRANSFERENCIAS_A_RECEBER,
            CREDITOS_NAO_TRIBUTARIOS_A_RECEBER,
            CREDITOS_DIVERSOS_A_RECEBER,
            DIVIDA_ATIVA_TRIBUTARIA,
            DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES,
            DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
