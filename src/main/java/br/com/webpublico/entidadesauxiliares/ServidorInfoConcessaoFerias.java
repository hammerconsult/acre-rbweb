package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoConcessaoFerias {

    private Integer item;
    private Date inicio;
    private Date fim;
    private String mesAno;
    private String periodoAquisitivo;
    private BigDecimal quantidadeDias;
    private BigDecimal saldoDireito;
    private Date dataCadastro;

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getMesAno() {
        return mesAno;
    }

    public void setMesAno(String mesAno) {
        this.mesAno = mesAno;
    }

    public String getPeriodoAquisitivo() {
        return periodoAquisitivo;
    }

    public void setPeriodoAquisitivo(String periodoAquisitivo) {
        this.periodoAquisitivo = periodoAquisitivo;
    }

    public BigDecimal getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(BigDecimal quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    public BigDecimal getSaldoDireito() {
        return saldoDireito;
    }

    public void setSaldoDireito(BigDecimal saldoDireito) {
        this.saldoDireito = saldoDireito;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
