package br.com.webpublico.enums.rh;

/**
 * Created by peixe on 15/09/17.
 */
public enum SituacaoPessoaPortal {
    AGUARDANDO_LIBERACAO("Aguardando Liberação"),
    LIBERADO("Liberado"),
    SEM_ALTERACAO("Sem Alteração");

    private String descricao;

    SituacaoPessoaPortal(String descricao) {
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
