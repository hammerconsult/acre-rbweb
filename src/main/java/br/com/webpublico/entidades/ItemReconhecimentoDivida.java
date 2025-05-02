package br.com.webpublico.entidades;


import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Audited
@Etiqueta("Item do Reconhecimento da Dívida")
public class ItemReconhecimentoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Reconhecimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;
    @Obrigatorio
    @Etiqueta("Quantidade")
    @Positivo(permiteZero = false)
    private BigDecimal quantidade;
    @Obrigatorio
    @Etiqueta("Valor Unitário")
    @Positivo(permiteZero = false)
    private BigDecimal valorUnitario;
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;
    @Etiqueta("Subtipo de Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private SubTipoObjetoCompra subTipoObjetoCompra;
    @Transient
    private List<SubTipoObjetoCompra> subTiposObjetoCompra;
    @OneToMany(mappedBy = "itemReconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ItemReconhecimentoDividaDotacao> dotacoes;

    public ItemReconhecimentoDivida() {
        super();
        dotacoes = Lists.newArrayList();
        subTiposObjetoCompra = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public List<SubTipoObjetoCompra> getSubTiposObjetoCompra() {
        return subTiposObjetoCompra;
    }

    public void setSubTiposObjetoCompra(List<SubTipoObjetoCompra> subTiposObjetoCompra) {
        this.subTiposObjetoCompra = subTiposObjetoCompra;
    }

    public boolean hasMaisDeUmSubTipo() {
        return subTiposObjetoCompra != null && subTiposObjetoCompra.size() > 1;
    }

    public String getTipoSolicitacao() {
        try {
            return objetoCompra.getTipoObjetoCompra().getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getDescricaoItem() {
        try {
            return objetoCompra.getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    public BigDecimal getValorTotalItem() {
        try {
            return quantidade.multiply(valorUnitario);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorReservado() {
        BigDecimal valorReservado = BigDecimal.ZERO;
        if (getDotacoes() != null) {
            for (ItemReconhecimentoDividaDotacao dotacao : getDotacoes()) {
                valorReservado = valorReservado.add(dotacao.getValorReservado());
            }
        }
        return valorReservado;
    }

    public BigDecimal getValorParaReservar() {
        BigDecimal valorTotal = getValorTotalItem();
        BigDecimal valorReservado = getValorReservado();
        return valorTotal.subtract(valorReservado).setScale(2, RoundingMode.HALF_EVEN);
    }

    public List<ItemReconhecimentoDividaDotacao> getDotacoes() {
        return dotacoes;
    }

    public void setDotacoes(List<ItemReconhecimentoDividaDotacao> dotacoes) {
        this.dotacoes = dotacoes;
    }

    public Boolean isObjetoCompraConsumoOrPermanenteMovel() {
        return objetoCompra.getTipoObjetoCompra() != null && (objetoCompra.getTipoObjetoCompra().isMaterialConsumo() || objetoCompra.getTipoObjetoCompra().isMaterialPermanente());
    }

    @Override
    public String toString() {
        return objetoCompra.toString();
    }
}
