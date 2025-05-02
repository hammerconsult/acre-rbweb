package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;

import java.util.Date;

public class FiltroPrevidenciaContribuicao {

    private VinculoFP vinculoFP;
    private Date inicio;
    private Date termino;
    private MatriculaFP matriculaFP;
    private String contrato;

    public FiltroPrevidenciaContribuicao() {
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }
}
