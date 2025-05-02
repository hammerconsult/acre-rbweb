package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.AnexoLei1232006FaixaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Audited
public class AnexoLei1232006Faixa extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Anexo Lei Complementar 123/2006")
    @ManyToOne
    private AnexoLei1232006 anexoLei1232006;

    @Obrigatorio
    @Etiqueta("Faixa")
    private String faixa;

    @Obrigatorio
    @Etiqueta("Até (R$)")
    private BigDecimal valorMaximo;

    @Obrigatorio
    @Etiqueta("Aliquota (%)")
    private BigDecimal aliquota;

    @Obrigatorio
    @Etiqueta("Valor a Deduzir (R$)")
    private BigDecimal valorDeduzir;

    @Etiqueta("IRPJ (%)")
    private BigDecimal irpj;

    @Etiqueta("CSLL (%)")
    private BigDecimal csll;

    @Etiqueta("Cofins (%)")
    private BigDecimal cofins;

    @Etiqueta("PIS/Pasep (%)")
    private BigDecimal pisPasep;

    @Etiqueta("CPP (%)")
    private BigDecimal cpp;

    @Etiqueta("ICMS (%)")
    private BigDecimal icms;

    @Etiqueta("IPI (%)")
    private BigDecimal ipi;

    @Etiqueta("ISS (%)")
    private BigDecimal iss;

    public AnexoLei1232006Faixa() {
        super();
        valorMaximo = BigDecimal.ZERO;
        aliquota = BigDecimal.ZERO;
        valorDeduzir = BigDecimal.ZERO;
        irpj = BigDecimal.ZERO;
        csll = BigDecimal.ZERO;
        cofins = BigDecimal.ZERO;
        pisPasep = BigDecimal.ZERO;
        cpp = BigDecimal.ZERO;
        icms = BigDecimal.ZERO;
        ipi = BigDecimal.ZERO;
        iss = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnexoLei1232006 getAnexoLei1232006() {
        return anexoLei1232006;
    }

    public void setAnexoLei1232006(AnexoLei1232006 anexoLei1232006) {
        this.anexoLei1232006 = anexoLei1232006;
    }

    public String getFaixa() {
        return faixa;
    }

    public void setFaixa(String faixa) {
        this.faixa = faixa;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorDeduzir() {
        return valorDeduzir;
    }

    public void setValorDeduzir(BigDecimal valorDeduzir) {
        this.valorDeduzir = valorDeduzir;
    }

    public BigDecimal getIrpj() {
        return irpj;
    }

    public void setIrpj(BigDecimal irpj) {
        this.irpj = irpj;
    }

    public BigDecimal getCsll() {
        return csll;
    }

    public void setCsll(BigDecimal csll) {
        this.csll = csll;
    }

    public BigDecimal getCofins() {
        return cofins;
    }

    public void setCofins(BigDecimal cofins) {
        this.cofins = cofins;
    }

    public BigDecimal getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(BigDecimal pisPasep) {
        this.pisPasep = pisPasep;
    }

    public BigDecimal getCpp() {
        return cpp;
    }

    public void setCpp(BigDecimal cpp) {
        this.cpp = cpp;
    }

    public BigDecimal getIcms() {
        return icms;
    }

    public void setIcms(BigDecimal icms) {
        this.icms = icms;
    }

    public BigDecimal getIpi() {
        return ipi;
    }

    public void setIpi(BigDecimal ipi) {
        this.ipi = ipi;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    @Override
    public NfseDTO toNfseDto() {
        return new AnexoLei1232006FaixaNfseDTO(id, faixa, valorMaximo,
            aliquota, valorDeduzir, irpj, csll, cofins, pisPasep, cpp, icms, ipi, iss);
    }
}
