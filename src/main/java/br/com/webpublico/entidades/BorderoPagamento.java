package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemBordero;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 07/05/14
 * Time: 20:56
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Ordem Bancária/Pagamento")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class BorderoPagamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ordem Bancária")
    private Bordero bordero;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pagamento")
    private Pagamento pagamento;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoItemBordero situacaoItemBordero;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conta Bancária Favorecido")
    private ContaCorrenteBancaria contaCorrenteFavorecido;
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

    public BorderoPagamento() {
        criadoEm = System.nanoTime();
    }

    public BorderoPagamento(Bordero bordero, Pagamento pagamento) {
        this.bordero = bordero;
        this.pagamento = pagamento;
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

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
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

    public ContaCorrenteBancaria getContaCorrenteFavorecido() {
        return contaCorrenteFavorecido;
    }

    public void setContaCorrenteFavorecido(ContaCorrenteBancaria contaCorrenteFavorecido) {
        this.contaCorrenteFavorecido = contaCorrenteFavorecido;
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
        return pagamento.toString();
    }
}
