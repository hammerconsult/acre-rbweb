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
 * @author gustavo
 */
@Entity

@Audited

public class ProcessoInconsistencia extends ProcessoCalculo implements Serializable {

    @ManyToOne
    private LoteBaixa loteBaixa;
    @OneToMany(mappedBy = "processoInconsistencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoInconsistencia> calculos;

    public ProcessoInconsistencia() {
        calculos = new ArrayList<CalculoInconsistencia>();
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public List<CalculoInconsistencia> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoInconsistencia> calculos) {
        this.calculos = calculos;
    }
}
