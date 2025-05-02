package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Transferência de Saldo de Conta Auxiliar")
@Table(name = "TRANSFSALDOCONTAAUXILIAR")
public class TransferenciaSaldoContaAuxiliar extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Etiqueta("Número")
    @Pesquisavel
    @Tabelavel
    private String numero;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Unidade Orçamentária")
    @Transient
    @Tabelavel
    private String unidadeOrcamentaria;
    @ManyToOne
    @Etiqueta("Conta Contabil")
    @Tabelavel
    @Obrigatorio
    private ContaContabil contaContabil;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta Auxiliar Detalhada - Débito")
    private ContaAuxiliarDetalhada contaAuxiliarDetalhadaDeb;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta Auxiliar Detalhada - Crédito")
    private ContaAuxiliarDetalhada contaAuxiliarDetalhadaCred;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta Auxiliar Siconfi - Débito")
    private ContaAuxiliar contaAuxiliarDebito;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta Auxiliar Siconfi - Crédito")
    private ContaAuxiliar contaAuxiliarCredito;
    @Etiqueta("Histórico")
    @Length(maximo = 3000)
    private String historico;
    @Etiqueta("Valor")
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Obrigatorio
    private BigDecimal valor;

    public TransferenciaSaldoContaAuxiliar() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public ContaAuxiliar getContaAuxiliarDebito() {
        return contaAuxiliarDebito;
    }

    public void setContaAuxiliarDebito(ContaAuxiliar contaAuxiliarDebito) {
        this.contaAuxiliarDebito = contaAuxiliarDebito;
    }

    public ContaAuxiliar getContaAuxiliarCredito() {
        return contaAuxiliarCredito;
    }

    public void setContaAuxiliarCredito(ContaAuxiliar contaAuxiliarCredito) {
        this.contaAuxiliarCredito = contaAuxiliarCredito;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public ContaAuxiliarDetalhada getContaAuxiliarDetalhadaDeb() {
        return contaAuxiliarDetalhadaDeb;
    }

    public void setContaAuxiliarDetalhadaDeb(ContaAuxiliarDetalhada contaAuxiliarDetalhadaDeb) {
        this.contaAuxiliarDetalhadaDeb = contaAuxiliarDetalhadaDeb;
    }

    public ContaAuxiliarDetalhada getContaAuxiliarDetalhadaCred() {
        return contaAuxiliarDetalhadaCred;
    }

    public void setContaAuxiliarDetalhadaCred(ContaAuxiliarDetalhada contaAuxiliarDetalhadaCred) {
        this.contaAuxiliarDetalhadaCred = contaAuxiliarDetalhadaCred;
    }

    @Override
    public String toString() {
        return  numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor);
    }
}
