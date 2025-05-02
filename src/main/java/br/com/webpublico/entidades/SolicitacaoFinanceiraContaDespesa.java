package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/08/14
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Solicitação Financeira com Conta Despesa")
@Table(name = "SOLICITAFINANCEIRADESPESA")
public class SolicitacaoFinanceiraContaDespesa extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitação Fianceira")
    private SolicitacaoCotaFinanceira solicitacao;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Elemento de Despesa")
    private DespesaORC despesaORC;

    @Obrigatorio
    @Monetario
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Transient
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;

    public SolicitacaoFinanceiraContaDespesa() {
        this.valor = BigDecimal.ZERO;
        this.saldo = BigDecimal.ZERO;
    }

    public SolicitacaoFinanceiraContaDespesa(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
        this.valor = BigDecimal.ZERO;
        this.saldo = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoCotaFinanceira getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoCotaFinanceira solicitacao) {
        this.solicitacao = solicitacao;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return despesaORC.getDescricaoContaDespesaPPA();
    }
}
