package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 16/05/2017.
 */
public class BensComGarantia {

    private String numeroOrdenacao;
    private String registroPatrimonial;
    private String descricaoBem;
    private String estadoBem;
    private String formaAquisicao;
    private Date dataAquisicao;
    private String descricaoGarantia;
    private Date dataVencimentoGarantia;
    private BigDecimal valorBem;

    public BensComGarantia() {
    }

    public String getNumeroOrdenacao() {
        return numeroOrdenacao;
    }

    public void setNumeroOrdenacao(String numeroOrdenacao) {
        this.numeroOrdenacao = numeroOrdenacao;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(String estadoBem) {
        this.estadoBem = estadoBem;
    }

    public String getFormaAquisicao() {
        return formaAquisicao;
    }

    public void setFormaAquisicao(String formaAquisicao) {
        this.formaAquisicao = formaAquisicao;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public String getDescricaoGarantia() {
        return descricaoGarantia;
    }

    public void setDescricaoGarantia(String descricaoGarantia) {
        this.descricaoGarantia = descricaoGarantia;
    }

    public Date getDataVencimentoGarantia() {
        return dataVencimentoGarantia;
    }

    public void setDataVencimentoGarantia(Date dataVencimentoGarantia) {
        this.dataVencimentoGarantia = dataVencimentoGarantia;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }
}