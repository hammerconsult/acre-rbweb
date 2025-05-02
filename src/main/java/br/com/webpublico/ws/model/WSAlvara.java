package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.Alvara;

import java.io.Serializable;
import java.util.Date;

public class WSAlvara implements Serializable {

    private Integer ano;
    private Date inicio;
    private Date fim;
    private Boolean provisorio;
    private Long id;
    private String tipo;
    private String cmc;
    private String assinaturaDigital;
    private Date emissao;
    private Date vencimento;
    private String situacaoParcela;
    private Boolean podeEmitirAlvara;

    public WSAlvara() {
    }

    public WSAlvara(Alvara alvara, String situacaoParcela, Boolean podeEmitirAlvara) {
        this.ano = alvara.getExercicio().getAno();
        this.inicio = alvara.getInicioVigencia();
        this.fim = alvara.getFinalVigencia();
        this.id = alvara.getId();
        this.tipo = alvara.getTipoAlvara().getDescricao();
        this.cmc = alvara.getCadastroEconomico().getInscricaoCadastral();
        this.assinaturaDigital = alvara.getAssinaturadigital();
        this.emissao = alvara.getEmissao();
        this.vencimento = alvara.getVencimento();
        this.situacaoParcela = situacaoParcela;
        this.podeEmitirAlvara = podeEmitirAlvara;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
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

    public Boolean getProvisorio() {
        return provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }

    public String getAssinaturaDigital() {
        return assinaturaDigital;
    }

    public void setAssinaturaDigital(String assinaturaDigital) {
        this.assinaturaDigital = assinaturaDigital;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(String situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public Boolean getPodeEmitirAlvara() {
        return podeEmitirAlvara;
    }

    public void setPodeEmitirAlvara(Boolean podeEmitirAlvara) {
        this.podeEmitirAlvara = podeEmitirAlvara;
    }
}
