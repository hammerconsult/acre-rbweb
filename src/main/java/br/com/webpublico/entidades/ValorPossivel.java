/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "AtributosDinamicos")
@Entity

@Audited
public class ValorPossivel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String valor;
    @Etiqueta("Código")
    @Tabelavel
    private Integer codigo;
    @Tabelavel
    @ManyToOne
    private Atributo atributo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private boolean valorPadrao;

    public ValorPossivel() {
        dataRegistro = new Date();
    }

    public ValorPossivel(String valor, Atributo atributo) {
        this.valor = valor;
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public boolean isValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(boolean valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {

        return codigo != null && valor != null ? codigo + " - " + valor : codigo != null ? codigo + "" : valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValorPossivel other = (ValorPossivel) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.dataRegistro != null ? this.dataRegistro.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

}
