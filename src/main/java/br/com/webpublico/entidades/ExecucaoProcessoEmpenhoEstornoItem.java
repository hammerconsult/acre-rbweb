package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
@Table(name = "EXECUCAOPROCESSOEMPESTITEM")
public class ExecucaoProcessoEmpenhoEstornoItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo Empenho Estorno")
    private ExecucaoProcessoEmpenhoEstorno execucaoProcessoEmpenhoEst;

    @ManyToOne
    @Etiqueta("Execução Ata Registro de Preço Item")
    private ExecucaoProcessoItem execucaoProcessoItem;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Obrigatorio
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Obrigatorio
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Transient
    private BigDecimal valorUtilizado;
    @Transient
    private BigDecimal saldoDisponivel;
    @Transient
    private Boolean selecionado;

    public ExecucaoProcessoEmpenhoEstornoItem() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorUtilizado = BigDecimal.ZERO;
        selecionado = Boolean.FALSE;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getValorUtilizado() {
        return valorUtilizado;
    }

    public void setValorUtilizado(BigDecimal valorUtilizado) {
        this.valorUtilizado = valorUtilizado;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcessoEmpenhoEstorno getExecucaoProcessoEmpenhoEstorno() {
        return execucaoProcessoEmpenhoEst;
    }

    public void setExecucaoProcessoEmpenhoEstorno(ExecucaoProcessoEmpenhoEstorno execucaoProcessoEmpenhoEstorno) {
        this.execucaoProcessoEmpenhoEst = execucaoProcessoEmpenhoEstorno;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void calcularValorTotal() {
        setValorTotal(getQuantidade().multiply(getValorUnitario()));
    }

    public void calcularQuantidade() {
        if (getValorTotal().compareTo(BigDecimal.ZERO) >0 && getValorUnitario().compareTo(BigDecimal.ZERO) >0) {
            setQuantidade(getValorTotal().divide(getValorUnitario(), 2, RoundingMode.HALF_EVEN));
        }
    }

    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public boolean hasSaldoDisponivel() {
        return getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        try {
            return getExecucaoProcessoItem().getItemProcessoCompra().toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
