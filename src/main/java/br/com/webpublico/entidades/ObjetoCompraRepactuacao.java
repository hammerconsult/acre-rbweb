package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 14/04/14
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Objetos de Compra da Repactuação de Preço")
public class ObjetoCompraRepactuacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RepactuacaoPreco repactuacaoPreco;

    @ManyToOne
    private ObjetoCompra objetoCompra;

    private String descricaoComplementar;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Preço Unitário Atual")
    private BigDecimal precoAtual;

    @Etiqueta("Preço Unitário Proposto")
    @Obrigatorio
    private BigDecimal precoProposto;

    public ObjetoCompraRepactuacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepactuacaoPreco getRepactuacaoPreco() {
        return repactuacaoPreco;
    }

    public void setRepactuacaoPreco(RepactuacaoPreco repactuacaoPreco) {
        this.repactuacaoPreco = repactuacaoPreco;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public BigDecimal getPrecoProposto() {
        return precoProposto;
    }

    public void setPrecoProposto(BigDecimal precoProposto) {
        this.precoProposto = precoProposto;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }
}
