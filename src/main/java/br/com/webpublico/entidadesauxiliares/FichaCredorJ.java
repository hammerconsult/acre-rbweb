package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class FichaCredorJ {

    private String cnpj;
    private String razaoSocial;
    private String nomeReduzido;
    private String nomeFantasia;
    private String tipoInscricao;
    private String cei;
    private String inscricaoEstadual;
    private String requerente;
    private String email;
    private String tipoEmpresa;
    private String homePage;
    private String observacao;
    private Boolean bloqueado;
    private String motivoBloqueio;
    private BigDecimal idPessoa;
    private List<FichaCredorClassePessoa> fichaCredorClassePessoas;
    private List<FichaCredorTelefone> fichaCredorTelefones;
    private List<FichaCredorEndereco> fichaCredorEnderecos;
    private List<FichaCredorContaCBanc> fichaCredorContaCBancs;

    public FichaCredorJ() {
        fichaCredorClassePessoas = Lists.newArrayList();
        fichaCredorTelefones = Lists.newArrayList();
        fichaCredorEnderecos = Lists.newArrayList();
        fichaCredorContaCBancs = Lists.newArrayList();
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(String tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public String getCei() {
        return cei;
    }

    public void setCei(String cei) {
        this.cei = cei;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getMotivoBloqueio() {
        return motivoBloqueio;
    }

    public void setMotivoBloqueio(String motivoBloqueio) {
        this.motivoBloqueio = motivoBloqueio;
    }

    public List<FichaCredorClassePessoa> getFichaCredorClassePessoas() {
        return fichaCredorClassePessoas;
    }

    public BigDecimal getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(BigDecimal idPessoa) {
        this.idPessoa = idPessoa;
    }

    public void setFichaCredorClassePessoas(List<FichaCredorClassePessoa> fichaCredorClassePessoas) {
        this.fichaCredorClassePessoas = fichaCredorClassePessoas;
    }

    public List<FichaCredorTelefone> getFichaCredorTelefones() {
        return fichaCredorTelefones;
    }

    public void setFichaCredorTelefones(List<FichaCredorTelefone> fichaCredorTelefones) {
        this.fichaCredorTelefones = fichaCredorTelefones;
    }

    public List<FichaCredorEndereco> getFichaCredorEnderecos() {
        return fichaCredorEnderecos;
    }

    public void setFichaCredorEnderecos(List<FichaCredorEndereco> fichaCredorEnderecos) {
        this.fichaCredorEnderecos = fichaCredorEnderecos;
    }

    public List<FichaCredorContaCBanc> getFichaCredorContaCBancs() {
        return fichaCredorContaCBancs;
    }

    public void setFichaCredorContaCBancs(List<FichaCredorContaCBanc> fichaCredorContaCBancs) {
        this.fichaCredorContaCBancs = fichaCredorContaCBancs;
    }
}
