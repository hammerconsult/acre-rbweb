package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;

public class RelatorioSolicitacaoEmissaoNotaDTO {

    private String cadastroEconomico;
    private String solicitante;
    private Date solicitadaEm;
    private Integer quantidadeSolicitada;
    private Integer quantidadeDeferida;
    private Date deferidaEm;
    private String situacao;
    private String deferidoPor;

    public RelatorioSolicitacaoEmissaoNotaDTO() {
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Date getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(Date solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }

    public Integer getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public Integer getQuantidadeDeferida() {
        return quantidadeDeferida;
    }

    public void setQuantidadeDeferida(Integer quantidadeDeferida) {
        this.quantidadeDeferida = quantidadeDeferida;
    }

    public Date getDeferidaEm() {
        return deferidaEm;
    }

    public void setDeferidaEm(Date deferidaEm) {
        this.deferidaEm = deferidaEm;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getDeferidoPor() {
        return deferidoPor;
    }

    public void setDeferidoPor(String deferidoPor) {
        this.deferidoPor = deferidoPor;
    }
}
