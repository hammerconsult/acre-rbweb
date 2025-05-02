package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoCalculoAlvara;
import br.com.webpublico.enums.TipoControleCalculoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Cálculo de Alvará")
@GrupoDiagrama(nome = "Alvara")
public class CalculoAlvara extends Calculo {
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoAlvara processoCalculoAlvara;
    @ManyToOne
    private Divida divida;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @Enumerated(EnumType.STRING)
    private TipoAlvara tipoAlvara;
    @Enumerated(EnumType.STRING)
    private TipoControleCalculoAlvara controleCalculo;
    @Enumerated(EnumType.STRING)
    private TipoCalculoAlvara tipoCalculoAlvara;
    @OneToMany(mappedBy = "calculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoAlvara> itensAlvara;
    @OneToMany(mappedBy = "calculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<CalculoAlvaraEfetivo> calculosAgrupados;

    public CalculoAlvara() {
        this.itensAlvara = Lists.newArrayList();
        this.calculosAgrupados = Lists.newArrayList();
        this.controleCalculo = TipoControleCalculoAlvara.CALCULO;
        super.setTipoCalculo(TipoCalculo.ALVARA);
    }

    public CalculoAlvara(CalculoAlvara calculoAlvara) {
        this.itensAlvara = Lists.newArrayList();
        this.calculosAgrupados = Lists.newArrayList();
        this.processoCalculoAlvara = calculoAlvara.getProcessoCalculoAlvara();
        this.divida = calculoAlvara.getDivida();
        this.vencimento = calculoAlvara.getVencimento();
        this.tipoAlvara = calculoAlvara.getTipoAlvara();
        this.controleCalculo = calculoAlvara.getControleCalculo();
        this.tipoCalculoAlvara = calculoAlvara.getTipoCalculoAlvara();
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
        super.setProcessoCalculo(processoCalculoAlvara);
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public TipoControleCalculoAlvara getControleCalculo() {
        return controleCalculo;
    }

    public void setControleCalculo(TipoControleCalculoAlvara controleCalculo) {
        this.controleCalculo = controleCalculo;
    }

    public TipoCalculoAlvara getTipoCalculoAlvara() {
        return tipoCalculoAlvara;
    }

    public void setTipoCalculoAlvara(TipoCalculoAlvara tipoCalculoAlvara) {
        this.tipoCalculoAlvara = tipoCalculoAlvara;
    }

    public List<ItemCalculoAlvara> getItensAlvara() {
        return itensAlvara;
    }

    public void setItensAlvara(List<ItemCalculoAlvara> itensAlvara) {
        this.itensAlvara = itensAlvara;
    }

    public List<CalculoAlvaraEfetivo> getCalculosAgrupados() {
        return calculosAgrupados;
    }

    public void setCalculosAgrupados(List<CalculoAlvaraEfetivo> calculosAgrupados) {
        this.calculosAgrupados = calculosAgrupados;
    }
}
