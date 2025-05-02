/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@GrupoDiagrama(nome = "Divida Ativa")
@Audited
public class Correspondencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String codigo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date emissao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date recebimento;
    private boolean entregue;
    private String motivo;
    @OneToOne
    private DocumentoOficial documentoOficial;
    @Invisivel
    @Transient
    private Long criadoEm;

    public Correspondencia() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public boolean isEntregue() {
        return entregue;
    }

    public void setEntregue(boolean entregue) {
        this.entregue = entregue;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getRecebimento() {
        return recebimento;
    }

    public void setRecebimento(Date recebimento) {
        this.recebimento = recebimento;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
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
        return "Codigo=" + codigo + ", Emissão=" + emissao + ", Recebimento=" + recebimento + ", Entregue=" + entregue + " DocumentoOficial=" + documentoOficial.getNumero();
    }
}
