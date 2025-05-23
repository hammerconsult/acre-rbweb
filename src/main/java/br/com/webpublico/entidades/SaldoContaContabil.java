package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Entity

/**
 * Consolida os débitos e créditos dos Lançamentos Contábeis de determinado dia.
 * Utilizado para melhorar o desempenho das consultas que apresentam os dados
 * contábeis a partir do saldo.
 */
public class SaldoContaContabil implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Conta contaContabil;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Temporal(TemporalType.DATE)
    private Date dataSaldo;
    @Monetario
    private BigDecimal totalDebito;
    @Monetario
    private BigDecimal totalCredito;
    @Enumerated(EnumType.STRING)
    private TipoBalancete tipoBalancete;
    @Transient
    private Long criadoEm;

    public SaldoContaContabil() {
        totalCredito = BigDecimal.ZERO;
        totalDebito = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
        tipoBalancete = TipoBalancete.MENSAL;
    }

    public Conta getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(Conta contaContabil) {
        this.contaContabil = contaContabil;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = totalCredito;
    }

    public BigDecimal getTotalDebito() {
        return totalDebito;
    }

    public void setTotalDebito(BigDecimal totalDebito) {
        this.totalDebito = totalDebito;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getSaldoDoDia() {
        return totalCredito.subtract(totalDebito);
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return contaContabil + " - " + Util.formataValor(getSaldoDoDia());
    }
}
