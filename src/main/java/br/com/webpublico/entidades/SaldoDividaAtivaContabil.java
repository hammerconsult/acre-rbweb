/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Contabil")

@Entity
@Audited
@Etiqueta("Saldo Dívida Ativa")
public class SaldoDividaAtivaContabil extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataSaldo;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private Conta contaReceita;
    private BigDecimal inscricao;
    private BigDecimal atualizacao;
    private BigDecimal provisao;
    private BigDecimal reversao;
    private BigDecimal recebimento;
    private BigDecimal baixa;
    private BigDecimal aumentativo;
    private BigDecimal diminutivo;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva;
    @Enumerated(EnumType.STRING)
    private Intervalo intervalo;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;

    public SaldoDividaAtivaContabil() {
        super();
        inscricao = BigDecimal.ZERO;
        atualizacao = BigDecimal.ZERO;
        provisao = BigDecimal.ZERO;
        reversao = BigDecimal.ZERO;
        recebimento = BigDecimal.ZERO;
        baixa = BigDecimal.ZERO;
        aumentativo = BigDecimal.ZERO;
        diminutivo = BigDecimal.ZERO;
    }

    public BigDecimal getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(BigDecimal atualizacao) {
        this.atualizacao = atualizacao;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getProvisao() {
        return provisao;
    }

    public void setProvisao(BigDecimal provisao) {
        this.provisao = provisao;
    }

    public BigDecimal getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(BigDecimal recebimento) {
        this.recebimento = recebimento;
    }

    public BigDecimal getReversao() {
        return reversao;
    }

    public void setReversao(BigDecimal reversao) {
        this.reversao = reversao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
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

    public NaturezaDividaAtivaCreditoReceber getNaturezaDividaAtiva() {
        return naturezaDividaAtiva;
    }

    public void setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva) {
        this.naturezaDividaAtiva = naturezaDividaAtiva;
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
        try {
            return " Data: " + DataUtil.getDataFormatada(this.dataSaldo)
                + ", Conta: " + this.contaReceita
                + ", Unidade: " + unidadeOrganizacional;
        } catch (Exception ex) {
            return "";
        }
    }

    public BigDecimal getSaldoAtual() {
        BigDecimal totalCredito = this.inscricao.add(this.atualizacao).add(this.reversao).add(this.aumentativo);
        BigDecimal totalDebito = this.provisao.add(this.recebimento).add(this.baixa).add(this.diminutivo);
        return totalCredito.subtract(totalDebito);
    }
}
