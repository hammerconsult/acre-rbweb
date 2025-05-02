package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 09/05/14
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Ordem Bancária/Transfêrencia Financeira")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "BORDEROTRANSFFINANCEIRA")
public class BorderoTransferenciaFinanceira implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bordero")
    private Bordero bordero;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Transferência Financeira")
    @JoinColumn(name = "TRANSFFINANCEIRA_ID")
    private TransferenciaContaFinanceira transferenciaContaFinanceira;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoItemBordero situacaoItemBordero;
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Tipo de Operação")
    private TipoOperacaoPagto tipoOperacaoPagto;
    @Monetario
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public BorderoTransferenciaFinanceira() {
        valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }

    public BorderoTransferenciaFinanceira(TransferenciaContaFinanceira transferenciaContaFinanceira, Bordero bordero) {
        this.transferenciaContaFinanceira = transferenciaContaFinanceira;
        this.bordero = bordero;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bordero getBordero() {
        return bordero;
    }

    public void setBordero(Bordero bordero) {
        this.bordero = bordero;
    }

    public TransferenciaContaFinanceira getTransferenciaContaFinanceira() {
        return transferenciaContaFinanceira;
    }

    public void setTransferenciaContaFinanceira(TransferenciaContaFinanceira transferenciaContaFinanceira) {
        this.transferenciaContaFinanceira = transferenciaContaFinanceira;
    }

    public SituacaoItemBordero getSituacaoItemBordero() {
        return situacaoItemBordero;
    }

    public void setSituacaoItemBordero(SituacaoItemBordero situacaoItemBordero) {
        this.situacaoItemBordero = situacaoItemBordero;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return transferenciaContaFinanceira.toString();
    }
}
