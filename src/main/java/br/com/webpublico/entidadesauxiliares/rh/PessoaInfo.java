package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author peixe on 15/09/2016  17:13.
 */
public class PessoaInfo {
    private PessoaFisica pessoa;
    private List<VinculoFP> vinculos;

    public PessoaInfo() {
        vinculos = Lists.newLinkedList();
    }


    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public List<VinculoFP> getVinculos() {
        return vinculos;
    }

    public void setVinculos(List<VinculoFP> vinculos) {
        this.vinculos = vinculos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PessoaInfo that = (PessoaInfo) o;

        return pessoa != null ? pessoa.equals(that.pessoa) : that.pessoa == null;
    }

    @Override
    public int hashCode() {
        return pessoa != null ? pessoa.hashCode() : 0;
    }
}
