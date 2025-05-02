package br.com.webpublico.enums.administrativo;

public enum OpcaoRelatorioReqCompra {
    COM_DESCRICAO("Com Descrição do Produto"),
    SEM_DESCRICAO("Sem Descrição do Produto");

    OpcaoRelatorioReqCompra(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;


    public String getDescricao() {
        return descricao;
    }


    @Override
    public String toString() {
        return this.descricao;
    }
}
