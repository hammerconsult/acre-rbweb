package br.com.webpublico.enums.administrativo;

public enum AgrupadorMovimentoBem {
    NAO_APLICAVEL("Não Aplicável"),
    AQUISICAO("Aquisição"),
    INCORPORACAO("Incorporação"),
    LEVANTAMENTO("Levantamento"),
    CESSAO("Cessão"),
    REDUCAO_VALOR_BEM("Depreciação"),
    REDUCAO_VALOR_RECUPERAVEL("Redução ao Valor Recuperável"),
    AJUSTE_BENS("Ajuste de Bens"),
    REAVALIACAO_BEM("Reavaliação de Bens"),
    TRANSFERENCIA("Transferência"),
    TRANSFERENCIA_GRUPO_PATRIMONIAL("Transferência Grupo Patrimonial"),
    ALIENACAO("Alienação"),
    BAIXA("Baixa"),
    INVENTARIO("Inventário"),
    MANUTENCAO("Manutenção"),
    ALTERACAO_CONSERVACAO("Alteração de Conservação");
    private String descricao;

    AgrupadorMovimentoBem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
