package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 16/03/2017.
 */
@Entity
@Audited
public class PagamentoCartao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valorPago;
    private Integer quantidadeParcela;
    @ManyToOne
    private BandeiraCartao bandeiraCartao;
    @OneToOne
    private ItemLoteBaixa itemLoteBaixa;

    @OneToMany(mappedBy = "pagamentoCartao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagamentoCartaoItem> item;

    public PagamentoCartao() {
        item = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Integer getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Integer quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public BandeiraCartao getBandeiraCartao() {
        return bandeiraCartao;
    }

    public void setBandeiraCartao(BandeiraCartao bandeiraCartao) {
        this.bandeiraCartao = bandeiraCartao;
    }

    public ItemLoteBaixa getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(ItemLoteBaixa itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public List<PagamentoCartaoItem> getItem() {
        return item;
    }

    public void setItem(List<PagamentoCartaoItem> item) {
        this.item = item;
    }
}
