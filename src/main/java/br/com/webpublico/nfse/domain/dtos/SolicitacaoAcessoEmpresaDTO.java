package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.SituacaoSolicitacaoAcesso;
import br.com.webpublico.nfse.domain.SolicitacaoAcessoEmpresa;

import java.io.Serializable;
import java.util.Date;

public class SolicitacaoAcessoEmpresaDTO implements Serializable {

    private Long id;

    private String cnpj;

    private String inscricaoMunicipal;

    private String razaoSocial;

    private String nomeFantasia;

    private String site;

    private String email;

    private String resumoEmpresa;

    private String nomeContato;

    private String telefoneContato;

    private SituacaoSolicitacaoAcesso situacao;

//    private NfseUserDTO nfseUser;

    private EscritorioContabilDTO escritorioContabil;

    private Date solicitadaEm;

    public SolicitacaoAcessoEmpresaDTO() {
    }

    public SolicitacaoAcessoEmpresaDTO(SolicitacaoAcessoEmpresa solicitacao) {
        this.id = solicitacao.getId();
        this.cnpj = solicitacao.getCnpj();
        this.razaoSocial = solicitacao.getRazaoSocial();
        this.nomeFantasia = solicitacao.getNomeFantasia();
        this.situacao = solicitacao.getSituacao();
        this.inscricaoMunicipal = solicitacao.getInscricaoMunicipal();
        this.site = solicitacao.getSite();
        this.email = solicitacao.getEmail();
        this.resumoEmpresa = solicitacao.getResumoEmpresa();
        this.nomeContato = solicitacao.getNomeContato();
        this.telefoneContato = solicitacao.getTelefoneContato();
        this.escritorioContabil = new EscritorioContabilDTO(solicitacao.getEscritorioContabil());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResumoEmpresa() {
        return resumoEmpresa;
    }

    public void setResumoEmpresa(String resumoEmpresa) {
        this.resumoEmpresa = resumoEmpresa;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public SituacaoSolicitacaoAcesso getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoAcesso situacao) {
        this.situacao = situacao;
    }

//    public NfseUserDTO getNfseUser() {
//        return nfseUser;
//    }
//
//    public void setNfseUser(NfseUserDTO nfseUser) {
//        this.nfseUser = nfseUser;
//    }

    public EscritorioContabilDTO getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabilDTO escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    public Date getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(Date solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }
}
