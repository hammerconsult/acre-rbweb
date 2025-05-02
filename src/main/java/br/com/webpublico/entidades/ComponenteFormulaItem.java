/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Componente")
@Entity
public class ComponenteFormulaItem extends ComponenteFormula implements Serializable {

    private static final long serialVersionUID = 1L;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Item Demonstrativo")
    @ManyToOne
    private ItemDemonstrativo itemDemonstrativo;

    public ComponenteFormulaItem() {
    }

    public ComponenteFormulaItem(ItemDemonstrativo itemDemonstrativo, OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.itemDemonstrativo = itemDemonstrativo;
    }


    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaItem componenteDestino = new ComponenteFormulaItem();
        componenteDestino.setItemDemonstrativo(getItemDemonstrativo());
        return componenteDestino;
    }
}
