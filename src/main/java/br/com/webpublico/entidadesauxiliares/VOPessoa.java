package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.util.StringUtil;

import java.util.Objects;

public class VOPessoa {

    private Long id;
    private String cpfCnpj;
    private String nome;
    private String tipoPessoa;

    private VOEndereco endereco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNomeSemAcento() {
        return nome != null ? StringUtil.removeAcentuacao(nome) : "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public VOEndereco getEndereco() {
        return endereco;
    }

    public void setEndereco(VOEndereco endereco) {
        this.endereco = endereco;
    }

    public boolean isPessoaFisica() {
        return TipoPessoa.FISICA.getSigla().equals(tipoPessoa);
    }

    public boolean isPessoaJuridica() {
        return TipoPessoa.JURIDICA.getSigla().equals(tipoPessoa);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOPessoa that = (VOPessoa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
