package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoIssqn;

import java.io.Serializable;
import java.util.Date;

public class FiltroMensagemContribuinte implements Serializable {

    private TipoIssqn tipoIssqn;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Boolean enviaRPS;
    private UsuarioSistema enviadaPor;
    private Date dataEnvioInicial;
    private Date dataEnvioFinal;

    public FiltroMensagemContribuinte() {
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Boolean getEnviaRPS() {
        return enviaRPS;
    }

    public void setEnviaRPS(Boolean enviaRPS) {
        this.enviaRPS = enviaRPS;
    }

    public UsuarioSistema getEnviadaPor() {
        return enviadaPor;
    }

    public void setEnviadaPor(UsuarioSistema enviadaPor) {
        this.enviadaPor = enviadaPor;
    }

    public Date getDataEnvioInicial() {
        return dataEnvioInicial;
    }

    public void setDataEnvioInicial(Date dataEnvioInicial) {
        this.dataEnvioInicial = dataEnvioInicial;
    }

    public Date getDataEnvioFinal() {
        return dataEnvioFinal;
    }

    public void setDataEnvioFinal(Date dataEnvioFinal) {
        this.dataEnvioFinal = dataEnvioFinal;
    }
}
