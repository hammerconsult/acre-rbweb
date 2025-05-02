/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "DividaDiversa")

@Entity
@Audited
public class ItemParametroTipoDividaDiv implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroTipoDividaDiversa parametroTipoDividaDiversa;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Integer quantidadeMaximaParcela;
    private BigDecimal percentualDescontoMulta;
    private BigDecimal percentualDescontoJuros;
    private BigDecimal percentualDescontoCorrecao;
    @Transient
    private Long criadoEm;

    public ItemParametroTipoDividaDiv() {
        criadoEm = System.nanoTime();
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

    public ParametroTipoDividaDiversa getParametroTipoDividaDiversa() {
        return parametroTipoDividaDiversa;
    }

    public void setParametroTipoDividaDiversa(ParametroTipoDividaDiversa parametroTipoDividaDiversa) {
        this.parametroTipoDividaDiversa = parametroTipoDividaDiversa;
    }

    public BigDecimal getPercentualDescontoCorrecao() {
        return percentualDescontoCorrecao;
    }

    public void setPercentualDescontoCorrecao(BigDecimal percentualDescontoCorrecao) {
        this.percentualDescontoCorrecao = percentualDescontoCorrecao;
    }

    public BigDecimal getPercentualDescontoJuros() {
        return percentualDescontoJuros;
    }

    public void setPercentualDescontoJuros(BigDecimal percentualDescontoJuros) {
        this.percentualDescontoJuros = percentualDescontoJuros;
    }

    public BigDecimal getPercentualDescontoMulta() {
        return percentualDescontoMulta;
    }

    public void setPercentualDescontoMulta(BigDecimal percentualDescontoMulta) {
        this.percentualDescontoMulta = percentualDescontoMulta;
    }

    public Integer getQuantidadeMaximaParcela() {
        return quantidadeMaximaParcela;
    }

    public void setQuantidadeMaximaParcela(Integer quantidadeMaximaParcela) {
        this.quantidadeMaximaParcela = quantidadeMaximaParcela;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemParametroTipoDividaDiv[ id=" + id + " ]";
    }
}
