package br.com.webpublico.entidadesauxiliares.contabil;

/**
 * Created by renatoromanini on 17/10/16.
 */
public class IdentificadorFonteDespesaORC {

    private Long idUnidade;
    private Long idFonteDeRecurso;
    private Long idDespesaOrc;
    private Long idExercicio;


    public IdentificadorFonteDespesaORC(Long idUnidade, Long idFonteDeRecurso, Long idDespesaOrc, Long idExercicio) {
        this.idUnidade = idUnidade;
        this.idFonteDeRecurso = idFonteDeRecurso;
        this.idDespesaOrc = idDespesaOrc;
        this.idExercicio = idExercicio;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public Long getIdFonteDeRecurso() {
        return idFonteDeRecurso;
    }

    public Long getIdDespesaOrc() {
        return idDespesaOrc;
    }

    public Long getIdExercicio() {
        return idExercicio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentificadorFonteDespesaORC that = (IdentificadorFonteDespesaORC) o;

        if (!idUnidade.equals(that.idUnidade)) return false;
        if (!idFonteDeRecurso.equals(that.idFonteDeRecurso)) return false;
        if (!idDespesaOrc.equals(that.idDespesaOrc)) return false;
        return idExercicio.equals(that.idExercicio);

    }

    @Override
    public int hashCode() {
        int result = idUnidade.hashCode();
        result = 31 * result + idFonteDeRecurso.hashCode();
        result = 31 * result + idDespesaOrc.hashCode();
        result = 31 * result + idExercicio.hashCode();
        return result;
    }
}
