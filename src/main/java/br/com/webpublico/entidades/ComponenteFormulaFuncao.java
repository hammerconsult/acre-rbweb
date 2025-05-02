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
public class ComponenteFormulaFuncao extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Função")
    @ManyToOne
    private Funcao funcao;

    public ComponenteFormulaFuncao() {
    }

    public ComponenteFormulaFuncao(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, Funcao funcao) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.funcao = funcao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaFuncao componenteDestino = new ComponenteFormulaFuncao();
        componenteDestino.setFuncao(getFuncao());
        return componenteDestino;
    }
}
