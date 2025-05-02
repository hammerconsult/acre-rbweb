package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;




/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 17/06/14
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class ArquivoSolicitacaoBaixa extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Arquivo arquivo;

    @ManyToOne
    private SolicitacaoBaixaPatrimonial solicitacaoBaixa;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataUpload;

    private Boolean excluido;

    @Transient
    private Object file;

    public ArquivoSolicitacaoBaixa() {
        super();
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

    public SolicitacaoBaixaPatrimonial getSolicitacaoBaixa() {
        return solicitacaoBaixa;
    }

    public void setSolicitacaoBaixa(SolicitacaoBaixaPatrimonial solicitacaoBaixa) {
        this.solicitacaoBaixa = solicitacaoBaixa;
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
}
