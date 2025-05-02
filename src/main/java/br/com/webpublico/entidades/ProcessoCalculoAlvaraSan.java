/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Alvara")
public class ProcessoCalculoAlvaraSan extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculoAlvaraSan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoAlvaraSanitario> calculosAlvaraSanitarios;

    public ProcessoCalculoAlvaraSan() {
        calculosAlvaraSanitarios = new ArrayList<CalculoAlvaraSanitario>();
    }

    public List<CalculoAlvaraSanitario> getCalculosAlvaraSanitarios() {
        return calculosAlvaraSanitarios;
    }

    public void setCalculosAlvaraSanitarios(List<CalculoAlvaraSanitario> calculosAlvaraSanitarios) {
        this.calculosAlvaraSanitarios = calculosAlvaraSanitarios;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
       return calculosAlvaraSanitarios;
    }
}
