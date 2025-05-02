package br.com.webpublico.enums;

public enum SubTipoMovimentoItemContrato {
    INCLUSAO("Inclusão"),
    EXCLUSAO("Exclusão"),
    NORMAL("Normal"),
    ESTORNO("Estorno"),
    ACRESCIMO("Acréscimo"),
    SUPRESSAO("Supressão");

    private String descricao;

    SubTipoMovimentoItemContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isSupressao(){
        return SUPRESSAO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static SubTipoMovimentoItemContrato retornaSubTipoMovimentoPorFinalidade(FinalidadeMovimentoAlteracaoContratual finalidade) {
        switch (finalidade) {
            case ACRESCIMO:
                return ACRESCIMO;
            case SUPRESSAO:
                return SUPRESSAO;
            default:
                return null;
        }
    }
}
