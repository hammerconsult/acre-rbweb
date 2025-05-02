package br.com.webpublico.enums.rh;

/**
 * Created by William on 06/11/2018.
 */
public enum TipoReintegracao {
    REINTEGRACAO_DECISAO_JUDICIAL(1, "Reintegração por Decisão Judicial"),
    REINTEGRACAO_ANISTIA_LEGAL(2, "Reintegração por Anistia Legal"),
    REVERSAO_SERVIDOR_PUBLICO(3, "Reversão de Servidor Públic"),
    RECONDUCAO_SERVIDOR_PUBLICO(4, "Recondução de Servidor Público"),
    REINCLUSAO_MILITAR(5, "Reinclusão de Militar"),
    OUTROS(9, "Outros");

    private Integer codigo;
    private String descricao;

    TipoReintegracao(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
