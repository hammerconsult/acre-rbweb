package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class ExecucaoContratoCorrecaoOrigemVO implements Serializable {

    private Long idOrigem;
    private Date dataLancamento;
    private String descricaoOrigem;
    private OrigemSaldoItemContrato origem;
    private OperacaoSaldoItemContrato operacaoOrigem;
    private BigDecimal valor;
    private Boolean origemCorreta;
    private Boolean selecionada;

    public ExecucaoContratoCorrecaoOrigemVO() {
        origemCorreta = false;
        selecionada = false;
        valor = BigDecimal.ZERO;
    }

    public Boolean getOrigemCorreta() {
        return origemCorreta;
    }

    public void setOrigemCorreta(Boolean origemCorreta) {
        this.origemCorreta = origemCorreta;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public OperacaoSaldoItemContrato getOperacaoOrigem() {
        return operacaoOrigem;
    }

    public void setOperacaoOrigem(OperacaoSaldoItemContrato operacaoOrigem) {
        this.operacaoOrigem = operacaoOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getDescricaoOrigem() {
        return descricaoOrigem;
    }

    public void setDescricaoOrigem(String descricaoOrigem) {
        this.descricaoOrigem = descricaoOrigem;
    }

    public OrigemSaldoItemContrato getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoItemContrato origem) {
        this.origem = origem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Boolean getSelecionada() {
        return selecionada;
    }

    public void setSelecionada(Boolean selecionada) {
        this.selecionada = selecionada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecucaoContratoCorrecaoOrigemVO that = (ExecucaoContratoCorrecaoOrigemVO) o;
        return Objects.equals(idOrigem, that.idOrigem) && origem == that.origem;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrigem, origem);
    }
}
