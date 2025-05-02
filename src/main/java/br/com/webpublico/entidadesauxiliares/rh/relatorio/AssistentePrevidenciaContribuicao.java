package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.PrevidenciaContribuicaoIndividualizada;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistentePrevidenciaContribuicao {

    private VinculoFP vinculoFP;
    private Date inicio;
    private Date termino;
    private List<PrevidenciaContribuicaoIndividualizada> contribuicoes;
    private PrevidenciaContribuicaoIndividualizada contribuicaoIndividualizada;
    private MatriculaFP matriculaFP;
    private String contrato;

    public AssistentePrevidenciaContribuicao() {
        contribuicoes = Lists.newLinkedList();
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

    public List<PrevidenciaContribuicaoIndividualizada> getContribuicoes() {
        return contribuicoes;
    }

    public void setContribuicoes(List<PrevidenciaContribuicaoIndividualizada> contribuicoes) {
        this.contribuicoes = contribuicoes;
    }

    public PrevidenciaContribuicaoIndividualizada getContribuicaoIndividualizada() {
        return contribuicaoIndividualizada;
    }

    public void setContribuicaoIndividualizada(PrevidenciaContribuicaoIndividualizada contribuicaoIndividualizada) {
        this.contribuicaoIndividualizada = contribuicaoIndividualizada;
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
