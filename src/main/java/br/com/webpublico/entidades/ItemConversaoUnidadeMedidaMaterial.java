/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Conversão Unidade Medida")
@Table(name = "ITEMCONVERSAOUNIDADEMEDMAT")
public class ItemConversaoUnidadeMedidaMaterial extends SuperEntidade implements Comparable<ItemConversaoUnidadeMedidaMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número do Item")
    private Integer numeroItem;

    @ManyToOne
    private ConversaoUnidadeMedidaMaterial conversaoUnidadeMedida;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Material de Saída")
    private Material materialSaida;

    @Obrigatorio
    @Etiqueta("Quantidade Saída")
    private BigDecimal quantidadeSaida;

    @Obrigatorio
    @Etiqueta("Valor Unitário Saída")
    private BigDecimal valorUnitarioSaida;

    @Obrigatorio
    @Etiqueta("Valor Total Saída")
    private BigDecimal valorTotalSaida;

    @OneToOne
    private MovimentoEstoque movimentoEstoqueSaida;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Material de Entrada")
    private Material materialEntrada;

    @Obrigatorio
    @Etiqueta("Quantidade Entrada")
    private BigDecimal quantidadeEntrada;

    @Obrigatorio
    @Etiqueta("Valor Unitário Entrada")
    private BigDecimal valorUnitarioEntrada;

    @Obrigatorio
    @Etiqueta("Valor Total Entrada")
    private BigDecimal valorTotalEntrada;

    @OneToOne
    private MovimentoEstoque movimentoEstoqueEntrada;

    @Transient
    private Estoque estoqueSaida;

    public ItemConversaoUnidadeMedidaMaterial() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConversaoUnidadeMedidaMaterial getConversaoUnidadeMedida() {
        return conversaoUnidadeMedida;
    }

    public void setConversaoUnidadeMedida(ConversaoUnidadeMedidaMaterial conversaoUnidadeMedida) {
        this.conversaoUnidadeMedida = conversaoUnidadeMedida;
    }

    public Material getMaterialSaida() {
        return materialSaida;
    }

    public void setMaterialSaida(Material materialSaida) {
        this.materialSaida = materialSaida;
    }

    public BigDecimal getQuantidadeSaida() {
        return quantidadeSaida;
    }

    public void setQuantidadeSaida(BigDecimal quantidadeSaida) {
        this.quantidadeSaida = quantidadeSaida;
    }

    public BigDecimal getValorUnitarioSaida() {
        return valorUnitarioSaida;
    }

    public void setValorUnitarioSaida(BigDecimal valorUnitarioSaida) {
        this.valorUnitarioSaida = valorUnitarioSaida;
    }

    public BigDecimal getValorTotalSaida() {
        return valorTotalSaida;
    }

    public void setValorTotalSaida(BigDecimal valorTotalSaida) {
        this.valorTotalSaida = valorTotalSaida;
    }

    public MovimentoEstoque getMovimentoEstoqueSaida() {
        return movimentoEstoqueSaida;
    }

    public void setMovimentoEstoqueSaida(MovimentoEstoque movimentoEstoqueSaida) {
        this.movimentoEstoqueSaida = movimentoEstoqueSaida;
    }

    public Material getMaterialEntrada() {
        return materialEntrada;
    }

    public void setMaterialEntrada(Material materialEntrada) {
        this.materialEntrada = materialEntrada;
    }

    public BigDecimal getQuantidadeEntrada() {
        return quantidadeEntrada;
    }

    public void setQuantidadeEntrada(BigDecimal quantidadeEntrada) {
        this.quantidadeEntrada = quantidadeEntrada;
    }

    public BigDecimal getValorUnitarioEntrada() {
        return valorUnitarioEntrada;
    }

    public void setValorUnitarioEntrada(BigDecimal valorUnitarioEntrada) {
        this.valorUnitarioEntrada = valorUnitarioEntrada;
    }

    public BigDecimal getValorTotalEntrada() {
        return valorTotalEntrada;
    }

    public void setValorTotalEntrada(BigDecimal valorTotalEntrada) {
        this.valorTotalEntrada = valorTotalEntrada;
    }

    public MovimentoEstoque getMovimentoEstoqueEntrada() {
        return movimentoEstoqueEntrada;
    }

    public void setMovimentoEstoqueEntrada(MovimentoEstoque movimentoEstoqueEntrada) {
        this.movimentoEstoqueEntrada = movimentoEstoqueEntrada;
    }

    public Estoque getEstoqueSaida() {
        return estoqueSaida;
    }

    public void setEstoqueSaida(Estoque estoqueSaida) {
        this.estoqueSaida = estoqueSaida;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    @Override
    public int compareTo(ItemConversaoUnidadeMedidaMaterial o) {
        if (getMaterialSaida() != null && o.getMaterialSaida() != null) {
            return getMaterialSaida().getCodigo().compareTo(o.getMaterialSaida().getCodigo());
        }
        return 0;
    }

    public void calcularValorTotalEntrada() {
        if (quantidadeEntrada != null && valorUnitarioEntrada != null) {
            BigDecimal total = quantidadeEntrada.multiply(valorUnitarioEntrada).setScale(2, RoundingMode.HALF_EVEN);
            setValorTotalEntrada(total);
        }
    }

    public void calcularValorTotalSaida() {
        if (quantidadeSaida != null && valorUnitarioSaida != null) {
            BigDecimal total = quantidadeSaida.multiply(valorUnitarioSaida).setScale(2, RoundingMode.HALF_EVEN);
            setValorTotalSaida(total);
        }
    }
}
