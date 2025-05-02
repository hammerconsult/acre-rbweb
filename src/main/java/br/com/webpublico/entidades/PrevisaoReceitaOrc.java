/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
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
@Etiqueta("Previsão de Receita Orçamentária")
public class PrevisaoReceitaOrc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @Tabelavel
    private BigDecimal percentual;
    @Tabelavel
    @Etiqueta("Valor Utilizado")
    @Obrigatorio
    private BigDecimal valorUtilizado;
    @Tabelavel
    @Etiqueta("Valor Programado")
    private BigDecimal valorProgramado;
    @Transient
    private BigDecimal saldo;
    @Invisivel
    @Etiqueta("Índice")
    private Integer indice;
    @Transient
    @Invisivel
    private Long criadoEm;
    @ManyToOne
    private ReceitaLOA receitaLOA;

    public PrevisaoReceitaOrc() {
        percentual = new BigDecimal(BigInteger.ZERO);
        valorUtilizado = new BigDecimal(BigInteger.ZERO);
        valorProgramado = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        indice = 0;
        criadoEm = System.nanoTime();
    }

    public PrevisaoReceitaOrc(Mes mes, BigDecimal percentual, BigDecimal valorUtilizado, BigDecimal valorProgramado, BigDecimal saldo, Integer indice, Long criadoEm, ReceitaLOA receitaLOA) {
        this.mes = mes;
        this.percentual = percentual;
        this.valorUtilizado = valorUtilizado;
        this.valorProgramado = valorProgramado;
        this.saldo = saldo;
        this.indice = indice;
        this.criadoEm = criadoEm;
        this.receitaLOA = receitaLOA;
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

    public Integer getIndice() {
        return indice;
    }

    public void setIndice(Integer indice) {
        this.indice = indice;
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

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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
        return "br.com.webpublico.entidades.PrevisaoReceitaOrc[ id=" + id + " ]";
    }
}
