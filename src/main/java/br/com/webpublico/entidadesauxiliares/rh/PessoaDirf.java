package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;

import java.io.Serializable;

/**
 * Created by Mailson on 02/03/2017.
 */
public class PessoaDirf implements Serializable {
    private PessoaFisica pessoaFisica;
    private VinculoFP vinculoFP;

    public PessoaDirf() {
    }

    public PessoaDirf(PessoaFisica pessoaFisica, VinculoFP vinculoFP) {
        this.pessoaFisica = pessoaFisica;
        this.vinculoFP = vinculoFP;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PessoaDirf that = (PessoaDirf) o;

        if (pessoaFisica != null ? !pessoaFisica.getId().equals(that.pessoaFisica.getId()) : that.pessoaFisica != null) return false;
        return vinculoFP != null ? vinculoFP.getId().equals(that.vinculoFP.getId()) : that.vinculoFP == null;
    }

    @Override
    public int hashCode() {
        int result = pessoaFisica != null ? pessoaFisica.getId().hashCode() : 0;
        result = 31 * result + (vinculoFP != null ? vinculoFP.getId().hashCode() : 0);
        return result;
    }
}


