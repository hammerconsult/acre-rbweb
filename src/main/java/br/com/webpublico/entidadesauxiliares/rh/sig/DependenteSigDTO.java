package br.com.webpublico.entidadesauxiliares.rh.sig;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class DependenteSigDTO implements Serializable {
    private Long id;
    private String nome;
    private String cpf;
    private Date dataNascimento;

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependenteSigDTO that = (DependenteSigDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(cpf, that.cpf) &&
            Objects.equals(dataNascimento, that.dataNascimento);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, cpf, dataNascimento);
    }
}
