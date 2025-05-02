package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * @author gustavo
 */
@Entity

@Audited
public class ProcessoCalcTaxasDiversas extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoCalcTaxasDiversas")
    private List<CalculoTaxasDiversas> calculos;

    public List<CalculoTaxasDiversas> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoTaxasDiversas> calculos) {
        this.calculos = calculos;
    }
}
