package br.com.webpublico.enums.tributario;

public enum OrigemFaleConosco {
    NFSE("Nfs-e");

    private String descricao;

    OrigemFaleConosco(String descricao) {
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
