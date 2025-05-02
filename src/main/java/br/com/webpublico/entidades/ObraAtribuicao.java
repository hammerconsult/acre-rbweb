package br.com.webpublico.entidades;

/**
 *
 * @author venom
 */
public enum ObraAtribuicao {

    FISCAL("Fiscal"),
    RESPONSAVEL_TECNICO("Responsável Técnico"),
    PROJETISTA("Projetista");
    private String descricao;

    private ObraAtribuicao(String descricao) {
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
