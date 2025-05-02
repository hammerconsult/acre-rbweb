package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.Pessoa;

public class PessoaClasseIntegracaoRHContabil {

    private Pessoa pessoa;
    private ClasseCredor classeCredor;

    public PessoaClasseIntegracaoRHContabil() {
    }

    public PessoaClasseIntegracaoRHContabil(Pessoa fornecedor, ClasseCredor classeCredor) {
        this.pessoa = fornecedor;
        this.classeCredor = classeCredor;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }
}
