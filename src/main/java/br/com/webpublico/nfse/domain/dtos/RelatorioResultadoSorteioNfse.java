package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioResultadoSorteioNfse implements Serializable {

    private Long idSorteio;
    private Integer numeroSorteio;
    private String descricaoSorteio;
    private Date inicioSorteio;
    private Date fimSorteio;
    private Date dataDivulgacaoCupom;
    private Date dataSorteio;
    private Long idPremio;
    private String descricaoPremio;
    private Integer quantidadePremio;
    private BigDecimal valorPremio;
    private String numeroCupom;
    private String cpfTomador;
    private String nomeTomador;
    private String logradouroTomador;
    private String bairroTomador;
    private String numeroTomador;
    private String complementoTomador;
    private String cidadeTomador;
    private String ufTomador;
    private String telefoneTomador;
    private String celularTomador;
    private String emailTomador;
    private String cpfCnpjPrestador;
    private String nomeRazaoSocialPrestador;
    private Long numeroNotaFiscal;
    private Date emissaoNotaFiscal;
    private String servico;
    private BigDecimal totalServico;

    public static List<RelatorioResultadoSorteioNfse> fromObjects(List<Object[]> objects) {
        List<RelatorioResultadoSorteioNfse> toReturn = Lists.newArrayList();
        if (objects != null) {
            for (Object[] obj : objects) {
                RelatorioResultadoSorteioNfse relatorio = new RelatorioResultadoSorteioNfse();
                relatorio.setIdSorteio(((Number) obj[0]).longValue());
                relatorio.setNumeroSorteio(((Number) obj[1]).intValue());
                relatorio.setDescricaoSorteio((String) obj[2]);
                relatorio.setInicioSorteio((Date) obj[3]);
                relatorio.setFimSorteio((Date) obj[4]);
                relatorio.setDataDivulgacaoCupom((Date) obj[5]);
                relatorio.setDataSorteio((Date) obj[6]);
                relatorio.setIdPremio(((Number) obj[7]).longValue());
                relatorio.setDescricaoPremio(obj[8] != null ? (String) obj[8] : "");
                relatorio.setQuantidadePremio(((Number) obj[9]).intValue());
                relatorio.setValorPremio((BigDecimal) obj[10]);
                relatorio.setNumeroCupom((String) obj[11]);
                relatorio.setCpfTomador(obj[12] != null ? (String) obj[12] : "");
                relatorio.setNomeTomador(obj[13] != null ? (String) obj[13] : "");
                relatorio.setLogradouroTomador(obj[14] != null ? (String) obj[14] : "");
                relatorio.setBairroTomador(obj[15] != null ? (String) obj[15] : "");
                relatorio.setNumeroTomador(obj[16] != null ? (String) obj[16] : "");
                relatorio.setComplementoTomador(obj[17] != null ? (String) obj[17] : "");
                relatorio.setCidadeTomador(obj[18] != null ? (String) obj[18] : "");
                relatorio.setUfTomador(obj[19] != null ? (String) obj[19] : "");
                relatorio.setTelefoneTomador(obj[20] != null ? (String) obj[20] : "");
                relatorio.setCelularTomador(obj[21] != null ? (String) obj[21] : "");
                relatorio.setEmailTomador(obj[22] != null ? (String) obj[22] : "");
                relatorio.setCpfCnpjPrestador(obj[23] != null ? (String) obj[23] : "");
                relatorio.setNomeRazaoSocialPrestador(obj[24] != null ? (String) obj[24] : "");
                relatorio.setNumeroNotaFiscal(((Number) obj[25]).longValue());
                relatorio.setEmissaoNotaFiscal((Date) obj[26]);
                relatorio.setServico((String) obj[27]);
                relatorio.setTotalServico((BigDecimal) obj[28]);
                toReturn.add(relatorio);
            }
        }
        return toReturn;
    }

    public Long getIdSorteio() {
        return idSorteio;
    }

    public void setIdSorteio(Long idSorteio) {
        this.idSorteio = idSorteio;
    }

    public Integer getNumeroSorteio() {
        return numeroSorteio;
    }

    public void setNumeroSorteio(Integer numeroSorteio) {
        this.numeroSorteio = numeroSorteio;
    }

    public String getDescricaoSorteio() {
        return descricaoSorteio;
    }

    public void setDescricaoSorteio(String descricaoSorteio) {
        this.descricaoSorteio = descricaoSorteio;
    }

    public Date getInicioSorteio() {
        return inicioSorteio;
    }

    public void setInicioSorteio(Date inicioSorteio) {
        this.inicioSorteio = inicioSorteio;
    }

    public Date getFimSorteio() {
        return fimSorteio;
    }

    public void setFimSorteio(Date fimSorteio) {
        this.fimSorteio = fimSorteio;
    }

    public Date getDataDivulgacaoCupom() {
        return dataDivulgacaoCupom;
    }

    public void setDataDivulgacaoCupom(Date dataDivulgacaoCupom) {
        this.dataDivulgacaoCupom = dataDivulgacaoCupom;
    }

    public Date getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(Date dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public Long getIdPremio() {
        return idPremio;
    }

    public void setIdPremio(Long idPremio) {
        this.idPremio = idPremio;
    }

    public String getDescricaoPremio() {
        return descricaoPremio;
    }

    public void setDescricaoPremio(String descricaoPremio) {
        this.descricaoPremio = descricaoPremio;
    }

    public Integer getQuantidadePremio() {
        return quantidadePremio;
    }

    public void setQuantidadePremio(Integer quantidadePremio) {
        this.quantidadePremio = quantidadePremio;
    }

    public BigDecimal getValorPremio() {
        return valorPremio;
    }

    public void setValorPremio(BigDecimal valorPremio) {
        this.valorPremio = valorPremio;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public String getCpfTomador() {
        return cpfTomador;
    }

    public void setCpfTomador(String cpfTomador) {
        this.cpfTomador = cpfTomador;
    }

    public String getNomeTomador() {
        return nomeTomador;
    }

    public void setNomeTomador(String nomeTomador) {
        this.nomeTomador = nomeTomador;
    }

    public String getLogradouroTomador() {
        return logradouroTomador;
    }

    public void setLogradouroTomador(String logradouroTomador) {
        this.logradouroTomador = logradouroTomador;
    }

    public String getBairroTomador() {
        return bairroTomador;
    }

    public void setBairroTomador(String bairroTomador) {
        this.bairroTomador = bairroTomador;
    }

    public String getNumeroTomador() {
        return numeroTomador;
    }

    public void setNumeroTomador(String numeroTomador) {
        this.numeroTomador = numeroTomador;
    }

    public String getComplementoTomador() {
        return complementoTomador;
    }

    public void setComplementoTomador(String complementoTomador) {
        this.complementoTomador = complementoTomador;
    }

    public String getCidadeTomador() {
        return cidadeTomador;
    }

    public void setCidadeTomador(String cidadeTomador) {
        this.cidadeTomador = cidadeTomador;
    }

    public String getUfTomador() {
        return ufTomador;
    }

    public void setUfTomador(String ufTomador) {
        this.ufTomador = ufTomador;
    }

    public String getTelefoneTomador() {
        return telefoneTomador;
    }

    public void setTelefoneTomador(String telefoneTomador) {
        this.telefoneTomador = telefoneTomador;
    }

    public String getCelularTomador() {
        return celularTomador;
    }

    public void setCelularTomador(String celularTomador) {
        this.celularTomador = celularTomador;
    }

    public String getEmailTomador() {
        return emailTomador;
    }

    public void setEmailTomador(String emailTomador) {
        this.emailTomador = emailTomador;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public String getNomeRazaoSocialPrestador() {
        return nomeRazaoSocialPrestador;
    }

    public void setNomeRazaoSocialPrestador(String nomeRazaoSocialPrestador) {
        this.nomeRazaoSocialPrestador = nomeRazaoSocialPrestador;
    }

    public Long getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(Long numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Date getEmissaoNotaFiscal() {
        return emissaoNotaFiscal;
    }

    public void setEmissaoNotaFiscal(Date emissaoNotaFiscal) {
        this.emissaoNotaFiscal = emissaoNotaFiscal;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getTotalServico() {
        return totalServico;
    }

    public void setTotalServico(BigDecimal totalServico) {
        this.totalServico = totalServico;
    }
}
