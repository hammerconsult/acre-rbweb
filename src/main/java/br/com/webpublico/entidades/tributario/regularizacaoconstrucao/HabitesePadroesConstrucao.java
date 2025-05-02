package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Padrões de Construções Habite-se")
public class HabitesePadroesConstrucao extends SuperEntidade implements Serializable {

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
    @Etiqueta("Grupos de Construção Habite-se")
    private HabiteseGruposConstrucao habiteseGruposConstrucao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    public HabitesePadroesConstrucao() {
    }

    public HabitesePadroesConstrucao(Integer codigo, HabiteseGruposConstrucao habiteseGruposConstrucao, String descricao) {
        this.codigo = codigo;
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

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
