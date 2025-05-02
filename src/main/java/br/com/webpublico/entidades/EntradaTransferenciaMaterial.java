/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Sarruf
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais por Transferência")
@Table(name = "ENTRADATRANSFMATERIAL")
public class EntradaTransferenciaMaterial extends EntradaMaterial implements Comparable<EntradaTransferenciaMaterial> {

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Saída de Material")
    private SaidaRequisicaoMaterial saidaRequisicaoMaterial;

    @Tabelavel
    @Pesquisavel
    @Transient
    @Etiqueta("Saída de Material")
    private String saidaMaterial;

    public SaidaRequisicaoMaterial getSaidaRequisicaoMaterial() {
        return saidaRequisicaoMaterial;
    }

    public void setSaidaRequisicaoMaterial(SaidaRequisicaoMaterial saidaRequisicaoMaterial) {
        this.saidaRequisicaoMaterial = saidaRequisicaoMaterial;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return saidaRequisicaoMaterial.getRequisicaoMaterial();
    }

    public String getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(String saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }

    @Override
    public int compareTo(EntradaTransferenciaMaterial o) {
        return o.getNumero().compareTo(o.getNumero());
    }
}
