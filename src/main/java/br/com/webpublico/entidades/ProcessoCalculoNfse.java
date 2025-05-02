package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/01/14
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Processo de Cálculos de NFS-e")
@Audited
public class ProcessoCalculoNfse extends ProcessoCalculo {
    @OneToMany(mappedBy = "processoCalculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoNfse> calculos;

    public ProcessoCalculoNfse() {
        calculos = new ArrayList<>();
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoNfse> calculos) {
        this.calculos = calculos;
    }

    public List<CalculoNfse> getCalculosNfse() {
        return calculos;
    }
}
