package br.com.webpublico.enums.rh.esocial;

import com.google.common.collect.Lists;

import java.util.List;

public enum ProvimentoEstatutarioEsocial {
    NOMEACAO_CARGO_EFETIVO("Nomeação em cargo efetivo", 1),
    NOMEACAO_EXCLUSIVAMENTE_CARGO_COMISSAO("Nomeação exclusivamente em cargo em comissão", 2),
    INCORPORACAO_MATRICULA_OU_NOMEACAO_MILITAR("Incorporação, matrícula ou nomeação (militar)", 3),
    REDISTRIBUICAO_OU_REFORMA_ADMINISTRATIVA("Redistribuição ou Reforma Administrativa", 5),
    DIPLOMACAO("Diplomação", 6),
    CONTRATACAO_TEMPO_DETERMINADO("Contratação por tempo determinado", 7),
    REMOCAO("Remoção (em caso de alteração do órgão declarante)", 8),
    DESIGNACAO("Designação", 9),
    MUDANCA_CPF("Mudança de CPF", 10),
    ESTABILIZADOS("Estabilizados - Art. 19 do ADCT", 11),
    OUTROS("Outros", 99);

    private String descricao;
    private Integer codigo;

    ProvimentoEstatutarioEsocial(String descricao, Integer codigo) {
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

    public static List<ProvimentoEstatutarioEsocial> provimentoAdministracaoPublica() {
        return Lists.newArrayList(INCORPORACAO_MATRICULA_OU_NOMEACAO_MILITAR, REDISTRIBUICAO_OU_REFORMA_ADMINISTRATIVA,
            DIPLOMACAO, CONTRATACAO_TEMPO_DETERMINADO, REMOCAO, DESIGNACAO);
    }
}
