/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Materiais que controlam lote exigem que a quantidade total em estoque seja
 * desmembrada para cada lote disponível.
 *
 * @author arthur
 */
public class EstoqueLoteMaterial extends SuperEntidade implements Comparable<EstoqueLoteMaterial> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal quantidade;

    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private Estoque estoque;

    public EstoqueLoteMaterial() {
    }

    public EstoqueLoteMaterial(Estoque estoque, LoteMaterial loteMaterial) {
        this.estoque = estoque;
        this.loteMaterial = loteMaterial;
        this.quantidade = BigDecimal.ZERO;
    }

    public EstoqueLoteMaterial(EstoqueLoteMaterial estoqueLote, Estoque estoque) {
        this.estoque = estoque;
        this.loteMaterial = estoqueLote.getLoteMaterial();
        this.quantidade = estoqueLote.getQuantidade();
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        try {
            return this.id + this.getEstoque().getMaterial().getDescricao() + this.getLoteMaterial().getIdentificacao();
        } catch (Exception e) {
            return super.toString();
        }
    }

    @Override
    public int compareTo(EstoqueLoteMaterial elm) {
        if (elm == null || this.loteMaterial == null || this.loteMaterial.getValidade() == null) {
            return 1;
        }
        return this.loteMaterial.getValidade().compareTo(elm.getLoteMaterial().getValidade());
    }

    public void atualizarValores(ItemMovimentoEstoque item) {
        if (item.getTipoOperacao().isCredito()) {
            quantidade = quantidade.add(item.getQuantidade());
        } else {
            quantidade = quantidade.subtract(item.getQuantidade());
        }
    }
}
