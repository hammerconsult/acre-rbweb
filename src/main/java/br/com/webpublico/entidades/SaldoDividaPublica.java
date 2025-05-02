/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Publica")
@Etiqueta("Saldo Diário Dívida Pública ")
public class SaldoDividaPublica extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private DividaPublica dividapublica;
    @Enumerated(EnumType.STRING)
    private Intervalo intervalo;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    private BigDecimal inscricao;
    private BigDecimal atualizacao;
    private BigDecimal apropriacao;
    private BigDecimal pagamento;
    private BigDecimal receita;
    private BigDecimal cancelamento;
    private BigDecimal transferenciaCredito;
    private BigDecimal transferenciaDebito;
    private BigDecimal empenhado;
    @Version
    private Long versao;

    public SaldoDividaPublica() {
        super();
        inscricao = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        apropriacao = BigDecimal.ZERO;
        pagamento = BigDecimal.ZERO;
        receita = BigDecimal.ZERO;
        cancelamento = BigDecimal.ZERO;
        transferenciaCredito = BigDecimal.ZERO;
        transferenciaDebito = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
    }

    public SaldoDividaPublica(Date data, UnidadeOrganizacional unidadeOrganizacional, DividaPublica dividaPublica,
                              Intervalo intervalo,
                              ContaDeDestinacao contaDeDestinacao) {
        super();
        this.data = data;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.dividapublica = dividaPublica;
        this.intervalo = intervalo;
        this.contaDeDestinacao = contaDeDestinacao;
        if (contaDeDestinacao != null) {
            this.fonteDeRecursos = contaDeDestinacao.getFonteDeRecursos();
        }
        inscricao = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        apropriacao = BigDecimal.ZERO;
        pagamento = BigDecimal.ZERO;
        receita = BigDecimal.ZERO;
        cancelamento = BigDecimal.ZERO;
        transferenciaCredito = BigDecimal.ZERO;
        transferenciaDebito = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public DividaPublica getDividapublica() {
        return dividapublica;
    }

    public void setDividapublica(DividaPublica dividapublica) {
        this.dividapublica = dividapublica;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public BigDecimal getApropriacao() {
        return apropriacao;
    }

    public void setApropriacao(BigDecimal apropriacao) {
        this.apropriacao = apropriacao;
    }

    public BigDecimal getPagamento() {
        return pagamento;
    }

    public void setPagamento(BigDecimal pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getReceita() {
        return receita;
    }

    public void setReceita(BigDecimal receita) {
        this.receita = receita;
    }

    public BigDecimal getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(BigDecimal cancelamento) {
        this.cancelamento = cancelamento;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public BigDecimal getTransferenciaCredito() {
        return transferenciaCredito;
    }

    public void setTransferenciaCredito(BigDecimal transferenciaCredito) {
        this.transferenciaCredito = transferenciaCredito;
    }

    public BigDecimal getTransferenciaDebito() {
        return transferenciaDebito;
    }

    public void setTransferenciaDebito(BigDecimal transferenciaDebito) {
        this.transferenciaDebito = transferenciaDebito;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    @Override
    public String toString() {
        return "DATA ..: " + new SimpleDateFormat("dd/MM/yyyy").format(data) + "ID : " + id + " INSCRICAO :" + inscricao + " ATUALIZADO : " + atualizacao + " RECEITA : " + receita + " APROPRIACAO : " + apropriacao + " PAGAMENTO : " + pagamento + " CANCELAMENTO : " + cancelamento;
    }

    public BigDecimal getSaldoReal() {
        return inscricao.add(atualizacao).add(receita).add(transferenciaCredito).subtract(pagamento).subtract(apropriacao).subtract(cancelamento).subtract(transferenciaDebito);
    }

    public BigDecimal getSaldoRealComPagamentoEEmpenhado() {
        return getSaldoReal().add(pagamento).subtract(empenhado);
    }

    public BigDecimal getSaldoRealComEmpenhado() {
        return getSaldoReal().subtract(empenhado);
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
