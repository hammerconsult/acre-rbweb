package br.com.webpublico.enums.contabil.reinf;

public enum TipoServicoReinf {
    LIMPEZA_CONSERVACAO_ZELADORIA("100000001", "Limpeza, conservação ou zeladoria"),
    VIGILANCIA_SEGURANCA("100000002", "Vigilância ou segurança"),
    CONSTRUCAO_CIVIL("100000003", "Construção civil"),
    SERVICO_NATUREZA_RURAL("100000004", "Serviços de natureza rural"),
    DIGITACAO("100000005", "Digitação"),
    PREPARACAO_DADOS_PROCESSAMENTO("100000006", "Preparação de dados para processamento"),
    ACABAMENTO("100000007", "Acabamento"),
    EMBALAGEM("100000008", "Embalagem"),
    ACONDICIONAMENTO("100000009", "Acondicionamento"),
    COBRANCA("100000010", "Cobrança"),
    COLETA_LIXO_RESIDUO("100000011", "Coleta ou reciclagem de lixo ou de resíduos"),
    COPA("100000012", "Copa"),
    HOTELARIA("100000013", "Hotelaria"),
    CORTE_LIGACAO_SERVICO_PUBLICO("100000014", "Corte ou ligação de serviços públicos"),
    DISTRIBUICAO("100000015", "Distribuição"),
    TREINAMENTO_ENSINO("100000016", "Treinamento e ensino"),
    ENTREGA_CONTAS_DOCUMENTO("100000017", "Entrega de contas e de documentos"),
    LIGACAO_MEDIDORES("100000018", "Ligação de medidores"),
    LEITURA_MEDIDORES("100000019", "Leitura de medidores"),
    MANUTENCAO_MAQUINAS_EQUIPAMENTOS("100000020", "Manutenção de instalações, de máquinas ou de equipamentos"),
    MONTAGEM("100000021", "Montagem"),
    OPERACAO_MAQUINAS_EQUIPAMENTOS_VEICULOS("100000022", "Operação de máquinas, de equipamentos e de veículos"),
    OPERACAO_PEGACIO_TERMINAL_TRANSPORTE("100000023", "Operação de pedágio ou de terminal de transporte"),
    OPERACAO_TRANSPORTE_PASSAGEIROS("100000024", "Operação de transporte de passageiros"),
    PORTARIA_RECEPCAO_ASCENSORISTA("100000025", "Portaria, recepção ou ascensorista"),
    RECEPCAO_TRIAGEM_MOVIMENTACAO_MATERIAIS("100000026", "Recepção, triagem ou movimentação de materiais"),
    PROMOCAO_VENDAS_EVENTOS("100000027", "Promoção de vendas ou de eventos"),
    SECRETARIA_EXPEDIENTE("100000028", "Secretaria e expediente"),
    SAUDE("100000029", "Saúde"),
    TELEFONIA_TELEMARKETING("100000030", "Telefonia ou telemarketing"),
    TRABALHO_TEMPORARIO("100000031", "Trabalho temporário na forma da Lei nº 6.019, de janeiro de 1974");

    private String codigo;
    private String descricao;

    TipoServicoReinf(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " -  " + descricao;
    }
}
