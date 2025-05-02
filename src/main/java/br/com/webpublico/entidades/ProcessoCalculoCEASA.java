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
public class ProcessoCalculoCEASA extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processoCalculoCEASA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoCEASA> calculos;

    public ProcessoCalculoCEASA() {
        calculos = new ArrayList<CalculoCEASA>();
    }

    public List<CalculoCEASA> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoCEASA> calculos) {
        this.calculos = calculos;
    }
}
