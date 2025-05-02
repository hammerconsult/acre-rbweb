package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

@Entity
@Audited
public class ProcessoCalcParcelamento extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculo", orphanRemoval = true)
    private List<ProcessoParcelamento> calculos;

    public ProcessoCalcParcelamento() {
        this.calculos = Lists.newArrayList();
    }

    @Override
    public List<ProcessoParcelamento> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<ProcessoParcelamento> calculos) {
        this.calculos = calculos;
    }
}
