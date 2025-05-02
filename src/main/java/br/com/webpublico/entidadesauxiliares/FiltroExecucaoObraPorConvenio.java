package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ConvenioReceita;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Obra;

import java.io.Serializable;

/**
 * Created by wellington on 23/05/2017.
 */
public class FiltroExecucaoObraPorConvenio implements Serializable {

    private Obra obra;
    private ConvenioReceita convenioReceita;
    private Integer numeroContrato;
    private Exercicio exercicioContrato;

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public Integer getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Integer numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Exercicio getExercicioContrato() {
        return exercicioContrato;
    }

    public void setExercicioContrato(Exercicio exercicioContrato) {
        this.exercicioContrato = exercicioContrato;
    }
}
