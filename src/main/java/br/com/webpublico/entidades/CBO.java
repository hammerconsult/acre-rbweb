/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
public class CBO implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Column(length = 150)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Column(length = 150)
    @Obrigatorio
    @Etiqueta("Código CBO")
    private Long codigo;
    @ManyToMany(mappedBy = "cbos")
    private List<Cargo> cargos;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    private TipoCBO tipoCBO;
    private Boolean ativo;

    public CBO() {
        this.criadoEm = System.nanoTime();
    }

    public CBO(String descricao, Long codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.criadoEm = System.nanoTime();
    }

    public CBO(String descricao, Long codigo, TipoCBO tipo, Boolean ativo) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.criadoEm = System.nanoTime();
        this.tipoCBO = tipo;
        this.ativo = ativo;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
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

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public TipoCBO getTipoCBO() {
        return tipoCBO;
    }

    public void setTipoCBO(TipoCBO tipoCBO) {
        this.tipoCBO = tipoCBO;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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
        if (codigo == null && descricao == null) {
            return "";
        }
        return codigo + " - " + descricao + (tipoCBO != null ? " (" + tipoCBO.getDescricao() + ")" : "");
    }
}
