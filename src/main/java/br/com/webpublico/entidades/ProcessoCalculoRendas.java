/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
public class ProcessoCalculoRendas extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processoCalculoRendas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoRendas> calculos;

    public ProcessoCalculoRendas() {
        calculos = new ArrayList<CalculoRendas>();
    }

    public List<CalculoRendas> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoRendas> calculos) {
        this.calculos = calculos;
    }


}
