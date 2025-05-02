package br.com.webpublico.enums;

public enum TipoResumoExecucao {
    CONTRATO("Contrato"),
    PROCESSO("Processo");
    private String descricao;

    TipoResumoExecucao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isExecucaoContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isExecucaoProcesso() {
        return PROCESSO.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }
}
