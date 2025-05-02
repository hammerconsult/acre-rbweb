package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 05/09/2018.
 */
public enum TipoGrupoCategoriaTrabalhador {
    EMPREGADO_TRABALHADOR_TEMPORARIO("Empregado e Trabalhador Temporário"),
    AVULSO("Avulso"),
    AGENTE_PUBLICO("Agente Público"),
    CESSAO("Cessão"),
    CONTRIBUICAO_INDIVIDUAL("Contribuinte Individual"),
    BOLSISTAS("Bolsistas");

    private String descricao;

    TipoGrupoCategoriaTrabalhador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
