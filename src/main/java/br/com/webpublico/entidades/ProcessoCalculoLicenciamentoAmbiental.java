package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Audited
public class ProcessoCalculoLicenciamentoAmbiental extends ProcessoCalculo {

    @OneToMany(mappedBy = "processoCalculoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoLicenciamentoAmbiental> calculos;

    public ProcessoCalculoLicenciamentoAmbiental() {
        super();
        calculos = Lists.newArrayList();
    }

    @Override
    public List<CalculoLicenciamentoAmbiental> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoLicenciamentoAmbiental> calculos) {
        this.calculos = calculos;
    }
}
