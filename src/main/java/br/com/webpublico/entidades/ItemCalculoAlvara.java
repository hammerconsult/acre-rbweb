package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Etiqueta("Item Cálculo Alvará")
@GrupoDiagrama(nome = "Alvara")
public class ItemCalculoAlvara extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoAlvara calculoAlvara;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private CaracFuncionamento caracFuncionamento;
    private BigDecimal valorReal;
    private BigDecimal valorEfetivo;
    @OneToMany(mappedBy = "itemCalculoAlvara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoAmbiental> itensAmbientais;

    public ItemCalculoAlvara() {
        itensAmbientais = Lists.newArrayList();
    }

    public ItemCalculoAlvara(ItemCalculoAlvara itemCalculoAlvara) {
        this.calculoAlvara = itemCalculoAlvara.getCalculoAlvara();
        this.tributo = itemCalculoAlvara.getTributo();
        this.caracFuncionamento = itemCalculoAlvara.getCaracFuncionamento();
        this.valorReal = itemCalculoAlvara.getValorReal();
        this.valorEfetivo = itemCalculoAlvara.getValorEfetivo();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoAlvara getCalculoAlvara() {
        return calculoAlvara;
    }

    public void setCalculoAlvara(CalculoAlvara calculoAlvara) {
        this.calculoAlvara = calculoAlvara;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorReal() {
        return valorReal;
    }

    public void setValorReal(BigDecimal valorReal) {
        this.valorReal = valorReal;
    }

    public BigDecimal getValorEfetivo() {
        return valorEfetivo;
    }

    public void setValorEfetivo(BigDecimal valorEfetivo) {
        this.valorEfetivo = valorEfetivo;
    }

    public CaracFuncionamento getCaracFuncionamento() {
        return caracFuncionamento;
    }

    public void setCaracFuncionamento(CaracFuncionamento caracFuncionamento) {
        this.caracFuncionamento = caracFuncionamento;
    }

    public List<ItemCalculoAmbiental> getItensAmbientais() {
        return itensAmbientais;
    }

    public void setItensAmbientais(List<ItemCalculoAmbiental> itensAmbientais) {
        this.itensAmbientais = itensAmbientais;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCalculoAlvara that = (ItemCalculoAlvara) o;
        if (!tributo.equals(that.tributo)) return false;
        return caracFuncionamento != null ? caracFuncionamento.equals(that.caracFuncionamento) : that.caracFuncionamento == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + tributo.hashCode();
        result = 31 * result + (caracFuncionamento != null ? caracFuncionamento.hashCode() : 0);
        return result;
    }
}
