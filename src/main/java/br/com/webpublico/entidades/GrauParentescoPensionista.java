/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Claudio
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Grau de Parentesco para Pensionista")
public class GrauParentescoPensionista implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer codigo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Etiqueta("Grau de Parentesco")
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private GrauDeParentesco grauDeParentesco;
    @Etiqueta("Temporário")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Boolean temporario;
    @Etiqueta("Vitalício")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Boolean vitalicio;
    private Integer idadeLimite;

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

    public GrauDeParentesco getGrauDeParentesco() {
        return grauDeParentesco;
    }

    public void setGrauDeParentesco(GrauDeParentesco grauDeParentesco) {
        this.grauDeParentesco = grauDeParentesco;
    }

    public Boolean getTemporario() {
        return temporario;
    }

    public void setTemporario(Boolean temporario) {
        this.temporario = temporario;
    }

    public Boolean getVitalicio() {
        return vitalicio;
    }

    public void setVitalicio(Boolean vitalicio) {
        this.vitalicio = vitalicio;
    }

    public Integer getIdadeLimite() {
        return idadeLimite;
    }

    public void setIdadeLimite(Integer idadeLimite) {
        this.idadeLimite = idadeLimite;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GrauParentescoPensionista)) {
            return false;
        }
        GrauParentescoPensionista other = (GrauParentescoPensionista) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
