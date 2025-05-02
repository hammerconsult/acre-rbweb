/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@GrupoDiagrama(nome = "Patrimonial")
@Entity
@Audited
public class FechamentoInventarioBem extends EventoBem {

    @ManyToOne
    private InventarioBens inventarioBens;

    public FechamentoInventarioBem() {
        super(TipoEventoBem.FECHAMENTOINVENTARIOBEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public InventarioBens getInventarioBens() {
        return inventarioBens;
    }

    public void setInventarioBens(InventarioBens inventarioBens) {
        this.inventarioBens = inventarioBens;
    }

}
