/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author venon
 */
@Entity
@Etiqueta("Saldo Conta Financeira")
public class SaldoSubConta extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataSaldo;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    private BigDecimal valor;
    @ManyToOne
    private SubConta subConta;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    private BigDecimal totalCredito;
    private BigDecimal totalDebito;
    @Version
    private Long versao;

    public SaldoSubConta() {
        valor = new BigDecimal(BigInteger.ZERO);
        totalCredito = BigDecimal.ZERO;
        totalDebito = BigDecimal.ZERO;
    }

    public SaldoSubConta(Date dataSaldo, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta) {
        this.valor = new BigDecimal(BigInteger.ZERO);
        this.totalCredito = new BigDecimal(BigInteger.ZERO);
        this.totalDebito = new BigDecimal(BigInteger.ZERO);
        this.dataSaldo = dataSaldo;
        this.contaDeDestinacao = contaDeDestinacao;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.subConta = subConta;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
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

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "Data: " + DataUtil.getDataFormatada(dataSaldo) + " , valor: " + Util.formataValor((totalCredito.subtract(totalDebito)));
    }

}
