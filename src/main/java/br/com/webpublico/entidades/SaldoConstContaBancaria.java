/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author reidocrime
 */
@Entity
@Audited
@Etiqueta("Saldo Constante Conta Bancária")

public class SaldoConstContaBancaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data do Saldo")
    @Tabelavel
    @Pesquisavel
    private Date dataSaldo;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Bancária")
    @Tabelavel
    @Pesquisavel
    private ContaBancariaEntidade contaBancariaEntidade;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    private BigDecimal valor;
    @Etiqueta("Histórico")
    private String historico;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public SaldoConstContaBancaria() {
        valor = new BigDecimal(BigInteger.ZERO);
    }

    public SaldoConstContaBancaria(String numero, Date dataSaldo, BigDecimal valor, ContaBancariaEntidade contaBancariaEntidade) {
        this.numero = numero;
        this.dataSaldo = dataSaldo;
        this.valor = valor;
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public String toString() {
        return numero;
    }
}
