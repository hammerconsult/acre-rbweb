/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Contabil")

@Entity
@Audited
@Etiqueta("Saldo Crédito a Receber")
public class SaldoCreditoReceber extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataSaldo;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private Conta contaReceita;
    private BigDecimal reconhecimentoCredito;
    private BigDecimal deducaoReconhecimento;
    private BigDecimal provisaoPerdaCredito;
    private BigDecimal reversaoProvisaoPerdaCredito;
    private BigDecimal baixaReconhecimento;
    private BigDecimal baixaDeducao;
    private BigDecimal recebimento;
    private BigDecimal aumentativo;
    private BigDecimal diminutivo;
    private BigDecimal atualizacao;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber;
    @Enumerated(EnumType.STRING)
    private Intervalo intervalo;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;

    public SaldoCreditoReceber() {
        super();
        reconhecimentoCredito = BigDecimal.ZERO;
        deducaoReconhecimento = BigDecimal.ZERO;
        provisaoPerdaCredito = BigDecimal.ZERO;
        reversaoProvisaoPerdaCredito = BigDecimal.ZERO;
        baixaReconhecimento = BigDecimal.ZERO;
        baixaDeducao = BigDecimal.ZERO;
        recebimento = BigDecimal.ZERO;
        aumentativo = BigDecimal.ZERO;
        diminutivo = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
    }

    public BigDecimal getAumentativo() {
        return aumentativo;
    }

    public void setAumentativo(BigDecimal aumentativo) {
        this.aumentativo = aumentativo;
    }

    public BigDecimal getDiminutivo() {
        return diminutivo;
    }

    public void setDiminutivo(BigDecimal diminutivo) {
        this.diminutivo = diminutivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaixaDeducao() {
        return baixaDeducao;
    }

    public void setBaixaDeducao(BigDecimal baixaDeducao) {
        this.baixaDeducao = baixaDeducao;
    }

    public BigDecimal getBaixaReconhecimento() {
        return baixaReconhecimento;
    }

    public void setBaixaReconhecimento(BigDecimal baixaReconhecimento) {
        this.baixaReconhecimento = baixaReconhecimento;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getDeducaoReconhecimento() {
        return deducaoReconhecimento;
    }

    public void setDeducaoReconhecimento(BigDecimal deducaoReconhecimento) {
        this.deducaoReconhecimento = deducaoReconhecimento;
    }

    public BigDecimal getProvisaoPerdaCredito() {
        return provisaoPerdaCredito;
    }

    public void setProvisaoPerdaCredito(BigDecimal provisaoPerdaCredito) {
        this.provisaoPerdaCredito = provisaoPerdaCredito;
    }

    public BigDecimal getReconhecimentoCredito() {
        return reconhecimentoCredito;
    }

    public void setReconhecimentoCredito(BigDecimal reconhecimentoCredito) {
        this.reconhecimentoCredito = reconhecimentoCredito;
    }

    public BigDecimal getReversaoProvisaoPerdaCredito() {
        return reversaoProvisaoPerdaCredito;
    }

    public void setReversaoProvisaoPerdaCredito(BigDecimal reversaoProvisaoPerdaCredito) {
        this.reversaoProvisaoPerdaCredito = reversaoProvisaoPerdaCredito;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(BigDecimal recebimento) {
        this.recebimento = recebimento;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public NaturezaDividaAtivaCreditoReceber getNaturezaCreditoReceber() {
        return naturezaCreditoReceber;
    }

    public void setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber) {
        this.naturezaCreditoReceber = naturezaCreditoReceber;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.SaldoCreditoReceber[ id=" + id + " ]";
    }

    public BigDecimal getSaldoAtual() {
        BigDecimal valorCredito = reconhecimentoCredito.add(reversaoProvisaoPerdaCredito).add(baixaDeducao).add(aumentativo).add(atualizacao);
        BigDecimal valorDebito = deducaoReconhecimento.add(provisaoPerdaCredito).add(recebimento).add(baixaReconhecimento).add(diminutivo);
        return valorCredito.subtract(valorDebito);
    }
}
