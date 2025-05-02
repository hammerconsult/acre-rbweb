/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
public class Testada extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal tamanho;
    private Boolean principal;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Face face;
    @ManyToOne
    private Lote lote;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Boolean getPrincipal() {
        return principal != null ? principal : false;
    }

    public String getPrincipalString() {
        if (principal != null && principal == true) {
            return "Sim";
        } else {
            return "Não";
        }
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public BigDecimal getTamanho() {
        return tamanho != null ? tamanho : BigDecimal.ZERO;
    }

    public void setTamanho(BigDecimal tamanho) {
        this.tamanho = tamanho;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (face != null) {
            sb.append(face.getLogradouroBairro().getLogradouro());
        }
        if (tamanho != null) {
            sb.append(", Tamanho: ").append(tamanho);
        }
        if (principal != null && principal) {
            sb.append(", Principal");
        }
        return sb.toString();
    }

    public String getDescricao() {
        return face.getLogradouroBairro().getLogradouro().getNome();
    }

}
