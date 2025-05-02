package br.com.webpublico.entidades;


import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoFonteItem extends SuperEntidade implements Comparable<ExecucaoProcessoFonteItem> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo Fonte")
    private ExecucaoProcessoFonte execucaoProcessoFonte;

    @ManyToOne
    @Etiqueta("Execução Processo Item")
    private ExecucaoProcessoItem execucaoProcessoItem;

    @ManyToOne
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;

    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ExecucaoProcessoFonteItem() {
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

    public ExecucaoProcessoFonte getExecucaoProcessoFonte() {
        return execucaoProcessoFonte;
    }

    public void setExecucaoProcessoFonte(ExecucaoProcessoFonte execucaoProcessoFonte) {
        this.execucaoProcessoFonte = execucaoProcessoFonte;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
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


    public Boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public Boolean hasValor() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoProcessoFonteItem o) {
        try {
            return ComparisonChain.start()
                .compare(getExecucaoProcessoItem().getItemProcessoCompra().getNumero(), o.getExecucaoProcessoItem().getItemProcessoCompra().getNumero())
                .result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
