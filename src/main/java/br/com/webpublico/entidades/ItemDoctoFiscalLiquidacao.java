package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 13/05/14
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Item Documento Fiscal da Liquidação")
public class ItemDoctoFiscalLiquidacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Quantidade")
    @Positivo
    @Tabelavel
    @Obrigatorio
    private BigDecimal quantidade;

    @Etiqueta("Valor Unitário")
    @Tabelavel
    @Obrigatorio
    private BigDecimal valorUnitario;

    @Etiqueta("Documento Fiscal da Liquidação")
    @Tabelavel
    @Obrigatorio
    @ManyToOne(cascade = CascadeType.ALL)
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;


    public ItemDoctoFiscalLiquidacao() {
        quantidade = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasQuantidade() && hasValorUnitario()) {
            total = quantidade.multiply(valorUnitario);
        }
        return total.setScale(2, RoundingMode.HALF_EVEN);
    }
}
