package br.com.webpublico.enums;

/**
 * Created by renat on 13/05/2016.
 */
public enum TipoObjetoPortalTransparencia {
    UNIDADE("Unidade", 1),
    ATO_LEGAL("Ato Legal", 1),
    LICITACAO("Licitação", 2),
    CONTRATO("Contrato", 2),
    DISPENSA_LICITACAO("Dispensa de Licitação", 2),
    ATA_REGISTRO_PRECO("Ata de registro de preço", 2),
    ATA_REGISTRO_PRECO_EXTERNO("Ata de registro de preço externo", 2),
    SERVIDOR("Servidor", 3),
    CONVENIO_RECEITA("Convênio de Despesa", 4),
    CONVENIO_DESPESA("Convênio de Receita", 5),
    DIARIA("Diária", 6),
    ALTERACAO_ORCAMENTARIA("Alteração Orçamentária", 7),
    ALTERACAO_ORCAMENTARIA_ESTORNO("Alteração Orçamentária estorno", 8),
    RECEITA_REALIZADA("Receita Realizada", 9),
    RECEITA_REALIZADA_ESTORNO("Receita Realizada estorno", 10),
    EMENDA("EMENDA", 10),
    EMPENHO("Empenho", 11),
    EMPENHO_ESTORNO("Empenho estorno", 12),
    LIQUIDACAO("Liquidação", 13),
    LIQUIDACAO_ESTORNO("Liquidação estorno", 14),
    PAGAMENTO("PAGAMENTO", 15),
    PAGAMENTO_ESTORNO("Pagamento estorno", 16),
    TRANSFERENCIA_CONTA_FINANCEIRA("Transferência Conta Financeira", 17),
    TRANSFERENCIA_CONTA_FINANCEIRA_ESTORNO("Transferência Conta Financeira Estorno", 18),
    LIBERACAO_COTA_FINANCEIRA("Liberação Cota Financeira", 19),
    LIBERACAO_COTA_FINANCEIRA_ESTORNO("Liberação Cota Financeira Estorno", 19),
    OBRA("Obra", 20),
    CALAMIDADE_PUBLICA("Calamidade Pública", 21),
    DICIONARIO_DE_DADOS("Dicionário de Dados", 22),
    LOA("LOA", 23),
    LDO("LDO", 24),
    PPA("PPA", 25),
    BEM("BEM", 26);

    private String descricao;
    private Integer ordem;

    TipoObjetoPortalTransparencia(String descricao, Integer ordem) {
        this.descricao = descricao;
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
