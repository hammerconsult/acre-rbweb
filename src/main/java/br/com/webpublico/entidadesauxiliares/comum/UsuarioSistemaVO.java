package br.com.webpublico.entidadesauxiliares.comum;

import br.com.webpublico.enums.TipoPessoa;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UsuarioSistemaVO implements Serializable {
    private Long id;
    private String nome;
    private String login;
    private String cpf;
    private String situacao;
    private Long idPessoa;
    private List<RecursoSistemaVO> recursos;

    public UsuarioSistemaVO() {
        recursos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public List<RecursoSistemaVO> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoSistemaVO> recursos) {
        this.recursos = recursos;
    }
}
