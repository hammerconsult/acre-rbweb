/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author java
 */
@Entity
@Audited

@Etiqueta(value = "Medida Administrativa")
public class MedidaAdministrativa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloqueio de Emissão e Reemissão de Credencial de Transporte")
    private Boolean bloqueioTransporte;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Bloqueio de Emissão e Reemissão de Credencial de Tráfego")
    private Boolean bloqueioTrafego;
    @Invisivel
    @Transient
    private Long criadoEm;

    public MedidaAdministrativa() {
        this.criadoEm = System.nanoTime();
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

    public Boolean getBloqueioTransporte() {
        return bloqueioTransporte;
    }

    public void setBloqueioTransporte(Boolean bloqueioTransporte) {
        this.bloqueioTransporte = bloqueioTransporte;
    }

    public Boolean getBloqueioTrafego() {
        return bloqueioTrafego;
    }

    public void setBloqueioTrafego(Boolean bloqueioTrafego) {
        this.bloqueioTrafego = bloqueioTrafego;
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
        return descricao;
    }
}
