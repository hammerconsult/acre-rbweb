/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoParentescoRPPS;
import br.com.webpublico.enums.rh.estudoatuarial.TipoDependenciaEstudoAtuarial;
import br.com.webpublico.enums.rh.previdencia.GrauParentescoBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.GrauDeParentescoDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Grau de Parentesco")
public class GrauDeParentesco implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Etiqueta("Código da RAIS")
    @Tabelavel
    private Integer codigoRais;
    @Pesquisavel
    @Etiqueta("Código do E-social")
    @Tabelavel
    @Obrigatorio
    private String codigoEsocial;
    private Boolean ativo;
    @Etiqueta("Tipo Dependente")
    @OneToMany(mappedBy = "grauDeParentesco", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrauParentTipoDepend> grauParentTipoDepends;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Parentesco")
    private TipoParentescoRPPS tipoParentesco;
    @Pesquisavel
    @Etiqueta("Código do Sig")
    @Tabelavel
    @Obrigatorio
    private String codigoSig;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação BBPrev")
    private GrauParentescoBBPrev grauParentescoBBPrev;
    @Etiqueta("Tipo de Dependente Estudo Atuarial")
    @Enumerated(EnumType.STRING)
    private TipoDependenciaEstudoAtuarial tipoDependEstudoAtuarial;

    public GrauDeParentesco() {
        criadoEm = System.nanoTime();
        grauParentTipoDepends = new ArrayList<>();
        ativo = true;
    }

    public static List<GrauDeParentescoDTO> toGrauDeParentescoDTO(List<GrauDeParentesco> grausDeParentesco) {
        if (grausDeParentesco == null) {
            return null;
        }
        List<GrauDeParentescoDTO> dtos = Lists.newLinkedList();
        for (GrauDeParentesco grauDeParentesco : grausDeParentesco) {
            GrauDeParentescoDTO dto = toGrauDeParentesco(grauDeParentesco);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static GrauDeParentescoDTO toGrauDeParentesco(GrauDeParentesco grauDeParentesco) {
        if (grauDeParentesco == null) {
            return null;
        }
        GrauDeParentescoDTO dto = new GrauDeParentescoDTO();
        dto.setId(grauDeParentesco.getId());
        dto.setCodigoEsocial(grauDeParentesco.getCodigoEsocial());
        dto.setDescricao(grauDeParentesco.getDescricao());
        return dto;
    }

    public List<GrauParentTipoDepend> getGrauParentTipoDepends() {
        return grauParentTipoDepends;
    }

    public void setGrauParentTipoDepends(List<GrauParentTipoDepend> grauParentTipoDepends) {
        this.grauParentTipoDepends = grauParentTipoDepends;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigoRais() {
        return codigoRais;
    }

    public void setCodigoRais(Integer codigoRais) {
        this.codigoRais = codigoRais;
    }

    public String getCodigoEsocial() {
        return codigoEsocial;
    }

    public void setCodigoEsocial(String codigoEsocial) {
        this.codigoEsocial = codigoEsocial;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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

    public TipoParentescoRPPS getTipoParentesco() {
        return tipoParentesco;
    }

    public void setTipoParentesco(TipoParentescoRPPS tipoParentesco) {
        this.tipoParentesco = tipoParentesco;
    }

    public String getCodigoSig() {
        return codigoSig;
    }

    public void setCodigoSig(String codigoSig) {
        this.codigoSig = codigoSig;
    }

    public GrauParentescoBBPrev getGrauParentescoBBPrev() {
        return grauParentescoBBPrev;
    }

    public void setGrauParentescoBBPrev(GrauParentescoBBPrev grauParentescoBBPrev) {
        this.grauParentescoBBPrev = grauParentescoBBPrev;
    }

    public TipoDependenciaEstudoAtuarial getTipoDependEstudoAtuarial() {
        return tipoDependEstudoAtuarial;
    }

    public void setTipoDependEstudoAtuarial(TipoDependenciaEstudoAtuarial tipoDependEstudoAtuarial) {
        this.tipoDependEstudoAtuarial = tipoDependEstudoAtuarial;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (descricao != null) {
            return codigoEsocial + " - " + descricao;
        }
        return codigoEsocial + "";
    }
}
