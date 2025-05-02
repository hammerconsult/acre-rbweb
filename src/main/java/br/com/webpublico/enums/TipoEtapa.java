package br.com.webpublico.enums;

/**
 * Created by Buzatto on 25/08/2015.
 */
public enum TipoEtapa {

    CRIACAO("Criação"),
    AVALIACAO_CONCURSO("Avaliação do Concurso"),
    BANCA_EXAMINADORA("Banca Examinadora"),
    FASES_E_PROVAS("Fases e Provas"),
    PUBLICACAO("Publicação"),
    INSCRICAO("Inscrição"),
    AVALIACAO_DE_PROVAS("Avaliação de Provas"),
    RECURSO("Recurso"),
    AVALIACAO_DE_RECURSO("Avaliação de Recurso"),
    HOMOLOGACAO("Homologação"),
    CONVOCACAO("Convocação");
    private final String descricao;

    TipoEtapa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
