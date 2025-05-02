/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author fernando
 */
@Etiqueta("Documento(s) do Protocolo/Processo")
@Entity
@Audited
public class DocumentoProcesso extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Processo")
    private Processo processo;

    @ManyToOne
    @Etiqueta("Documento")
    private Documento documento;

    @Etiqueta("Entregue")
    private Boolean entregue;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)

    @Etiqueta("Data")
    private Date dataRegistro;

    @Etiqueta("Número")
    private String numeroDocumento;

    public DocumentoProcesso() {
        dataRegistro = new Date();
        numeroDocumento = "";
    }

    public DocumentoProcesso(Processo processo, Documento documento, Boolean entregue, Date dataRegistro, String numeroDocumento) {
        this.processo = processo;
        this.documento = documento;
        this.entregue = entregue;
        this.dataRegistro = dataRegistro;
        this.numeroDocumento = numeroDocumento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }


    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Boolean getEntregue() {
        return entregue;
    }

    public void setEntregue(Boolean entregue) {
        this.entregue = entregue;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return "ID: " + id + "-" + processo + "-" + documento;
    }
}
