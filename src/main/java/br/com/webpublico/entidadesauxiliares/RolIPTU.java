package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RolIPTU implements Serializable, Comparable<RolIPTU> {

    private BigDecimal id;
    private String inscricao;
    private String unidade;
    private String proprietario;
    private String lograoduro;
    private String bairro;
    private String numeroEndereco;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal desconto;
    private BigDecimal lancado;
    private Boolean isentoImposto;
    private Boolean isentoTaxa;
    private BigDecimal idValorDivida;
    private Long idCadastro;

    public RolIPTU() {
    }

    public RolIPTU(Object[] obj) {
        this.id = (BigDecimal) obj[0];
        this.idValorDivida = (BigDecimal) obj[1];
        this.idCadastro = ((BigDecimal) obj[2]).longValue();
        this.inscricao = (String) obj[3];
        this.unidade = (String) obj[4];
        this.proprietario = (String) obj[5];
        this.lograoduro = (String) obj[6];
        this.bairro = (String) obj[7];
        this.numeroEndereco = (String) obj[8];
        this.imposto = (BigDecimal) obj[9];
        this.taxa = (BigDecimal) obj[10];
        this.desconto = (BigDecimal) obj[11];
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getLograoduro() {
        return lograoduro;
    }

    public void setLograoduro(String lograoduro) {
        this.lograoduro = lograoduro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        if (imposto == null) {
            imposto = BigDecimal.ZERO;
        }
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        if (taxa == null) {
            taxa = BigDecimal.ZERO;
        }
        this.taxa = taxa;
    }

    public BigDecimal getDesconto() {
        return desconto != null && desconto.compareTo(BigDecimal.ZERO) >= 0 ? desconto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        if (desconto == null) {
            desconto = BigDecimal.ZERO;
        }
        this.desconto = desconto;
    }

    public BigDecimal getLancado() {
        return lancado;
    }

    public void setLancado(BigDecimal lancado) {
        this.lancado = lancado;
    }

    public Boolean getIsentoImposto() {
        return isentoImposto;
    }

    public void setIsentoImposto(Boolean isentoImposto) {
        this.isentoImposto = isentoImposto;
    }

    public Boolean getIsentoTaxa() {
        return isentoTaxa;
    }

    public void setIsentoTaxa(Boolean isentoTaxa) {
        this.isentoTaxa = isentoTaxa;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public BigDecimal getIdValorDivida() {
        return idValorDivida;
    }

    public void setIdValorDivida(BigDecimal idValorDivida) {
        this.idValorDivida = idValorDivida;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    @Override
    public int compareTo(RolIPTU o) {
        Long idLocal = this.getIdValorDivida() != null ? this.getIdValorDivida().longValue() : Long.MAX_VALUE;
        Long idExterno = o.getIdValorDivida() != null ? o.getIdValorDivida().longValue() : Long.MAX_VALUE;
        int compare = idLocal.compareTo(idExterno);
        if (compare == 0) {
            compare = this.getInscricao().compareTo(o.getInscricao());
        }
        return compare;
    }
}
