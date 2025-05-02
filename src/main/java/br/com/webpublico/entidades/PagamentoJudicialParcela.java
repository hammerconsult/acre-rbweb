package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 06/04/15
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class PagamentoJudicialParcela implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private Exercicio exercicio;
    private String parcela;
    private Integer sd;
    private Date vencimento;
    private BigDecimal valorOriginal;
    private BigDecimal valorHonorarios;
    private BigDecimal valorCompensado;
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacao;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal desconto;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private String referencia;
    private String tipoCadastro;
    private String tipoDeDebito;
    private BigDecimal valorResidual;
    @ManyToOne
    private PagamentoJudicial pagamentoJudicial;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    private Calculo.TipoCalculo tipoCalculo;
    private String sequencia;
    private Long quantidadeParcela;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;

    public PagamentoJudicialParcela() {
        valorOriginal = BigDecimal.ZERO;
        valorHonorarios = BigDecimal.ZERO;
        valorResidual = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }


    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public Integer getSd() {
        return sd;
    }

    public void setSd(Integer sd) {
        this.sd = sd;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorCompensado() {
        return valorCompensado;
    }

    public void setValorCompensado(BigDecimal valorCompensado) {
        this.valorCompensado = valorCompensado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public BigDecimal getValorResidual() {
        return valorResidual;
    }

    public void setValorResidual(BigDecimal valorResidual) {
        this.valorResidual = valorResidual;
    }

    public SituacaoParcela getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoParcela situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getTipoDeDebito() {
        return tipoDeDebito;
    }

    public PagamentoJudicial getPagamentoJudicial() {
        return pagamentoJudicial;
    }

    public void setPagamentoJudicial(PagamentoJudicial pagamentoJudicial) {
        this.pagamentoJudicial = pagamentoJudicial;
    }


    public void setTipoDeDebito(String tipoDeDebito) {
        this.tipoDeDebito = tipoDeDebito;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

}


