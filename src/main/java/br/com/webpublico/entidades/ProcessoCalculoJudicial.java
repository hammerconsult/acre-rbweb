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
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 28/05/15
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ProcessoCalculoJudicial extends ProcessoCalculo implements Serializable {
    @OneToMany(mappedBy = "processoCalculoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoPagamentoJudicial> calculos;


    public ProcessoCalculoJudicial() {
        calculos = Lists.newArrayList();
    }

    @Override
    public List<CalculoPagamentoJudicial> getCalculos() {
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
