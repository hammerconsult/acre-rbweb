/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contrato")
@Etiqueta("Documento Contrato")
public class DocumentoContrato implements Serializable,ValidadorEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Contrato")
    @ManyToOne
    @Pesquisavel
    private Contrato contrato;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Modelo Documento")
    @ManyToOne
    @Pesquisavel
    private ModeloDocumentoContrato modeloDocumentoContrato;
    @Transient
    @Invisivel
    private Long criadoEm;

    public DocumentoContrato() {
        criadoEm = System.nanoTime();
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ModeloDocumentoContrato getModeloDocumentoContrato() {
        return modeloDocumentoContrato;
    }

    public void setModeloDocumentoContrato(ModeloDocumentoContrato modeloDocumentoContrato) {
        this.modeloDocumentoContrato = modeloDocumentoContrato;
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
        return "Documento Contrato = " + modeloDocumentoContrato;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
    }
}
