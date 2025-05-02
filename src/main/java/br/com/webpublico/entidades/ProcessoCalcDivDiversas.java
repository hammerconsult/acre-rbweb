package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
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
public class ProcessoCalcDivDiversas extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "processoCalcDivDiversas")
    private List<CalculoDividaDiversa> calculos;

    public ProcessoCalcDivDiversas() {
        this.calculos = Lists.newArrayList();
    }

    public List<CalculoDividaDiversa> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoDividaDiversa> calculos) {
        this.calculos = calculos;
    }
}
