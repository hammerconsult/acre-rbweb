package br.com.webpublico.enums.rh;

/**
 * Created by William on 23/10/2018.
 */
public enum TipoCumprimentoAvisoPrevio {
    CUMPRIMENTO_TOTAL(0, "Cumprimento total"),
    CUMPRIMENTO_PARCIAL_OUTRO_EMPREGO(1, "Cumprimento parcial em razão de obtenção de novo emprego pelo empregado"),
    CUMPRIMENTO_PARCIAL_INICIATIVA_EMPREGADOR(2, "Cumprimento parcial por iniciativa do empregador"),
    OUTRAS_HIPOTESES_CUMPRIMENTO_PARCIAL(3, "Outras hipóteses de cumprimento parcial do aviso prévio"),
    AVISO_PREVIO_INDENIZADO_OU_NAO_EXIGIVEL(4, "Aviso prévio indenizado ou não exigível");

    private Integer codigo;
    private String descricao;

    TipoCumprimentoAvisoPrevio(Integer codigo, String descricao) {
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
        return codigo + " - " + descricao;
    }
}
