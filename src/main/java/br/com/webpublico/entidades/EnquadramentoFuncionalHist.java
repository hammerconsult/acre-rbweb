package br.com.webpublico.entidades;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/07/2015
 * Time: 17:46
 */

@Entity
public class EnquadramentoFuncionalHist extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date inicioVigencia;
    private Date finalVigencia;
    @ManyToOne
    private CategoriaPCS categoriaPCS;
    @ManyToOne
    private ProgressaoPCS progressaoPCS;
    private Date dataCadastro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadatro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean temId() {
        return id != null;
    }
}
