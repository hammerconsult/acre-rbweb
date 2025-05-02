package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.entidades.Tramite;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;

import java.util.Date;

public class FiltroListaProtocolo {
    private Date dataOperacao;
    private String numero;
    private String ano;
    private String solicitante;
    private Boolean aceito;
    private Boolean status;
    private Boolean protocolo;
    private Boolean gestor;
    private Boolean confidencial;
    private UsuarioSistema usuarioSistema;
    private UnidadeOrganizacional unidadeOrganizacional;
    private Tramite tramite;
    private String destinoExterno;

    public FiltroListaProtocolo(String numero, String ano, String solicitante, UsuarioSistema usuarioSistema) {
        this.numero = numero;
        this.ano = ano;
        this.solicitante = solicitante;
        this.aceito = false;
        this.status = false;
        this.protocolo = false;
        this.gestor = false;
        this.confidencial = false;
        this.usuarioSistema = usuarioSistema;
        this.dataOperacao = new Date();
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Boolean getGestor() {
        return gestor;
    }

    public void setGestor(Boolean gestor) {
        this.gestor = gestor;
    }

    public Boolean getAceito() {
        return aceito;
    }

    public void setAceito(Boolean aceito) {
        this.aceito = aceito;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Boolean protocolo) {
        this.protocolo = protocolo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Boolean getConfidencial() {
        return confidencial;
    }

    public void setConfidencial(Boolean confidencial) {
        this.confidencial = confidencial;
    }

    public String getDestinoExterno() {
        return destinoExterno;
    }

    public void setDestinoExterno(String destinoExterno) {
        this.destinoExterno = destinoExterno;
    }

    public Tramite getTramite() {
        return tramite;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }
}
