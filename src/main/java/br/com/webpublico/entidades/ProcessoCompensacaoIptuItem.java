package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity

@GrupoDiagrama(nome = "Tributario")
@Audited
public class ProcessoCompensacaoIptuItem implements Serializable, Comparable<ProcessoCompensacaoIptuItem> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoCompensacaoIptu processoCompensacaoIptu;
    @ManyToOne
    private Exercicio exercicio;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorDesconto;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorUfm;
    @OneToMany(mappedBy = "processoCompensacaoIptuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoCompensacaoIptuParcela> processoCompensacaoIptuParcelas;
    @Enumerated(EnumType.STRING)
    private TipoItem tipoItem;

    public ProcessoCompensacaoIptuItem() {
        processoCompensacaoIptuParcelas = Lists.newArrayList();
    }

    public List<ProcessoCompensacaoIptuParcela> getProcessoCompensacaoIptuParcelas() {
        return processoCompensacaoIptuParcelas;
    }

    public void setProcessoCompensacaoIptuParcelas(List<ProcessoCompensacaoIptuParcela> processoCompensacaoIptuParcelas) {
        this.processoCompensacaoIptuParcelas = processoCompensacaoIptuParcelas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCompensacaoIptu getProcessoCompensacaoIptu() {
        return processoCompensacaoIptu;
    }

    public void setProcessoCompensacaoIptu(ProcessoCompensacaoIptu processoCompensacaoIptu) {
        this.processoCompensacaoIptu = processoCompensacaoIptu;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValorImposto() {
        return valorImposto != null ? valorImposto : BigDecimal.ZERO;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa != null ? valorTaxa : BigDecimal.ZERO;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto != null ? valorDesconto : BigDecimal.ZERO;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorJuros() {
        return valorJuros != null ? valorJuros : BigDecimal.ZERO;
    }

    public BigDecimal getValorJurosMulta() {
        return getValorJuros().add(getValorMulta());
    }

    public BigDecimal getValorTotal() {
        return getValorImposto().add(getValorTaxa().add(getValorJurosMulta())).subtract(getValorDesconto());
    }

    public BigDecimal getValorAtualizado() {
        return getValorTotal().multiply(getIndiceAtualizacao()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getIndiceAtualizacao() {
        return processoCompensacaoIptu.getUfmProcesso().divide(getValorUfm(), 8, RoundingMode.HALF_UP);
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta != null ? valorMulta : BigDecimal.ZERO;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorUfm() {
        return valorUfm != null ? valorUfm : BigDecimal.ZERO;
    }

    public void setValorUfm(BigDecimal valorUfm) {
        this.valorUfm = valorUfm;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItem tipoItem) {
        this.tipoItem = tipoItem;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoCompensacaoIptuItem[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ProcessoCompensacaoIptuItem o) {
        int i = this.tipoItem.compareTo(o.getTipoItem());
        if (i == 0) {
            i = this.exercicio.getAno().compareTo(o.getExercicio().getAno());
        }
        return i;
    }

    public enum TipoItem {
        INCORRETO, CORRETO;
    }
}
