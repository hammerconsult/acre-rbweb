/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.EstadoConservacaoBemDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Arthur
 */
public enum EstadoConservacaoBem implements EnumComDescricao {

    OPERACIONAL("Operacional", EstadoConservacaoBemDTO.OPERACIONAL),
    INSERVIVEL("Inservível", EstadoConservacaoBemDTO.OPERACIONAL),
    BAIXADO("Baixado", EstadoConservacaoBemDTO.OPERACIONAL),
    EM_MANUTENCAO("Em Manutenção", EstadoConservacaoBemDTO.EM_MANUTENCAO);
    private final String descricao;
    private final List<SituacaoConservacaoBem> situacoes;
    private final EstadoConservacaoBemDTO toDto;

    private EstadoConservacaoBem(String descricao, EstadoConservacaoBemDTO toDto) {
        this.descricao = descricao;
        this.situacoes = new ArrayList<>();
        this.toDto = toDto;

        if (this.name().equals("OPERACIONAL")) {
            situacoes.add(SituacaoConservacaoBem.USO_NORMAL);
            situacoes.add(SituacaoConservacaoBem.OCIOSO);
            situacoes.add(SituacaoConservacaoBem.RECUPERAVEL);
        } else {
            situacoes.add(SituacaoConservacaoBem.ANTIECONOMICO);
            situacoes.add(SituacaoConservacaoBem.IRRECUPERAVEL);
            situacoes.add(SituacaoConservacaoBem.OBSOLETO);
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public List<SituacaoConservacaoBem> getSituacoes() {
        return Collections.unmodifiableList(situacoes);
    }

    @Override
    public String toString() {
        return this.descricao;
    }

    public static EstadoConservacaoBem[] getValoresPossiveisParaAlteracaoConservacao(TipoBem tipoBem) {
        if (tipoBem == null) {
            return null;
        }
        if (tipoBem.equals(TipoBem.IMOVEIS)) {
            return new EstadoConservacaoBem[]{EstadoConservacaoBem.OPERACIONAL};
        }
        return new EstadoConservacaoBem[]{EstadoConservacaoBem.OPERACIONAL, EstadoConservacaoBem.INSERVIVEL};
    }

    public static EstadoConservacaoBem[] getValoresPossiveisParaLevantamentoDeBemPatrimonial() {
        return new EstadoConservacaoBem[]{EstadoConservacaoBem.OPERACIONAL, EstadoConservacaoBem.INSERVIVEL};
    }

    public EstadoConservacaoBemDTO getToDto() {
        return toDto;
    }
}
