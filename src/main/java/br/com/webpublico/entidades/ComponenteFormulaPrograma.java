package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/09/13
 * Time: 08:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ComponenteFormulaPrograma extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Programa")
    @ManyToOne
    private ProgramaPPA programaPPA;

    public ComponenteFormulaPrograma() {
    }

    public ComponenteFormulaPrograma(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, ProgramaPPA programaPPA) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.programaPPA = programaPPA;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaPrograma componenteDestino = new ComponenteFormulaPrograma();
        componenteDestino.setProgramaPPA(getProgramaPPA());
        return componenteDestino;
    }
}
