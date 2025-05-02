package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class ItemParcelaValorDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private ItemValorDivida itemValorDivida;
    private BigDecimal valor;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "itemParcelaValorDivida")
    private List<DescontoItemParcela> descontos;
    @Transient
    private Long criadoEm;


    public ItemParcelaValorDivida() {
        this.descontos = new ArrayList<>();
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemValorDivida getItemValorDivida() {
        return itemValorDivida;
    }

    public void setItemValorDivida(ItemValorDivida item) {
        this.itemValorDivida = item;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcela) {
        this.parcelaValorDivida = parcela;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public List<DescontoItemParcela> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<DescontoItemParcela> descontos) {
        this.descontos = descontos;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return itemValorDivida.getTributo().getDescricao() + " " + parcelaValorDivida.getSequenciaParcela() + ": " + this.valor;
    }

    public Tributo getTributo() {
        return itemValorDivida.getTributo();
    }
}
