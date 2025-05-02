package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.base.Strings;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PessoaFisicaRFB extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private String cpf;
    private String nome;
    private String nomeMae;
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    private String bairro;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String municipio;
    private String ddd;
    private String telefone;
    private String email;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    public PessoaFisicaRFB() {
        super();
        situacao = Situacao.AGUARDANDO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email != null ? !"<n/d>".equalsIgnoreCase(email) ? email : "" : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public String getDddTelefone() {
        String dddTelefone = "";
        if (!Strings.isNullOrEmpty(ddd) && ddd.length() == 2) {
            dddTelefone += ddd;
        }
        if (!Strings.isNullOrEmpty(telefone) && telefone.length() <= 9) {
            dddTelefone += telefone;
        }
        return dddTelefone;
    }

    public enum Situacao {
        AGUARDANDO, ATUALIZANDO, ATUALIZADO;
    }
}
