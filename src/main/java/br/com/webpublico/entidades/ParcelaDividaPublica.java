/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class ParcelaDividaPublica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data da Parcela")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataParcela;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor Amortizado")
    @Tabelavel
    private BigDecimal valorAmortizado;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor de Juros")
    @Tabelavel
    private BigDecimal valorJuros;
    @Obrigatorio
    @Monetario
    @Etiqueta("Saldo Devedor")
    @Tabelavel
    private BigDecimal saldo;
    @Obrigatorio
    @Monetario
    @Tabelavel
    @Etiqueta("Valor da Prestação")
    private BigDecimal valorPrestacao;
    @Monetario
    @Tabelavel
    @Etiqueta("Multa")
    private BigDecimal multa;
    @Monetario
    @Tabelavel
    @Etiqueta("Outros Encargos")
    private BigDecimal outrosEncargos;
    @Obrigatorio
    @Etiqueta("Dívida Pública")
    @ManyToOne
    private DividaPublica dividaPublica;
    @Transient
    private Long criadoEm;

    public ParcelaDividaPublica() {
        criadoEm = System.nanoTime();
        valorAmortizado = BigDecimal.ZERO;
        valorJuros = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        outrosEncargos = BigDecimal.ZERO;
        valorPrestacao = BigDecimal.ZERO;
    }

    public ParcelaDividaPublica(Date dataParcela, BigDecimal valorAmortizado, BigDecimal valorJuros, BigDecimal saldo, DividaPublica dividaPublica, BigDecimal valorPrestacao) {
        this.dataParcela = dataParcela;
        this.valorAmortizado = valorAmortizado;
        this.valorJuros = valorJuros;
        this.saldo = saldo;
        this.dividaPublica = dividaPublica;
        this.valorPrestacao = valorPrestacao;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataParcela() {
        return dataParcela;
    }

    public void setDataParcela(Date dataParcela) {
        this.dataParcela = dataParcela;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public BigDecimal getValorAmortizado() {
        return valorAmortizado;
    }

    public void setValorAmortizado(BigDecimal valorAmortizado) {
        this.valorAmortizado = valorAmortizado;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getValorPrestacao() {
        return valorPrestacao;
    }

    public void setValorPrestacao(BigDecimal valorPrestacao) {
        this.valorPrestacao = valorPrestacao;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getOutrosEncargos() {
        return outrosEncargos;
    }

    public void setOutrosEncargos(BigDecimal outrosEncargos) {
        this.outrosEncargos = outrosEncargos;
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
        return DataUtil.getDataFormatada(dataParcela, "MM/yyyy")
                + ", Prestação: " + Util.formataValor(valorPrestacao)
                + ", Juros: " + Util.formataValor(valorJuros)
                + ", Outros Encargos: " + Util.formataValor(outrosEncargos)
                + ", referente a: " + dividaPublica;
    }
}
