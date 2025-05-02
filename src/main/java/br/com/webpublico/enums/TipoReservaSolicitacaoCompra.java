package br.com.webpublico.enums;

public enum TipoReservaSolicitacaoCompra {

    SOLICITACAO_COMPRA("Solicitação de Compra"),
    EXECUCAO_CONTRATO("Execução Contrato"),
    EXECUCAO_PROCESSO("Execução Ata/Dispensa"),
    ADITIVO("Aditivo"),
    APOSTILAMENTO("Apostilamento");
    private String descricao;

    TipoReservaSolicitacaoCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSolicitacaoCompra() {
        return SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isExecucaoContrato() {
        return EXECUCAO_CONTRATO.equals(this);
    }


    public boolean isExecucaoProcesso() {
        return EXECUCAO_PROCESSO.equals(this);
    }

    public boolean isOutrasReservas() {
        return isExecucaoContrato() || isExecucaoProcesso();
    }

    @Override
    public String toString() {
        return descricao;
    }
}
