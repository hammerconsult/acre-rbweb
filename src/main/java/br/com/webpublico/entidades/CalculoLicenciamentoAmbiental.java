package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
public class CalculoLicenciamentoAmbiental extends Calculo {

    @ManyToOne
    private ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental;
    @OneToMany(mappedBy = "calculoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoLicenciamentoAmbiental> itensCalculo;

    public CalculoLicenciamentoAmbiental() {
        super();
        setTipoCalculo(TipoCalculo.LICENCIAMENTO_AMBIENTAL);
        itensCalculo = Lists.newArrayList();
    }

    public ProcessoCalculoLicenciamentoAmbiental getProcessoCalculoLicenciamentoAmbiental() {
        return processoCalculoLicenciamentoAmbiental;
    }

    public void setProcessoCalculoLicenciamentoAmbiental(ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental) {
        this.processoCalculoLicenciamentoAmbiental = processoCalculoLicenciamentoAmbiental;
        setProcessoCalculo(this.processoCalculoLicenciamentoAmbiental);
    }

    public List<ItemCalculoLicenciamentoAmbiental> getItensCalculo() {
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoLicenciamentoAmbiental> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public void somarValores() {
        setValorReal(itensCalculo.stream()
            .map(ItemCalculoLicenciamentoAmbiental::getValorUFM)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        setValorEfetivo(itensCalculo.stream()
            .map(ItemCalculoLicenciamentoAmbiental::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
