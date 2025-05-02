package br.com.webpublico.enums;

/**
 * Created by mga on 14/02/2018.
 */
public enum SituacaoInventarioEstoque {

    ABERTO("Aberto"),
    CONCLUIDO("Concluído"),
    FINALIZADO("Finalizado");
    private String descricao;

    SituacaoInventarioEstoque(String descricao) {
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
