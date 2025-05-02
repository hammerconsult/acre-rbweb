package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ContratoAprovacaoVO {

    private BigDecimal saldoAtualContrato;
    private Boolean checkValorContrato;
    private BigDecimal valorAtualContrato;
    private Date terminoVigencia;
    private Boolean checkTerminoVigencia;
    private Boolean checkItens;
    private Date terminoExecucao;
    private Boolean checkTerminoExecucao;
    private List<ItemContratoAprovacaoVO> itensAprovacao;

    public ContratoAprovacaoVO() {
        itensAprovacao = Lists.newArrayList();
        checkValorContrato = false;
        checkTerminoVigencia = false;
        checkTerminoExecucao = false;
        checkItens = false;
    }

    public BigDecimal getSaldoAtualContrato() {
        return saldoAtualContrato;
    }

    public void setSaldoAtualContrato(BigDecimal saldoAtualContrato) {
        this.saldoAtualContrato = saldoAtualContrato;
    }

    public Boolean getCheckValorContrato() {
        return checkValorContrato;
    }

    public void setCheckValorContrato(Boolean checkValorContrato) {
        this.checkValorContrato = checkValorContrato;
    }

    public BigDecimal getValorAtualContrato() {
        return valorAtualContrato;
    }

    public void setValorAtualContrato(BigDecimal valorAtualContrato) {
        this.valorAtualContrato = valorAtualContrato;
    }

    public Date getTerminoVigencia() {
        return terminoVigencia;
    }

    public void setTerminoVigencia(Date terminoVigencia) {
        this.terminoVigencia = terminoVigencia;
    }

    public Boolean getCheckTerminoVigencia() {
        return checkTerminoVigencia;
    }

    public void setCheckTerminoVigencia(Boolean checkTerminoVigencia) {
        this.checkTerminoVigencia = checkTerminoVigencia;
    }

    public Date getTerminoExecucao() {
        return terminoExecucao;
    }

    public void setTerminoExecucao(Date terminoExecucao) {
        this.terminoExecucao = terminoExecucao;
    }

    public Boolean getCheckTerminoExecucao() {
        return checkTerminoExecucao;
    }

    public void setCheckTerminoExecucao(Boolean checkTerminoExecucao) {
        this.checkTerminoExecucao = checkTerminoExecucao;
    }

    public List<ItemContratoAprovacaoVO> getItensAprovacao() {
        return itensAprovacao;
    }

    public void setItensAprovacao(List<ItemContratoAprovacaoVO> itensAprovacao) {
        this.itensAprovacao = itensAprovacao;
    }

    public Boolean getCheckItens() {
        return checkItens;
    }

    public void setCheckItens(Boolean checkItens) {
        this.checkItens = checkItens;
    }

    public boolean isAprovacaoConferida(){
        return checkValorContrato && checkTerminoVigencia && checkTerminoExecucao && checkItens;
    }
}
