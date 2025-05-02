package br.com.webpublico.enums;

public enum TipoTermoAlteracaoContratual {

    PRAZO("Prazo"),
    VALOR("Valor"),
    PRAZO_VALOR("Prazo e Valor"),
    ALTERACAO_CADASTRAL("Alteração Cadastral"),
    NAO_APLICAVEL("Não Aplicável");
    private String descricao;

    TipoTermoAlteracaoContratual(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isPrazo() {
        return TipoTermoAlteracaoContratual.PRAZO.equals(this);
    }

    public boolean isValor() {
        return TipoTermoAlteracaoContratual.VALOR.equals(this);
    }

    public boolean isPrazoValor() {
        return TipoTermoAlteracaoContratual.PRAZO_VALOR.equals(this);
    }

    public boolean isAlteracaoCadastral() {
        return TipoTermoAlteracaoContratual.ALTERACAO_CADASTRAL.equals(this);
    }

    public boolean isNaoAplicavel() {
        return TipoTermoAlteracaoContratual.NAO_APLICAVEL.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
