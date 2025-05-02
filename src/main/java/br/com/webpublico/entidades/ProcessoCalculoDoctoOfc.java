/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Certidao")
public class ProcessoCalculoDoctoOfc extends ProcessoCalculo implements Serializable {
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "processoCalculoDoctoOfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoDoctoOficial> calculosDoctoOficiais;

    public ProcessoCalculoDoctoOfc() {
        calculosDoctoOficiais = new ArrayList<CalculoDoctoOficial>();
    }

    public List<CalculoDoctoOficial> getCalculosDoctoOficiais() {
        return calculosDoctoOficiais;
    }

    public void setCalculosDoctoOficiais(List<CalculoDoctoOficial> calculosDoctoOficiais) {
        this.calculosDoctoOficiais = calculosDoctoOficiais;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
         return calculosDoctoOficiais;
    }

}
