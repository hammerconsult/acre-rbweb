package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Audited
public class ComponenteFormulaSubacao extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Ação")
    @ManyToOne
    private SubAcaoPPA subAcaoPPA;

    public ComponenteFormulaSubacao() {
    }

    public ComponenteFormulaSubacao(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, SubAcaoPPA subAcaoPPA) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.subAcaoPPA = subAcaoPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaSubacao componenteDestino = new ComponenteFormulaSubacao();
        componenteDestino.setSubAcaoPPA(getSubAcaoPPA());
        return componenteDestino;
    }
}
