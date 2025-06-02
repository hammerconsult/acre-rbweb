package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.interfaces.AgrupadorLevantamentoEstoque;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Desenvolvimento on 27/01/2017.
 */
@Entity
@Audited
@Etiqueta("Item Levantamento de Estoque")
public class ItemLevantamentoEstoque extends SuperEntidade implements AgrupadorLevantamentoEstoque, Comparable<ItemLevantamentoEstoque> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Material")
    private Material material;

    @ManyToOne
    @Etiqueta("Lote Material")
    private LoteMaterial loteMaterial;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Obrigatorio
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Levantamento Estoque")
    private LevantamentoEstoque levantamentoEstoque;

    public ItemLevantamentoEstoque() {
        super();
        this.quantidade = BigDecimal.ZERO;
        this.valorUnitario = BigDecimal.ZERO;
    }

    public ItemLevantamentoEstoque(Material material, LevantamentoEstoque selecionado) {
        super();
        this.material = material;
        this.quantidade = BigDecimal.ZERO;
        this.valorUnitario = BigDecimal.ZERO;
        this.levantamentoEstoque = selecionado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public BigDecimal getQuantidade() {
        if (quantidade != null) {
            return quantidade;
        }
        return BigDecimal.ZERO;
    }

    public void setQuantidade(BigDecimal quantidade) {
        if (quantidade == null) {
            this.quantidade = BigDecimal.ZERO;
        } else {
            this.quantidade = quantidade;
        }
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public LevantamentoEstoque getLevantamentoEstoque() {
        return levantamentoEstoque;
    }

    public void setLevantamentoEstoque(LevantamentoEstoque levantamentoEstoque) {
        this.levantamentoEstoque = levantamentoEstoque;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            return BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(getQuantidade(), material.getMascaraQuantidade());
    }

    public void calcularValorTotal() {
        if (valorUnitario != null && quantidade != null) {
            BigDecimal valorTotal = (valorUnitario.multiply(quantidade)).setScale(2, RoundingMode.HALF_EVEN);
            setValorTotal(valorTotal);
        }
    }

    @Override
    public Boolean getGrupoExterno() {
        return Boolean.FALSE;
    }

    @Override
    public GrupoMaterial getGrupoMaterial() {
        return material.getGrupo();
    }

    @Override
    public TipoEstoque getTipoEstoque() {
        return levantamentoEstoque.getLocalEstoque().getTipoEstoque();
    }

    @Override
    public int compareTo(ItemLevantamentoEstoque o) {
        return this.material.getCodigo().compareTo(o.material.getCodigo());
    }
}
