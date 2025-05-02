/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.PaisNfseDTO;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Endereçamento")
@Entity

@Audited
@Etiqueta("País")
public class Pais implements Serializable, NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código do IBGE")
    @Obrigatorio
    @Length(maximo = 4)
    @Positivo(permiteZero = false)
    private Integer codigo;
    @Etiqueta("Código Bacen")
    @Obrigatorio
    private Integer codigoBacen;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String sigla;

    public Pais() {
    }

    public Pais(String nome, Integer codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Integer getCodigoBacen() {
        return codigoBacen;
    }

    public void setCodigoBacen(Integer codigoBacen) {
        this.codigoBacen = codigoBacen;
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
        if (!(object instanceof Pais)) {
            return false;
        }
        Pais other = (Pais) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public PaisNfseDTO toNfseDto() {
        PaisNfseDTO dto = new PaisNfseDTO();
        dto.setCodigo(this.getCodigoBacen() != null ? this.getCodigoBacen().toString() : this.getCodigo().toString());
        dto.setNome(this.getNome());
        dto.setId(this.getId());
        dto.setSigla(this.getSigla());
        return dto;
    }
}
