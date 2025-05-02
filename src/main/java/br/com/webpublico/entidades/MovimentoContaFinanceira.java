/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Edi
 */
@Entity
@Etiqueta("Movimento Conta Financeira")
public class MovimentoContaFinanceira extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Etiqueta("Movimentação Financeira")
    @Enumerated(EnumType.STRING)
    MovimentacaoFinanceira movimentacaoFinanceira;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Saldo")
    private Date dataSaldo;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;
    @ManyToOne
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @Etiqueta("Histórico")
    private String historico;
    @Etiqueta("Total de Débito")
    private BigDecimal totalCredito;

    @Etiqueta("Total de Crédito")
    private BigDecimal totalDebito;

    @Transient
    private TipoOperacao tipoOperacao;

    private String uuid;

    public MovimentoContaFinanceira() {
        totalCredito = BigDecimal.ZERO;
        totalDebito = BigDecimal.ZERO;
    }

    public BigDecimal getSaldoDoDia() {
        return totalCredito.subtract(totalDebito);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public MovimentacaoFinanceira getMovimentacaoFinanceira() {
        return movimentacaoFinanceira;
    }

    public void setMovimentacaoFinanceira(MovimentacaoFinanceira movimentacaoFinanceira) {
        this.movimentacaoFinanceira = movimentacaoFinanceira;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }
}
