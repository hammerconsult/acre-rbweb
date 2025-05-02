package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 12/08/14
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */
public enum TipoSolicitacaoRegistroPreco {

    INTERNA("Interna"),
    EXTERNA("Externa");

    private String descricao;

    private TipoSolicitacaoRegistroPreco(String descricao) {
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

    public TipoFuncionalidadeParaAnexo getTipoFuncionalidadeParaAnexo() {
        if (this == null) {
            return null;
        }
        switch (this) {
            case INTERNA: {
                return TipoFuncionalidadeParaAnexo.SOLICITACAO_ADESAO_INTERNA;
            }
            case EXTERNA: {
                return TipoFuncionalidadeParaAnexo.SOLICITACAO_ADESAO_EXTERNA;
            }
            default: {
                return null;
            }
        }
    }
}
