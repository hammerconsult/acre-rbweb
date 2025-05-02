/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoBaixaDTO;

/**
 * @author cheles
 */
public enum TipoBaixa {
    OBSOLESCENCIA("Obsolescência", TipoBaixaDTO.OBSOLESCENCIA),
    INUTILIZACAO("Inutilização", TipoBaixaDTO.INUTILIZACAO),
    EXTRAVIO("Extravio", TipoBaixaDTO.EXTRAVIO),
    UTILIZACAO_OU_RECUPERACAO_ANTIECONOMICA("Utilização ou Recuperação Antieconômica", TipoBaixaDTO.UTILIZACAO_OU_RECUPERACAO_ANTIECONOMICA),
    DESTRUICAO("Destruição", TipoBaixaDTO.DESTRUICAO),
    DESUSO("Desuso", TipoBaixaDTO.DESUSO),
    IRRECUPERABILIDADE("Irrecuperabilidade", TipoBaixaDTO.IRRECUPERABILIDADE),
    DOACAO_EXTERNA("Doação Externa", TipoBaixaDTO.DOACAO_EXTERNA),
    ALIENACAO("Alienação", TipoBaixaDTO.ALIENACAO),
    INCORPORACAO_INDEVIDA("Incorporação Indevida", TipoBaixaDTO.INCORPORACAO_INDEVIDA),
    AQUISICAO_INDEVIDA("Aquisição Indevida", TipoBaixaDTO.AQUISICAO_INDEVIDA),
    REDISTRIBUICAO("Redistribuição", TipoBaixaDTO.REDISTRIBUICAO),
    TRANSFERENCIA("Transferência", TipoBaixaDTO.TRANSFERENCIA);
    private String descricao;
    private TipoBaixaDTO toDTO;

    TipoBaixa(String descricao, TipoBaixaDTO toDTO) {
        this.descricao = descricao;
        this.toDTO = toDTO;
    }

    public TipoBaixaDTO getToDTO() {
        return toDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
