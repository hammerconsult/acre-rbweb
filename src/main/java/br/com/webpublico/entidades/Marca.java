/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMarca;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.ott.MarcaDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Marcas")
public class Marca implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @Pesquisavel
    @Etiqueta("Tipo Marca")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoMarca tipoMarca;
    @Invisivel
    @Transient
    private Long criadoEm;

    public Marca() {
        this.criadoEm = System.nanoTime();
    }

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

    public TipoMarca getTipoMarca() {
        return tipoMarca;
    }

    public void setTipoMarca(TipoMarca tipoMarca) {
        this.tipoMarca = tipoMarca;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return descricao;
    }

    public MarcaDTO toDTO() {
        MarcaDTO dto = new MarcaDTO();
        dto.setId(this.id);
        dto.setDescricao(this.descricao);
        return dto;
    }
}
