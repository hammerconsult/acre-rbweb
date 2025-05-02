package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.esocial.TipoGrupoCategoriaTrabalhador;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 05/09/2018.
 */
@Entity
@Audited
@Etiqueta("Categorias de Trabalhadores")
public class CategoriaTrabalhador extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer codigo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Grupo de Categoria do Trabalhador")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoGrupoCategoriaTrabalhador tipo;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoGrupoCategoriaTrabalhador getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoCategoriaTrabalhador tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        if (this.descricao.length() >= 180) {
            this.descricao = this.descricao.substring(0, 180).concat("...");
        }
        return this.codigo + " - " + this.descricao;
    }
}

