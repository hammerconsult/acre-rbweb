/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author claudio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Certidao")
public class ModeloDoctoOficial extends SuperEntidade implements Serializable, Comparable<ModeloDoctoOficial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String conteudo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date vigenciaInicial;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date vigenciaFinal;
    @Enumerated(EnumType.STRING)
    private TipoModeloDocto.TipoModeloDocumento tipoModeloDocto;
    private Integer sequencia;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficial;

    public ModeloDoctoOficial() {
    }

    public ModeloDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        setTipoDoctoOficial(tipoDoctoOficial);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public TipoModeloDocto.TipoModeloDocumento getTipoModeloDocto() {
        return tipoModeloDocto;
    }

    public void setTipoModeloDocto(TipoModeloDocto.TipoModeloDocumento tipoModeloDocto) {
        this.tipoModeloDocto = tipoModeloDocto;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    @Override
    public int compareTo(ModeloDoctoOficial o) {
        return this.sequencia.compareTo(o.getSequencia());
    }
}
