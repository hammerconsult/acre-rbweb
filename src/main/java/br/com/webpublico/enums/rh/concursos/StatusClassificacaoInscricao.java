package br.com.webpublico.enums.rh.concursos;

public enum StatusClassificacaoInscricao {

    CLASSIFICADO("Classificado", 3),
    FILA_DE_ESPERA("Fila de Espera", 2),
    DESCLASSIFICADO("Desclassificado", 1);

    private String descricao;
    private Integer ordemApresentacao;

    private StatusClassificacaoInscricao(String descricao, Integer ordemApresentacao) {
        this.descricao = descricao;
        this.ordemApresentacao = ordemApresentacao;
    }

    public static StatusClassificacaoInscricao getValue(String texto) {
        for (StatusClassificacaoInscricao statusClassificacaoInscricao : values()) {
            if (statusClassificacaoInscricao.name().equals(texto.toUpperCase())) {
                return statusClassificacaoInscricao;
            }
        }
        return null;

    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdemApresentacao() {
        return ordemApresentacao;
    }

    public void setOrdemApresentacao(Integer ordemApresentacao) {
        this.ordemApresentacao = ordemApresentacao;
    }
}
