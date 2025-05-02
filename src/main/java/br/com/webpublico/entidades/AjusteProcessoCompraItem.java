package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Item Ajsute Processo de Compra")
public class AjusteProcessoCompraItem extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número Item")
    private Long numeroItem;

    @ManyToOne
    @Etiqueta("Ajuste Processo Compra")
    private AjusteProcessoCompra ajusteProcessoCompra;

    @ManyToOne
    @Etiqueta("Item Proposta Fornecedor")
    private ItemPropostaFornecedor itemPropostaFornecedor;

    @ManyToOne
    @Etiqueta("Item Processo de Compra")
    private ItemProcessoDeCompra itemProcessoCompra;

    @ManyToOne
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @ManyToOne
    @Etiqueta("Objeto de Compra Para")
    private ObjetoCompra objetoCompraPara;

    @Etiqueta("Especificação De")
    @Lob
    private String especificacaoDe;

    @Etiqueta("Especificação Para")
    @Lob
    private String especificacaoPara;

    @ManyToOne
    @Etiqueta("Unidade Medida De")
    private UnidadeMedida unidadeMedidaDe;

    @ManyToOne
    @Etiqueta("Unidade Medida Para")
    private UnidadeMedida unidadeMedidaPara;

    @Etiqueta("Quantidade De")
    private BigDecimal quantidadeDe;

    @Etiqueta("Quantidade Para")
    private BigDecimal quantidadePara;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public AjusteProcessoCompra getAjusteProcessoCompra() {
        return ajusteProcessoCompra;
    }

    public void setAjusteProcessoCompra(AjusteProcessoCompra ajusteProcessoCompra) {
        this.ajusteProcessoCompra = ajusteProcessoCompra;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
    }

    public ObjetoCompra getObjetoCompraPara() {
        return objetoCompraPara;
    }

    public void setObjetoCompraPara(ObjetoCompra objetoCompraPara) {
        this.objetoCompraPara = objetoCompraPara;
    }

    public String getEspecificacaoDe() {
        return especificacaoDe;
    }

    public void setEspecificacaoDe(String especificacaoDe) {
        this.especificacaoDe = especificacaoDe;
    }

    public String getEspecificacaoPara() {
        return especificacaoPara;
    }

    public void setEspecificacaoPara(String especificacaoPara) {
        this.especificacaoPara = especificacaoPara;
    }

    public UnidadeMedida getUnidadeMedidaDe() {
        return unidadeMedidaDe;
    }

    public void setUnidadeMedidaDe(UnidadeMedida unidadeMedidaDe) {
        this.unidadeMedidaDe = unidadeMedidaDe;
    }

    public UnidadeMedida getUnidadeMedidaPara() {
        return unidadeMedidaPara;
    }

    public void setUnidadeMedidaPara(UnidadeMedida unidadeMedidaPara) {
        this.unidadeMedidaPara = unidadeMedidaPara;
    }

    public BigDecimal getQuantidadeDe() {
        return quantidadeDe;
    }

    public void setQuantidadeDe(BigDecimal quantidadeDe) {
        this.quantidadeDe = quantidadeDe;
    }

    public BigDecimal getQuantidadePara() {
        return quantidadePara;
    }

    public void setQuantidadePara(BigDecimal quantidadePara) {
        this.quantidadePara = quantidadePara;
    }
}
