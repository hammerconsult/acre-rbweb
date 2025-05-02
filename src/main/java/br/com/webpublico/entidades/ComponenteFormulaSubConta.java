package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by mateus on 24/07/17.
 */
@Entity
@Audited
public class ComponenteFormulaSubConta extends ComponenteFormula implements Serializable {

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @ManyToOne
    private SubConta subConta;

    public ComponenteFormulaSubConta() {
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaSubConta componenteDestino = new ComponenteFormulaSubConta();
        componenteDestino.setSubConta(getSubConta());
        return componenteDestino;
    }
}
