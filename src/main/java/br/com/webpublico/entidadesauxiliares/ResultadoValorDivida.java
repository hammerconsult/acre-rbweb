package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 19/09/13
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class ResultadoValorDivida {
    private Long id;
    private Long dividaId;
    private String dividaDescricao;
    private Integer exercicioAno;
    private Long subDivida;
    private String tipoCadastro;
    private String tipoCalculo;
    private String inscricao;
    private BigDecimal valor;
    private Date emissao;
    private Long calculoId;


    public ResultadoValorDivida() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDividaId() {
        return dividaId;
    }

    public void setDividaId(Long dividaId) {
        this.dividaId = dividaId;
    }

    public String getDividaDescricao() {
        return dividaDescricao;
    }

    public void setDividaDescricao(String dividaDescricao) {
        this.dividaDescricao = dividaDescricao;
    }

    public Integer getExercicioAno() {
        return exercicioAno;
    }

    public void setExercicioAno(Integer exercicioAno) {
        this.exercicioAno = exercicioAno;
    }

    public Long getSubDivida() {
        return subDivida;
    }

    public void setSubDivida(Long subDivida) {
        this.subDivida = subDivida;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Long getCalculoId() {
        return calculoId;
    }

    public void setCalculoId(Long calculoId) {
        this.calculoId = calculoId;
    }

    public String getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(String tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }
}
