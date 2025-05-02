/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCotacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Romanini
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Grupo de Objeto de Compra")
public class GrupoObjetoCompra extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Código")
    private String codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Descrição")
    private String descricao;
    @Tabelavel
    @ManyToOne
    @Etiqueta(value = "Superior")
    private GrupoObjetoCompra superior;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Cotação")
    @Enumerated(EnumType.STRING)
    private TipoCotacao tipoCotacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public GrupoObjetoCompra getSuperior() {
        return superior;
    }

    public void setSuperior(GrupoObjetoCompra superior) {
        this.superior = superior;
    }

    public TipoCotacao getTipoCotacao() {
        return tipoCotacao;
    }

    public void setTipoCotacao(TipoCotacao tipoCotacao) {
        this.tipoCotacao = tipoCotacao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
