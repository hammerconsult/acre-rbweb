package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by RenatoRomanini on 11/05/2015.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class GuiaDARFSimples extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código de Receita Tributo")
    private String codigoReceitaTributo;
    @Obrigatorio
    @Etiqueta("Código Identificador do Tributo")
    private String codigoIdentificacaoTributo;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Período de Apuração")
    private Date periodoApuracao;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor de Receita Bruta")
    private BigDecimal valorReceitaBruta;
    @Obrigatorio
    @Monetario
    @Etiqueta("Percentual de Receita Bruta")
    private BigDecimal percentualReceitaBruta;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor Principal")
    private BigDecimal valorPrincipal;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor de Multa")
    private BigDecimal valorMulta;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor de Juros")
    private BigDecimal valorJuros;

    public GuiaDARFSimples() {
        this.valorReceitaBruta = BigDecimal.ZERO;
        this.percentualReceitaBruta = BigDecimal.ZERO;
        this.valorPrincipal = BigDecimal.ZERO;
        this.valorMulta = BigDecimal.ZERO;
        this.valorJuros = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoReceitaTributo() {
        return codigoReceitaTributo;
    }

    public void setCodigoReceitaTributo(String codigoReceitaTributo) {
        this.codigoReceitaTributo = codigoReceitaTributo;
    }

    public String getCodigoIdentificacaoTributo() {
        return codigoIdentificacaoTributo;
    }

    public void setCodigoIdentificacaoTributo(String codigoIdentificacaoTributo) {
        this.codigoIdentificacaoTributo = codigoIdentificacaoTributo;
    }

    public Date getPeriodoApuracao() {
        return periodoApuracao;
    }

    public void setPeriodoApuracao(Date periodoApuracao) {
        this.periodoApuracao = periodoApuracao;
    }

    public BigDecimal getValorReceitaBruta() {
        return valorReceitaBruta;
    }

    public void setValorReceitaBruta(BigDecimal valorReceitaBruta) {
        this.valorReceitaBruta = valorReceitaBruta;
    }

    public BigDecimal getPercentualReceitaBruta() {
        return percentualReceitaBruta;
    }

    public void setPercentualReceitaBruta(BigDecimal percentualReceitaBruta) {
        this.percentualReceitaBruta = percentualReceitaBruta;
    }

    public BigDecimal getValorPrincipal() {
        return valorPrincipal;
    }

    public void setValorPrincipal(BigDecimal valorPrincipal) {
        this.valorPrincipal = valorPrincipal;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(periodoApuracao) + " - " + Util.formataValor(valorPrincipal);
    }
}
