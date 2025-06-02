package br.com.webpublico.enums;

public enum TipoRequisicaoCompra {
    CONTRATO("Contrato"),
    ATA_REGISTRO_PRECO("Ata Registro de Preço"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa/Inexigibilidade"),
    RECONHECIMENTO_DIVIDA("Reconhecimento de Dívida");
    private String descricao;

    TipoRequisicaoCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isAtaRegistroPreco() {
        return ATA_REGISTRO_PRECO.equals(this);
    }

    public boolean isDispensaInexigibilidade() {
        return DISPENSA_LICITACAO_INEXIGIBILIDADE.equals(this);
    }

    public boolean isReconhecimentoDivida() {
        return RECONHECIMENTO_DIVIDA.equals(this);
    }

    public boolean isExecucaoProcesso() {
        return isAtaRegistroPreco() || isDispensaInexigibilidade();
    }

    public boolean isExecucao() {
        return isContrato() || isAtaRegistroPreco() || isDispensaInexigibilidade();
    }

    @Override
    public String toString() {
        return descricao;
    }
}
