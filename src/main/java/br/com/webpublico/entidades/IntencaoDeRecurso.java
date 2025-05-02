package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Julio
 * Date: 16/04/14
 * Time: 09:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class IntencaoDeRecurso extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pessoa fornecedor;

    @ManyToOne
    private Pessoa representante;

    @ManyToOne
    private Pregao pregao;

    @Temporal(TemporalType.DATE)
    private Date dataIntencao;

    private String motivo;

    public IntencaoDeRecurso() {
        super();
        dataIntencao = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Pregao getPregao() {
        return pregao;
    }

    public void setPregao(Pregao pregao) {
        this.pregao = pregao;
    }

    public Date getDataIntencao() {
        return dataIntencao;
    }

    public void setDataIntencao(Date dataIntencao) {
        this.dataIntencao = dataIntencao;
    }

    public Pessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(Pessoa representante) {
        this.representante = representante;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
