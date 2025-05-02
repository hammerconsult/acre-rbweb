package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by rodolfo on 13/06/18.
 */
public class LancamentoGeralMensal {

    private Exercicio exercicio;
    private TipoMovimentoMensal tipoMovimentoMensal;
    private Date competenciaInicial;
    private Date competenciaFinal;
    private String cmcInicial;
    private String cmcFinal;
    private Boolean selecionarNotas;

    public LancamentoGeralMensal() {
        this.tipoMovimentoMensal = TipoMovimentoMensal.NORMAL;
        this.selecionarNotas = Boolean.FALSE;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getCompetenciaInicial() {
        return competenciaInicial;
    }

    public void setCompetenciaInicial(Date competenciaInicial) {
        this.competenciaInicial = competenciaInicial;
    }

    public Date getCompetenciaFinal() {
        return competenciaFinal;
    }

    public void setCompetenciaFinal(Date competenciaFinal) {
        this.competenciaFinal = competenciaFinal;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcIncial) {
        this.cmcInicial = cmcIncial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public TipoMovimentoMensal getTipoMovimentoMensal() {
        return tipoMovimentoMensal;
    }

    public void setTipoMovimentoMensal(TipoMovimentoMensal tipoMovimentoMensal) {
        this.tipoMovimentoMensal = tipoMovimentoMensal;
    }

    public Boolean getSelecionarNotas() {
        return selecionarNotas;
    }

    public void setSelecionarNotas(Boolean selecionarNotas) {
        this.selecionarNotas = selecionarNotas;
    }
}
