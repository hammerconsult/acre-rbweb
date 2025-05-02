/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@GrupoDiagrama(nome = "Arquivos")
@Audited
public class ArquivoCalcDividaDiversa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @ManyToOne
    private CalculoDividaDiversa calculoDividaDiversa;
    @Temporal(TemporalType.DATE)
    private Date dataUpload;
    private Boolean excluido;
    @Transient
    private Object file;

    public ArquivoCalcDividaDiversa() {
        dataUpload = new Date();
        excluido = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public CalculoDividaDiversa getCalculoDividaDiversa() {
        return calculoDividaDiversa;
    }

    public void setCalculoDividaDiversa(CalculoDividaDiversa calculoDividaDiversa) {
        this.calculoDividaDiversa = calculoDividaDiversa;
    }

    public Date getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(Date dataUpload) {
        this.dataUpload = dataUpload;
    }

    public Boolean getExcluido() {
        return excluido;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
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
        if (!(object instanceof ArquivoCalcDividaDiversa)) {
            return false;
        }
        ArquivoCalcDividaDiversa other = (ArquivoCalcDividaDiversa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id.toString();
    }

}
