package br.com.webpublico.enums.rh;

public enum InconsistenciaProgressao {
    MAIS_5_ANOS_SEM_PROGRESSAO("Faz mais de 5 anos que o servidor não tem provimento de progressão", Boolean.FALSE),
    PERIODOS_ENQUADRAMENTO_CONCOMITANTES("Progressão bloqueada! Foram encontrados enquadramentos concomitantes com o novo enquadramento gerado.", Boolean.TRUE),
    ALONGAMENTO_ENQUADRAMENTO_POR_AFASTAMENTO("O servidor(a) sem direito a progressão na competência em virtude de afastamentos marcados para \"Não Permitir Progressão\".", Boolean.TRUE);

    private String descricao;
    private Boolean bloquearSalvar;

    InconsistenciaProgressao(String descricao, Boolean permiteSalvar) {
        this.descricao = descricao;
        this.bloquearSalvar = permiteSalvar;
    }

    public String getDescricao() {
        return descricao;
    }

    public Boolean getBloquearSalvar() {
        return bloquearSalvar != null && bloquearSalvar;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
