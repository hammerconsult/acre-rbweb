package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.dtos.EntidadeConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.dtos.IsencaoAcrescimosParcelaDTO;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Conta Corrente")
@Entity
@Audited
public class IsencaoAcrescimoParcela implements EntidadeConsultaDebitos {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParcelaValorDivida parcela;
    @ManyToOne
    private ProcessoIsencaoAcrescimos processoIsencaoAcrescimos;
    private String referenciaOriginal;
    @ManyToOne
    private Divida dividaOriginal;
    @ManyToOne
    private Exercicio exercicio;
    private String tipoDebitoOriginal;
    private String numeroParcelaOriginal;
    private String sdOriginal;
    private Date vencimentoOriginal;
    private BigDecimal valorImpostoOriginal;
    private BigDecimal valorTaxaOriginal;
    private BigDecimal valorDescontoOriginal;
    private BigDecimal valorJurosOriginal;
    private BigDecimal valorJurosDeduzido;
    private BigDecimal valorMultaOriginal;
    private BigDecimal valorMultaDeduzido;
    private BigDecimal valorCorrecaoOriginal;
    private BigDecimal valorCorrecaoDeduzido;
    private BigDecimal valorHonorariosOriginal;
    private BigDecimal valorHonorariosAtualizado;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcelaOriginal;

    public IsencaoAcrescimoParcela() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public ProcessoIsencaoAcrescimos getProcessoIsencaoAcrescimos() {
        return processoIsencaoAcrescimos;
    }

    public void setProcessoIsencaoAcrescimos(ProcessoIsencaoAcrescimos processoIsencaoAcrescimos) {
        this.processoIsencaoAcrescimos = processoIsencaoAcrescimos;
    }

    public BigDecimal getValorJurosOriginal() {
        if (valorJurosOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorJurosOriginal;
    }

    public void setValorJurosOriginal(BigDecimal valorJurosOriginal) {
        this.valorJurosOriginal = valorJurosOriginal;
    }

    public BigDecimal getValorMultaOriginal() {
        if (valorMultaOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorMultaOriginal;
    }

    public void setValorMultaOriginal(BigDecimal valorMultaOriginal) {
        this.valorMultaOriginal = valorMultaOriginal;
    }

    public BigDecimal getValorCorrecaoOriginal() {
        if (valorCorrecaoOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorCorrecaoOriginal;
    }

    public void setValorCorrecaoOriginal(BigDecimal valorCorrecaoOriginal) {
        this.valorCorrecaoOriginal = valorCorrecaoOriginal;
    }

    public BigDecimal getValorHonorariosOriginal() {
        if (valorHonorariosOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorHonorariosOriginal;
    }

    public void setValorHonorariosOriginal(BigDecimal valorHonorariosOriginal) {
        this.valorHonorariosOriginal = valorHonorariosOriginal;
    }

    public String getReferenciaOriginal() {
        return referenciaOriginal;
    }

    public void setReferenciaOriginal(String referenciaOriginal) {
        this.referenciaOriginal = referenciaOriginal;
    }

    public Divida getDividaOriginal() {
        return dividaOriginal;
    }

    public void setDividaOriginal(Divida dividaOriginal) {
        this.dividaOriginal = dividaOriginal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getTipoDebitoOriginal() {
        return tipoDebitoOriginal;
    }

    public void setTipoDebitoOriginal(String tipoDebitoOriginal) {
        this.tipoDebitoOriginal = tipoDebitoOriginal;
    }

    public String getNumeroParcelaOriginal() {
        return numeroParcelaOriginal;
    }

    public void setNumeroParcelaOriginal(String numeroParcelaOriginal) {
        this.numeroParcelaOriginal = numeroParcelaOriginal;
    }

    public String getSdOriginal() {
        return sdOriginal;
    }

    public void setSdOriginal(String sdOriginal) {
        this.sdOriginal = sdOriginal;
    }

    public Date getVencimentoOriginal() {
        return vencimentoOriginal;
    }

    public void setVencimentoOriginal(Date vencimentoOriginal) {
        this.vencimentoOriginal = vencimentoOriginal;
    }

    public BigDecimal getValorImpostoOriginal() {
        if (valorImpostoOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorImpostoOriginal;
    }

    public void setValorImpostoOriginal(BigDecimal valorImpostoOriginal) {
        this.valorImpostoOriginal = valorImpostoOriginal;
    }

    public BigDecimal getValorTaxaOriginal() {
        if (valorImpostoOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorTaxaOriginal;
    }

    public void setValorTaxaOriginal(BigDecimal valorTaxaOriginal) {
        this.valorTaxaOriginal = valorTaxaOriginal;
    }

    public BigDecimal getValorDescontoOriginal() {
        if (valorDescontoOriginal == null) {
            return BigDecimal.ZERO;
        }
        return valorDescontoOriginal;
    }

    public void setValorDescontoOriginal(BigDecimal valorDescontoOriginal) {
        this.valorDescontoOriginal = valorDescontoOriginal;
    }

    public SituacaoParcela getSituacaoParcelaOriginal() {
        return situacaoParcelaOriginal;
    }

    public void setSituacaoParcelaOriginal(SituacaoParcela situacaoParcelaOriginal) {
        this.situacaoParcelaOriginal = situacaoParcelaOriginal;
    }

    public BigDecimal getValorJurosDeduzido() {
        return valorJurosDeduzido;
    }

    public void setValorJurosDeduzido(BigDecimal valorJurosDeduzido) {
        this.valorJurosDeduzido = valorJurosDeduzido;
    }

    public BigDecimal getValorMultaDeduzido() {
        if (valorMultaDeduzido == null) {
            return BigDecimal.ZERO;
        }
        return valorMultaDeduzido;
    }

    public void setValorMultaDeduzido(BigDecimal valorMultaDeduzido) {
        this.valorMultaDeduzido = valorMultaDeduzido;
    }

    public BigDecimal getValorCorrecaoDeduzido() {
        if (valorCorrecaoDeduzido == null) {
            return BigDecimal.ZERO;
        }
        return valorCorrecaoDeduzido;
    }

    public void setValorCorrecaoDeduzido(BigDecimal valorCorrecaoDeduzido) {
        this.valorCorrecaoDeduzido = valorCorrecaoDeduzido;
    }

    public BigDecimal getValorHonorariosAtualizado() {
        return valorHonorariosAtualizado;
    }

    public void setValorHonorariosAtualizado(BigDecimal valorHonorariosAtualizado) {
        this.valorHonorariosAtualizado = valorHonorariosAtualizado;
    }

    public BigDecimal getValorTotalOriginal() {
        return getValorImpostoOriginal().add(getValorTaxaOriginal()).add(getValorJurosOriginal()).add(getValorMultaOriginal())
            .add(getValorCorrecaoOriginal()).add(getValorHonorariosOriginal()).subtract(getValorDescontoOriginal());
    }

    public BigDecimal getValorTotalDeduzido() {
        return getValorImpostoOriginal().add(getValorTaxaOriginal()).add(getValorJurosDeduzido()).add(getValorMultaDeduzido())
            .add(getValorCorrecaoDeduzido()).add(getValorHonorariosAtualizado()).subtract(getValorDescontoOriginal());
    }

    public BigDecimal getValorTotalDeduzidoSemHonorarios() {
        return getValorImpostoOriginal().add(getValorTaxaOriginal()).add(getValorJurosDeduzido()).add(getValorMultaDeduzido())
            .add(getValorCorrecaoDeduzido()).subtract(getValorDescontoOriginal());
    }

    @Override
    public IsencaoAcrescimosParcelaDTO toDto() {
        IsencaoAcrescimosParcelaDTO isencao = new IsencaoAcrescimosParcelaDTO();
        isencao.setValorJurosOriginal(getValorJurosOriginal());
        isencao.setValorMultaOriginal(getValorMultaOriginal());
        isencao.setValorCorrecaoOriginal(getValorCorrecaoOriginal());
        isencao.setValorHonorariosOriginal(getValorHonorariosOriginal());
        isencao.setValorImpostoOriginal(getValorImpostoOriginal());
        isencao.setValorTaxaOriginal(getValorTaxaOriginal());
        isencao.setValorDescontoOriginal(getValorDescontoOriginal());
        isencao.setValorJurosDeduzido(getValorJurosDeduzido());
        isencao.setValorMultaDeduzido(getValorMultaDeduzido());
        isencao.setValorCorrecaoDeduzido(getValorCorrecaoDeduzido());
        isencao.setValorHonorariosAtualizado(getValorHonorariosAtualizado());
        isencao.setDataLancamento(getProcessoIsencaoAcrescimos().getDataLancamento());
        isencao.setDataIsencao(getProcessoIsencaoAcrescimos().getDataInicio());
        isencao.setDataInicio(getProcessoIsencaoAcrescimos().getDataInicio());
        isencao.setDataFim(getProcessoIsencaoAcrescimos().getDataFim());
        isencao.setIsentaJuros(getProcessoIsencaoAcrescimos().getIsentaJuros());
        isencao.setIsentaMulta(getProcessoIsencaoAcrescimos().getIsentaMulta());
        isencao.setIsentaCorrecao(getProcessoIsencaoAcrescimos().getIsentaCorrecao());
        isencao.setSituacao(getProcessoIsencaoAcrescimos().getSituacao().toDto());
        isencao.setTipoDeducao(getProcessoIsencaoAcrescimos().getTipoDeducao() != null ? getProcessoIsencaoAcrescimos().getTipoDeducao().toDto() : null);
        isencao.setPercentualDeducao(getProcessoIsencaoAcrescimos().getPercentualDeducao());
        isencao.setLancouDeducoes(getProcessoIsencaoAcrescimos().getLancouDeducoes());
        isencao.setExercicioCorrecao(getProcessoIsencaoAcrescimos().getExercicioCorrecao() != null ? getProcessoIsencaoAcrescimos().getExercicioCorrecao().getAno() : 0);
        return isencao;
    }
}
