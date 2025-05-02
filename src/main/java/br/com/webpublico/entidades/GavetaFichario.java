/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity
@Audited
public class GavetaFichario extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Código")
    @OrderBy
    private Integer codigo;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Quantidade de Pastas")
    private Integer quantidadePastas;
    @ManyToOne
    private Fichario fichario;
    @OneToMany(mappedBy = "gavetaFichario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastaGaveta> pastasGavetas;
    @Transient
    private Date dataRegistro;

    public GavetaFichario() {
        dataRegistro = new Date();
        pastasGavetas = new ArrayList<>();
    }

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

    public Fichario getFichario() {
        return fichario;
    }

    public void setFichario(Fichario fichario) {
        this.fichario = fichario;
    }

    public List<PastaGaveta> getPastasGavetas() {
        return pastasGavetas;
    }

    public void setPastasGavetas(List<PastaGaveta> pastasGavetas) {
        this.pastasGavetas = pastasGavetas;
    }

    public Integer getQuantidadePastas() {
        return quantidadePastas;
    }

    public void setQuantidadePastas(Integer quantidadePastas) {
        this.quantidadePastas = quantidadePastas;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (codigo != null && descricao != null && quantidadePastas != null) {
            return codigo + " - " + descricao + " - " + quantidadePastas;
        }
        return "";
    }
}
