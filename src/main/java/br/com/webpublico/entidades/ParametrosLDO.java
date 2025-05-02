/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.type.DoubleType;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Usuario
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Parâmetros LDO")
public class ParametrosLDO extends ConfiguracaoModulo implements Serializable {

    @Tabelavel
    @Etiqueta("PIB")
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Positivo
    private BigDecimal pib;
    @Tabelavel
    @Etiqueta("Inflação Projetada")
    @Obrigatorio
    @Pesquisavel
    private Double inflacaoProjetada;
    @Tabelavel
    @Etiqueta("Inflação Real")
    @Obrigatorio
    @Pesquisavel
    private Double inflacaoReal;
    @Tabelavel
    @Etiqueta("Estimativa de Crescimento")
    @Obrigatorio
    @Pesquisavel
    private Double estimativaCrescimento;
    @Tabelavel
    @Etiqueta("Exercício")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @Tabelavel
    @Etiqueta("LDO")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private LDO ldo;

    public ParametrosLDO() {
        this.pib = new BigDecimal(BigInteger.ZERO);
        this.estimativaCrescimento = new Double(DoubleType.ZERO);
        this.inflacaoProjetada = new Double(DoubleType.ZERO);
        this.inflacaoReal = new Double(DoubleType.ZERO);
    }

    public ParametrosLDO(BigDecimal pib, Double inflacaoProjetada, Double inflacaoReal, Double estimativaCrescimento, Exercicio exercicio) {
        this.pib = pib;
        this.inflacaoProjetada = inflacaoProjetada;
        this.inflacaoReal = inflacaoReal;
        this.estimativaCrescimento = estimativaCrescimento;
        this.exercicio = exercicio;
    }

    public BigDecimal getPib() {
        return pib;
    }

    public void setPib(BigDecimal pib) {
        this.pib = pib;
    }

    public Double getInflacaoProjetada() {
        return inflacaoProjetada;
    }

    public void setInflacaoProjetada(Double inflacaoProjetada) {
        this.inflacaoProjetada = inflacaoProjetada;
    }

    public Double getInflacaoReal() {
        return inflacaoReal;
    }

    public void setInflacaoReal(Double inflacaoReal) {
        this.inflacaoReal = inflacaoReal;
    }

    public Double getEstimativaCrescimento() {
        return estimativaCrescimento;
    }

    public void setEstimativaCrescimento(Double estimativaCrescimento) {
        this.estimativaCrescimento = estimativaCrescimento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }
}
