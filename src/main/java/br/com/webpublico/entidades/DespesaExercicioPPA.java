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
 * Representa os valores de despesas planejados por exercício do PPA. Embora estes valores possam ser
 * obtidos a partir do desmembramento das ações previstas no PPA, também se faz necessáriio proporcionar
 * meios de previsão de despesas a partir dos montantes realizados em anos anteriores. Desta forma os dados
 * representados por esta entidade poderão ser avaliados em função do detalhamento das ações (se necessário).
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Despesa Exercício PPA")
public class DespesaExercicioPPA implements Serializable, Comparable<Object> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Somente uma conta de despesa pode ser selecionada! Verificar qual é o plano de contas
     * de despesa vigente no exercício do PPA para filtrar as contas que poderão ser vinculadas
     * (ver PlanoDecontasExercicio)
     */
    @ManyToOne
    @Etiqueta("Conta de Despesa")
    @Tabelavel
    private Conta contaDeDespesa;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("PPA")
    @Tabelavel
    private PPA ppa;
    @Tabelavel
    @Etiqueta("Valor Estimado")
    @Monetario
    private BigDecimal valorEstimado;

    public DespesaExercicioPPA() {
    }

    public DespesaExercicioPPA(Conta contaDeDespesa, Exercicio exercicio, PPA ppa, BigDecimal valorEstimado) {
        this.contaDeDespesa = contaDeDespesa;
        this.exercicio = exercicio;
        this.ppa = ppa;
        this.valorEstimado = valorEstimado;
    }

    public Conta getContaDeDespesa() {
        return contaDeDespesa;
    }

    public void setContaDeDespesa(Conta contaDeDespesa) {
        this.contaDeDespesa = contaDeDespesa;
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
        final DespesaExercicioPPA other = (DespesaExercicioPPA) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {

        return this.contaDeDespesa.getCodigo().compareTo(((DespesaExercicioPPA) o).getContaDeDespesa().getCodigo());

    }
}
