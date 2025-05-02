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
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Ordem Bancária/Liberação Financeria")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "BORDEROLIBCOTAFINANCEIRA")
public class BorderoLiberacaoFinanceira implements Serializable {
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
    @Etiqueta("Liberação Financeira")
    @JoinColumn(name = "LIBCOTAFINANCEIRA_ID")
    private LiberacaoCotaFinanceira liberacaoCotaFinanceira;
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


    public BorderoLiberacaoFinanceira() {
        valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }

    public BorderoLiberacaoFinanceira(Bordero bordero, LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.bordero = bordero;
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
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

    public LiberacaoCotaFinanceira getLiberacaoCotaFinanceira() {
        return liberacaoCotaFinanceira;
    }

    public void setLiberacaoCotaFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
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
        return liberacaoCotaFinanceira.toString();
    }
}
