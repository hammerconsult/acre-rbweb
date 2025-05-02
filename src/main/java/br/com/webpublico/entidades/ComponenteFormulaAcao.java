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
public class ComponenteFormulaAcao extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Ação")
    @ManyToOne
    private AcaoPPA acaoPPA;

    public ComponenteFormulaAcao() {
    }

    public ComponenteFormulaAcao(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, AcaoPPA acaoPPA) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.acaoPPA = acaoPPA;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaAcao componenteDestino = new ComponenteFormulaAcao();
        componenteDestino.setAcaoPPA(getAcaoPPA());
        return componenteDestino;
    }
}
