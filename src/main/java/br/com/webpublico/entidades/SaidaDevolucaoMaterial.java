/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída de Materiais por Devolução")
public class SaidaDevolucaoMaterial extends SaidaMaterial implements Serializable {

    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Entrada de Material")
    private EntradaMaterial entradaMaterial;

    public EntradaMaterial getEntradaMaterial() {
        return entradaMaterial;
    }

    public void setEntradaMaterial(EntradaMaterial entradaMaterial) {
        this.entradaMaterial = entradaMaterial;
    }

    @Override
    public TipoSaidaMaterial getTipoDestaSaida() {
        return TipoSaidaMaterial.DEVOLUCAO;
    }

    public void setarQuantidadeDevolvida() {
        BigDecimal acumulado;
        BigDecimal devolvida;

        for (ItemSaidaMaterial ism : getListaDeItemSaidaMaterial()) {
            acumulado = BigDecimal.ZERO;

            devolvida = ism.getItemDevolucaoMaterial().getItemEntradaMaterial().getQuantidadeDevolvida();
            acumulado = acumulado.add(devolvida);
            acumulado = acumulado.add(ism.getQuantidade());

            ism.getItemDevolucaoMaterial().getItemEntradaMaterial().setQuantidadeDevolvida(acumulado);
        }
    }
}
