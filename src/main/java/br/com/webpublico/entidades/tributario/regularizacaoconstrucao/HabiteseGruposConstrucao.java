package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
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
@Etiqueta("Grupos de Construções Habite-se")
public class HabiteseGruposConstrucao extends SuperEntidade implements Serializable {

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
    @Etiqueta("Classes de Construção Habite-se")
    private HabiteseClassesConstrucao habiteseClassesConstrucao;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    public HabiteseGruposConstrucao() {
    }

    public HabiteseGruposConstrucao(Integer codigo, HabiteseClassesConstrucao habiteseClassesConstrucao, String descricao) {
        this.codigo = codigo;
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
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

    public HabiteseClassesConstrucao getHabiteseClassesConstrucao() {
        return habiteseClassesConstrucao;
    }

    public void setHabiteseClassesConstrucao(HabiteseClassesConstrucao habiteseClassesConstrucao) {
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
