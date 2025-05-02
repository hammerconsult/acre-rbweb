package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContrato;

import java.math.BigDecimal;

public class ExecucaoContratoCorrecaoVO {

    private ExecucaoContrato execucaoContrato;
    private BigDecimal valorEstornado;
    private ExecucaoContratoCorrecaoOrigemVO origemRelacionadaVO;
    private Boolean origemCorreta;
    private Boolean selecionada;

    public ExecucaoContratoCorrecaoVO() {
        origemCorreta = false;
        selecionada = false;
        valorEstornado = BigDecimal.ZERO;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public Boolean getOrigemCorreta() {
        return origemCorreta;
    }

    public void setOrigemCorreta(Boolean origemCorreta) {
        this.origemCorreta = origemCorreta;
    }

    public Boolean getSelecionada() {
        return selecionada;
    }

    public void setSelecionada(Boolean selecionada) {
        this.selecionada = selecionada;
    }

    public ExecucaoContratoCorrecaoOrigemVO getOrigemRelacionadaVO() {
        return origemRelacionadaVO;
    }

    public void setOrigemRelacionadaVO(ExecucaoContratoCorrecaoOrigemVO origemRelacionadaVO) {
        this.origemRelacionadaVO = origemRelacionadaVO;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public BigDecimal getValorLiquidoExecucao(){
        return execucaoContrato.getValor().subtract(valorEstornado);
    }
}
