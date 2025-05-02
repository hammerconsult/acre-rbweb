package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 15/01/14
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ArquivoLancamentoOutorga implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Arquivo arquivo;
    @ManyToOne
    private LancamentoOutorga lancamentoOutorga;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataUpload;
    private Boolean excluido;
    @Transient
    private Object file;
    private String origem;
    private String descricao;

    public ArquivoLancamentoOutorga() {
        dataUpload = new Date();
        excluido = false;
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

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public LancamentoOutorga getLancamentoOutorga() {
        return lancamentoOutorga;
    }

    public void setLancamentoOutorga(LancamentoOutorga lancamentoOutorga) {
        this.lancamentoOutorga = lancamentoOutorga;
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
