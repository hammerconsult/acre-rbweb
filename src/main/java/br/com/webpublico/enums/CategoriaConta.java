package br.com.webpublico.enums;

/**
 * Determina as categorias das contas em um plano de contas. Contas ANALITICAS
 * sÃo contas que não possuem contas filhas e que podem receber lançamentos.
 * Contas SINTETICAS são conta que não podem receber lançamentos por possuirem
 * (ou vir a possuir) contas filhas.
 */
public enum CategoriaConta {

    ANALITICA("Analítica"),
    SINTETICA("Sintética");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    private CategoriaConta(String descricao) {
        this.descricao = descricao;
    }
}
