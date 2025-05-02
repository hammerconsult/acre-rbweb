/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.OperacaoDividaAtivaDTO;

/**
 * @author juggernaut
 */
public enum OperacaoDividaAtiva {

    A_INSCREVER("A Inscrever em Dívida Ativa", OperacaoDividaAtivaDTO.A_INSCREVER),
    INSCRICAO("Inscrição em Dívida Ativa", OperacaoDividaAtivaDTO.INSCRICAO),
    ATUALIZACAO("Atualização de Dívida Ativa", OperacaoDividaAtivaDTO.ATUALIZACAO),
    PROVISAO("Ajuste de Perdas de Dívida Ativa", OperacaoDividaAtivaDTO.PROVISAO),
    AJUSTE_PERDAS_LONGO_PRAZO("Ajuste de Perdas de Dívida Ativa a Longo Prazo", OperacaoDividaAtivaDTO.AJUSTE_PERDAS_LONGO_PRAZO),
    REVERSAO("Reversão de Ajuste de Perdas de Dívida Ativa", OperacaoDividaAtivaDTO.REVERSAO),
    REVERSAO_AJUSTE_LONGO_PRAZO("Reversão de Ajuste de Perdas de Dívida Ativa a Longo Prazo", OperacaoDividaAtivaDTO.REVERSAO_AJUSTE_LONGO_PRAZO),
    TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO("Transferência de Dívida Ativa do Curto para Longo Prazo", OperacaoDividaAtivaDTO.TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO),
    TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO("Transferência de Dívida Ativa do Longo para Curto Prazo", OperacaoDividaAtivaDTO.TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO),
    TRANSFERENCIA_AJUSTE_PERDAS_CURTO_PARA_LONGO_PRAZO("Transferência de Ajuste de Perdas de Dívida Ativa do Curto para Longo Prazo", OperacaoDividaAtivaDTO.TRANSFERENCIA_AJUSTE_PERDAS_CURTO_PARA_LONGO_PRAZO),
    TRANSFERENCIA_AJUSTE_PERDAS_LONGO_PARA_CURTO_PRAZO("Transferência de Ajuste de Perdas de Dívida Ativa do Longo para Curto Prazo", OperacaoDividaAtivaDTO.TRANSFERENCIA_AJUSTE_PERDAS_LONGO_PARA_CURTO_PRAZO),
    BAIXA("Baixa de Dívida Ativa", OperacaoDividaAtivaDTO.BAIXA),
    RECEBIMENTO("Recebimento de Dívida Ativa", OperacaoDividaAtivaDTO.RECEBIMENTO),
    AUMENTATIVO("Ajuste Aumentativo de Dívida Ativa", OperacaoDividaAtivaDTO.AUMENTATIVO),
    DIMINUTIVO("Ajuste Diminutivo de Dívida Ativa", OperacaoDividaAtivaDTO.DIMINUTIVO);

    private String descricao;
    private OperacaoDividaAtivaDTO toDto;

    OperacaoDividaAtiva(String descricao, OperacaoDividaAtivaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public OperacaoDividaAtivaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
