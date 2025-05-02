/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@GrupoDiagrama(nome = "Patrimonial")
@Entity
@Audited
public class ItemInventarioBens extends EventoBem{

    @ManyToOne
    private InventarioBens inventarioBens;

    private Boolean naoEncontrado;
    private Boolean transferir;

    public ItemInventarioBens() {
        super(TipoEventoBem.ITEMINVENTARIOBENS, TipoOperacao.NENHUMA_OPERACAO);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        this.setNaoEncontrado(Boolean.FALSE);
        this.setTransferir(Boolean.FALSE);
    }

    public InventarioBens getInventarioBens() {
        return inventarioBens;
    }

    public void setInventarioBens(InventarioBens inventarioBens) {
        this.inventarioBens = inventarioBens;
    }

    public Boolean getNaoEncontrado() {
        return naoEncontrado != null ? naoEncontrado : false;
    }

    public void setNaoEncontrado(Boolean naoEncontrado) {
        this.naoEncontrado = naoEncontrado;
    }

    public Boolean getTransferir() {
        return transferir != null ? transferir : false;
    }

    public void setTransferir(Boolean transferir) {
        this.transferir = transferir;
    }
}
