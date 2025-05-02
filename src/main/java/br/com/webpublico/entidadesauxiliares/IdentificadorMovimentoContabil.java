package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import java.util.Date;

/**
 * Created by Edi on 18/02/2016.
 */
public class IdentificadorMovimentoContabil {

    public UnidadeOrganizacional unidadeOrganizacional;
    public Exercicio exercicio;
    public Date dataMovimento;

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentificadorMovimentoContabil that = (IdentificadorMovimentoContabil) o;

        if (unidadeOrganizacional.getId() != null ? !unidadeOrganizacional.getId().equals(that.unidadeOrganizacional.getId()) : that.unidadeOrganizacional.getId() != null)
            return false;
        return !(exercicio.getId() != null ? !exercicio.getId().equals(that.exercicio.getId()) : that.exercicio.getId() != null);

    }

    @Override
    public int hashCode() {
        int result = unidadeOrganizacional.getId() != null ? unidadeOrganizacional.getId().hashCode() : 0;
        result = 31 * result + (exercicio.getId() != null ? exercicio.getId().hashCode() : 0);
        return result;
    }
}
