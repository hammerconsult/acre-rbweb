package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Tributo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class VOValoresPagosIptuBI implements Serializable {
    private Long idItemDam;
    private Integer ano;
    private Integer mes;
    private String matricula;
    private String cotaUnica;
    private String dataPagamento;
    private String codigoTipoIptu;
    private String dividaAtiva;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;

    private BigDecimal jurosImposto;
    private BigDecimal jurosTaxa;
    private BigDecimal multaImposto;
    private BigDecimal multaTaxa;
    private BigDecimal correcaoImposto;
    private BigDecimal correcaoTaxa;

    private Tributo.TipoTributo tipoTributo;

    public VOValoresPagosIptuBI(Object[] obj) {
        this.idItemDam = obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null;
        this.matricula = (String) obj[1];
        this.ano = obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null;
        this.mes = obj[3] != null ? ((BigDecimal) obj[3]).intValue() : null;
        this.cotaUnica = String.valueOf(obj[4]);
        this.dataPagamento = (String) obj[5];
        this.codigoTipoIptu = String.valueOf(obj[6]);
        this.dividaAtiva = (String) obj[7];
        this.imposto = (BigDecimal) obj[8];
        this.taxa = (BigDecimal) obj[9];
        this.juros = (BigDecimal) obj[10];
        this.multa = (BigDecimal) obj[11];
        this.correcao = (BigDecimal) obj[12];
        this.tipoTributo = Tributo.TipoTributo.IMPOSTO;
    }

    public VOValoresPagosIptuBI(VOValoresPagosIptuBI valorPago) {
        this.idItemDam = valorPago.getIdItemDam();
        this.matricula = valorPago.getMatricula();
        this.ano = valorPago.getAno();
        this.mes = valorPago.getMes();
        this.cotaUnica = valorPago.getCotaUnica();
        this.dataPagamento = valorPago.getDataPagamento();
        this.codigoTipoIptu = valorPago.getCodigoTipoIptu();
        this.dividaAtiva = valorPago.getDividaAtiva();
        this.imposto = valorPago.getImposto();
        this.taxa = valorPago.getTaxa();
        this.juros = valorPago.getJuros();
        this.multa = valorPago.getMulta();
        this.correcao = valorPago.getCorrecao();

        this.jurosImposto = valorPago.getJurosImposto();
        this.jurosTaxa = valorPago.getJurosTaxa();
        this.multaImposto = valorPago.getMultaImposto();
        this.multaTaxa = valorPago.getMultaTaxa();
        this.correcaoImposto = valorPago.getCorrecaoImposto();
        this.correcaoTaxa = valorPago.getCorrecaoTaxa();
    }

    public Long getIdItemDam() {
        return idItemDam;
    }

    public void setIdItemDam(Long idItemDam) {
        this.idItemDam = idItemDam;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCotaUnica() {
        return cotaUnica;
    }

    public void setCotaUnica(String cotaUnica) {
        this.cotaUnica = cotaUnica;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getCodigoTipoIptu() {
        return codigoTipoIptu;
    }

    public void setCodigoTipoIptu(String codigoTipoIptu) {
        this.codigoTipoIptu = codigoTipoIptu;
    }

    public String getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(String dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao != null ? correcao.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getJurosImposto() {
        return jurosImposto != null ? jurosImposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setJurosImposto(BigDecimal jurosImposto) {
        this.jurosImposto = jurosImposto;
    }

    public BigDecimal getJurosTaxa() {
        return jurosTaxa != null ? jurosTaxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setJurosTaxa(BigDecimal jurosTaxa) {
        this.jurosTaxa = jurosTaxa;
    }

    public BigDecimal getMultaImposto() {
        return multaImposto != null ? multaImposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setMultaImposto(BigDecimal multaImposto) {
        this.multaImposto = multaImposto;
    }

    public BigDecimal getMultaTaxa() {
        return multaTaxa != null ? multaTaxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setMultaTaxa(BigDecimal multaTaxa) {
        this.multaTaxa = multaTaxa;
    }

    public BigDecimal getCorrecaoImposto() {
        return correcaoImposto != null ? correcaoImposto.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setCorrecaoImposto(BigDecimal correcaoImposto) {
        this.correcaoImposto = correcaoImposto;
    }

    public BigDecimal getCorrecaoTaxa() {
        return correcaoTaxa != null ? correcaoTaxa.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setCorrecaoTaxa(BigDecimal correcaoTaxa) {
        this.correcaoTaxa = correcaoTaxa;
    }

    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public boolean isDividaAtivida() {
        return "1".equals(dividaAtiva);
    }

    public BigDecimal getValorPagoPorTipoTributo() {
        if (tipoTributo != null) {
            return tipoTributo.isImposto() ? getImposto() : getTaxa();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getJurosPorTipoTributo() {
        if (tipoTributo != null) {
            if (getImposto().compareTo(BigDecimal.ZERO) > 0 && getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                return tipoTributo.isImposto() ? getJurosImposto() : getJurosTaxa();
            }
            return getJuros();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getMultaPorTipoTributo() {
        if (tipoTributo != null) {
            if (getImposto().compareTo(BigDecimal.ZERO) > 0 && getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                return tipoTributo.isImposto() ? getMultaImposto() : getMultaTaxa();
            }
            return getMulta();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getCorrecaoPorTipoTributo() {
        if (tipoTributo != null) {
            if (getImposto().compareTo(BigDecimal.ZERO) > 0 && getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                return tipoTributo.isImposto() ? getCorrecaoImposto() : getCorrecaoTaxa();
            }
            return getCorrecao();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotalPagoPorTipoTributo() {
        return getValorPagoPorTipoTributo().add(getJurosPorTipoTributo()).add(getMultaPorTipoTributo()).add(getCorrecaoPorTipoTributo());
    }
}
