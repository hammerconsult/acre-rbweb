package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
@Etiqueta("Usuário do Sistema")

public class UsuarioSistema extends SuperEntidade implements Comparable<UsuarioSistema>, UserDetails {

    private static final long serialVersionUID = 1L;
    private static final List<GrantedAuthority> DEFAULT_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_USER");
    private static final PasswordEncoder PASSWORD_ENCODER = new ShaPasswordEncoder();
    public static int TEMPO_MAXIMO_INATIVO_MINUTOS = 15;
    public static final String USUARIO_AGENDAMENTO_TAREFAS = "AGENDAMENTO DE TAREFAS";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Login")
    private String login;

    @Type(type = "uuid-char")
    private UUID salt = UUID.randomUUID();

    @Column
    private String senha;

    @Transient
    @Etiqueta("Nome da Pessoa")
    private String nomePessoa;

    @OneToOne
    @Tabelavel
    @Etiqueta("Pessoa")
    private PessoaFisica pessoaFisica;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioUnidadeOrganizacional> usuarioUnidadeOrganizacional;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioUnidadeOrganizacionalOrcamentaria> usuarioUnidadeOrganizacionalOrc;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Expiração")
    private Date dataExpiracao;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Etiqueta("Bloqueado")
    @Tabelavel
    private Boolean expira;

    @ManyToMany
    @JoinTable(name = "GRUPOUSUARIOSISTEMA",
        joinColumns = @JoinColumn(name = "USUARIOSISTEMA_ID",
            referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "GRUPOUSUARIO_ID",
            referencedColumnName = "ID"))
    private List<GrupoUsuario> grupos = Lists.newLinkedList();

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoRecursosUsuario> grupoRecursosUsuario;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursosUsuario> recursosUsuario;

    private String observacao;
    private Boolean validaStatusRh;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoUsuarioSistema> arquivoUsuarioSistemas;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VigenciaTribUsuario> vigenciaTribUsuarios;

    private String nomeLegado;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;

    @Etiqueta("Pode Alterar Data?")
    @Tabelavel(campoSelecionado = false)
    private Boolean podeAlterarData;

    @Etiqueta("Pode visualizar históricos?")
    @Tabelavel(campoSelecionado = false)
    private Boolean visualizarHistorico;

    private Boolean exibirVersao;
    @Etiqueta("Pode visualizar históricos?")
    private Boolean acessoTodosVinculosRH;

    @Etiqueta("Último Acesso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoAcesso;

    @ManyToOne
    private UnidadeOrganizacional orcamentariaCorrente;
    @ManyToOne
    private UnidadeOrganizacional administrativaCorrente;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PerfilUsuario> perfis;

    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AutorizacaoUsuarioRH> autorizacaoUsuarioRH;

    @Etiqueta("Não Bloquear Automaticamente?")
    private Boolean naoBloquearAutomaticamente;
    private String ultimaVersao;
    @OneToMany(mappedBy = "usuarioSistema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssinaturaUsuarioSistema> assinaturas;
    @Etiqueta("Tempo Máximo Inativo em Minutos")
    private Integer tempoMaximoInativoMinutos;

    public Boolean getExibirVersao() {
        return Boolean.TRUE.equals(exibirVersao);
    }

    public void setExibirVersao(Boolean exibirVersao) {
        this.exibirVersao = exibirVersao;
    }

    public UsuarioSistema() {
        this.login = "";
        this.dataRegistro = new Date();
        this.validaStatusRh = false;
        this.expira = false;
        this.dataCadastro = new Date();

        this.grupos = new ArrayList<>();
        this.arquivoUsuarioSistemas = new ArrayList<>();
        this.grupoRecursosUsuario = new ArrayList<>();
        this.recursosUsuario = new ArrayList<>();
        this.usuarioUnidadeOrganizacional = new ArrayList<>();
        this.usuarioUnidadeOrganizacionalOrc = new ArrayList<>();
        this.vigenciaTribUsuarios = new ArrayList<>();
        this.autorizacaoUsuarioRH = Lists.newArrayList();
        this.assinaturas = Lists.newArrayList();
        naoBloquearAutomaticamente = false;
        this.exibirVersao = true;
    }

    public UsuarioSistema(String login, String senha, PessoaFisica pessoaFisica, Date dataExpiracao, Boolean expira) {
        dataRegistro = new Date();
        this.login = login;
        this.senha = senha;
        this.pessoaFisica = pessoaFisica;
        this.dataExpiracao = dataExpiracao;
        this.expira = expira;
        this.exibirVersao = true;
    }

    public UsuarioSistema(Long id, String login, String nome, Boolean expira) {
        dataRegistro = new Date();
        this.id = id;
        this.login = login;
        this.nomePessoa = nome;
        this.expira = expira;
        this.exibirVersao = true;
    }

    public UsuarioSistema(Long id, String login, String nome, Boolean expira, Date dataCadastro) {
        dataRegistro = new Date();
        this.id = id;
        this.login = login;
        this.nomePessoa = nome;
        this.expira = expira;
        this.dataCadastro = dataCadastro;
        this.exibirVersao = true;
    }

    public UsuarioSistema(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
        this.exibirVersao = true;
    }

    @PreUpdate
    void initSalt() {
        if (salt == null) {
            salt = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UsuarioSistema) {
            UsuarioSistema other = (UsuarioSistema) obj;
            return Objects.equal(this.login, other.login);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.login);
    }

    @Override
    public int compareTo(UsuarioSistema o) {
        try {
            return this.login.compareTo(o.login);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public String toString() {
        if (pessoaFisica != null) {
            return pessoaFisica.getNome();
        } else {
            return login;
        }
    }

    public boolean isAutenticacaoCorreta(String credentials) {
        String encodedPassword = PASSWORD_ENCODER.encodePassword(credentials, salt);
        if (encodedPassword.equals(senha)) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (perfis == null || perfis.isEmpty()) {
            return DEFAULT_AUTHORITIES;
        }
        List<GrantedAuthority> authorityList = Lists.newArrayList();
        for (PerfilUsuario perfil : perfis) {
            authorityList.addAll(AuthorityUtils.createAuthorityList(perfil.getPerfil().name()));
        }
        return authorityList;
    }

    public boolean isAdmin() {
        if (perfis == null || perfis.isEmpty()) {
            return false;
        }
        for (PerfilUsuario perfil : perfis) {
            if (PerfilUsuario.Perfil.ROLE_ADMIN.equals(perfil.getPerfil())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<AutorizacaoUsuarioRH> getAutorizacaoUsuarioRH() {
        return autorizacaoUsuarioRH;
    }

    public void setAutorizacaoUsuarioRH(List<AutorizacaoUsuarioRH> autorizacaoUsuarioRH) {
        this.autorizacaoUsuarioRH = autorizacaoUsuarioRH;
    }

    public List<PerfilUsuario> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<PerfilUsuario> perfis) {
        this.perfis = perfis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getExpira() {
        return expira != null ? expira : false;
    }

    public void setExpira(Boolean expira) {
        this.expira = expira;
    }

    public String getLogin() {
        return login;
    }

    public String getCpf() {
        return pessoaFisica != null ? pessoaFisica.getCpfSemFormatacao() : "";
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UUID getSalt() {
        return salt;
    }

    public void setSalt(UUID salt) {
        this.salt = salt;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public List<GrupoUsuario> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoUsuario> grupos) {
        this.grupos = Lists.newArrayList(grupos);
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<GrupoRecursosUsuario> getGrupoRecursosUsuario() {
        return grupoRecursosUsuario;
    }

    public void setGrupoRecursosUsuario(List<GrupoRecursosUsuario> grupoRecursosUsuario) {
        this.grupoRecursosUsuario = grupoRecursosUsuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getValidaStatusRh() {
        return validaStatusRh;
    }

    public void setValidaStatusRh(Boolean validaStatusRh) {
        this.validaStatusRh = validaStatusRh;
    }

    public List<ArquivoUsuarioSistema> getArquivoUsuarioSistemas() {
        return arquivoUsuarioSistemas;
    }

    public void setArquivoUsuarioSistemas(List<ArquivoUsuarioSistema> arquivoUsuarioSistemas) {
        this.arquivoUsuarioSistemas = arquivoUsuarioSistemas;
    }

    public Integer getTempoMaximoInativoMinutos() {
        int tempoMaximoInativoMinutosPadrao = TEMPO_MAXIMO_INATIVO_MINUTOS;
        if (tempoMaximoInativoMinutos != null && tempoMaximoInativoMinutos > 0) {
            return tempoMaximoInativoMinutos;
        }
        return tempoMaximoInativoMinutosPadrao;
    }

    public void setTempoMaximoInativoMinutos(Integer tempoMaximoInativoMinutos) {
        this.tempoMaximoInativoMinutos = tempoMaximoInativoMinutos;
    }

    public boolean loginIgualSenha(String login) {
        String encodedLogin = PASSWORD_ENCODER.encodePassword(login, salt);
        if (encodedLogin.equals(senha)) {
            return true;
        }
        return false;
    }

    public boolean senhaValida(String senhaRecebida) {
        String encodedSenha = PASSWORD_ENCODER.encodePassword(senhaRecebida, salt);
        if (encodedSenha.equals(senha)) {
            return true;
        }
        return false;
    }

    public List<RecursosUsuario> getRecursosUsuario() {
        return recursosUsuario;
    }

    public void setRecursosUsuario(List<RecursosUsuario> recursosUsuario) {
        this.recursosUsuario = recursosUsuario;
    }

    public String toStringAutoComplete() {
        try {
            return getLogin() + " - " + getPessoaFisica().getNome() + " (" + getPessoaFisica().getCpf_Cnpj() + ")";
        } catch (NullPointerException e) {
            return getLogin();
        }
    }

    public List<UsuarioUnidadeOrganizacional> getUsuarioUnidadeOrganizacional() {
        return usuarioUnidadeOrganizacional;
    }

    public void setUsuarioUnidadeOrganizacional(List<UsuarioUnidadeOrganizacional> usuarioUnidadeOrganizacional) {
        this.usuarioUnidadeOrganizacional = usuarioUnidadeOrganizacional;
    }

    public List<VigenciaTribUsuario> getVigenciaTribUsuarios() {
        return vigenciaTribUsuarios;
    }

    public void setVigenciaTribUsuarios(List<VigenciaTribUsuario> vigenciaTribUsuarios) {
        this.vigenciaTribUsuarios = vigenciaTribUsuarios;
    }

    public String getNomeLegado() {
        return nomeLegado;
    }

    public void setNomeLegado(String nomeLegado) {
        this.nomeLegado = nomeLegado;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getPodeAlterarData() {
        return podeAlterarData;
    }

    public void setPodeAlterarData(Boolean podeAlterarData) {
        this.podeAlterarData = podeAlterarData;
    }

    public String getNome() {
        if (pessoaFisica != null) {
            return pessoaFisica.getNome();
        }
        return login;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public Boolean getVisualizarHistorico() {
        return visualizarHistorico;
    }

    public void setVisualizarHistorico(Boolean visualizarHistorico) {
        this.visualizarHistorico = visualizarHistorico;
    }

    public boolean temEstaLotacao(LotacaoVistoriadora lotacao, Date finalDaVigencia) {
        for (VigenciaTribUsuario vigenciaTribUsuario : vigenciaTribUsuarios) {
            if (vigenciaTribUsuario.getVigenciaFinal() == null || vigenciaTribUsuario.getVigenciaFinal().after(finalDaVigencia)) {
                for (LotacaoTribUsuario lotacaoTribUsuario : vigenciaTribUsuario.getLotacaoTribUsuarios()) {
                    if (lotacaoTribUsuario.getLotacao().equals(lotacao)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<UsuarioUnidadeOrganizacionalOrcamentaria> getUsuarioUnidadeOrganizacionalOrc() {
        return usuarioUnidadeOrganizacionalOrc;
    }

    public void setUsuarioUnidadeOrganizacionalOrc(List<UsuarioUnidadeOrganizacionalOrcamentaria> usuarioUnidadeOrganizacionalOrc) {
        this.usuarioUnidadeOrganizacionalOrc = usuarioUnidadeOrganizacionalOrc;
    }

    public Boolean getAcessoTodosVinculosRH() {
        return acessoTodosVinculosRH;
    }

    public void setAcessoTodosVinculosRH(Boolean acessoTodosVinculosRH) {
        this.acessoTodosVinculosRH = acessoTodosVinculosRH;
    }

    public Boolean getNaoBloquearAutomaticamente() {
        return naoBloquearAutomaticamente == null ? Boolean.FALSE : naoBloquearAutomaticamente;
    }

    public void setNaoBloquearAutomaticamente(Boolean naoBloquearAutomaticamente) {
        this.naoBloquearAutomaticamente = naoBloquearAutomaticamente;
    }

    public boolean possuiAcessoTodosVinculosRH() {
        try {
            return acessoTodosVinculosRH.equals(Boolean.TRUE);
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public UnidadeOrganizacional getOrcamentariaCorrente() {
        return orcamentariaCorrente;
    }

    public void setOrcamentariaCorrente(UnidadeOrganizacional orcamentariaCorrente) {
        this.orcamentariaCorrente = orcamentariaCorrente;
    }

    public UnidadeOrganizacional getAdministrativaCorrente() {
        return administrativaCorrente;
    }

    public void setAdministrativaCorrente(UnidadeOrganizacional administrativaCorrente) {
        this.administrativaCorrente = administrativaCorrente;
    }


    public boolean hasRole(String role) {
        for (GrantedAuthority grantedAuthority : getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }


    public VigenciaTribUsuario getVigenciaTribUsuarioVigente() {
        for (VigenciaTribUsuario vigenciaTribUsuario : getVigenciaTribUsuarios()) {
            if (DataUtil.isBetween(vigenciaTribUsuario.getVigenciaInicial(), vigenciaTribUsuario.getVigenciaFinal(), new Date()))
                return vigenciaTribUsuario;
        }
        return null;
    }


    public boolean isFiscalTributario() {
        VigenciaTribUsuario vigenciaTribUsuarioVigente = getVigenciaTribUsuarioVigente();
        if (vigenciaTribUsuarioVigente == null || vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios() == null || vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios().isEmpty())
            return false;
        for (TipoUsuarioTribUsuario tipoUsuarioTribUsuario : vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios()) {
            if (TipoUsuarioTributario.FISCAL_TRIBUTARIO.equals(tipoUsuarioTribUsuario.getTipoUsuarioTributario())) {
                return true;
            }
        }
        return false;
    }

    public boolean isFiscalTributarioSupervisor() {
        VigenciaTribUsuario vigenciaTribUsuarioVigente = getVigenciaTribUsuarioVigente();
        if (vigenciaTribUsuarioVigente == null || vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios() == null || vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios().isEmpty())
            return false;
        for (TipoUsuarioTribUsuario tipoUsuarioTribUsuario : vigenciaTribUsuarioVigente.getTipoUsuarioTribUsuarios()) {
            if (TipoUsuarioTributario.FISCAL_TRIBUTARIO.equals(tipoUsuarioTribUsuario.getTipoUsuarioTributario()) &&
                tipoUsuarioTribUsuario.getSupervisor()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermissaoLancamentoAverbacaoContratosEncerrados() {
        if (getAutorizacaoUsuarioRH() != null && !getAutorizacaoUsuarioRH().isEmpty()) {
            for (AutorizacaoUsuarioRH autorizacaoUsuarioRH : getAutorizacaoUsuarioRH()) {
                if (TipoAutorizacaoRH.PERMITIR_LANCAMENTO_AVERBACAO_CONTRATOS_ENCERRADOS.equals(autorizacaoUsuarioRH.getTipoAutorizacaoRH())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUltimaVersao() {
        return ultimaVersao == null ? "" : ultimaVersao;
    }

    public void setUltimaVersao(String ultimaVersao) {
        this.ultimaVersao = ultimaVersao;
    }

    public List<AssinaturaUsuarioSistema> getAssinaturas() {
        return assinaturas;
    }

    public void setAssinaturas(List<AssinaturaUsuarioSistema> assinaturas) {
        this.assinaturas = assinaturas;
    }

    public Date getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(Date ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    public boolean hasAutorizacaoTributario(AutorizacaoTributario autorizacaoTributario) {
        VigenciaTribUsuario vigenciaTribUsuarioVigente = getVigenciaTribUsuarioVigente();
        if (vigenciaTribUsuarioVigente != null) {
            List<AutorizacaoTributarioUsuario> autorizacaoTributarioUsuarios = vigenciaTribUsuarioVigente.getAutorizacaoTributarioUsuarios();
            if (autorizacaoTributarioUsuarios != null && !autorizacaoTributarioUsuarios.isEmpty()) {
                for (AutorizacaoTributarioUsuario autorizacaoTributarioUsuario : autorizacaoTributarioUsuarios) {
                    if (autorizacaoTributarioUsuario.getAutorizacao().equals(autorizacaoTributario)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
