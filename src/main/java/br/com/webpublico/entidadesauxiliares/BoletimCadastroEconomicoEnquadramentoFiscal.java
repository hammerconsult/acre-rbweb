package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoEnquadramentoFiscal implements Serializable {
    private String porte;
    private String tipoDeContribuinte;
    private String regimeTributario;
    private String regimeEspecialTributacao;
    private String tipoIssqn;
    private String substitutoTributario;
    private String tipoNotaFiscalServico;
    private String escritorioContabil;

    public BoletimCadastroEconomicoEnquadramentoFiscal() {
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getTipoDeContribuinte() {
        return tipoDeContribuinte;
    }

    public void setTipoDeContribuinte(String tipoDeContribuinte) {
        this.tipoDeContribuinte = tipoDeContribuinte;
    }

    public String getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(String regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public String getRegimeEspecialTributacao() {
        return regimeEspecialTributacao;
    }

    public void setRegimeEspecialTributacao(String regimeEspecialTributacao) {
        this.regimeEspecialTributacao = regimeEspecialTributacao;
    }

    public String getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(String tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public String getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(String substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public String getTipoNotaFiscalServico() {
        return tipoNotaFiscalServico;
    }

    public void setTipoNotaFiscalServico(String tipoNotaFiscalServico) {
        this.tipoNotaFiscalServico = tipoNotaFiscalServico;
    }

    public String getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(String escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }
}
