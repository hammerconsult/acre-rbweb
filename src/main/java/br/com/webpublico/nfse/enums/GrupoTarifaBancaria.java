package br.com.webpublico.nfse.enums;

public enum GrupoTarifaBancaria {
    CADASTRO("Cadastro"), CARTAO_MAGNETICO("Cartão Magnético"), CHEQUE("Cheque"),
    CONTA_CORRENTE("Conta Corrente"), MOVIMENTACAO_RECURSOS("Movimentação de Recursos"),
    EXTRATO_CONTA("Extrato de Conta"), COBRANCA("Cobrança"), CREDITOS("Créditos"),
    OUTROS_SERVICOS("Outros Serviços"), CAPITAIS_ESTRANGEIROS_CAMBIO("Capitais Estrangeiros e Câmbio"),
    CONTA_DEPOSITOS("Conta de Depósitos"), TRANSFERENCIA_RECURSOS("Transferência de Recursos"),
    OPERACOES_CREDITO_ARRENDAMENTO_MERCANTIL("Operações de Crédito e de Arrendamento Mercantil"),
    PACOTE_PADRONIZADO_PESSOA_NATURAL("Pacote Padronizado Pessoa Natural"),
    CARTAO_CREDITO("Cartão de Crédito"), OPERACAO_CAMBIO_MANUAL("Operação de Câmbio Manual");

    private String descricao;

    GrupoTarifaBancaria(String descricao) {
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
