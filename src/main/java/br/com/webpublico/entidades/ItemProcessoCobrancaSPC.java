package br.com.webpublico.entidades;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemProcessoCobrancaSPC extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ProcessoCobrancaSPC processoCobrancaSPC;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorDesconto;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorCorrecao;
    private BigDecimal valorHonorarios;
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private StatusSPC statusSPC;

    @Transient
    private ResultadoParcela resultadoParcela;

    public ItemProcessoCobrancaSPC() {
        super();
        this.valorImposto = BigDecimal.ZERO;
        this.valorTaxa = BigDecimal.ZERO;
        this.valorDesconto = BigDecimal.ZERO;
        this.valorJuros = BigDecimal.ZERO;
        this.valorMulta = BigDecimal.ZERO;
        this.valorCorrecao = BigDecimal.ZERO;
        this.valorHonorarios = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
        this.statusSPC = StatusSPC.AGUARDANDO_ENVIO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCobrancaSPC getProcessoCobrancaSPC() {
        return processoCobrancaSPC;
    }

    public void setProcessoCobrancaSPC(ProcessoCobrancaSPC processoCobrancaSPC) {
        this.processoCobrancaSPC = processoCobrancaSPC;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusSPC getStatusSPC() {
        return statusSPC;
    }

    public void setStatusSPC(StatusSPC statusSPC) {
        this.statusSPC = statusSPC;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public enum StatusSPC {
        AGUARDANDO_ENVIO("Aguardando envio"),
        FALHA_INCLUSAO("Falha ao incluir"),
        INCLUIDO("Incluído"),
        EXCLUIDO("Excluído"),
        FALHA_EXCLUSAO("Falha ao excluir");

        private final String descricao;

        StatusSPC(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
