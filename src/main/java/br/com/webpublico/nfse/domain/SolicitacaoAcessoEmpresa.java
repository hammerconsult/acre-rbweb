package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.EscritorioContabil;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaAvaliacaoDTO;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "solicitacaoacessoempresa")
public class SolicitacaoAcessoEmpresa extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "cnpj")
    private String cnpj;

    @Column(name = "inscricaomunicipal")
    private String inscricaoMunicipal;

    @Column(name = "razaosocial")
    private String razaoSocial;

    @Column(name = "nomefantasia")
    private String nomeFantasia;

    @Column(name = "site")
    private String site;

    @Column(name = "email")
    private String email;

    @Column(name = "resumoempresa")
    private String resumoEmpresa;

    @Column(name = "nomecontato")
    private String nomeContato;

    @Column(name = "telefonecontato")
    private String telefoneContato;

    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoSolicitacaoAcesso situacao;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nfseuser_id")
    private UsuarioWeb nfseUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "escritoriocontabil_id")
    private EscritorioContabil escritorioContabil;

    @Column(name = "solicitadaem")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date solicitadaEm = null;

    @Column(name = "avaliadaem")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date avaliadaem = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nfseuser_avaliacao_id")
    private UsuarioWeb nfseUserAvaliacao;

    @Column(name = "motivorejeicao")
    private String motivoRejeicao;

    public SolicitacaoAcessoEmpresa() {
    }

    public SolicitacaoAcessoEmpresa(SolicitacaoAcessoEmpresaDTO sae) {
        this.id = sae.getId();
        this.cnpj = sae.getCnpj();
        this.inscricaoMunicipal = sae.getInscricaoMunicipal();
        this.razaoSocial = sae.getRazaoSocial();
        this.nomeFantasia = sae.getNomeFantasia();
        this.site = sae.getSite();
        this.email = sae.getEmail();
        this.resumoEmpresa = sae.getResumoEmpresa();
        this.nomeContato = sae.getNomeContato();
        this.telefoneContato = sae.getTelefoneContato();
        this.situacao = sae.getSituacao();
        this.solicitadaEm = new Date();
    }

    public SolicitacaoAcessoEmpresa(Long id, String cnpj, String razaoSocial, String nomeFantasia, SituacaoSolicitacaoAcesso situacao, Date solicitadaEm) {
        this.id = id;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.situacao = situacao;
        this.solicitadaEm = solicitadaEm;
    }

    public SolicitacaoAcessoEmpresa(Long id, String cnpj, String razaoSocial, String nomeFantasia, SituacaoSolicitacaoAcesso situacao, Date solicitadaEm, Long userId, String usuarioLogin) {
        this.id = id;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.situacao = situacao;
        this.solicitadaEm = solicitadaEm;
        UsuarioWeb usuario = new UsuarioWeb();
        usuario.setId(userId);
        usuario.setLogin(usuarioLogin);
        this.nfseUser = usuario;
    }

    public SolicitacaoAcessoEmpresa(Long id, String cnpj, String razaoSocial, String nomeFantasia, SituacaoSolicitacaoAcesso situacao, Date solicitadaEm, String inscricaoMunicipal, String site, String email, String resumoEmpresa, String nomeContato, String telefoneContato, Long escritorioContabilId, Long userId, String usuarioLogin) {
        this.id = id;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.inscricaoMunicipal = inscricaoMunicipal;
        this.situacao = situacao;
        this.solicitadaEm = solicitadaEm;
        this.site = site;
        this.email = email;
        this.resumoEmpresa = resumoEmpresa;
        this.nomeContato = nomeContato;
        this.telefoneContato = telefoneContato;
        UsuarioWeb usuario = new UsuarioWeb();
        usuario.setId(userId);
        usuario.setLogin(usuarioLogin);
        this.nfseUser = usuario;
        EscritorioContabil escritorio = new EscritorioContabil();
        escritorio.setId(escritorioContabilId);
        this.escritorioContabil = escritorio;
    }

    public SolicitacaoAcessoEmpresa(SolicitacaoAcessoEmpresaAvaliacaoDTO saea) {
        this.id = saea.getId();
        this.cnpj = saea.getCnpj();
        this.inscricaoMunicipal = saea.getInscricaoMunicipal();
        this.razaoSocial = saea.getRazaoSocial();
        this.nomeFantasia = saea.getNomeFantasia();
        this.site = saea.getSite();
        this.email = saea.getEmail();
        this.resumoEmpresa = saea.getResumoEmpresa();
        this.nomeContato = saea.getNomeContato();
        this.telefoneContato = saea.getTelefoneContato();
        this.situacao = saea.getSituacao();
        this.solicitadaEm = saea.getSolicitadaEm();
//        this.escritorioContabil = new EscritorioContabil(saea.getEscritorioContabil());
//        this.nfseUser = new UsuarioWeb(saea.getNfseUser());
        this.avaliadaem = new Date();
        this.motivoRejeicao = saea.getMotivoRejeicao();
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

    public UsuarioWeb getNfseUser() {
        return nfseUser;
    }

    public void setNfseUser(UsuarioWeb nfseUser) {
        this.nfseUser = nfseUser;
    }

    public EscritorioContabil getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabil escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    public Date getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(Date solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }

    public Date getAvaliadaem() {
        return avaliadaem;
    }

    public void setAvaliadaem(Date avaliadaem) {
        this.avaliadaem = avaliadaem;
    }

    public UsuarioWeb getNfseUserAvaliacao() {
        return nfseUserAvaliacao;
    }

    public void setNfseUserAvaliacao(UsuarioWeb nfseUserAvaliacao) {
        this.nfseUserAvaliacao = nfseUserAvaliacao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
