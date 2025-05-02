package br.com.webpublico.enums;

import br.com.webpublico.tributario.consultadebitos.enums.TipoDeDebitoDTO;

/**
 * @author GhouL
 */
public enum SituacaoDebito {
    EXERCICIO("Do Exercício", false, false),
    DIVIDA_ATIVA("Dívida Ativa", true, false),
    AJUIZADA("Dívida Ativa Ajuizada", true, true);

    private String descricao;
    private Boolean dividaAtiva;
    private Boolean dividaAjuizada;

    public String getDescricao() {
        return descricao;
    }

    public Boolean getDividaAjuizada() {
        return dividaAjuizada;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    private SituacaoDebito(String descricao, Boolean dividaAtiva, Boolean dividaAjuizada) {
        this.descricao = descricao;
        this.dividaAtiva = dividaAtiva;
        this.dividaAjuizada = dividaAjuizada;
    }

    public static SituacaoDebito findByTipoDeDebito(TipoDeDebitoDTO tipoDeDebito) {
        switch (tipoDeDebito) {
            case DA:
            case DAP:
                return SituacaoDebito.DIVIDA_ATIVA;
            case EX:
            case EXP:
                return SituacaoDebito.EXERCICIO;
            case AJZ:
            case AJZP:
                return SituacaoDebito.AJUIZADA;
        }
        return null;
    }
}
