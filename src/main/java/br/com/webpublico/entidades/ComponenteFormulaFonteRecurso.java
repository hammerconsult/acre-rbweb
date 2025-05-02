package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/09/13
 * Time: 08:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "COMPONENTEFORMULAFONTEREC")
public class ComponenteFormulaFonteRecurso extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fonte de Recurso")
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;

    public ComponenteFormulaFonteRecurso() {
    }

    public ComponenteFormulaFonteRecurso(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, FonteDeRecursos fonteDeRecursos) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaFonteRecurso componenteDestino = new ComponenteFormulaFonteRecurso();
        componenteDestino.setFonteDeRecursos(getFonteDeRecursos());
        return componenteDestino;
    }
}
