package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BoletimCadastroEconomicoCadastro implements Serializable {
    private String situacaoCadastral;
    private String regimeTributario;
    private String porte;
    private String mei;
    private String substitutoTributario;
    private Date dataAbertura;
    private Date dataAberturaCnpj;
    private Date dataCadastro;
    private BigDecimal areaUtilizacao;

    public BoletimCadastroEconomicoCadastro() {
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(String situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public String getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(String regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getMei() {
        return mei;
    }

    public void setMei(String mei) {
        this.mei = mei;
    }

    public String getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(String substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Date getDataAberturaCnpj() {
        return dataAberturaCnpj;
    }

    public void setDataAberturaCnpj(Date dataAberturaCnpj) {
        this.dataAberturaCnpj = dataAberturaCnpj;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public BigDecimal getAreaUtilizacao() {
        return areaUtilizacao;
    }

    public void setAreaUtilizacao(BigDecimal areaUtilizacao) {
        this.areaUtilizacao = areaUtilizacao;
    }
}
