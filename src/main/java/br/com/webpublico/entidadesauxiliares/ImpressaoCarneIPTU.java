package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by FABIO on 16/02/2016.
 */
public class ImpressaoCarneIPTU {

    private Long idCalculo;
    private Long idParcela;
    private Long idDam;
    private String inscricaoCadastral;
    private String numeroDAM;
    private String qrCodePix;
    private BigDecimal valor;
    private String codigoDeBarras;
    private Date vencimento;
    private BigDecimal fracaoIdeal;
    private BigDecimal areaConstruida;
    private BigDecimal areaExcedente;
    private BigDecimal vlrVenalTerreno;
    private BigDecimal vlrVenalEdificacao;
    private BigDecimal vlrVenalExcedente;
    private BigDecimal aliquota;
    private String sequenciaParcela;
    private String barras;
    private BigDecimal valorComDesconto;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal desconto;
    private BigDecimal totalTaxa;
    private BigDecimal totalImposto;
    private BigDecimal valorCalculo;
    private BigDecimal acrescimos;
    private Long ano;
    private String contribuinte;
    private String cpfCnpj;

    private String qualidade;
    private String utilizacao;
    private String tipoConstrucao;
    private String coletaLixo;
    private String iluminacaoPublica;
    private String topografia;
    private String pedologia;
    private String situacaoLote;
    private String situacaoDam;
    private String tipoDam;
    private String patrimonio;

    public Long getIdCalculo() {
        return idCalculo;
    }

    public void setIdCalculo(Long idCalculo) {
        this.idCalculo = idCalculo;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getNumeroDAM() {
        return numeroDAM;
    }

    public void setNumeroDAM(String numeroDAM) {
        this.numeroDAM = numeroDAM;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getFracaoIdeal() {
        return fracaoIdeal;
    }

    public void setFracaoIdeal(BigDecimal fracaoIdeal) {
        this.fracaoIdeal = fracaoIdeal;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getAreaExcedente() {
        return areaExcedente;
    }

    public void setAreaExcedente(BigDecimal areaExcedente) {
        this.areaExcedente = areaExcedente;
    }

    public BigDecimal getVlrVenalTerreno() {
        return vlrVenalTerreno;
    }

    public void setVlrVenalTerreno(BigDecimal vlrVenalTerreno) {
        this.vlrVenalTerreno = vlrVenalTerreno;
    }

    public BigDecimal getVlrVenalEdificacao() {
        return vlrVenalEdificacao;
    }

    public void setVlrVenalEdificacao(BigDecimal vlrVenalEdificacao) {
        this.vlrVenalEdificacao = vlrVenalEdificacao;
    }

    public BigDecimal getVlrVenalExcedente() {
        return vlrVenalExcedente;
    }

    public void setVlrVenalExcedente(BigDecimal vlrVenalExcedente) {
        this.vlrVenalExcedente = vlrVenalExcedente;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public String getBarras() {
        return barras;
    }

    public void setBarras(String barras) {
        this.barras = barras;
    }

    public BigDecimal getValorComDesconto() {
        return valorComDesconto;
    }

    public void setValorComDesconto(BigDecimal valorComDesconto) {
        this.valorComDesconto = valorComDesconto;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getTotalTaxa() {
        return totalTaxa;
    }

    public void setTotalTaxa(BigDecimal totalTaxa) {
        this.totalTaxa = totalTaxa;
    }

    public BigDecimal getTotalImposto() {
        return totalImposto;
    }

    public void setTotalImposto(BigDecimal totalImposto) {
        this.totalImposto = totalImposto;
    }

    public Long getAno() {
        return ano;
    }

    public void setAno(Long ano) {
        this.ano = ano;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public BigDecimal getValorCalculo() {
        return valorCalculo;
    }

    public void setValorCalculo(BigDecimal valorCalculo) {
        this.valorCalculo = valorCalculo;
    }

    public BigDecimal getAcrescimos() {
        return acrescimos;
    }

    public void setAcrescimos(BigDecimal acrescimos) {
        this.acrescimos = acrescimos;
    }

    public String getQualidade() {
        return qualidade;
    }

    public void setQualidade(String qualidade) {
        this.qualidade = qualidade;
    }

    public String getUtilizacao() {
        return utilizacao;
    }

    public void setUtilizacao(String utilizacao) {
        this.utilizacao = utilizacao;
    }

    public String getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(String tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    public String getColetaLixo() {
        return coletaLixo;
    }

    public void setColetaLixo(String coletaLixo) {
        this.coletaLixo = coletaLixo;
    }

    public String getIluminacaoPublica() {
        return iluminacaoPublica;
    }

    public void setIluminacaoPublica(String iluminacaoPublica) {
        this.iluminacaoPublica = iluminacaoPublica;
    }

    public String getTopografia() {
        return topografia;
    }

    public void setTopografia(String topografia) {
        this.topografia = topografia;
    }

    public String getPedologia() {
        return pedologia;
    }

    public void setPedologia(String pedologia) {
        this.pedologia = pedologia;
    }

    public String getSituacaoLote() {
        return situacaoLote;
    }

    public void setSituacaoLote(String situacaoLote) {
        this.situacaoLote = situacaoLote;
    }

    public String getSituacaoDam() {
        return situacaoDam != null ? situacaoDam : "";
    }

    public void setSituacaoDam(String situacaoDam) {
        this.situacaoDam = situacaoDam;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getTipoDam() {
        return tipoDam;
    }

    public void setTipoDam(String tipoDam) {
        this.tipoDam = tipoDam;
    }
}
