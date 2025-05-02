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
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 19/11/14
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Receita de Fechamento do Exercício")
@Table(name = "PLANODECONTASFECHAEXER")
public class PlanoDeContasFechamentoExercicio extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Plano de Contas")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PlanoDeContas planoDeContas;
    @Etiqueta("Plano de Contas Novo")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private PlanoDeContas planoDeContasNovo;

    public PlanoDeContasFechamentoExercicio() {
    }

    public PlanoDeContasFechamentoExercicio(PlanoDeContas plano, AberturaFechamentoExercicio selecionado) {
        this.planoDeContas = plano;
        this.aberturaFechamentoExercicio = selecionado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public PlanoDeContas getPlanoDeContasNovo() {
        return planoDeContasNovo;
    }

    public void setPlanoDeContasNovo(PlanoDeContas planoDeContasNovo) {
        this.planoDeContasNovo = planoDeContasNovo;
    }
}
