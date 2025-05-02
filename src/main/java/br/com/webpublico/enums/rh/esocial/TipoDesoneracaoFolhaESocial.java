package br.com.webpublico.enums.rh.esocial;

public enum TipoDesoneracaoFolhaESocial {

    NAO_APLICAVEL("Não Aplicável", 0),
    EMPRESA_ENQUADRADA("Empresa enquadrada nos art. 7o a 9o da Lei 12.546/2011", 1);

    private String descricao;
    private Integer codigo;

    TipoDesoneracaoFolhaESocial(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
