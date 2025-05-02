/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoAquisicaoBemDTO;

/**
 * @author Arthur
 */
public enum TipoAquisicaoBem {

    ADJUDICACAO("Adjudicação", TipoAquisicaoBemDTO.ADJUDICACAO),
    APREENSAO("Apreensão", TipoAquisicaoBemDTO.APREENSAO),
    AVALIACAO("Avaliação", TipoAquisicaoBemDTO.AVALIACAO),
    CESSAO("Cessão", TipoAquisicaoBemDTO.CESSAO),
    COMODATO("Comodato", TipoAquisicaoBemDTO.COMODATO),
    COMPRA("Compra", TipoAquisicaoBemDTO.COMPRA),
    CONSTRUCAO("Construção", TipoAquisicaoBemDTO.CONSTRUCAO),
    CONTRATO("Contrato", TipoAquisicaoBemDTO.CONTRATO),
    CONVENIO("Convênio", TipoAquisicaoBemDTO.CONVENIO),
    DACAO("Dação em Pagamento", TipoAquisicaoBemDTO.DACAO),
    DESAPROPRIACAO("Desapropriação", TipoAquisicaoBemDTO.DESAPROPRIACAO),
    DOACAO("Doação", TipoAquisicaoBemDTO.DOACAO),
    PERMUTA("Permuta", TipoAquisicaoBemDTO.PERMUTA),
    REDISTRIBUICAO("Redistribuição", TipoAquisicaoBemDTO.REDISTRIBUICAO),
    USUCAPIAO("Usucapião", TipoAquisicaoBemDTO.USUCAPIAO),
    TRANSFERENCIA("Transferência", TipoAquisicaoBemDTO.TRANSFERENCIA);

    private String descricao;
    private TipoAquisicaoBemDTO toDto;

    private TipoAquisicaoBem(String descricao, TipoAquisicaoBemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoAquisicaoBemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
