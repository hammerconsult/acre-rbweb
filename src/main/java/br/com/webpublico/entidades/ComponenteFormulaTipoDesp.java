package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoDespesaORC;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 09/12/13
 * Time: 09:25
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ComponenteFormulaTipoDesp extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(value = EnumType.STRING)
    private TipoDespesaORC tipoDespesaORC;

    public ComponenteFormulaTipoDesp() {
    }

    public ComponenteFormulaTipoDesp(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, TipoDespesaORC tipoDespesaORC) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaTipoDesp componenteDestino = new ComponenteFormulaTipoDesp();
        componenteDestino.setTipoDespesaORC(getTipoDespesaORC());
        return componenteDestino;
    }
}
