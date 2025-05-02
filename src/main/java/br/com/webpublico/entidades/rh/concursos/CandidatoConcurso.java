package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.util.anotacoes.Etiqueta;

/**
 * Created by Buzatto on 14/08/2015.
 */
public class CandidatoConcurso {

    @Etiqueta("Concurso")
    private Concurso concurso;

    @Etiqueta("Pessoa")
    private PessoaFisica pessoa;

    public CandidatoConcurso() {
    }

    public CandidatoConcurso(Concurso concurso) {
        setConcurso(concurso);
        this.pessoa = new PessoaFisica();
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }
}
