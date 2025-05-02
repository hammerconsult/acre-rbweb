package br.com.webpublico.entidades.rh.esocial;

public enum GrupoBeneficioEsocial {
    GRUPO_1("Grupo 01 - Aposentadoria por Idade e Tempo de Contribuição"),
    GRUPO_2("Grupo 02 - Aposentadoria Especial"),
    GRUPO_3("Grupo 03 - Aposentadoria por Invalidez/Incapacidade Permanente"),
    GRUPO_4("Grupo 04 - Militares (Reforma)"),
    GRUPO_5("Grupo 05 - Militares (Reserva)"),
    GRUPO_6("Grupo 06 - Pensão por Morte"),
    GRUPO_7("Grupo 07 - Complementação do Benefício do Regime Geral de Previdência Social (RGPS)"),
    GRUPO_8("Grupo 08 - Benefícios Concedidos Antes da Obrigatoriedade de Envio dos Eventos Não Periódicos para Entes Públicos no eSocial (Carga Inicial)"),
    GRUPO_9("Grupo 09 - Benefícios Especiais com Vínculo Previdenciário"),
    GRUPO_10("Grupo 10 - Benefícios Especiais sem Vínculo Previdenciário"),
    GRUPO_11("Grupo 11 - Parlamentares");

    private String descricao;

    GrupoBeneficioEsocial(String descricao) {
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
