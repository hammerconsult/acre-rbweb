package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Table(name = "MOVIMENTOALTERACAOCONTITEM")
@Etiqueta("Movimento Alteração Contratual Item")
public class MovimentoAlteracaoContratualItem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private MovimentoAlteracaoContratual movimentoAlteracaoCont;

    @ManyToOne
    private ItemContrato itemContrato;

    @ManyToOne
    private ItemProcessoDeCompra itemProcessoCompra;

    @ManyToOne
    private Pessoa fornecedor;

    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    @Transient
    private BigDecimal valorUnitarioProcesso;
    @Transient
    private BigDecimal valorTotalProcesso;

    public MovimentoAlteracaoContratualItem() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorUnitarioProcesso = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorTotalProcesso = BigDecimal.ZERO;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public MovimentoAlteracaoContratual getMovimentoAlteracaoCont() {
        return movimentoAlteracaoCont;
    }

    public void setMovimentoAlteracaoCont(MovimentoAlteracaoContratual movimentoAlteracaoContratual) {
        this.movimentoAlteracaoCont = movimentoAlteracaoContratual;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
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

    public void setQuantidade(BigDecimal quantidadeAjuste) {
        this.quantidade = quantidadeAjuste;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorAjuste) {
        this.valorUnitario = valorAjuste;
    }

    public BigDecimal calcularValorTotal() {
        if (hasQuantidadeMaiorQueZero() && hasValorUnitarioMaiorQueZero()) {
            BigDecimal total = getQuantidade().multiply(getValorUnitario());
            return total.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            if (itemContrato.getControleQuantidade()) {
                return getUnidadeMedida().getMascaraValorUnitario();
            }
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        } catch (NullPointerException e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        try {
            return getUnidadeMedida().getMascaraQuantidade();
        } catch (NullPointerException e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public BigDecimal getValorUnitarioProcesso() {
        return valorUnitarioProcesso;
    }

    public void setValorUnitarioProcesso(BigDecimal valorUnitarioProcesso) {
        this.valorUnitarioProcesso = valorUnitarioProcesso;
    }

    public BigDecimal getValorTotalProcesso() {
        return valorTotalProcesso;
    }

    public void setValorTotalProcesso(BigDecimal valorTotalProcesso) {
        this.valorTotalProcesso = valorTotalProcesso;
    }

    public String getNumeroDescricao(){
        if (isMovItemContrato()){
            return itemContrato.getNumeroDescricao();
        }
        return itemProcessoCompra.getNumeroDescricao();
    }

    public ObjetoCompra getObjetoCompra(){
        if (isMovItemContrato()){
            return itemContrato.getItemAdequado().getObjetoCompra();
        }
        return itemProcessoCompra.getObjetoCompra();
    }

    public UnidadeMedida getUnidadeMedida(){
        if (isMovItemContrato()){
            return itemContrato.getItemAdequado().getUnidadeMedida();
        }
        return itemProcessoCompra.getUnidadeMedida();
    }

    public BigDecimal getQuantidadeProcesso(){
        if (isMovItemContrato()){
            return itemContrato.getQuantidadeTotalContrato();
        }
        return itemProcessoCompra.getQuantidade();
    }


    public boolean isMovItemContrato(){
        return itemContrato !=null;
    }

    public boolean hasValorTotalMaiorQueZero() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitarioMaiorQueZero() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasQuantidadeMaiorQueZero() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }
}
