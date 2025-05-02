/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.administrativo.TipoStatusSolicitacaoDTO;

/**
 * @author Sarruf
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoStatusSolicitacao {

    APROVADA("Aprovada", TipoStatusSolicitacaoDTO.APROVADA),
    REJEITADA("Rejeitada", TipoStatusSolicitacaoDTO.REJEITADA),
    AGUARDANDO_AVALIACAO("Aguardando Avaliação", TipoStatusSolicitacaoDTO.AGUARDANDO_AVALIACAO);
    private String descricao;
    private TipoStatusSolicitacaoDTO toDto;

    TipoStatusSolicitacao(String descricao, TipoStatusSolicitacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoStatusSolicitacaoDTO getToDto() {
        return toDto;
    }

    public boolean isAprovada(){
        return APROVADA.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
