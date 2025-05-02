package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 15/07/14
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class DisponibilidadeDestinacaoRecursoItem {
    private String descricao;
    private BigDecimal coluna1;
    private BigDecimal coluna2;
    private BigDecimal coluna3;
    private BigDecimal coluna4;
    private BigDecimal coluna5;
    private BigDecimal coluna6;
    private BigDecimal coluna7;
    private BigDecimal coluna8;
    private Integer nivel;
    private String orgaoDescricao;
    private String orgaoCodigo;
    private String unidadeDescricao;
    private String unidadeCodigo;
    private String unidadeGestoraDescricao;
    private String unidadeGestoraCodigo;

    public DisponibilidadeDestinacaoRecursoItem() {
        coluna1 = BigDecimal.ZERO;
        coluna2 = BigDecimal.ZERO;
        coluna3 = BigDecimal.ZERO;
        coluna4 = BigDecimal.ZERO;
        coluna5 = BigDecimal.ZERO;
        coluna6 = BigDecimal.ZERO;
        coluna7 = BigDecimal.ZERO;
        coluna8 = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getColuna1() {
        return coluna1;
    }

    public void setColuna1(BigDecimal coluna1) {
        this.coluna1 = coluna1;
    }

    public BigDecimal getColuna2() {
        return coluna2;
    }

    public void setColuna2(BigDecimal coluna2) {
        this.coluna2 = coluna2;
    }

    public BigDecimal getColuna3() {
        return coluna3;
    }

    public void setColuna3(BigDecimal coluna3) {
        this.coluna3 = coluna3;
    }

    public BigDecimal getColuna4() {
        return coluna4;
    }

    public void setColuna4(BigDecimal coluna4) {
        this.coluna4 = coluna4;
    }

    public BigDecimal getColuna5() {
        return coluna5;
    }

    public void setColuna5(BigDecimal coluna5) {
        this.coluna5 = coluna5;
    }

    public BigDecimal getColuna6() {
        return coluna6;
    }

    public void setColuna6(BigDecimal coluna6) {
        this.coluna6 = coluna6;
    }

    public BigDecimal getColuna7() {
        return coluna7;
    }

    public void setColuna7(BigDecimal coluna7) {
        this.coluna7 = coluna7;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getColuna8() {
        return coluna8;
    }

    public void setColuna8(BigDecimal coluna8) {
        this.coluna8 = coluna8;
    }

    public String getUnidadeGestoraCodigo() {
        return unidadeGestoraCodigo;
    }

    public void setUnidadeGestoraCodigo(String unidadeGestoraCodigo) {
        this.unidadeGestoraCodigo = unidadeGestoraCodigo;
    }

    public String getUnidadeGestoraDescricao() {
        return unidadeGestoraDescricao;
    }

    public void setUnidadeGestoraDescricao(String unidadeGestoraDescricao) {
        this.unidadeGestoraDescricao = unidadeGestoraDescricao;
    }

    public String getUnidadeCodigo() {
        return unidadeCodigo;
    }

    public void setUnidadeCodigo(String unidadeCodigo) {
        this.unidadeCodigo = unidadeCodigo;
    }

    public String getUnidadeDescricao() {
        return unidadeDescricao;
    }

    public void setUnidadeDescricao(String unidadeDescricao) {
        this.unidadeDescricao = unidadeDescricao;
    }

    public String getOrgaoCodigo() {
        return orgaoCodigo;
    }

    public void setOrgaoCodigo(String orgaoCodigo) {
        this.orgaoCodigo = orgaoCodigo;
    }

    public String getOrgaoDescricao() {
        return orgaoDescricao;
    }

    public void setOrgaoDescricao(String orgaoDescricao) {
        this.orgaoDescricao = orgaoDescricao;
    }
}
