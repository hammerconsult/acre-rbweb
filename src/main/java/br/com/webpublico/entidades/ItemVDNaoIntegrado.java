package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemVDNaoIntegrado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ItemValorDivida itemValorDivida;
    private BigDecimal valor;
    private BigDecimal acrescimo;
    private BigDecimal desconto;
    @Enumerated(EnumType.STRING)
    private TipoIntegracao tipoIntegracao;
    private Boolean resolvido;
    private String motivo;
    @ManyToOne
    private SituacaoParcelaValorDivida situacaoParcela;
    @ManyToOne
    private LoteBaixa loteBaixa;
    private Boolean dividaAtiva;

    public ItemVDNaoIntegrado() {
        criadoEm = System.nanoTime();
        resolvido = false;
        dividaAtiva = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemValorDivida getItemValorDivida() {
        return itemValorDivida;
    }

    public void setItemValorDivida(ItemValorDivida itemValorDivida) {
        this.itemValorDivida = itemValorDivida;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoIntegracao getTipoIntegracao() {
        return tipoIntegracao;
    }

    public void setTipoIntegracao(TipoIntegracao tipoIntegracao) {
        this.tipoIntegracao = tipoIntegracao;
    }

    public Boolean getResolvido() {
        return resolvido;
    }

    public void setResolvido(Boolean resolvido) {
        this.resolvido = resolvido;
    }

    public BigDecimal getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(BigDecimal acrescimo) {
        this.acrescimo = acrescimo;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public SituacaoParcelaValorDivida getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcelaValorDivida situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public boolean isDividaAtiva() {
        return dividaAtiva != null ? dividaAtiva : false;
    }

    public void setDividaAtiva(boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }
}
