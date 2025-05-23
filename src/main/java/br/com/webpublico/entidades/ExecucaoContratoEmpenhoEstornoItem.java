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
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Execução Contrato Empenho Estorno Item")
@Table(name = "EXECUCAOCONTRATOEMPESTITEM")
public class ExecucaoContratoEmpenhoEstornoItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato Empenho Estorno")
    private ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEst;

    @ManyToOne
    @Etiqueta("Execução Contrato Item")
    private ExecucaoContratoItem execucaoContratoItem;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Obrigatorio
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Obrigatorio
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    public ExecucaoContratoEmpenhoEstornoItem() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContratoEmpenhoEstorno getExecucaoContratoEmpenhoEst() {
        return execucaoContratoEmpenhoEst;
    }

    public void setExecucaoContratoEmpenhoEst(ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEst) {
        this.execucaoContratoEmpenhoEst = execucaoContratoEmpenhoEst;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
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
        if (valorTotal != null) {
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        try {
            return getExecucaoContratoItem().getItemContrato().toString();
        } catch (NullPointerException e) {
            return "";
        }
    }
}
