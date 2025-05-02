package br.com.webpublico.enums;

/**
 * Created by venom on 30/10/14.
 */
public enum TipoArt {
    OBRA_SERVICO("ART de Obra ou Serviço"),
    MULTIPLA("ART Múltipla"),
    CARGO_FUNCAO("ART de Cargo ou Função");
    private String descricao;

    TipoArt(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
