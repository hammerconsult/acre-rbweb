/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEndereco;
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
@GrupoDiagrama(nome = "Endereçamento")
@Entity

@Audited
public class Endereco implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    @Tabelavel

    private Logradouro logradouro;
    private String numero;
    private String complemento;
    @Etiqueta("Tipo de Endereço")
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public Endereco(Logradouro logradouro, String numero, String complemento, TipoEndereco tipoEndereco) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.tipoEndereco = tipoEndereco;
    }

    public Endereco() {
        logradouro = new Logradouro();
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof Endereco)) {
            return false;
        }
        Endereco other = (Endereco) object;
        if ((this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        String num = "";
        String tipoLogradouro = "";
        String nome = "";
        if (numero != null) {
            num = numero;
        }
        if (logradouro != null) {
            tipoLogradouro = logradouro.getTipoLogradouro().toString();
            nome = logradouro.getNome();
        }
        return String.valueOf(tipoLogradouro + " " + nome + " " + num );
    }
}
