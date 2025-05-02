package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Parcelas Bloqueio Judicial")
public class ParcelaBloqueioJudicial extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Bloqueio Judicial")
    private BloqueioJudicial bloqueioJudicial;
    private Long idParcela;
    private BigDecimal imposto;
    private BigDecimal impostoOriginada;
    private BigDecimal taxa;
    private BigDecimal taxaOriginada;
    private BigDecimal juros;
    private BigDecimal jurosOriginada;
    private BigDecimal multa;
    private BigDecimal multaOriginada;
    private BigDecimal correcao;
    private BigDecimal correcaoOriginada;
    private BigDecimal honorarios;
    private BigDecimal honorariosOriginada;
    private BigDecimal desconto;
    private BigDecimal descontoOriginada;
    private BigDecimal total;
    private BigDecimal totalOriginada;
    private Boolean quitada;

    @Transient
    private BigDecimal valor;

    public ParcelaBloqueioJudicial() {
        this.imposto = BigDecimal.ZERO;
        this.impostoOriginada = BigDecimal.ZERO;
        this.taxa = BigDecimal.ZERO;
        this.taxaOriginada = BigDecimal.ZERO;
        this.juros = BigDecimal.ZERO;
        this.jurosOriginada = BigDecimal.ZERO;
        this.multa = BigDecimal.ZERO;
        this.multaOriginada = BigDecimal.ZERO;
        this.correcao = BigDecimal.ZERO;
        this.correcaoOriginada = BigDecimal.ZERO;
        this.desconto = BigDecimal.ZERO;
        this.descontoOriginada = BigDecimal.ZERO;
        this.honorarios = BigDecimal.ZERO;
        this.honorariosOriginada = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.totalOriginada = BigDecimal.ZERO;
        this.quitada = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloqueioJudicial getBloqueioJudicial() {
        return bloqueioJudicial;
    }

    public void setBloqueioJudicial(BloqueioJudicial bloqueioJudicial) {
        this.bloqueioJudicial = bloqueioJudicial;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getImpostoOriginada() {
        return impostoOriginada;
    }

    public void setImpostoOriginada(BigDecimal impostoOriginada) {
        this.impostoOriginada = impostoOriginada;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getTaxaOriginada() {
        return taxaOriginada;
    }

    public void setTaxaOriginada(BigDecimal taxaOriginada) {
        this.taxaOriginada = taxaOriginada;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getJurosOriginada() {
        return jurosOriginada;
    }

    public void setJurosOriginada(BigDecimal jurosOriginada) {
        this.jurosOriginada = jurosOriginada;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getMultaOriginada() {
        return multaOriginada;
    }

    public void setMultaOriginada(BigDecimal multaOriginada) {
        this.multaOriginada = multaOriginada;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getCorrecaoOriginada() {
        return correcaoOriginada;
    }

    public void setCorrecaoOriginada(BigDecimal correcaoOriginada) {
        this.correcaoOriginada = correcaoOriginada;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getHonorariosOriginada() {
        return honorariosOriginada;
    }

    public void setHonorariosOriginada(BigDecimal honorariosOriginada) {
        this.honorariosOriginada = honorariosOriginada;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getDescontoOriginada() {
        return descontoOriginada;
    }

    public void setDescontoOriginada(BigDecimal descontoOriginada) {
        this.descontoOriginada = descontoOriginada;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalOriginada() {
        return totalOriginada;
    }

    public void setTotalOriginada(BigDecimal totalOriginada) {
        this.totalOriginada = totalOriginada;
    }

    public Boolean getQuitada() {
        return quitada;
    }

    public void setQuitada(Boolean quitada) {
        this.quitada = quitada;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
