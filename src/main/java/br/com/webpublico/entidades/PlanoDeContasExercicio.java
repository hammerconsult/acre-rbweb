package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Determina os planos de contas Contábil, de Receitas, de Despesas, de Fontes
 * de Recursos de de Centros de Custo que serão utilizados em determinado
 * exercício por determinada Unidade Organizacional.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Plano De Contas Exercício")
public class PlanoDeContasExercicio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Plano Contábil")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private PlanoDeContas planoContabil;
    @Etiqueta("Plano de Receitas")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private PlanoDeContas planoDeReceitas;
    @Etiqueta("Plano de Despesas")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private PlanoDeContas planoDeDespesas;
    @Etiqueta("Plano de Destinação de Recursos")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private PlanoDeContas planoDeDestinacaoDeRecursos;
    @Etiqueta("Plano Extraorçamentário")
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private PlanoDeContas planoExtraorcamentario;
    @Etiqueta("Plano Auxiliar")
    @Tabelavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Pesquisavel
    private PlanoDeContas planoAuxiliar;

    public PlanoDeContasExercicio() {
    }

    public PlanoDeContasExercicio(Exercicio exercicio, PlanoDeContas planoContabil, PlanoDeContas planoDeReceitas, PlanoDeContas planoDeDespesas, PlanoDeContas planoDeDestinacaoDeRecursos, PlanoDeContas planoExtraorcamentario) {
        this.exercicio = exercicio;
        this.planoContabil = planoContabil;
        this.planoDeReceitas = planoDeReceitas;
        this.planoDeDespesas = planoDeDespesas;
        this.planoDeDestinacaoDeRecursos = planoDeDestinacaoDeRecursos;
        this.planoExtraorcamentario = planoExtraorcamentario;
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

    public PlanoDeContas getPlanoContabil() {
        return planoContabil;
    }

    public void setPlanoContabil(PlanoDeContas planoContabil) {
        this.planoContabil = planoContabil;
    }

    public PlanoDeContas getPlanoDeDespesas() {
        return planoDeDespesas;
    }

    public void setPlanoDeDespesas(PlanoDeContas planoDeDespesas) {
        this.planoDeDespesas = planoDeDespesas;
    }

    public PlanoDeContas getPlanoDeReceitas() {
        return planoDeReceitas;
    }

    public void setPlanoDeReceitas(PlanoDeContas planoDeReceitas) {
        this.planoDeReceitas = planoDeReceitas;
    }

    public PlanoDeContas getPlanoExtraorcamentario() {
        return planoExtraorcamentario;
    }

    public void setPlanoExtraorcamentario(PlanoDeContas planoExtraorcamentario) {
        this.planoExtraorcamentario = planoExtraorcamentario;
    }

    public PlanoDeContas getPlanoDeDestinacaoDeRecursos() {
        return planoDeDestinacaoDeRecursos;
    }

    public void setPlanoDeDestinacaoDeRecursos(PlanoDeContas planoDeDestinacaoDeRecursos) {
        this.planoDeDestinacaoDeRecursos = planoDeDestinacaoDeRecursos;
    }

    public PlanoDeContas getPlanoAuxiliar() {
        return planoAuxiliar;
    }

    public void setPlanoAuxiliar(PlanoDeContas planoAuxiliar) {
        this.planoAuxiliar = planoAuxiliar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlanoDeContasExercicio other = (PlanoDeContasExercicio) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
