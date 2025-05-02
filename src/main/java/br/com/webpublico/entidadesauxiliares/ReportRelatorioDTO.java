package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


public class ReportRelatorioDTO implements Serializable{


    private String nome;
    private String usuario;
    private LocalDateTime solicitadoEm;
    private String uuid;
    private BigDecimal porcentagem;
    private String conteudo;
    private String mimeType;
    private Long inicio;
    private Long fim;
    private String situacao;
    private Long threadId;
    private String containerName;
    private String status;
    private String observacao;

    private String tempoAsString;

    public ReportRelatorioDTO() {
        porcentagem = BigDecimal.ZERO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getSolicitadoEm() {
        return solicitadoEm;
    }

    public void setSolicitadoEm(LocalDateTime solicitadoEm) {
        this.solicitadoEm = solicitadoEm;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getInicio() {
        return inicio;
    }

    public Date getDataInicio() {
        return getInicio() != null ? new Date(getInicio()) : null;
    }

    public void setInicio(Long inicio) {
        this.inicio = inicio;
    }

    public Long getFim() {
        return fim;
    }

    public Date getDataFim() {
        return getFim() != null ? new Date(getFim()) : null;
    }

    public void setFim(Long fim) {
        this.fim = fim;
    }

    public String getTempoAsString() {
        return tempoAsString;
    }

    public void setTempoAsString(String tempoAsString) {
        this.tempoAsString = tempoAsString;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
