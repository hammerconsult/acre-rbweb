package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AnexoLei1232006NfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private String descricao;
    private Date vigenteAte;
    private BigDecimal irpjExcedente;
    private BigDecimal csllExcedente;
    private BigDecimal cofinsExcedente;
    private BigDecimal pisPasepExcedente;
    private BigDecimal cppExcedente;
    private BigDecimal icmsExcedente;
    private BigDecimal ipiExcedente;
    private List<br.com.webpublico.nfse.domain.dtos.AnexoLei1232006FaixaNfseDTO> faixas;

    public AnexoLei1232006NfseDTO() {
    }

    public AnexoLei1232006NfseDTO(Long id, String descricao, Date vigenteAte, BigDecimal irpjExcedente, BigDecimal csllExcedente, BigDecimal cofinsExcedente, BigDecimal pisPasepExcedente, BigDecimal cppExcedente, BigDecimal icmsExcedente, BigDecimal ipiExcedente) {
        this.id = id;
        this.descricao = descricao;
        this.vigenteAte = vigenteAte;
        this.irpjExcedente = irpjExcedente;
        this.csllExcedente = csllExcedente;
        this.cofinsExcedente = cofinsExcedente;
        this.pisPasepExcedente = pisPasepExcedente;
        this.cppExcedente = cppExcedente;
        this.icmsExcedente = icmsExcedente;
        this.ipiExcedente = ipiExcedente;
    }

    public BigDecimal getIrpjExcedente() {
        return irpjExcedente;
    }

    public void setIrpjExcedente(BigDecimal irpjExcedente) {
        this.irpjExcedente = irpjExcedente;
    }

    public BigDecimal getCsllExcedente() {
        return csllExcedente;
    }

    public void setCsllExcedente(BigDecimal csllExcedente) {
        this.csllExcedente = csllExcedente;
    }

    public BigDecimal getCofinsExcedente() {
        return cofinsExcedente;
    }

    public void setCofinsExcedente(BigDecimal cofinsExcedente) {
        this.cofinsExcedente = cofinsExcedente;
    }

    public BigDecimal getPisPasepExcedente() {
        return pisPasepExcedente;
    }

    public void setPisPasepExcedente(BigDecimal pisPasepExcedente) {
        this.pisPasepExcedente = pisPasepExcedente;
    }

    public BigDecimal getCppExcedente() {
        return cppExcedente;
    }

    public void setCppExcedente(BigDecimal cppExcedente) {
        this.cppExcedente = cppExcedente;
    }

    public BigDecimal getIcmsExcedente() {
        return icmsExcedente;
    }

    public void setIcmsExcedente(BigDecimal icmsExcedente) {
        this.icmsExcedente = icmsExcedente;
    }

    public BigDecimal getIpiExcedente() {
        return ipiExcedente;
    }

    public void setIpiExcedente(BigDecimal ipiExcedente) {
        this.ipiExcedente = ipiExcedente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getVigenteAte() {
        return vigenteAte;
    }

    public void setVigenteAte(Date vigenteAte) {
        this.vigenteAte = vigenteAte;
    }

    public List<br.com.webpublico.nfse.domain.dtos.AnexoLei1232006FaixaNfseDTO> getFaixas() {
        return faixas;
    }

    public void setFaixas(List<br.com.webpublico.nfse.domain.dtos.AnexoLei1232006FaixaNfseDTO> faixas) {
        this.faixas = faixas;
    }
}
