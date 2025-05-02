package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ComprasAnulacoesTransparencia {
    private Date dataEstorno;
    private String numeroEstorno;
    private String numeroEmpenho;
    private String funcional;
    private String contaCodigo;
    private String fonte;
    private String pessoa;
    private BigDecimal valorEstorno;

    public ComprasAnulacoesTransparencia() {
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getNumeroEstorno() {
        return numeroEstorno;
    }

    public void setNumeroEstorno(String numeroEstorno) {
        this.numeroEstorno = numeroEstorno;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getContaCodigo() {
        return contaCodigo;
    }

    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public BigDecimal getValorEstorno() {
        return valorEstorno;
    }

    public void setValorEstorno(BigDecimal valorEstorno) {
        this.valorEstorno = valorEstorno;
    }
}
