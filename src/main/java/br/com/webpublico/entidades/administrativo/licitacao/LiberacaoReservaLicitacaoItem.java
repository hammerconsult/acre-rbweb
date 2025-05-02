package br.com.webpublico.entidades.administrativo.licitacao;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by wellington on 24/10/17.
 */
@Table(name = "LIBERACAORESERVALICITEM")
@Entity
public class LiberacaoReservaLicitacaoItem extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private LiberacaoReservaLicitacao liberacaoReservaLicitacao;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;
    private BigDecimal valorExecutado;
    @Transient
    private BigDecimal valorLiberar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LiberacaoReservaLicitacao getLiberacaoReservaLicitacao() {
        return liberacaoReservaLicitacao;
    }

    public void setLiberacaoReservaLicitacao(LiberacaoReservaLicitacao liberacaoReservaLicitacao) {
        this.liberacaoReservaLicitacao = liberacaoReservaLicitacao;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorLiberar() {
        if (getValorReservado() == null || getValorExecutado() == null) {
            return BigDecimal.ZERO;
        }
        return getValorReservado().subtract(getValorExecutado());
    }
}
