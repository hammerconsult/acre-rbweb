package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;

import java.io.Serializable;
import java.util.List;

public class BoletimCadastroEconomicoDados implements Serializable {
    private Long idPessoa;
    private String nome;
    private String nomeFantasia;
    private String cpfCnpj;
    private String tipoPessoa;
    private String naturezaJuridica;
    private String autonomo;
    private String classificacaoAtividade;
    private List<BoletimCadastroEconomicoTelefones> telefones;

    public BoletimCadastroEconomicoDados() {
        telefones = Lists.newArrayList();
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getAutonomo() {
        return autonomo;
    }

    public void setAutonomo(String autonomo) {
        this.autonomo = autonomo;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public List<BoletimCadastroEconomicoTelefones> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<BoletimCadastroEconomicoTelefones> telefones) {
        this.telefones = telefones;
    }
}
