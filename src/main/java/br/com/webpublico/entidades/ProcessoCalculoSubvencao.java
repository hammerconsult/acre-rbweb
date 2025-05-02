package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * Created by tharlyson on 04/12/19.
 */
@Entity
@Audited
public class ProcessoCalculoSubvencao extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processoCalculoSubvencao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoPagamentoSubvencao> calculos;


    public ProcessoCalculoSubvencao() {
        calculos = Lists.newArrayList();
    }

    @Override
    public List<CalculoPagamentoSubvencao> getCalculos() {
        return calculos;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
