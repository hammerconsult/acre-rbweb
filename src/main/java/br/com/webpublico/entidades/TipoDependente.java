/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.DependenteVinculoFPDTO;
import br.com.webpublico.pessoa.dto.TipoDependenteDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peixe
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Etiqueta("Tipo de Dependência")

public class TipoDependente implements Serializable {

    public static final String PREVIDENCIA_RBPREV = "11";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    @NotNull
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Size(max = 30)
    private String descricao;
    @Tabelavel
    @Etiqueta("Idade Máxima")
    @Obrigatorio
    private Integer idadeMaxima;
    @OneToMany(mappedBy = "tipoDependente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrauParentTipoDepend> grauParentTipoDepends;
    @Invisivel
    @Transient
    private Long criadoEm;
    private Boolean mostrarNoPortal;

    public TipoDependente() {
        this.criadoEm = System.nanoTime();
        this.grauParentTipoDepends = new ArrayList<>();
    }

    public static List<DependenteVinculoFPDTO> toTipoDependentesDTO(List<DependenteVinculoFP> tipos) {
        if (tipos == null) {
            return null;
        }
        List<DependenteVinculoFPDTO> dtos = Lists.newLinkedList();
        for (DependenteVinculoFP tipo : tipos) {
            DependenteVinculoFPDTO dto = toTipoDependenteDTO(tipo);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    private static DependenteVinculoFPDTO toTipoDependenteDTO(DependenteVinculoFP tipo) {
        if (tipo == null) {
            return null;
        }
        DependenteVinculoFPDTO dto = new DependenteVinculoFPDTO();
        dto.setId(tipo.getId());
        dto.setInicioVigencia(tipo.getInicioVigencia());
        dto.setFinalVigencia(tipo.getFinalVigencia());
        dto.setTipoDependenteDTO(toTipo(tipo.getTipoDependente()));
        return dto;
    }

    private static TipoDependenteDTO toTipo(TipoDependente tipo) {
        if (tipo == null) {
            return null;
        }
        TipoDependenteDTO dto = new TipoDependenteDTO();
        dto.setId(tipo.getId());
        dto.setDescricao(tipo.getDescricao());
        dto.setIdTipoDependente(tipo.getId());
        return dto;
    }

    public static List<TipoDependenteDTO> toTiposDependentesDTO(List<TipoDependente> tipos) {
        if (tipos == null) {
            return null;
        }
        List<TipoDependenteDTO> dtos = Lists.newLinkedList();
        for (TipoDependente tipo : tipos) {
            TipoDependenteDTO dto = toTipo(tipo);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<GrauParentTipoDepend> getGrauParentTipoDepends() {
        return grauParentTipoDepends;
    }

    public void setGrauParentTipoDepends(List<GrauParentTipoDepend> grauParentTipoDepends) {
        this.grauParentTipoDepends = grauParentTipoDepends;
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

    public Integer getIdadeMaxima() {
        return idadeMaxima;
    }

    public void setIdadeMaxima(Integer idadeMaxima) {
        this.idadeMaxima = idadeMaxima;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getMostrarNoPortal() {
        return mostrarNoPortal != null ? mostrarNoPortal : false;
    }

    public void setMostrarNoPortal(Boolean mostrarNoPortal) {
        this.mostrarNoPortal = mostrarNoPortal;
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
        return codigo + " - " + descricao;
    }
}
