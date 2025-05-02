package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.ExecucaoProcesso;
import br.com.webpublico.enums.TipoResumoExecucao;

public class FiltroResumoExecucaoVO {

   private Long idProcesso;
   private Contrato contrato;
   private ExecucaoContrato execucaoContrato;
   private ExecucaoProcesso execucaoProcesso;
   private TipoResumoExecucao tipoResumoExecucao;

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public FiltroResumoExecucaoVO(TipoResumoExecucao tipoResumoExecucao) {
        this.tipoResumoExecucao = tipoResumoExecucao;
    }

    public FiltroResumoExecucaoVO(Contrato contrato, ExecucaoContrato execucaoContrato) {
        this.contrato = contrato;
        this.execucaoContrato = execucaoContrato;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public TipoResumoExecucao getTipoResumoExecucao() {
        return tipoResumoExecucao;
    }

    public void setTipoResumoExecucao(TipoResumoExecucao tipoResumoExecucao) {
        this.tipoResumoExecucao = tipoResumoExecucao;
    }
}
