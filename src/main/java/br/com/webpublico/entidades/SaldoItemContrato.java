package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@GrupoDiagrama(nome = "Contrato")
@Audited
@Etiqueta("Saldo Item Contrato")
public class SaldoItemContrato extends SuperEntidade implements Comparable<SaldoItemContrato> {

    private static final Logger logger = LoggerFactory.getLogger(SaldoItemContrato.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Contrato")
    private ItemContrato itemContrato;

    @Etiqueta("Id Origem")
    private Long idOrigem;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data Saldo")
    private Date dataSaldo;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem")
    private OrigemSaldoItemContrato origem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Subtipo")
    private OperacaoSaldoItemContrato operacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("SubTipo")
    private SubTipoSaldoItemContrato subTipo;

    @Monetario
    @Etiqueta("Quantidade")
    private BigDecimal saldoQuantidade;

    @Monetario
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Monetario
    @Etiqueta("Valor Total")
    private BigDecimal saldoValor;

    public SaldoItemContrato() {
        saldoQuantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        saldoValor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public OrigemSaldoItemContrato getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoItemContrato origem) {
        this.origem = origem;
    }

    public OperacaoSaldoItemContrato getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoSaldoItemContrato operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getSaldoQuantidade() {
        return saldoQuantidade;
    }

    public void setSaldoQuantidade(BigDecimal saldoQuantidade) {
        this.saldoQuantidade = saldoQuantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getSaldoValor() {
        return saldoValor;
    }

    public void setSaldoValor(BigDecimal saldoValor) {
        this.saldoValor = saldoValor;
    }

    public SubTipoSaldoItemContrato getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(SubTipoSaldoItemContrato subTipo) {
        this.subTipo = subTipo;
    }

    @Override
    public int compareTo(SaldoItemContrato o) {
        return ComparisonChain.start()
            .compare(this.origem.getOrdem(), o.getOrigem().getOrdem())
            .compare(this.idOrigem, o.getIdOrigem())
            .compare(this.dataSaldo, o.getDataSaldo())
            .result();
    }
}
