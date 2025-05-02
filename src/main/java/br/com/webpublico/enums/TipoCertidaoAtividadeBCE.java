package br.com.webpublico.enums;


import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoCertidaoAtividadeBCE implements EnumComDescricao {

    ATIVO("Ativo", "ATIVAÇÃO", SituacaoCadastralCadastroEconomico.ATIVA_REGULAR),
    SUSPENSO("Suspenso", "SUSPENSÃO", SituacaoCadastralCadastroEconomico.SUSPENSA),
    BAIXADO("Baixado", "BAIXA", SituacaoCadastralCadastroEconomico.BAIXADA),
    INATIVO("Inativo", "INATIVAÇÃO", SituacaoCadastralCadastroEconomico.INATIVO);

    private String descricao;
    private String descricaoCertidao;
    private SituacaoCadastralCadastroEconomico situacaoCadastral;

    private TipoCertidaoAtividadeBCE(String descricao, String descricaoCertidao, SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.descricao = descricao;
        this.descricaoCertidao = descricaoCertidao;
        this.situacaoCadastral = situacaoCadastral;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoCertidao() {
        return descricaoCertidao;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public static TipoCertidaoAtividadeBCE getTipoPorSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        for (TipoCertidaoAtividadeBCE value : values()) {
            if (value.getSituacaoCadastral().equals(situacaoCadastral)) {
                return value;
            }
        }
        return null;
    }
}
