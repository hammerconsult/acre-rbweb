/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.OperacaoORCDTO;

/**
 * @author venon
 */
public enum OperacaoORC {
    DOTACAO("Dotação", OperacaoORCDTO.DOTACAO),
    SUPLEMENTACAO("Suplementação", OperacaoORCDTO.SUPLEMENTACAO),
    ANULACAO("Anulação", OperacaoORCDTO.ANULACAO),
    EMPENHO("Empenho", OperacaoORCDTO.EMPENHO),
    LIQUIDACAO("Liquidação", OperacaoORCDTO.LIQUIDACAO),
    RESERVADO("Reservado", OperacaoORCDTO.RESERVADO),
    RESERVADO_POR_LICITACAO("Reservado por Licitação", OperacaoORCDTO.RESERVADO_POR_LICITACAO),
    PAGAMENTO("Pagamento", OperacaoORCDTO.PAGAMENTO),
    EMPENHORESTO_PROCESSADO("Empenho de Resto - Processado", OperacaoORCDTO.EMPENHORESTO_PROCESSADO),
    EMPENHORESTO_NAO_PROCESSADO("Empenho de Resto - Não Processado", OperacaoORCDTO.EMPENHORESTO_NAO_PROCESSADO),
    LIQUIDACAORESTO_PROCESSADO("Liquidação de Resto - Processado", OperacaoORCDTO.LIQUIDACAORESTO_PROCESSADO),
    LIQUIDACAORESTO_NAO_PROCESSADO("Liquidação de Resto - Não Processado", OperacaoORCDTO.LIQUIDACAORESTO_NAO_PROCESSADO),
    PAGAMENTORESTO_PROCESSADO("Pagamento de Resto - Processado", OperacaoORCDTO.PAGAMENTORESTO_PROCESSADO),
    PAGAMENTORESTO_NAO_PROCESSADO("Pagamento de Resto - Não Processado", OperacaoORCDTO.PAGAMENTORESTO_NAO_PROCESSADO);
    private String descricao;
    private OperacaoORCDTO toDto;

    OperacaoORC(String descricao, OperacaoORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OperacaoORCDTO getToDto() {
        return toDto;
    }

    public boolean isEmpenhado(){
        return EMPENHO.equals(this);
    }

    public boolean isReservadoPorLicitacao(){
        return RESERVADO_POR_LICITACAO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }

}
