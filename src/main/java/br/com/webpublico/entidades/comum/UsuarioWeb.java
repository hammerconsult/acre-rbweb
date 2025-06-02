package br.com.webpublico.entidades.comum;

import br.com.webpublico.dte.dto.DteEntity;
import br.com.webpublico.dte.dto.UsuarioWebDTO;
import br.com.webpublico.dte.entidades.UsuarioWebTermoAdesaoDte;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.ChaveNegocio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidadeComChaveNegocio;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.PrestadorUsuarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.UserNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.nfse.enums.SituacaoBloqueioEmissaoNfse;
import br.com.webpublico.repositorios.jdbc.chavenegocio.ChaveNegocioUsuarioWeb;
import br.com.webpublico.solicitacaodispositivo.AuthoritiesConstants;
import br.com.webpublico.util.anotacoes.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * A user.
 */
@Entity
@Audited
@Etiqueta("Usuário Web")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioWeb extends SuperEntidadeComChaveNegocio implements NfseEntity, DteEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @NotNull
    @Length(minimo = 1, maximo = 50)
    @Column(length = 50, unique = true, nullable = false)
    @Etiqueta("Usuário")
    private String login;

    @NotNull
    @Length(minimo = 5, maximo = 60)
    @Column(name = "password_hash", length = 60)
    @Etiqueta("Senha")
    private String password;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Email
    @Length(maximo = 100)
    @Column(length = 100, unique = true)
    @Etiqueta("E-mail")
    private String email;

    @Pesquisavel
    @Tabelavel
    @NotNull
    @Column(nullable = false)
    @Etiqueta("Ativo")
    private Boolean activated;

    @Column(name = "image_url")
    private String imageUrl;

    @Length(maximo = 20)
    @Column(name = "activation_key", length = 20)
    private String activationKey;

    @Length(maximo = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Pesquisavel
    private Date resetDate = null;

    @Length(maximo = 256)
    @Column(name = "dicasenha", length = 256)
    @Etiqueta("Dica da Senha")
    private String dicaSenha;

    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    @JsonIgnore
    private Date createdDate = new Date();

    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @JsonIgnore
    private Date lastModifiedDate = new Date();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<NfseUserAuthority> nfseUserAuthorities;

    @Etiqueta("Pessoa")
    @Obrigatorio
    @ManyToOne
    private Pessoa pessoa;

    @ManyToOne
    private UserNfseCadastroEconomico userNfseCadastroEconomico;

    @OneToMany(mappedBy = "usuarioNfse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNfseCadastroEconomico> userNfseCadastroEconomicos;

    @JsonIgnore
    @Tabelavel
    @Etiqueta("Histórico de Ativações/Inativações")
    @OneToMany(mappedBy = "usuarioNfse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNfseHistoricoAtivacao> userNfseHistoricosAtivacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tentativa de Login")
    private Integer tentativaLogin;

    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ultimo acesso")
    private Date ultimoAcesso;

    @OneToMany(mappedBy = "usuarioWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserDteCadastroEconomico> userDteCadastrosEconomicos;

    @ManyToOne
    private UserDteCadastroEconomico userDteCadastroEconomico;

    @OneToMany(mappedBy = "usuarioWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioWebTermoAdesaoDte> termosAdesaoDte;


    @OneToMany(mappedBy = "usuarioWeb", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloqueioEmissaoNfse> bloqueiosEmissaoNfse;

    private boolean passwordTemporary;

    private String primeiraSenha;

    public UsuarioWeb(Long id) {
        this();
        setId(id);
    }

    public UsuarioWeb() {
        this.passwordTemporary = false;
        this.activated = Boolean.TRUE;
        this.createdBy = "#register";
        this.createdDate = new Date();
        this.resetDate = new Date();
        this.lastModifiedDate = new Date();
        this.userNfseCadastroEconomicos = Lists.newArrayList();
        this.userNfseHistoricosAtivacao = Lists.newArrayList();
        this.tentativaLogin = 0;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isActivated() {
        return activated;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public String getDicaSenha() {
        return dicaSenha;
    }

    public void setDicaSenha(String dicaSenha) {
        this.dicaSenha = dicaSenha;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<NfseUserAuthority> getNfseUserAuthorities() {
        return nfseUserAuthorities;
    }

    public void setNfseUserAuthorities(List<NfseUserAuthority> nfseUserAuthorities) {
        this.nfseUserAuthorities = nfseUserAuthorities;
    }

    @JsonProperty("authorities")
    public List<NfseAuthority> getAuthorities() {
        List<NfseAuthority> authorities = Lists.newArrayList();
        if (this.nfseUserAuthorities != null && !this.nfseUserAuthorities.isEmpty()) {
            for (NfseUserAuthority nfseUserAuthority : this.nfseUserAuthorities) {
                authorities.add(nfseUserAuthority.getNfseAuthority());
            }
        }
        return authorities;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<UserNfseCadastroEconomico> getUserNfseCadastroEconomicos() {
        return userNfseCadastroEconomicos;
    }

    public void setUserNfseCadastroEconomicos(List<UserNfseCadastroEconomico> userNfseCadastroEconomicos) {
        this.userNfseCadastroEconomicos = userNfseCadastroEconomicos;
    }

    public Integer getTentativaLogin() {
        return tentativaLogin != null ? tentativaLogin : 0;
    }

    public void setTentativaLogin(Integer tentativaLogin) {
        this.tentativaLogin = tentativaLogin;
    }

    public List<UserNfseHistoricoAtivacao> getUserNfseHistoricosAtivacao() {
        return userNfseHistoricosAtivacao;
    }

    public void setUserNfseHistoricosAtivacao(List<UserNfseHistoricoAtivacao> userNfseHistoricosAtivacao) {
        this.userNfseHistoricosAtivacao = userNfseHistoricosAtivacao;
    }

    public UserNfseCadastroEconomico getUserNfseCadastroEconomico() {
        return userNfseCadastroEconomico;
    }

    public void setUserNfseCadastroEconomico(UserNfseCadastroEconomico userNfseCadastroEconomico) {
        this.userNfseCadastroEconomico = userNfseCadastroEconomico;
    }

    public Date getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(Date ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    public List<UserDteCadastroEconomico> getUserDteCadastrosEconomicos() {
        return userDteCadastrosEconomicos;
    }

    public void setUserDteCadastrosEconomicos(List<UserDteCadastroEconomico> userDteCadastrosEconomicos) {
        this.userDteCadastrosEconomicos = userDteCadastrosEconomicos;
    }

    public UserDteCadastroEconomico getUserDteCadastroEconomico() {
        return userDteCadastroEconomico;
    }

    public void setUserDteCadastroEconomico(UserDteCadastroEconomico userDteCadastroEconomico) {
        this.userDteCadastroEconomico = userDteCadastroEconomico;
    }

    public List<UsuarioWebTermoAdesaoDte> getTermosAdesaoDte() {
        return termosAdesaoDte;
    }

    public void setTermosAdesaoDte(List<UsuarioWebTermoAdesaoDte> termosAdesaoDte) {
        this.termosAdesaoDte = termosAdesaoDte;
    }

    public List<BloqueioEmissaoNfse> getBloqueiosEmissaoNfse() {
        return bloqueiosEmissaoNfse;
    }

    public void setBloqueiosEmissaoNfse(List<BloqueioEmissaoNfse> bloqueiosEmissaoNfse) {
        this.bloqueiosEmissaoNfse = bloqueiosEmissaoNfse;
    }

    @Override
    public UserNfseDTO toNfseDto() {
        List<String> roles = Lists.newArrayList();
        for (NfseAuthority nfseAuthority : getAuthorities()) {
            roles.add(nfseAuthority.getName());
        }
        UserNfseDTO userNfseDTO =
            new UserNfseDTO(id, login, password, pessoa.getNome(), email, roles, activated,
                pessoa != null && pessoa.isPessoaFisica(), pessoa != null ? pessoa.getId() : null);
        userNfseDTO.setActivationKey(activationKey);
        userNfseDTO.setResetKey(resetKey);
        if (userNfseCadastroEconomico != null && userNfseCadastroEconomico.getCadastroEconomico() != null) {
            CadastroEconomico cadastroEconomico = userNfseCadastroEconomico.getCadastroEconomico();
            PrestadorUsuarioNfseDTO prestadorUsuario = new PrestadorUsuarioNfseDTO();
            prestadorUsuario.setContador(userNfseCadastroEconomico.getContador());
            prestadorUsuario.setPermitido(userNfseCadastroEconomico.isAprovado());
            prestadorUsuario.setPrestador(cadastroEconomico.toNfseDto());
            for (PermissaoUsuarioEmpresaNfse permissao : userNfseCadastroEconomico.getPermissoes()) {
                prestadorUsuario.getRoles().add(permissao.name());
            }
            userNfseDTO.setEmpresa(prestadorUsuario);
        }
        return userNfseDTO;
    }

    @Override
    public String toString() {
        return pessoa != null ? pessoa.getNomeCpfCnpj() : login;
    }

    public boolean isAdmin() {
        for (NfseAuthority a : getAuthorities()) {
            if (a.getName().equals(AuthoritiesConstants.ADMIN)) {
                return true;
            }
        }
        return false;
    }

    public NfseUserAuthority getAdminAuthotity() {
        for (NfseUserAuthority nfseUserAuthority : this.nfseUserAuthorities) {
            if (nfseUserAuthority.getNfseAuthority().getName().equals(AuthoritiesConstants.ADMIN)) {
                return nfseUserAuthority;
            }
        }
        return null;
    }


    public boolean hasPermissao(CadastroEconomico prestador) {
        for (UserNfseCadastroEconomico u : getUserNfseCadastroEconomicos()) {
            if (u.getCadastroEconomico().getId().equals(prestador.getId())) {
                return true;
            }
        }
        return false;
    }

    public void atribuirEmpresa() {
        if (this.getUserNfseCadastroEconomico() == null && !this.getUserNfseCadastroEconomicos().isEmpty()) {
            UserNfseCadastroEconomico userNfseCadastroEconomico = this.getUserNfseCadastroEconomicos().get(0);
            this.setUserNfseCadastroEconomico(userNfseCadastroEconomico);
        }
    }

    @Override
    public ChaveNegocio getChaveNegocio() {
        return new ChaveNegocioUsuarioWeb(login);
    }

    @Override
    public UsuarioWebDTO toDteDto() {
        List<String> roles = Lists.newArrayList();
        for (NfseAuthority nfseAuthority : getAuthorities()) {
            roles.add(nfseAuthority.getName());
        }
        UsuarioWebDTO usuarioWebDTO =
            new UsuarioWebDTO(id, login, password, pessoa != null ? pessoa.getNome() : login, email, roles, activated,
                pessoa != null && pessoa.isPessoaFisica(), pessoa.getId());
        usuarioWebDTO.setActivationKey(activationKey);
        usuarioWebDTO.setResetKey(resetKey);
//        if (userNfseCadastroEconomico != null && userNfseCadastroEconomico.getCadastroEconomico() != null) {
//            CadastroEconomico cadastroEconomico = userNfseCadastroEconomico.getCadastroEconomico();
//            PrestadorUsuarioNfseDTO prestadorUsuario = new PrestadorUsuarioNfseDTO();
//            prestadorUsuario.setContador(userNfseCadastroEconomico.getContador());
//            prestadorUsuario.setPermitido(userNfseCadastroEconomico.isAprovado());
//            prestadorUsuario.setPrestador(cadastroEconomico.toNfseDto());
//            for (PermissaoUsuarioEmpresaNfse permissao : userNfseCadastroEconomico.getPermissoes()) {
//                prestadorUsuario.getRoles().add(permissao.name());
//            }
//            userNfseDTO.setEmpresa(prestadorUsuario);
//        }
        return usuarioWebDTO;
    }

    public void adicionarBloqueioEmissaoNfse(BloqueioEmissaoNfse bloqueioEmissaoNfse) {
        bloqueioEmissaoNfse.setUsuarioWeb(this);
        bloqueiosEmissaoNfse.add(bloqueioEmissaoNfse);
        UserNfseCadastroEconomico userNfseCadastroEconomico = getUserNfseCadastroEconomico(bloqueioEmissaoNfse.getCadastroEconomico());
        if (userNfseCadastroEconomico != null) {
            if (SituacaoBloqueioEmissaoNfse.BLOQUEAR.equals(bloqueioEmissaoNfse.getSituacao())) {
                userNfseCadastroEconomico.setBloqueadoEmissaoNfse(true);
                userNfseCadastroEconomico.setTelefoneDesbloqueio(bloqueioEmissaoNfse.getTelefone());
            } else {
                userNfseCadastroEconomico.setBloqueadoEmissaoNfse(false);
                userNfseCadastroEconomico.setTelefoneDesbloqueio("");
            }
        }
    }

    public UserNfseCadastroEconomico getUserNfseCadastroEconomico(CadastroEconomico cadastroEconomico) {
        for (UserNfseCadastroEconomico nfseCadastroEconomico : getUserNfseCadastroEconomicos()) {
            if (nfseCadastroEconomico.getCadastroEconomico().equals(cadastroEconomico)) {
                return nfseCadastroEconomico;
            }
        }
        return null;
    }

    public boolean isPasswordTemporary() {
        return passwordTemporary;
    }

    public void setPasswordTemporary(boolean passwordTemporary) {
        this.passwordTemporary = passwordTemporary;
    }

    public String getPrimeiraSenha() {
        return primeiraSenha;
    }

    public void setPrimeiraSenha(String primeiraSenha) {
        this.primeiraSenha = primeiraSenha;
    }
}
