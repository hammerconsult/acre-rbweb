package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoReservaDotacao implements EnumComDescricao {

    SOLICITACAO_COMPRA("Solicitação de Compra"),
    EXECUCAO_CONTRATO("Execução de Contrato/Ata/Dispensa");
    private String descricao;

    TipoReservaDotacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isReservaSolicitacaoCompra(){
        return SOLICITACAO_COMPRA.equals(this);
    }

    public boolean isReservaExecucao(){
        return EXECUCAO_CONTRATO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
