package br.com.webpublico.enums;

/**
 * Created by William on 19/02/2016.
 */
public enum TipoFuncionalidadeParaAnexo {
    SOLICITACAO_ADESAO_INTERNA("Solicitação de Adesão (Interna)"),
    SOLICITACAO_ADESAO_EXTERNA("Solicitação de Adesão (Externa)"),
    APROVACAO_TRANSFERENCIA_EXTERNA("Aprovação de Transferência Externa"),
    PLANILHA_ORCAMENTARIA("Planilha Orçamentária"),
    CONTRATO("Contrato"),
    LICITACAO("Licitação"),
    OBRA("Medições da Obra");

    private String descricao;

    TipoFuncionalidadeParaAnexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
