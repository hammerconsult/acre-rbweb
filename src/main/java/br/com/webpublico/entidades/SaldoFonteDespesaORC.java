/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author venon
 */
@Entity
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Saldo Fonte Despesa Orçamentária")
public class SaldoFonteDespesaORC extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Dotação")
    private BigDecimal dotacao;

    @Tabelavel
    @Etiqueta("Reservado")
    private BigDecimal reservado;

    @Tabelavel
    @Etiqueta("Reservado po Licitação")
    private BigDecimal reservadoPorLicitacao;

    @Tabelavel
    @Etiqueta("Alteração")
    private BigDecimal alteracao;

    @Tabelavel
    @Etiqueta("Suplementação")
    private BigDecimal suplementado;

    @Tabelavel
    @Etiqueta("Redução")
    private BigDecimal reduzido;

    @Tabelavel
    @Etiqueta("Empenhado")
    private BigDecimal empenhado;

    @Tabelavel
    @Etiqueta("Liquidado")
    private BigDecimal liquidado;

    @Tabelavel
    @Etiqueta("Pago")
    private BigDecimal pago;

    @Tabelavel
    @Etiqueta("Empenhado Processado")
    private BigDecimal empenhadoProcessado;

    @Tabelavel
    @Etiqueta("Empenhado não Processado")
    private BigDecimal empenhadoNaoProcessado;

    @Tabelavel
    @Etiqueta("Liquidado Processado")
    private BigDecimal liquidadoProcessado;

    @Tabelavel
    @Etiqueta("Liquidado não Processado")
    private BigDecimal liquidadoNaoProcessado;

    @Tabelavel
    @Etiqueta("Pago Processado")
    private BigDecimal pagoProcessado;

    @Tabelavel
    @Etiqueta("Pago não Processado")
    private BigDecimal pagoNaoProcessado;

    @Tabelavel
    @Etiqueta("Data do Saldo")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataSaldo;

    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    private FonteDespesaORC fonteDespesaORC;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Version
    private Long versao;

    public SaldoFonteDespesaORC() {
        dotacao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        reservado = BigDecimal.ZERO;
        suplementado = BigDecimal.ZERO;
        reduzido = BigDecimal.ZERO;
        empenhadoProcessado = BigDecimal.ZERO;
        empenhadoNaoProcessado = BigDecimal.ZERO;
        liquidadoProcessado = BigDecimal.ZERO;
        liquidadoNaoProcessado = BigDecimal.ZERO;
        pagoProcessado = BigDecimal.ZERO;
        pagoNaoProcessado = BigDecimal.ZERO;
        reservadoPorLicitacao = BigDecimal.ZERO;
    }


    public SaldoFonteDespesaORC(Date dataSaldo, FonteDespesaORC fonteDespesaORC, UnidadeOrganizacional unidadeOrganizacional) {
        this.dataSaldo = dataSaldo;
        this.fonteDespesaORC = fonteDespesaORC;
        this.unidadeOrganizacional = unidadeOrganizacional;
        dotacao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        reservado = BigDecimal.ZERO;
        alteracao = BigDecimal.ZERO;
        suplementado = BigDecimal.ZERO;
        reduzido = BigDecimal.ZERO;
        empenhadoProcessado = BigDecimal.ZERO;
        empenhadoNaoProcessado = BigDecimal.ZERO;
        liquidadoProcessado = BigDecimal.ZERO;
        liquidadoNaoProcessado = BigDecimal.ZERO;
        pagoProcessado = BigDecimal.ZERO;
        pagoNaoProcessado = BigDecimal.ZERO;
        reservadoPorLicitacao = BigDecimal.ZERO;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getDotacao() {
        return dotacao;
    }

    public void setDotacao(BigDecimal dotacao) {
        this.dotacao = dotacao;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(BigDecimal alteracao) {
        this.alteracao = alteracao;
    }

    public BigDecimal getReservado() {
        return reservado;
    }

    public void setReservado(BigDecimal reservado) {
        this.reservado = reservado;
    }

    public BigDecimal getSuplementado() {
        return suplementado;
    }

    public void setSuplementado(BigDecimal suplementado) {
        this.suplementado = suplementado;
    }

    public BigDecimal getReduzido() {
        return reduzido;
    }

    public void setReduzido(BigDecimal reduzido) {
        this.reduzido = reduzido;
    }

    public BigDecimal getEmpenhadoProcessado() {
        return empenhadoProcessado;
    }

    public void setEmpenhadoProcessado(BigDecimal empenhadoProcessado) {
        this.empenhadoProcessado = empenhadoProcessado;
    }

    public BigDecimal getEmpenhadoNaoProcessado() {
        return empenhadoNaoProcessado;
    }

    public void setEmpenhadoNaoProcessado(BigDecimal empenhadoNaoProcessado) {
        this.empenhadoNaoProcessado = empenhadoNaoProcessado;
    }

    public BigDecimal getLiquidadoProcessado() {
        return liquidadoProcessado;
    }

    public void setLiquidadoProcessado(BigDecimal liquidadoProcessado) {
        this.liquidadoProcessado = liquidadoProcessado;
    }

    public BigDecimal getLiquidadoNaoProcessado() {
        return liquidadoNaoProcessado;
    }

    public void setLiquidadoNaoProcessado(BigDecimal liquidadoNaoProcessado) {
        this.liquidadoNaoProcessado = liquidadoNaoProcessado;
    }

    public BigDecimal getPagoProcessado() {
        return pagoProcessado;
    }

    public void setPagoProcessado(BigDecimal pagoProcessado) {
        this.pagoProcessado = pagoProcessado;
    }

    public BigDecimal getPagoNaoProcessado() {
        return pagoNaoProcessado;
    }

    public void setPagoNaoProcessado(BigDecimal pagoNaoProcessado) {
        this.pagoNaoProcessado = pagoNaoProcessado;
    }

    public BigDecimal getReservadoPorLicitacao() {
        return reservadoPorLicitacao;
    }

    public void setReservadoPorLicitacao(BigDecimal reservadoPorLicitacao) {
        this.reservadoPorLicitacao = reservadoPorLicitacao;
    }

    @Override
    public String toString() {
        return "Data do Saldo: " + dataSaldo + " - Dotação: " + dotacao;
    }

    public BigDecimal getSaldoAtual() {
        BigDecimal dotacaoAtual = getSaldoDotacaoAtual();
        BigDecimal debito = (this.getEmpenhado().add(this.getReservado().add(this.getReservadoPorLicitacao())));
        return dotacaoAtual.subtract(debito);
    }


    public BigDecimal getSaldoDotacaoAtual() {
        return this.getDotacao().add(getAlteracao());
    }

    public Boolean isNegativo() {
        return getSaldoAtual().compareTo(BigDecimal.ZERO) < 0;
    }
}
