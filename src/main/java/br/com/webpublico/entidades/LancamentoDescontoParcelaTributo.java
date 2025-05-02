package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Arrecadação")
@Entity
@Audited
@Etiqueta("Descontos das Parcelas do Lançamento de Desconto")
@Table(name = "LANCAMENTODESCPARCTRIBUTO")
public class LancamentoDescontoParcelaTributo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LancamentoDescontoParcela lancamentoDescontoParcela;
    @ManyToOne
    private ItemParcelaValorDivida itemParcelaValorDivida;
    private BigDecimal desconto;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DescontoItemParcela descontoItemParcela;
    @Enumerated(EnumType.STRING)
    private Tributo.TipoTributo tipoTributo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LancamentoDescontoParcela getLancamentoDescontoParcela() {
        return lancamentoDescontoParcela;
    }

    public void setLancamentoDescontoParcela(LancamentoDescontoParcela lancamentoDescontoParcela) {
        this.lancamentoDescontoParcela = lancamentoDescontoParcela;
    }

    public ItemParcelaValorDivida getItemParcelaValorDivida() {
        return itemParcelaValorDivida;
    }

    public void setItemParcelaValorDivida(ItemParcelaValorDivida itemParcelaValorDivida) {
        this.itemParcelaValorDivida = itemParcelaValorDivida;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public DescontoItemParcela getDescontoItemParcela() {
        return descontoItemParcela;
    }

    public void setDescontoItemParcela(DescontoItemParcela descontoItemParcela) {
        this.descontoItemParcela = descontoItemParcela;
    }

    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }
}
