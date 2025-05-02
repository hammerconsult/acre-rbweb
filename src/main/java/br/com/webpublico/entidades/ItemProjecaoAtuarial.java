/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author major
 */
@Entity
@Audited

public class ItemProjecaoAtuarial implements Serializable, Comparable<ItemProjecaoAtuarial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    private Integer exercicio;
    @Monetario
    @Obrigatorio
    private BigDecimal receitasPrevidenciarias;
    @Obrigatorio
    @Monetario
    private BigDecimal despesasPrevidenciarias;
    private BigDecimal resultadoPrevidenciario;
    private BigDecimal saldoPrevidenciario;
    @ManyToOne
    private ProjecaoAtuarial projecaoAtuarial;
    @Transient
    private Long criadoEm;

    public ItemProjecaoAtuarial() {
        criadoEm = System.nanoTime();
        receitasPrevidenciarias = new BigDecimal(BigInteger.ZERO);
        despesasPrevidenciarias = new BigDecimal(BigInteger.ZERO);
    }

    public ItemProjecaoAtuarial(Integer exercicio, BigDecimal receitasPrevidenciarias, BigDecimal despesasPrevidenciarias, BigDecimal resultadoPrevidenciario, BigDecimal saldoPrevidenciario, ProjecaoAtuarial projecaoAtuarial, Long criadoEm) {
        this.exercicio = exercicio;
        this.receitasPrevidenciarias = new BigDecimal(BigInteger.ZERO);
        this.despesasPrevidenciarias = new BigDecimal(BigInteger.ZERO);
        this.resultadoPrevidenciario = resultadoPrevidenciario;
        this.saldoPrevidenciario = saldoPrevidenciario;
        this.projecaoAtuarial = projecaoAtuarial;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDespesasPrevidenciarias() {
        return despesasPrevidenciarias;
    }

    public void setDespesasPrevidenciarias(BigDecimal despesasPrevidenciarias) {
        this.despesasPrevidenciarias = despesasPrevidenciarias;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }


    public BigDecimal getReceitasPrevidenciarias() {
        return receitasPrevidenciarias;
    }

    public void setReceitasPrevidenciarias(BigDecimal receitasPrevidenciarias) {
        this.receitasPrevidenciarias = receitasPrevidenciarias;
    }

    public BigDecimal getResultadoPrevidenciario() {
        return resultadoPrevidenciario;
    }

    public void setResultadoPrevidenciario(BigDecimal resultadoPrevidenciario) {
        this.resultadoPrevidenciario = resultadoPrevidenciario;
    }

    public BigDecimal getSaldoPrevidenciario() {
        return saldoPrevidenciario;
    }

    public void setSaldoPrevidenciario(BigDecimal saldoPrevidenciario) {
        this.saldoPrevidenciario = saldoPrevidenciario;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ProjecaoAtuarial getProjecaoAtuarial() {
        return projecaoAtuarial;
    }

    public void setProjecaoAtuarial(ProjecaoAtuarial projecaoAtuarial) {
        this.projecaoAtuarial = projecaoAtuarial;
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
        return id.toString();
    }

    @Override
    public int compareTo(ItemProjecaoAtuarial t) {
        if (this.exercicio < t.exercicio) {
            return -1;
        }
        if (this.exercicio > t.exercicio) {
            return 1;
        }
        return 0;
    }
}
