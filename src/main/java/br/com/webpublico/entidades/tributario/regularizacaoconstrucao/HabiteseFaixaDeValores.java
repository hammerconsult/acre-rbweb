package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Faixa de Valores de Construções Habite-se")
public class HabiteseFaixaDeValores extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Classe de Construção")
    private HabiteseClassesConstrucao habiteseClassesConstrucao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Grupo de Construção")
    private HabiteseGruposConstrucao habiteseGruposConstrucao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "habiteseFaixaDeValores")
    @Etiqueta("Lista Faixa de Valores Habite-se")
    private List<FaixaDeValoresHL> listaFaixaDeValoresHL;

    public HabiteseFaixaDeValores() {
        listaFaixaDeValoresHL = Lists.newArrayList();
    }

    public HabiteseFaixaDeValores(Integer codigo, Exercicio exercicio, HabiteseClassesConstrucao habiteseClassesConstrucao, HabiteseGruposConstrucao habiteseGruposConstrucao, String descricao) {
        this.codigo = codigo;
        this.exercicio = exercicio;
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
        this.habiteseGruposConstrucao = habiteseGruposConstrucao;
        this.descricao = descricao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public HabiteseClassesConstrucao getHabiteseClassesConstrucao() {
        return habiteseClassesConstrucao;
    }

    public void setHabiteseClassesConstrucao(HabiteseClassesConstrucao habiteseClassesConstrucao) {
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
    }

    public HabiteseGruposConstrucao getHabiteseGruposConstrucao() {
        return habiteseGruposConstrucao;
    }

    public void setHabiteseGruposConstrucao(HabiteseGruposConstrucao habiteseGruposConstrucao) {
        this.habiteseGruposConstrucao = habiteseGruposConstrucao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<FaixaDeValoresHL> getListaFaixaDeValoresHL() {
        return listaFaixaDeValoresHL;
    }

    public void setListaFaixaDeValoresHL(List<FaixaDeValoresHL> listaFaixaDeValoresHL) {
        this.listaFaixaDeValoresHL = listaFaixaDeValoresHL;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
