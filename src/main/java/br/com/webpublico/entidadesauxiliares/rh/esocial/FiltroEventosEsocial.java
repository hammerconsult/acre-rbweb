package br.com.webpublico.entidadesauxiliares.rh.esocial;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.esocial.enums.SituacaoESocial;

import java.util.Date;

public class FiltroEventosEsocial {
    private Mes mes;
    private Date inicioVigencia;
    private Date finalVigencia;
    private Exercicio exercicio;
    private VinculoFP vinculoFP;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private SituacaoESocial situacaoESocial;
    private Entidade empregador;


    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public SituacaoESocial getSituacaoESocial() {
        return situacaoESocial;
    }

    public void setSituacaoESocial(SituacaoESocial situacaoESocial) {
        this.situacaoESocial = situacaoESocial;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Entidade getEmpregador() {
        return empregador;
    }

    public void setEmpregador(Entidade empregador) {
        this.empregador = empregador;
    }

    public boolean isFiltroPreenchido() {
        return mes != null || inicioVigencia != null || finalVigencia != null || exercicio != null || vinculoFP != null || tipoFolhaDePagamento != null || situacaoESocial != null;
    }
}
