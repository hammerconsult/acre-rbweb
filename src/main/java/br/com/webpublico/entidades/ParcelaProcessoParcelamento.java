package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Fabio
 */
@Entity
@Audited
@Table(name = "PARCELAPARCELAMENTO")
@GrupoDiagrama(nome = "Tributario")
public class ParcelaProcessoParcelamento implements Serializable, Comparable<ParcelaProcessoParcelamento> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoParcelamento processoParcelamento;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private BigDecimal imposto;
    private BigDecimal descontoImposto;
    private BigDecimal taxa;
    private BigDecimal descontoTaxa;
    private BigDecimal juros;
    private BigDecimal descontoJuros;
    private BigDecimal multa;
    private BigDecimal descontoMulta;
    private BigDecimal correcao;
    private BigDecimal descontoCorrecao;
    private BigDecimal honorarios;
    private BigDecimal descontoHonorarios;
    @Transient
    private Long criadoEm = System.nanoTime();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public BigDecimal getImposto() {
        return imposto != null ? imposto :
            BigDecimal.ZERO;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa != null ? taxa :
            BigDecimal.ZERO;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros :
            BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa :
            BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao != null ? correcao :
            BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios != null ? honorarios :
            BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getTotalSemDesconto() {
        BigDecimal total = getImposto().add(getTaxa()).add(getJuros()).add(getMulta()).add(getCorrecao()).add(getHonorarios());
        //if (total.compareTo(processoParcelamento.getTotalGeral()) < 0) {
            total = total.add(getDesconto());
        //}
        return total;
    }

    public BigDecimal getTotalComDesconto() {
        return getTotalSemDesconto().subtract(getDesconto());
    }

    public BigDecimal getDesconto() {
        return getDescontoImposto().add(getDescontoTaxa().add(getDescontoJuros().add(getDescontoMulta().add(getDescontoCorrecao().add(getDescontoHonorarios())))));
    }

    public BigDecimal getDescontoImposto() {
        return descontoImposto != null ? descontoImposto : BigDecimal.ZERO;
    }

    public void setDescontoImposto(BigDecimal descontoImposto) {
        this.descontoImposto = descontoImposto;
    }

    public BigDecimal getDescontoTaxa() {
        return descontoTaxa != null ? descontoTaxa : BigDecimal.ZERO;
    }

    public void setDescontoTaxa(BigDecimal descontoTaxa) {
        this.descontoTaxa = descontoTaxa;
    }

    public BigDecimal getDescontoJuros() {
        return descontoJuros != null ? descontoJuros : BigDecimal.ZERO;
    }

    public void setDescontoJuros(BigDecimal descontoJuros) {
        this.descontoJuros = descontoJuros;
    }

    public BigDecimal getDescontoMulta() {
        return descontoMulta != null ? descontoMulta : BigDecimal.ZERO;
    }

    public void setDescontoMulta(BigDecimal descontoMulta) {
        this.descontoMulta = descontoMulta;
    }

    public BigDecimal getDescontoCorrecao() {
        return descontoCorrecao != null ? descontoCorrecao : BigDecimal.ZERO;
    }

    public void setDescontoCorrecao(BigDecimal descontoCorrecao) {
        this.descontoCorrecao = descontoCorrecao;
    }

    public BigDecimal getDescontoHonorarios() {
        return descontoHonorarios != null ? descontoHonorarios : BigDecimal.ZERO;
    }

    public void setDescontoHonorarios(BigDecimal descontoHonorarios) {
        this.descontoHonorarios = descontoHonorarios;
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
        return "br.com.webpublico.entidades.ItemProcessoParcelamento[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ParcelaProcessoParcelamento o) {
        return parcelaValorDivida.getVencimento().compareTo(o.getParcelaValorDivida().getVencimento());
    }
}
