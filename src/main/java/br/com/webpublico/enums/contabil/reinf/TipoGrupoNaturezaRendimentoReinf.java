package br.com.webpublico.enums.contabil.reinf;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoGrupoNaturezaRendimentoReinf implements EnumComDescricao {
    GRUPO10("Grupo 10 - Rendimento do Trabalho e da Previdência Social"),
    GRUPO11("Grupo 11 - Rendimento Decorrente de Decisão Judicial"),
    GRUPO12("Grupo 12 - Rendimento do Capital"),
    GRUPO13("Grupo 13 - Rendimento de Direitos (Royalties)"),
    GRUPO14("Grupo 14 - Prêmios e demais rendimentos"),
    GRUPO15("Grupo 15 - Rendimento Pago/Creditado a Pessoa Jurídica"),
    GRUPO16("Grupo 16 - Demais Rendimentos de Residentes ou domiciliados no Exterior"),
    GRUPO17("Grupo 17 - Rendimentos pagos/creditados EXCLUSIVAMENTE por órgãos da administração federal direta, autarquias e " +
        "fundações federais, empresas públicas, sociedades de economia mista e demais entidades em que a União, direta " +
        "ou indiretamente detenha a maioria do capital social sujeito a voto, e que recebam recursos do Tesouro Nacional"),
    GRUPO18("Grupo 18 - Rendimentos pagos/creditados EXCLUSIVAMENTE por órgãos, autarquias e fundações dos estados, do Distrito Federal e dos municípios"),
    GRUPO19("Grupo 19 - Pagamento a Beneficiário não Identificado – Uso exclusivo para o evento R-4040"),
    GRUPO20("Grupo 20 - Rendimento Pago/Creditado a Pessoa Jurídica – Uso exclusivo para o evento R-4080 – Retenção no Recebimento");
    private String descricao;

    TipoGrupoNaturezaRendimentoReinf(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
