package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.FuncionalidadeAdministrativo;

/**
 * Created by wellington on 14/01/2017.
 */
public class ChaveGeradorCodigoAdministrativo {

    private Long idUnidadeAdministrativa;
    private Long idExercicio;
    private FuncionalidadeAdministrativo funcionalidadeAdministrativo;

    public Long getIdUnidadeAdministrativa() {
        return idUnidadeAdministrativa;
    }

    public void setIdUnidadeAdministrativa(Long idUnidadeAdministrativa) {
        this.idUnidadeAdministrativa = idUnidadeAdministrativa;
    }

    public Long getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(Long idExercicio) {
        this.idExercicio = idExercicio;
    }

    public FuncionalidadeAdministrativo getFuncionalidadeAdministrativo() {
        return funcionalidadeAdministrativo;
    }

    public void setFuncionalidadeAdministrativo(FuncionalidadeAdministrativo funcionalidadeAdministrativo) {
        this.funcionalidadeAdministrativo = funcionalidadeAdministrativo;
    }

    public static ChaveGeradorCodigoAdministrativo newChave(Long idUnidade,
                                                            Long idExercicio,
                                                            FuncionalidadeAdministrativo funcionalidade) {
        ChaveGeradorCodigoAdministrativo chave = new ChaveGeradorCodigoAdministrativo();
        chave.setIdUnidadeAdministrativa(idUnidade);
        chave.setIdExercicio(idExercicio);
        chave.setFuncionalidadeAdministrativo(funcionalidade);
        return chave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChaveGeradorCodigoAdministrativo that = (ChaveGeradorCodigoAdministrativo) o;

        if (idUnidadeAdministrativa != null ? !idUnidadeAdministrativa.equals(that.idUnidadeAdministrativa) : that.idUnidadeAdministrativa != null)
            return false;
        if (idExercicio != null ? !idExercicio.equals(that.idExercicio) : that.idExercicio != null) return false;
        return funcionalidadeAdministrativo == that.funcionalidadeAdministrativo;

    }

    @Override
    public int hashCode() {
        int result = idUnidadeAdministrativa != null ? idUnidadeAdministrativa.hashCode() : 0;
        result = 31 * result + (idExercicio != null ? idExercicio.hashCode() : 0);
        result = 31 * result + (funcionalidadeAdministrativo != null ? funcionalidadeAdministrativo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChaveGeradorCodigoAdministrativo{" +
            "idUnidadeAdministrativa=" + idUnidadeAdministrativa +
            ", idExercicio=" + idExercicio +
            ", funcionalidadeAdministrativo=" + funcionalidadeAdministrativo +
            '}';
    }
}
