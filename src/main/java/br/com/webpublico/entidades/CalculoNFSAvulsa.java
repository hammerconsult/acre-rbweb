/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@GrupoDiagrama(nome = "NSFAvulsa")
@Etiqueta("Calculo NFS Avulsa")
public class CalculoNFSAvulsa extends Calculo implements Serializable {

    @OneToOne
    private NFSAvulsa nfsAvulsa;
    @ManyToOne
    private ProcessoCalculoNFSAvulsa processo;
    @ManyToOne
    private Tributo tributo;

    public CalculoNFSAvulsa() {
        super.setTipoCalculo(TipoCalculo.NFS_AVULSA);
    }

    public NFSAvulsa getNfsAvulsa() {
        super.setTipoCalculo(TipoCalculo.NFS_AVULSA);
        return nfsAvulsa;
    }

    public void setNfsAvulsa(NFSAvulsa nfsAvulsa) {
        this.nfsAvulsa = nfsAvulsa;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public ProcessoCalculoNFSAvulsa getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoCalculoNFSAvulsa processo) {
        super.setProcessoCalculo(processo);
        this.processo = processo;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processo;
    }

}
