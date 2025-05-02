package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class LinhaCompSimplesNacionalNfseDTO implements Comparable<LinhaCompSimplesNacionalNfseDTO> {

    private String cnpj;
    private Date competencia;
    private BigDecimal baseCalculo;

    public LinhaCompSimplesNacionalNfseDTO() {
    }

    public LinhaCompSimplesNacionalNfseDTO(String cnpj, Date competencia, BigDecimal baseCalculo) {
        this.cnpj = cnpj;
        this.competencia = competencia;
        this.baseCalculo = baseCalculo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    @Override
    public int compareTo(LinhaCompSimplesNacionalNfseDTO o) {
        int compare = cnpj.compareTo(o.getCnpj());
        if (compare == 0) {
            compare = competencia.compareTo(o.getCompetencia());
        }
        return compare;
    }
}
