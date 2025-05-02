package br.com.webpublico.pncp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModalidadeContratacaoPncp {

    LEILAO_ELETRONICO(1, "Leilão - Eletrônico"),
    DIALOGO_COMPETITIVO(2, "Diálogo Competitivo"),
    CONCURSO(3, "Concurso"),
    CONCORRENCIA_ELETRONICA(4, "Concorrência - Eletrônica"),
    CONCORRENCIA_PRESENCIAL(5, "Concorrência - Presencial"),
    PREGAO_ELETRONICO(6, "Pregão - Eletrônico"),
    PREGAO_PRESENCIAL(7, "Pregão - Presencial"),
    DISPENSA_LICITACAO(8, "Dispensa de Licitação"),
    INEXIGIBILIDADE(9, "Inexigibilidade"),
    MANIFESTACAO_INTERESSE(10, "Manifestação de Interesse"),
    PRE_QUALIFICACAO(11, "Pré-qualificação"),
    CREDENCIAMENTO(12, "Credenciamento"),
    LEILAO_PRESENCIAL(13, "Leilão - Presencial");

    private final Integer codigo;
    private final String descricao;

    ModalidadeContratacaoPncp(Integer codigo, String descricao) {
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
        return codigo.toString();
    }

    @JsonValue
    public Integer toValue() {
        return codigo;
    }
}
