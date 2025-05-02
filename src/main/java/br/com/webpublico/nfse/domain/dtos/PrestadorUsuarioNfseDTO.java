package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by rodolfo on 31/10/17.
 */
public class PrestadorUsuarioNfseDTO {

    private Long id;
    private String login;
    private String nome;
    private String email;
    private String descricao;
    private boolean permitido;
    private boolean contador;
    private PrestadorServicoNfseDTO prestador;
    private List<String> roles;

    public PrestadorUsuarioNfseDTO() {
        roles = Lists.newArrayList();
    }

    public PrestadorUsuarioNfseDTO(boolean permitido, PrestadorServicoNfseDTO prestador) {
        this();
        this.permitido = permitido;
        this.prestador = prestador;
    }

    public boolean isPermitido() {
        return permitido;
    }

    public void setPermitido(boolean permitido) {
        this.permitido = permitido;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isContador() {
        return contador;
    }

    public void setContador(boolean contador) {
        this.contador = contador;
    }
}
