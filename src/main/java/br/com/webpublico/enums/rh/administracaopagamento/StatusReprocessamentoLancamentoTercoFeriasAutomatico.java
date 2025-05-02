package br.com.webpublico.enums.rh.administracaopagamento;

public enum StatusReprocessamentoLancamentoTercoFeriasAutomatico {
    PRONTO_INCLUSAO("Pronto Para Inclusão", Boolean.TRUE),
    CONCEDIDO("Concedido (não será incluído)", Boolean.FALSE),
    LANCAMENTO_JA_REALIZADO("Lançamento Automático Já Realizado (não será incluído)", Boolean.FALSE),
    PA_VIGENTE_CONCEDIDO("Período Aquisitivo Vigente Concedido (será incluído)", Boolean.TRUE),
    PA_VIGENTE_COM_LANCAMENTO("Período Aquisitivo Vigente Com Lançamento de 1/3 Automático (será incluído)", Boolean.TRUE),
    PA_VIGENTE_COM_LANCAMENTO_NA_COMPETENCIA("Período Aquisitivo Vigente Com Lançamento de 1/3 Automático na Competência (não será incluído)", Boolean.FALSE),
    LANCAMENTO_EXISTENTE_NA_COMPETENCIA("Registro de Lançamento de 1/3 Automático Já Existente na Competência", Boolean.FALSE);

    private String descricao;
    private Boolean seraIncluido;

    StatusReprocessamentoLancamentoTercoFeriasAutomatico(String descricao, Boolean seraIncluido) {
        this.descricao = descricao;
        this.seraIncluido = seraIncluido;
    }

    public String getDescricao() {
        return descricao;
    }

    public Boolean getSeraIncluido() {
        return seraIncluido;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
