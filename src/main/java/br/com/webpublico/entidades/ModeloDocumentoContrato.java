/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Modelo de Documento de Contratos")
public class ModeloDocumentoContrato implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Obrigatorio
    private String modelo;
    @OneToMany(mappedBy = "modeloDocumentoContrato")
    @Invisivel
    private List<DocumentoContrato> documentosContrato;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ModeloDocumentoContrato() {
        criadoEm = System.nanoTime();
    }

    public List<DocumentoContrato> getDocumentosContrato() {
        return documentosContrato;
    }

    public void setDocumentosContrato(List<DocumentoContrato> documentosContrato) {
        this.documentosContrato = documentosContrato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        return nome;
    }
}
