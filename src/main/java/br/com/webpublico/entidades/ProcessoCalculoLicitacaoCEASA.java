/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre
 */
@Entity

@Audited
@Table(name = "PROCESSOCALCULOLICCEASA")
public class ProcessoCalculoLicitacaoCEASA extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processoCalculoLicitacaoCEASA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoLicitacaoCEASA> calculos;

    public ProcessoCalculoLicitacaoCEASA() {
        calculos = new ArrayList<CalculoLicitacaoCEASA>();
    }

    public List<CalculoLicitacaoCEASA> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoLicitacaoCEASA> calculos) {
        this.calculos = calculos;
    }
}
