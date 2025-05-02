package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParametrosFiltroNfseDTO {

    private OperacaoRelatorio operacao;
    private String campo;
    private Integer valorInteiro;
    private BigDecimal valorDecimal;
    private String valorTexto;
    private Boolean valorLogico;
    private Date valorData;
    private String parametro;

    public OperacaoRelatorio getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoRelatorio operacao) {
        this.operacao = operacao;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public Integer getValorInteiro() {
        return valorInteiro;
    }

    public void setValorInteiro(Integer valorInteiro) {
        this.valorInteiro = valorInteiro;
    }

    public BigDecimal getValorDecimal() {
        return valorDecimal;
    }

    public void setValorDecimal(BigDecimal valorDecimal) {
        this.valorDecimal = valorDecimal;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public void setValorTexto(String valorTexto) {
        this.valorTexto = valorTexto;
    }

    public Boolean getValorLogico() {
        return valorLogico;
    }

    public void setValorLogico(Boolean valorLogico) {
        this.valorLogico = valorLogico;
    }

    public Date getValorData() {
        if (valorData != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(valorData);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }
        return valorData;
    }

    public void setValorData(Date valorData) {
        this.valorData = valorData;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public Object getValor() {
        if (valorTexto != null)
            return valorTexto;
        if (valorDecimal != null)
            return valorDecimal;
        if (valorInteiro != null)
            return valorInteiro;
        if (valorData != null)
            return valorData;
        if (valorLogico != null)
            return valorLogico;
        return null;
    }
}
