package br.com.webpublico.entidades.rh.creditodesalario;


public enum TipoArquivoCreditoSalario {
    REMESSA("Remessa"),
    RELATORIO("Relatório");

    private String descricao;

    TipoArquivoCreditoSalario(String descricao) {
        this.descricao = descricao;
    }
}
