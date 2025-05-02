/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoTermoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author cheles
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Termo do RBTRANS")
@Inheritance(strategy = InheritanceType.JOINED)
public class TermoRBTrans implements Serializable, Comparable<TermoRBTrans> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PermissaoTransporte permissaoTransporte;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTermo;
    @Enumerated(EnumType.STRING)
    private TipoTermoRBTrans tipoTermoRBTrans;
    @Transient
    @Invisivel
    private Long criadoEm;
    private String descricao;

    public TermoRBTrans() {
        dataTermo = new Date();
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PermissaoTransporte getPermissaoTransporte() {
        return permissaoTransporte;
    }

    public void setPermissaoTransporte(PermissaoTransporte permissaoTransporte) {
        this.permissaoTransporte = permissaoTransporte;
    }

    public Date getDataTermo() {
        return dataTermo;
    }

    public void setDataTermo(Date dataTermo) {
        this.dataTermo = dataTermo;
    }

    public TipoTermoRBTrans getTipoTermoRBTrans() {
        return tipoTermoRBTrans;
    }

    public void setTipoTermoRBTrans(TipoTermoRBTrans tipoTermoRBTrans) {
        this.tipoTermoRBTrans = tipoTermoRBTrans;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        return this.getClass().getSimpleName() + " " + this.getId();
    }

    @Override
    public int compareTo(TermoRBTrans o) {
        return o.dataTermo.compareTo(this.dataTermo);
    }
}
