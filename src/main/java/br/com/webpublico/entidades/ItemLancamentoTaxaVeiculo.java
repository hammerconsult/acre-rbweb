package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemLancamentoTaxaVeiculo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LancamentoTaxaVeiculo lancamentoTaxaVeiculo;
    @Obrigatorio
    @Etiqueta("Taxa")
    @ManyToOne
    private TaxaVeiculo taxaVeiculo;
    @Obrigatorio
    @Etiqueta("Vencimento")
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;
    @Etiqueta("Valor")
    @Obrigatorio
    private BigDecimal valor;
    @Etiqueta("Já foi Paga?")
    @Obrigatorio
    private Boolean foiPaga;


    public ItemLancamentoTaxaVeiculo() {
        this.foiPaga = Boolean.FALSE;
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LancamentoTaxaVeiculo getLancamentoTaxaVeiculo() {
        return lancamentoTaxaVeiculo;
    }

    public void setLancamentoTaxaVeiculo(LancamentoTaxaVeiculo lancamentoTaxaVeiculo) {
        this.lancamentoTaxaVeiculo = lancamentoTaxaVeiculo;
    }

    public TaxaVeiculo getTaxaVeiculo() {
        return taxaVeiculo;
    }

    public void setTaxaVeiculo(TaxaVeiculo taxaVeiculo) {
        this.taxaVeiculo = taxaVeiculo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Boolean getFoiPaga() {
        return foiPaga;
    }

    public void setFoiPaga(Boolean foiPaga) {
        this.foiPaga = foiPaga;
    }

    public static ItemLancamentoTaxaVeiculo clonar(ItemLancamentoTaxaVeiculo itemLancamentoTaxaVeiculo) {
        ItemLancamentoTaxaVeiculo clone = new ItemLancamentoTaxaVeiculo();
        clone.setId(itemLancamentoTaxaVeiculo.getId());
        clone.setLancamentoTaxaVeiculo(itemLancamentoTaxaVeiculo.getLancamentoTaxaVeiculo());
        clone.setTaxaVeiculo(itemLancamentoTaxaVeiculo.getTaxaVeiculo());
        clone.setDataVencimento(itemLancamentoTaxaVeiculo.getDataVencimento());
        clone.setValor(itemLancamentoTaxaVeiculo.getValor());
        clone.setFoiPaga(itemLancamentoTaxaVeiculo.getFoiPaga());
        return clone;
    }
}
