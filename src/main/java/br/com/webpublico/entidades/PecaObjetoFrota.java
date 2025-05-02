/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Peça")
public class PecaObjetoFrota implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Para")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Prazo de Revisão")
    @Positivo(permiteZero = true)
    private Integer prazoRevisao;
    private BigDecimal kmVidaUtil;
    private BigDecimal horasUsoVidaUtil;


    public PecaObjetoFrota() {
        kmVidaUtil = BigDecimal.ZERO;
        horasUsoVidaUtil = BigDecimal.ZERO;
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

    public Integer getPrazoRevisao() {
        return prazoRevisao;
    }

    public void setPrazoRevisao(Integer prazoRevisao) {
        this.prazoRevisao = prazoRevisao;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public BigDecimal getKmVidaUtil() {
        return kmVidaUtil;
    }

    public void setKmVidaUtil(BigDecimal kmVidaUtil) {
        this.kmVidaUtil = kmVidaUtil;
    }

    public BigDecimal getHorasUsoVidaUtil() {
        return horasUsoVidaUtil;
    }

    public void setHorasUsoVidaUtil(BigDecimal horasUsoVidaUtil) {
        this.horasUsoVidaUtil = horasUsoVidaUtil;
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
        if (!(object instanceof PecaObjetoFrota)) {
            return false;
        }
        PecaObjetoFrota other = (PecaObjetoFrota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
