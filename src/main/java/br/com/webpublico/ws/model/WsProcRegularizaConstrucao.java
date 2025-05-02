package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.ProcRegularizaConstrucao;

import java.io.Serializable;
import java.util.Date;

public class WsProcRegularizaConstrucao implements Serializable {

    private Integer exercicio;
    private Long codigo;
    private Date dataCriacao;
    private String numeroProtocolo;
    private String anoProtocolo;
    private Date dataInicioObra;
    private Date dataFimObra;
    private String responsavelProjeto;
    private String responsavelExecucao;
    private String situacao;
    private String inscricaoImobiliaria;
    private String enderecoImovel;
    private String proprietarioImovel;
    private WsAlvaraConstrucao alvara;

    public WsProcRegularizaConstrucao() {

    }

    public WsProcRegularizaConstrucao(ProcRegularizaConstrucao proc) {
        this.exercicio = proc.getExercicio().getAno();
        this.codigo = proc.getCodigo();
        this.dataCriacao = proc.getDataCriacao();
        this.numeroProtocolo = proc.getNumeroProtocolo();
        this.anoProtocolo = proc.getAnoProtocolo();
        this.dataInicioObra = proc.getDataInicioObra();
        this.dataFimObra = proc.getDataFimObra();
        this.responsavelProjeto = proc.getResponsavelProjeto().toString();
        this.responsavelExecucao = proc.getResponsavelExecucao().toString();
        this.situacao = proc.getSituacao().getDescricao();
        this.inscricaoImobiliaria = proc.getCadastroImobiliario().getInscricaoCadastral();
        this.enderecoImovel = proc.getCadastroImobiliario().getEnderecoCompleto();
        this.proprietarioImovel = proc.getCadastroImobiliario().getDescricaoProprietarios();
        if (proc.getUltimoAlvara() != null) {
            this.alvara = new WsAlvaraConstrucao(proc.getUltimoAlvara(), this);
        }
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public Date getDataInicioObra() {
        return dataInicioObra;
    }

    public void setDataInicioObra(Date dataInicioObra) {
        this.dataInicioObra = dataInicioObra;
    }

    public Date getDataFimObra() {
        return dataFimObra;
    }

    public void setDataFimObra(Date dataFimObra) {
        this.dataFimObra = dataFimObra;
    }

    public String getResponsavelProjeto() {
        return responsavelProjeto;
    }

    public void setResponsavelProjeto(String responsavelProjeto) {
        this.responsavelProjeto = responsavelProjeto;
    }

    public String getResponsavelExecucao() {
        return responsavelExecucao;
    }

    public void setResponsavelExecucao(String responsavelExecucao) {
        this.responsavelExecucao = responsavelExecucao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getInscricaoImobiliaria() {
        return inscricaoImobiliaria;
    }

    public void setInscricaoImobiliaria(String inscricaoImobiliaria) {
        this.inscricaoImobiliaria = inscricaoImobiliaria;
    }

    public String getEnderecoImovel() {
        return enderecoImovel;
    }

    public void setEnderecoImovel(String enderecoImovel) {
        this.enderecoImovel = enderecoImovel;
    }

    public String getProprietarioImovel() {
        return proprietarioImovel;
    }

    public void setProprietarioImovel(String proprietarioImovel) {
        this.proprietarioImovel = proprietarioImovel;
    }

    public WsAlvaraConstrucao getAlvara() {
        return alvara;
    }

    public void setAlvara(WsAlvaraConstrucao alvara) {
        this.alvara = alvara;
    }
}
