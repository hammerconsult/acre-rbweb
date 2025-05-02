package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroMalaDiretaGeralPessoa {

    private Pessoa pessoa;
    private List<Pessoa> pessoas;

    public FiltroMalaDiretaGeralPessoa() {
        pessoas = Lists.newArrayList();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void addPessoa() {
        Util.adicionarObjetoEmLista(pessoas, pessoa);
        pessoa = null;
    }

    public void removerDaLista(List list, Object object) {
        if (list.contains(object)) {
            list.remove(object);
        }
    }
}
