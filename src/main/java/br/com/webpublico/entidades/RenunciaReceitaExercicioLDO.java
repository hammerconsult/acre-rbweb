/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Representa o valor da renúncia de receita para cada um dos exercícios
 * subsequentes (exercício corrente e os dois seguintes).
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Table(name = "RENUNCIARECEITAEXERCLDO")
public class RenunciaReceitaExercicioLDO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RenunciaReceitaLDO renunciaDeReceita;
    /**
     * Validado com base no exercício da RenunciaReceitaLDO (somente o atual + 2
     * posteriores são permitidos)
     */
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private LDO ldo;
    @Monetario
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public RenunciaReceitaExercicioLDO() {
        criadoEm = System.nanoTime();
    }

    public RenunciaReceitaExercicioLDO(RenunciaReceitaLDO renunciaDeReceita, Exercicio exercicio, LDO ldo, BigDecimal valor) {
        this.renunciaDeReceita = renunciaDeReceita;
        this.exercicio = exercicio;
        this.ldo = ldo;
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RenunciaReceitaLDO getRenunciaDeReceita() {
        return renunciaDeReceita;
    }

    public void setRenunciaDeReceita(RenunciaReceitaLDO renunciaDeReceita) {
        this.renunciaDeReceita = renunciaDeReceita;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
