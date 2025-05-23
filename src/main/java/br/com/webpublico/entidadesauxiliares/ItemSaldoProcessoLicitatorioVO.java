package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ItemPropostaFornecedor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemSaldoProcessoLicitatorioVO {

    private SaldoProcessoLicitatorioVO saldoVO;
    private ItemPropostaFornecedor itemPropostaFornecedor;
    private ItemProcessoDeCompra itemProcessoCompra;
    private Long idFornecedor;
    private BigDecimal quantidadeProcesso;
    private BigDecimal quantidadeAta;
    private BigDecimal quantidadeExecutadaSemContrato;
    private BigDecimal quantidadeEstornadaExecucaoSemContrato;
    private BigDecimal quantidadeContratada;
    private BigDecimal valorUnitario;
    private BigDecimal valorProcesso;
    private BigDecimal valorAta;
    private BigDecimal valorContratado;
    private BigDecimal valorExecutadoSemContrato;
    private BigDecimal valorEstornadoExecucaoSemContrato;

    public ItemSaldoProcessoLicitatorioVO() {
        quantidadeProcesso = BigDecimal.ZERO;
        quantidadeAta = BigDecimal.ZERO;
        quantidadeExecutadaSemContrato = BigDecimal.ZERO;
        quantidadeEstornadaExecucaoSemContrato = BigDecimal.ZERO;
        quantidadeContratada = BigDecimal.ZERO;
        valorAta = BigDecimal.ZERO;
        valorExecutadoSemContrato = BigDecimal.ZERO;
        valorEstornadoExecucaoSemContrato = BigDecimal.ZERO;
        valorContratado = BigDecimal.ZERO;
        valorProcesso = BigDecimal.ZERO;
    }

    public BigDecimal getValorProcesso() {
        return valorProcesso;
    }

    public void setValorProcesso(BigDecimal valorProcesso) {
        this.valorProcesso = valorProcesso;
    }

    public BigDecimal getQuantidadeAta() {
        return quantidadeAta;
    }

    public void setQuantidadeAta(BigDecimal quantidadeAta) {
        this.quantidadeAta = quantidadeAta;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    public BigDecimal getQuantidadeProcesso() {
        return quantidadeProcesso;
    }

    public void setQuantidadeProcesso(BigDecimal quantidadeProcesso) {
        this.quantidadeProcesso = quantidadeProcesso;
    }

    public BigDecimal getQuantidadeContratada() {
        return quantidadeContratada;
    }

    public void setQuantidadeContratada(BigDecimal quantidadeContratada) {
        this.quantidadeContratada = quantidadeContratada;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorAta() {
        return valorAta;
    }

    public void setValorAta(BigDecimal valorAta) {
        this.valorAta = valorAta;
    }

    public BigDecimal getValorContratado() {
        return valorContratado;
    }

    public void setValorContratado(BigDecimal valorContratado) {
        this.valorContratado = valorContratado;
    }

    public BigDecimal getQuantidadeExecutadaSemContrato() {
        return quantidadeExecutadaSemContrato;
    }

    public void setQuantidadeExecutadaSemContrato(BigDecimal quantidadeExecutadaSemContrato) {
        this.quantidadeExecutadaSemContrato = quantidadeExecutadaSemContrato;
    }

    public BigDecimal getQuantidadeEstornadaExecucaoSemContrato() {
        return quantidadeEstornadaExecucaoSemContrato;
    }

    public void setQuantidadeEstornadaExecucaoSemContrato(BigDecimal quantidadeEstornadaExecucaoSemContrato) {
        this.quantidadeEstornadaExecucaoSemContrato = quantidadeEstornadaExecucaoSemContrato;
    }

    public BigDecimal getValorExecutadoSemContrato() {
        return valorExecutadoSemContrato;
    }

    public void setValorExecutadoSemContrato(BigDecimal valorExecutadoSemContrato) {
        this.valorExecutadoSemContrato = valorExecutadoSemContrato;
    }

    public BigDecimal getValorEstornadoExecucaoSemContrato() {
        return valorEstornadoExecucaoSemContrato;
    }

    public void setValorEstornadoExecucaoSemContrato(BigDecimal valorEstornadoExecucaoSemContrato) {
        this.valorEstornadoExecucaoSemContrato = valorEstornadoExecucaoSemContrato;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public BigDecimal getQuantidadeDisponivel(){
        if (saldoVO.getTipoProcesso().isAta()){
            return quantidadeAta.subtract(quantidadeExecutadaSemContrato).subtract(quantidadeContratada).add(quantidadeEstornadaExecucaoSemContrato);
        }
        return quantidadeProcesso.subtract(quantidadeExecutadaSemContrato).subtract(quantidadeContratada).add(quantidadeEstornadaExecucaoSemContrato);
    }

    public BigDecimal getValorDisponivel(){
        if (saldoVO.getTipoProcesso().isAta()){
            return valorAta.subtract(valorExecutadoSemContrato).subtract(valorContratado).add(valorEstornadoExecucaoSemContrato);
        }
        return valorProcesso.subtract(valorExecutadoSemContrato).subtract(valorContratado).add(valorEstornadoExecucaoSemContrato);
    }

    public BigDecimal calcularValorTotalAta() {
        try {
            if (getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade()) {
                BigDecimal total = getQuantidadeAta().multiply(getValorUnitario());
                return total.setScale(2, RoundingMode.HALF_EVEN);
            }
            return getValorUnitario();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public SaldoProcessoLicitatorioVO getSaldoVO() {
        return saldoVO;
    }

    public void setSaldoVO(SaldoProcessoLicitatorioVO saldoVO) {
        this.saldoVO = saldoVO;
    }
}
