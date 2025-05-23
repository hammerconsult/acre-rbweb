package br.com.webpublico.pncp.enums;

public enum TipoEventoPncp {
    CARGA_INICIAL_ORGAO_ENTIDADE("Carga Inicial Entidade"),
    ORGAO_ENTIDADE("Órgão/Entidade"),
    UNIDADE("Unidade"),
    USUARIO("Usuário"),
    CARGA_INICIAL_LICITACAO("Carga Inicial Licitação"),
    LICITACAO("Licitação"),
    CARGA_INICIAL_ATA_REGISTRO_PRECO("Carga Inicial Ata Registro de Preço"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço"),
    ITEM_LICITACAO("Item Licitação"),
    ATUALIZACAO_ID_SEQUENCIAO_PNCP("Atualização Id/Sequencial PNCP"),
    CARGA_INICIAL_CONTRATO_EMPENHO("Carga Inicial Contrato/Empenho"),
    CONTRATO_EMPENHO("Contrato/Empenho"),
    PLANO_CONTRATACAO_ANUAL("Plano Contratacao Anual - PCA"),
    ITEM_PLANO_CONTRATACAO_ANUAL("Item PCA");
    private String descricao;

    TipoEventoPncp(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtaRegistroPreco(){
        return ATA_REGISTRO_PRECO.equals(this);
    }
}
