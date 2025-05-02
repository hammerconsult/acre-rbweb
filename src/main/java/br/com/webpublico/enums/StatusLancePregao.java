/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.StatusLancePregaoDTO;

/**
 * @author lucas
 */
public enum StatusLancePregao {
    ATIVO("Ativo", StatusLancePregaoDTO.ATIVO),
    DESCLASSIFICADO("Desclassificado", StatusLancePregaoDTO.DESCLASSIFICADO),
    CANCELADO("Cancelado", StatusLancePregaoDTO.CANCELADO),
    DECLINADO("Declinado", StatusLancePregaoDTO.DECLINADO),
    INEXEQUIVEL("Inexequível", StatusLancePregaoDTO.INEXEQUIVEL),
    VENCEDOR("Vencedor", StatusLancePregaoDTO.VENCEDOR),
    NAO_REALIZADO("Não Realizado", StatusLancePregaoDTO.NAO_REALIZADO);
    private String descricao;
    private StatusLancePregaoDTO toDto;

    StatusLancePregao(String descricao, StatusLancePregaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusLancePregaoDTO getToDto() {
        return toDto;
    }

    public boolean isAtivo() {
        return ATIVO.equals(this);
    }

    public boolean isAtivoOrVencedor() {
        return ATIVO.equals(this) || VENCEDOR.equals(this);
    }

    public boolean isVencedor() {
        return VENCEDOR.equals(this);
    }

    public boolean isCancelado() {
        return CANCELADO.equals(this);
    }

    public boolean isInexiquivel() {
        return INEXEQUIVEL.equals(this);
    }

    public boolean isDeclinado() {
        return DECLINADO.equals(this);
    }

    public boolean isDesclassificado() {
        return DESCLASSIFICADO.equals(this);
    }

    public boolean isInativo() {
        return !isAtivo() && !isVencedor();
    }

    public StatusLancePregao getStatusLancePregaoPorTipoStatusItemPregao(TipoStatusItemPregao statusItemPregao) {
        if (statusItemPregao != null) {
            switch (statusItemPregao) {
                case DESERTO:
                    return DECLINADO;
                case CANCELADO:
                    return CANCELADO;
                case DECLINADO:
                    return DECLINADO;
                case INEXEQUIVEL:
                    return INEXEQUIVEL;
                case PREJUDICADO:
                    return CANCELADO;
            }
        }
        return null;
    }
}
