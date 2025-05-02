/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.EtapaUploadArquivoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
public class FiscalizacaoArquivoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FiscalizacaoRBTrans fiscalizacaoRBTrans;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataUpLoad;
    @Enumerated(EnumType.STRING)
    private EtapaUploadArquivoRBTrans etapaUploadArquivoRBTrans;
    @Transient
    private Object file;
    private Boolean excluido;
    @Transient
    private Long criadoEm;

    public FiscalizacaoArquivoRBTrans() {
        dataUpLoad = new Date();
        excluido = false;
        criadoEm = System.nanoTime();
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

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public EtapaUploadArquivoRBTrans getEtapaUploadArquivoRBTrans() {
        return etapaUploadArquivoRBTrans;
    }

    public void setEtapaUploadArquivoRBTrans(EtapaUploadArquivoRBTrans etapaUploadArquivoRBTrans) {
        this.etapaUploadArquivoRBTrans = etapaUploadArquivoRBTrans;
    }

    public Date getDataUpLoad() {
        return dataUpLoad;
    }

    public void setDataUpLoad(Date dataUpLoad) {
        this.dataUpLoad = dataUpLoad;
    }

    public FiscalizacaoRBTrans getFiscalizacaoRBTrans() {
        return fiscalizacaoRBTrans;
    }

    public void setFiscalizacaoRBTrans(FiscalizacaoRBTrans fiscalizacaoRBTrans) {
        this.fiscalizacaoRBTrans = fiscalizacaoRBTrans;
    }

    public Arquivo getArquivos() {
        return arquivo;
    }

    public void setArquivos(Arquivo arquivos) {
        this.arquivo = arquivos;
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.FiscalizacaoArquivoRBTrans[ id=" + id + " ]";
    }
}
