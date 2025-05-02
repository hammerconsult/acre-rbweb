package br.com.webpublico.enums.contabil;

public enum SituacaoMovimentoContabil {
    PENDENTE("Pendente"),
    PROCESSAMENTO("Processado"),
    FINALIZADO("Finalizado");

    private String descricao;

    SituacaoMovimentoContabil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
