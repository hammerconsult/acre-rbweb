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
@Table(name = "COMPONENTEFORMULAUO")
public class ComponenteFormulaUnidadeOrganizacional extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    public ComponenteFormulaUnidadeOrganizacional() {
    }

    public ComponenteFormulaUnidadeOrganizacional(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo, UnidadeOrganizacional unidadeOrganizacional) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaUnidadeOrganizacional componenteDestino = new ComponenteFormulaUnidadeOrganizacional();
        componenteDestino.setUnidadeOrganizacional(getUnidadeOrganizacional());
        return componenteDestino;
    }
}
