package br.com.webpublico.enums;

public enum OrigemCotacao {
    FORNECEDOR("Fornecedor"),
    BANCO_DE_PRECO_RBWEB("Banco de Preço RBWEB"),
    BANCO_DE_PRECO_EXTERNO("Banco de Preço Externo");


    private String descricao;

    OrigemCotacao(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isOrigemBancoPrecoREWEB(){
        return BANCO_DE_PRECO_RBWEB.equals(this);
    }

    public boolean isOrigemBancoExterno(){
        return BANCO_DE_PRECO_EXTERNO.equals(this);
    }

    public boolean isOrigemFornecedor(){
        return FORNECEDOR.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
