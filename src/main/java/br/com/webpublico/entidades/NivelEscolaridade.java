/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.GrauEscolaridadeDependente;
import br.com.webpublico.enums.GrauInstrucaoCAGED;
import br.com.webpublico.enums.rh.GrauEscolaridadeSICAP;
import br.com.webpublico.enums.rh.esocial.GrauEscolaridadeESocial;
import br.com.webpublico.enums.rh.previdencia.GrauInstrucaoBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.NivelEscolaridadeDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
@Etiqueta("Nível de Escolaridade")
public class NivelEscolaridade extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ordem")
    private Long ordem;
    @Pesquisavel
    @Tabelavel
    @Column(length = 150)
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Etiqueta("Grau de Instrução do CAGED")
    @Enumerated(EnumType.STRING)
    private GrauInstrucaoCAGED grauInstrucaoCAGED;
    @Tabelavel
    @Etiqueta("Grau de Escolaridade do SIPREV")
    @ManyToOne
    private GrauEscolaridadeSiprev grauEscolaridadeSiprev;
    @Tabelavel
    @Etiqueta("Grau de Escolaridade do Dependente (RPPS)")
    @Enumerated(EnumType.STRING)
    private GrauEscolaridadeDependente grauEscolaridadeDependente;
    @Etiqueta("Grau de Escolaridade SICAP")
    @Enumerated(EnumType.STRING)
    private GrauEscolaridadeSICAP grauEscolaridadeSICAP;
    @Etiqueta("Grau de Escolaridade SICAP")
    @Enumerated(EnumType.STRING)
    private GrauEscolaridadeESocial grauEscolaridadeESocial;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação BBPrev")
    private GrauInstrucaoBBPrev grauInstrucaoBBPrev;

    public NivelEscolaridade() {
    }

    public NivelEscolaridade(String descricao) {
        this.descricao = descricao;
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

    public Long getOrdem() {
        return ordem;
    }

    public void setOrdem(Long ordem) {
        this.ordem = ordem;
    }

    public GrauInstrucaoCAGED getGrauInstrucaoCAGED() {
        return grauInstrucaoCAGED;
    }

    public void setGrauInstrucaoCAGED(GrauInstrucaoCAGED grauInstrucaoCAGED) {
        this.grauInstrucaoCAGED = grauInstrucaoCAGED;
    }

    public GrauEscolaridadeSiprev getGrauEscolaridadeSiprev() {
        return grauEscolaridadeSiprev;
    }

    public void setGrauEscolaridadeSiprev(GrauEscolaridadeSiprev grauEscolaridadeSiprev) {
        this.grauEscolaridadeSiprev = grauEscolaridadeSiprev;
    }

    public GrauEscolaridadeDependente getGrauEscolaridadeDependente() {
        return grauEscolaridadeDependente;
    }

    public void setGrauEscolaridadeDependente(GrauEscolaridadeDependente grauEscolaridadeDependente) {
        this.grauEscolaridadeDependente = grauEscolaridadeDependente;
    }

    public GrauEscolaridadeSICAP getGrauEscolaridadeSICAP() {
        return grauEscolaridadeSICAP;
    }

    public void setGrauEscolaridadeSICAP(GrauEscolaridadeSICAP grauEscolaridadeSICAP) {
        this.grauEscolaridadeSICAP = grauEscolaridadeSICAP;
    }

    public GrauEscolaridadeESocial getGrauEscolaridadeESocial() {
        return grauEscolaridadeESocial;
    }

    public void setGrauEscolaridadeESocial(GrauEscolaridadeESocial grauEscolaridadeESocial) {
        this.grauEscolaridadeESocial = grauEscolaridadeESocial;
    }

    public GrauInstrucaoBBPrev getGrauInstrucaoBBPrev() {
        return grauInstrucaoBBPrev;
    }

    public void setGrauInstrucaoBBPrev(GrauInstrucaoBBPrev grauInstrucaoBBPrev) {
        this.grauInstrucaoBBPrev = grauInstrucaoBBPrev;
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
        if (!(object instanceof NivelEscolaridade)) {
            return false;
        }
        NivelEscolaridade other = (NivelEscolaridade) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<NivelEscolaridadeDTO> toNiveisEscolaridadesDTOs(List<NivelEscolaridade> niveis) {
        List<NivelEscolaridadeDTO> dtos = Lists.newLinkedList();
        for (NivelEscolaridade nivei : niveis) {
            NivelEscolaridadeDTO dto = toNivelEscolaridadeDTO(nivei);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static NivelEscolaridadeDTO toNivelEscolaridadeDTO(NivelEscolaridade nivelEscolaridade) {
        if (nivelEscolaridade == null) {
            return null;
        }
        NivelEscolaridadeDTO dto = new NivelEscolaridadeDTO();
        dto.setDescricao(nivelEscolaridade.getDescricao());
        dto.setId(nivelEscolaridade.getId());
        dto.setOrdem(nivelEscolaridade.getOrdem());
        return dto;
    }

    public static NivelEscolaridade dtoToNivelEscolaridade(NivelEscolaridadeDTO dto) {
        if (dto == null) {
            return null;
        }
        NivelEscolaridade nivel = new NivelEscolaridade();
        nivel.setId(dto.getId());
        return nivel;
    }
}
