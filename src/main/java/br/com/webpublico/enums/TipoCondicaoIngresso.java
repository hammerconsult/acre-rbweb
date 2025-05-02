package br.com.webpublico.enums;

/**
 * Created by William on 08/08/2018.
 */
public enum TipoCondicaoIngresso {
    VISTO_PERMANENTE("Visto permanente", 1),
    VISTO_TEMPORARIO("Visto temporário", 2),
    ASILADO("Asilado", 3),
    REFUGIADO("Refugiado", 4),
    SOLICITANTE_REFUGIO("Solicitante de Refúgio", 5),
    RESIDENTE_FORA_BRASIL("Residente fora do Brasil", 6),
    DEFICIENTE_FISICO_MAIS_51ANOS("Deficiente físico e com mais de 51anos", 7),
    RESIDENCIA_PROVISORIA_ANISTIADO_IRREGULAR("Com residência provisória e anistiado,em situação irregular", 8),
    RESIDENCIA_BRASIL_FILHOS_OU_CONJUGUE("Permanência no Brasil em razão de filhos ou cônjuge brasileiros", 9),
    ACORDO_MERCOSUL("Beneficiado pelo acordo entre países do Mercosul", 10),
    AGENTE_DIPLOMATICO_OU_CONSULADO("Dependente de agente diplomático e/ou consular de países que mantém convênio de reciprocidade para o exercício de atividade remunerada no Brasil", 11),
    TRATADO_AMIZADE_COOPERACAO_BRASIL_PORTUGAL("Beneficiado pelo Tratado de Amizade,Cooperação e Consulta entre a República Federativa do Brasil e a República Portuguesa", 12);

    private String descricao;
    private Integer codigo;
    TipoCondicaoIngresso(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
