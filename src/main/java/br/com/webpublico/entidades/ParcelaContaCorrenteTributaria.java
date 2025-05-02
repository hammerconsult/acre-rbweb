/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDiferencaContaCorrente;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Fabio
 * @data 06/08/2018
 */
@GrupoDiagrama(nome = "Arrecadação")
@Entity
@Audited
@Table(name = "PARCELACONTACORRENTETRIB")
public class ParcelaContaCorrenteTributaria extends SuperEntidade implements Serializable, Comparable<ParcelaContaCorrenteTributaria> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private ContaCorrenteTributaria contaCorrente;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private CalculoContaCorrente calculoContaCorrente;
    @Enumerated(EnumType.STRING)
    private TipoDiferencaContaCorrente tipoDiferenca;
    private BigDecimal valorDiferenca;
    private BigDecimal valorDiferencaUtilizada;
    @Transient
    private BigDecimal valorDiferencaAtualizada;
    @Transient
    private ResultadoParcela resultadoParcela;
    @Enumerated(EnumType.STRING)
    private ContaCorrenteTributaria.Origem origem;
    @ManyToOne
    private ItemLoteBaixa itemLoteBaixa;
    private Boolean compensada;
    private BigDecimal valorCompesado;

    public ParcelaContaCorrenteTributaria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaCorrenteTributaria getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(ContaCorrenteTributaria contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public TipoDiferencaContaCorrente getTipoDiferenca() {
        return tipoDiferenca;
    }

    public void setTipoDiferenca(TipoDiferencaContaCorrente tipoDiferenca) {
        this.tipoDiferenca = tipoDiferenca;
    }

    public BigDecimal getValorDiferenca() {
        return valorDiferenca != null ? valorDiferenca : BigDecimal.ZERO;
    }

    public void setValorDiferenca(BigDecimal valorDiferenca) {
        this.valorDiferenca = valorDiferenca;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public BigDecimal getValorDiferencaAtualizada() {
        return valorDiferencaUtilizada != null && valorDiferencaUtilizada.compareTo(BigDecimal.ZERO) != 0
                ? valorDiferencaUtilizada : valorDiferencaAtualizada != null ? valorDiferencaAtualizada : valorDiferenca;
    }

    public void setValorDiferencaAtualizada(BigDecimal valorDiferencaAtualizada) {
        this.valorDiferencaAtualizada = valorDiferencaAtualizada;
    }

    public CalculoContaCorrente getCalculoContaCorrente() {
        return calculoContaCorrente;
    }

    public void setCalculoContaCorrente(CalculoContaCorrente calculoContaCorrente) {
        this.calculoContaCorrente = calculoContaCorrente;
    }

    public BigDecimal getValorDiferencaUtilizada() {
        return valorDiferencaUtilizada;
    }

    public void setValorDiferencaUtilizada(BigDecimal valorDiferencaUtilizada) {
        this.valorDiferencaUtilizada = valorDiferencaUtilizada;
    }

    public ContaCorrenteTributaria.Origem getOrigem() {
        return origem;
    }

    public void setOrigem(ContaCorrenteTributaria.Origem origem) {
        this.origem = origem;
    }

    public ItemLoteBaixa getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(ItemLoteBaixa itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public Boolean getCompensada() {
        return compensada != null ? compensada : false;
    }

    public void setCompensada(Boolean compensada) {
        this.compensada = compensada;
    }

    public BigDecimal getValorCompesado() {
        return valorCompesado != null ? valorCompesado : BigDecimal.ZERO;
    }

    public void setValorCompesado(BigDecimal valorCompesado) {
        this.valorCompesado = valorCompesado;
    }

    @Override
    public int compareTo(ParcelaContaCorrenteTributaria o) {
        int retorno = this.getParcelaValorDivida() != null ? this.getParcelaValorDivida().getId().compareTo(o.getParcelaValorDivida().getId()) : 0;
        if (this.getResultadoParcela() != null) {
            retorno = this.getResultadoParcela().getCadastro().compareTo(o.getResultadoParcela().getCadastro());
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getDivida().compareTo(o.getResultadoParcela().getDivida());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getExercicio().compareTo(o.getResultadoParcela().getExercicio());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getReferencia().compareTo(o.getResultadoParcela().getReferencia());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getSd().compareTo(o.getResultadoParcela().getSd());
            }
            if (retorno == 0) {
                retorno = o.getResultadoParcela().getCotaUnica().compareTo(this.getResultadoParcela().getCotaUnica());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getVencimento().compareTo(o.getResultadoParcela().getVencimento());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getParcela().compareTo(o.getResultadoParcela().getParcela());
            }
            if (retorno == 0) {
                retorno = this.getResultadoParcela().getSituacao().compareTo(o.getResultadoParcela().getSituacao());
            }
        }
        return retorno;
    }
}
