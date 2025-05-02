package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.Pessoa;
import com.google.common.base.Objects;

public class VOConsultaCda {
    private Pessoa pessoa;
    private Cadastro cadastro;

    public VOConsultaCda() {
    }

    public VOConsultaCda(Pessoa pessoa, Cadastro cadastro) {
        this.pessoa = pessoa;
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOConsultaCda that = (VOConsultaCda) o;
        return Objects.equal(this.pessoa.getId(), that.pessoa.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.pessoa.getId());
    }
}
