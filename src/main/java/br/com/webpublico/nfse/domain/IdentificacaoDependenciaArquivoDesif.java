package br.com.webpublico.nfse.domain;

public enum IdentificacaoDependenciaArquivoDesif {
    INSCRICAO_MUNICIPAL(1, "1 - Inscrição Municipal"),
    CODIGO(2, "2 - Código");

    private Integer codigo;
    private String descricao;

    IdentificacaoDependenciaArquivoDesif(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
