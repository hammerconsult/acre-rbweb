/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 *
 * @author terminal4
 */
@GrupoDiagrama(nome = "Protocolo")
@Etiqueta(value = "Documento")
@Audited
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
public class Documento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Column(length = 250)
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;

    public Documento() {
    }

    public Documento(String descricao) {
        this.descricao = descricao;
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

    @Override
    public String toString() {
        return descricao;
    }
}
