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
public class ComponenteFormulaSubFuncao extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Subfunção")
    @ManyToOne
    private SubFuncao subFuncao;

    public ComponenteFormulaSubFuncao() {
    }

    public ComponenteFormulaSubFuncao(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, SubFuncao subFuncao) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.subFuncao = subFuncao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaSubFuncao componenteDestino = new ComponenteFormulaSubFuncao();
        componenteDestino.setSubFuncao(getSubFuncao());
        return componenteDestino;
    }
}
