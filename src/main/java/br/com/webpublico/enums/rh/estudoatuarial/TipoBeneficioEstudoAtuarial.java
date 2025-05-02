package br.com.webpublico.enums.rh.estudoatuarial;

public enum TipoBeneficioEstudoAtuarial {
    APOSENTADORIA_POR_IDADE("Aposentadoria por Idade", "1"),
    APOSENTADORIA_POR_TEMPO_DE_CONTRIBUICAO ("Aposentadoria por Tempo de Contribuição", "2"),
    APOSENTADORIA_COMPULSORIA("Aposentadoria Compulsória", "3"),
    APOSENTADORIA_POR_INVALIDEZ("Aposentadoria por Invalidez", "4"),
    APOSENTADORIA_COMO_PROFESSOR("Aposentadoria como Professor", "5"),
    APOSENTADORIA_ESPECIAL_ATIVIDADE_DE_RISCO("Aposentadoria Especial - atividade de risco (Art. 40,  Â§ 4Âº, inc. II, CF)", "6"),
    APOSENTADORIA_ESPECIAL_ATIVIDADE_PREJUDICIAIS_A_SAUDE_OU_INTEGRIDADE_FISICA("Aposentadoria Especial -  atividade prejudiciais à saúde ou integridade física (Art. 40,  § 4º, inc. III, CF)", "7"),
    MILITARES_INATIVOS_RESERVA_REMUNERADA("Militares Inativos - Reserva Remunerada", "9"),
    MILITARES_INATIVOS_REFORMA("Militares Inativos – Reforma", "10");

    private String descricao;
    private String codigo;

    TipoBeneficioEstudoAtuarial(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
