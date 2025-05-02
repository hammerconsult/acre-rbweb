package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNfseDTO implements NfseDTO {

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 100;
    public static final int COMUM_FIELD = 255;

    private Long id;

    private String login;

    private String nome;

    private String password;

    private String activationKey;

    private String resetKey;

    private String email;

    private List<String> roles;

    private boolean pessoaFisica;

    private PrestadorUsuarioNfseDTO empresa;

    private boolean activated;

    private Long pessoaId;

    public UserNfseDTO() {
    }

    public UserNfseDTO(Long id, String login, String password, String nome, String email,
                       List<String> roles, boolean activated, boolean pessoaFisica, Long pessoaId) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.nome = nome;
        this.roles = roles;
        this.activated = activated;
        this.pessoaFisica = pessoaFisica;
        this.pessoaId = pessoaId;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PrestadorUsuarioNfseDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(PrestadorUsuarioNfseDTO empresa) {
        this.empresa = empresa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(boolean pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
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

    @Override
    public String toString() {
        return "UserDTO{" +
            "login='" + login + '\'' +
            ", password='" + password + '\'' +
            ", email='" + email + '\'' +
            ", roles=" + roles +
            ", activated=" + activated +
            '}';
    }

    @JsonIgnore
    public void validarDadosPessoaisUsuario() throws NfseOperacaoNaoPermitidaException {
        List<String> mensagens = Lists.newArrayList();

        if (Strings.isNullOrEmpty(getLogin())) {
            mensagens.add("O campo CPF ou CNPJ é obrigatório!");
        }

        if (Strings.isNullOrEmpty(getEmail())) {
            mensagens.add("O campo e-mail é obrigatório!");
        }
        if (Strings.isNullOrEmpty(getPassword())) {
            mensagens.add("O campo senha é obrigatório!");
        }

        if (!mensagens.isEmpty()) {
            throw new NfseOperacaoNaoPermitidaException("Atenção!", mensagens);
        }
    }

}
