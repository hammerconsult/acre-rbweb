package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Cálculo de Bloqueio Judicial")
public class CalculoBloqueioJudicial extends Calculo {
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private ProcessoCalcBloqJudicial processoCalcBloqJudicial;
    @ManyToOne
    private Exercicio exercicioParcelaOriginal;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;
    @OneToMany(mappedBy = "calculoBloqueioJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoBloqueioJudicial> itensBloqueio;
    @ManyToOne
    private ParcelaBloqueioJudicial parcelaBloqueioJudicial;
    private BigDecimal valorDiferenca;
    private Long idParcelaOriginal;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    @Transient
    private String referencia;

    public CalculoBloqueioJudicial() {
        this.itensBloqueio = Lists.newArrayList();
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public ProcessoCalcBloqJudicial getProcessoCalcBloqJudicial() {
        return processoCalcBloqJudicial;
    }

    public void setProcessoCalcBloqJudicial(ProcessoCalcBloqJudicial processoCalcBloqJudicial) {
        this.processoCalcBloqJudicial = processoCalcBloqJudicial;
    }

    public Exercicio getExercicioParcelaOriginal() {
        return exercicioParcelaOriginal;
    }

    public void setExercicioParcelaOriginal(Exercicio exercicioParcelaOriginal) {
        this.exercicioParcelaOriginal = exercicioParcelaOriginal;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public List<ItemCalculoBloqueioJudicial> getItensBloqueio() {
        return itensBloqueio;
    }

    public void setItensBloqueio(List<ItemCalculoBloqueioJudicial> itensBloqueio) {
        this.itensBloqueio = itensBloqueio;
    }

    public ParcelaBloqueioJudicial getParcelaBloqueioJudicial() {
        return parcelaBloqueioJudicial;
    }

    public void setParcelaBloqueioJudicial(ParcelaBloqueioJudicial parcelaBloqueioJudicial) {
        this.parcelaBloqueioJudicial = parcelaBloqueioJudicial;
    }

    public BigDecimal getValorDiferenca() {
        return valorDiferenca;
    }

    public void setValorDiferenca(BigDecimal valorDiferenca) {
        this.valorDiferenca = valorDiferenca;
    }

    @Override
    public String getReferencia() {
        return referencia;
    }

    @Override
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Long getIdParcelaOriginal() {
        return idParcelaOriginal;
    }

    public void setIdParcelaOriginal(Long idParcelaOriginal) {
        this.idParcelaOriginal = idParcelaOriginal;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }
}
