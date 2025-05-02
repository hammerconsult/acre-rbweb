/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Terminal-2
 */

@Entity

@Audited
@GrupoDiagrama(nome = "NSFAvulsa")
@Etiqueta("Processo Calculo NFS Avulsa")
public class ProcessoCalculoNFSAvulsa extends ProcessoCalculo implements Serializable {
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoNFSAvulsa> calculos;

    public ProcessoCalculoNFSAvulsa() {
        calculos = new ArrayList<>();
    }

    public List<CalculoNFSAvulsa> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoNFSAvulsa> calculos) {
        this.calculos = calculos;
    }

}
