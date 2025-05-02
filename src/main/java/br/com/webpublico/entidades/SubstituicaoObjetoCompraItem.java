package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Etiqueta("Item Substituição Objeto de Compra")
public class SubstituicaoObjetoCompraItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número Item")
    private Long numeroItem;

    @ManyToOne
    @Etiqueta("Substituição Objeto de Compra")
    private SubstituicaoObjetoCompra substituicaoObjetoCompra;

    @ManyToOne
    @Etiqueta("Objeto de Compra De")
    private ObjetoCompra objetoCompraDe;

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

    public void setId(Long id) {
        this.id = id;
    }

    public SubstituicaoObjetoCompra getSubstituicaoObjetoCompra() {
        return substituicaoObjetoCompra;
    }

    public void setSubstituicaoObjetoCompra(SubstituicaoObjetoCompra substituicaoObjetoCompra) {
        this.substituicaoObjetoCompra = substituicaoObjetoCompra;
    }

    public ObjetoCompra getObjetoCompraDe() {
        return objetoCompraDe;
    }

    public void setObjetoCompraDe(ObjetoCompra objetoCompraDe) {
        this.objetoCompraDe = objetoCompraDe;
    }

    public ObjetoCompra getObjetoCompraPara() {
        return objetoCompraPara;
    }

    public void setObjetoCompraPara(ObjetoCompra objetoCompraPara) {
        this.objetoCompraPara = objetoCompraPara;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
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
}
