package br.com.webpublico.pncp.enums;

public enum OperacaoPncp {
    INSERIR("Inserir"), ALTERAR("Alterar"), EXCLUIR("Excluir");
    private String descricao;

    OperacaoPncp(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
