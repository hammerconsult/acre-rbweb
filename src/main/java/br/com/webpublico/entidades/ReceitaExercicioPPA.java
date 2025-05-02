/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Determina a previsão de receitas para cada conta de receita no exercício.
 * Esta previsão comporá as receitas disponíveis para a execução das ações
 * planejadas no PPA
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Receita Exercício PPA")
public class ReceitaExercicioPPA implements Serializable, Comparable<Object> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Somente uma conta de receita pode ser selecionada! Verificar qual é o
     * plano de contas de receita vigente no exercício do PPA para filtrar as
     * contas que poderão ser vinculadas (ver PlanoDecontasExercicio)
     */
    @ManyToOne
    @Tabelavel
    private Conta contaDeReceita;
    @Tabelavel
    @ManyToOne
    private Exercicio exercicio;
    @Tabelavel
    @ManyToOne
    private PPA ppa;
    @Tabelavel
    @Monetario
    private BigDecimal valorEstimado;

    public ReceitaExercicioPPA() {
    }

    public ReceitaExercicioPPA(Conta contaDeReceita, Exercicio exercicio, PPA ppa, BigDecimal valorEstimado) {
        this.contaDeReceita = contaDeReceita;
        this.exercicio = exercicio;
        this.ppa = ppa;
        this.valorEstimado = valorEstimado;
    }

    public ReceitaExercicioPPA(PPA ppa) {
        this.ppa = ppa;
    }

    public Conta getContaDeReceita() {
        return contaDeReceita;
    }

    public void setContaDeReceita(Conta contaDeReceita) {
        this.contaDeReceita = contaDeReceita;
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

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReceitaExercicioPPA other = (ReceitaExercicioPPA) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        return this.contaDeReceita.getCodigo().compareTo(((ReceitaExercicioPPA) o).getContaDeReceita().getCodigo());
    }
}
