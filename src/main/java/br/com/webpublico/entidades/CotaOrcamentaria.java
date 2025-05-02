/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "PPA")
@Etiqueta("Cota Orçamentária")
public class CotaOrcamentaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Mês")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @Obrigatorio
    @Tabelavel
    private BigDecimal percentual;
    @Obrigatorio
    @Etiqueta("Valor Programado")
    @Tabelavel
    private BigDecimal valorProgramado;
    @Etiqueta("Valor Utilizado")
    @Tabelavel
    private BigDecimal valorUtilizado;
    @Transient
    private BigDecimal saldo;
    @Invisivel
    @Obrigatorio
    private Integer indice;
    @Etiqueta("Cota Orçamentária")
    @ManyToOne
    private GrupoCotaORC grupoCotaORC;
    @Transient
    @Invisivel
    private Long criadoEm;

    public CotaOrcamentaria() {
        this.criadoEm = System.nanoTime();
        percentual = new BigDecimal(BigInteger.ZERO);
        valorProgramado = new BigDecimal(BigInteger.ZERO);
        valorUtilizado = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        indice = 0;
    }

    public CotaOrcamentaria(Mes mes, BigDecimal percentual, BigDecimal valorProgramado, BigDecimal valorUtilizado, BigDecimal saldo, Integer indice, GrupoCotaORC grupoCotaORC) {
        this.mes = mes;
        this.percentual = percentual;
        this.valorProgramado = valorProgramado;
        this.valorUtilizado = valorUtilizado;
        this.saldo = saldo;
        this.indice = indice;
        this.grupoCotaORC = grupoCotaORC;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getValorProgramado() {
        return valorProgramado;
    }

    public void setValorProgramado(BigDecimal valorProgramado) {
        this.valorProgramado = valorProgramado;
    }

    public BigDecimal getValorUtilizado() {
        return valorUtilizado;
    }

    public void setValorUtilizado(BigDecimal valorUtilizado) {
        this.valorUtilizado = valorUtilizado;
    }

    public GrupoCotaORC getGrupoCotaORC() {
        return grupoCotaORC;
    }

    public void setGrupoCotaORC(GrupoCotaORC grupoCotaORC) {
        this.grupoCotaORC = grupoCotaORC;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return mes.getDescricao() + " - Programado: " + Util.formataValor(valorProgramado) + " - Utilizado: " + Util.formataValor(valorUtilizado) + "("+percentual+"%) - Índice: " + indice;
    }
}
