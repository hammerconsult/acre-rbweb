package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.FonteDespesaORC;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * @author Alex
 */
public class AuxObraFontesDespesas {

    private DespesaORC despesaORC;
    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;
    @Transient
    private BigDecimal valorSolicitadoPorFonte = BigDecimal.ZERO;

    public AuxObraFontesDespesas() {
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
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

    public BigDecimal getValorSolicitadoPorFonte() {
        return valorSolicitadoPorFonte;
    }

    public void setValorSolicitadoPorFonte(BigDecimal valorSolicitadoPorFonte) {
        this.valorSolicitadoPorFonte = valorSolicitadoPorFonte;
    }
}
