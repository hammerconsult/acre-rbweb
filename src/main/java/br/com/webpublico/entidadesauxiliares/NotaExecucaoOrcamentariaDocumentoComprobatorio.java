package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class NotaExecucaoOrcamentariaDocumentoComprobatorio {
    private String numero;
    private String serie;
    private Date data;
    private String tipo;
    private String uf;
    private BigDecimal valor;

    public NotaExecucaoOrcamentariaDocumentoComprobatorio() {
        valor = BigDecimal.ZERO;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
