/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Agrupa os materiais (mercadorias) em categorias hierarquizadas.
 *
 * @author arthur
 */
@Etiqueta("Grupo de Materiais")
public class GrupoMaterial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * O código do GrupoMaterial deve ser gerado a partir de uma máscara, da
     * mesma maneira que o código da entidade Conta.
     */
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    /**
     * Auto-relacionamento que possibilita a criação do número desejado de
     * níveis.
     */
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Superior")
    private GrupoMaterial superior;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoCadastralContabil ativoInativo;

    public GrupoMaterial() {
        super();
        this.ativoInativo = SituacaoCadastralContabil.ATIVO;
    }

    public GrupoMaterial(String descricao, String codigo, GrupoMaterial superior) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.superior = superior;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoMaterial getSuperior() {
        return superior;
    }

    public void setSuperior(GrupoMaterial superior) {
        this.superior = superior;
    }

    public SituacaoCadastralContabil getAtivoInativo() {
        return ativoInativo;
    }

    public void setAtivoInativo(SituacaoCadastralContabil ativoInativo) {
        this.ativoInativo = ativoInativo;
    }

    @Override
    public String toString() {
        if (codigo != null) {
            return codigo + " - " + descricao;
        } else if (descricao != null) {
            return descricao;
        } else {
            return "";
        }
    }
}
