/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Lance Pregão")
public class LancePregao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Status")
    private StatusLancePregao statusLancePregao;

    @ManyToOne
    @Etiqueta("Rodada Pregão")
    private RodadaPregao rodadaPregao;

    @ManyToOne
    @Etiqueta("Proposta do Fornecedor")
    private PropostaFornecedor propostaFornecedor;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Etiqueta("Percentual Desconto")
    private BigDecimal percentualDesconto;

    @Etiqueta("Motivo")
    private String justificativaCancelamento;

    @Invisivel
    @Transient
    private BigDecimal valorPropostaInicial;


    @Invisivel
    @Transient
    private BigDecimal valorNaRodadaAnterior;

    @Invisivel
    @Transient
    private BigDecimal valorFinal;

    @Invisivel
    @Transient
    private String marca;

    public LancePregao() {
        super();
        this.valor = BigDecimal.ZERO;
        this.percentualDesconto = BigDecimal.ZERO;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusLancePregao getStatusLancePregao() {
        return statusLancePregao;
    }

    public void setStatusLancePregao(StatusLancePregao statusLancePregao) {
        this.statusLancePregao = statusLancePregao;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public RodadaPregao getRodadaPregao() {
        return rodadaPregao;
    }

    public void setRodadaPregao(RodadaPregao rodadaPregao) {
        this.rodadaPregao = rodadaPregao;
    }

    public String getJustificativaCancelamento() {
        return justificativaCancelamento;
    }

    public void setJustificativaCancelamento(String justificativaCancelamento) {
        this.justificativaCancelamento = justificativaCancelamento;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public BigDecimal getValorPropostaInicial() {
        if (valorPropostaInicial == null) {
            valorPropostaInicial = BigDecimal.ZERO;
        }
        return valorPropostaInicial;
    }

    public void setValorPropostaInicial(BigDecimal valorPropostaInicial) {
        this.valorPropostaInicial = valorPropostaInicial;
    }

    public BigDecimal getValorNaRodadaAnterior() {
        if (valorNaRodadaAnterior == null) {
            valorNaRodadaAnterior = BigDecimal.ZERO;
        }
        return valorNaRodadaAnterior;
    }

    public void setValorNaRodadaAnterior(BigDecimal valorNaRodadaAnterior) {
        this.valorNaRodadaAnterior = valorNaRodadaAnterior;
    }

    public Pessoa getFornecedorDoLanceVencedor() {
        return this.getPropostaFornecedor().getFornecedor();
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    @Override
    public String toString() {
        return this.valor != null ? this.valor.toString() : this.percentualDesconto != null ? this.percentualDesconto.toString() : "";
    }
}
