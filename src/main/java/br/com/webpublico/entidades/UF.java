/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.UFNfseDTO;
import br.com.webpublico.pessoa.dto.UFDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Endereçamento")
@Entity
@Audited
@Etiqueta("Estado")

public class UF implements NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Column(length = 2, unique = true)
    @Tabelavel
    @Obrigatorio
    private String uf;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Column(length = 30)
    private String nome;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("País")
    private Pais pais;
    private Integer codigoIBGE;

    public UF() {
    }

    public UF(Long id, String nome, String uf, Pais pais) {
        this.id = id;
        this.uf = uf;
        this.nome = nome;
        this.pais = pais;
    }

    public UF(String nome, String uf, Pais pais) {
        this.uf = uf;
        this.nome = nome;
        this.pais = pais;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        if (nome == null) {
            return " ";
        }
        return nome.toUpperCase();
    }

    public void setNome(String nome) {
        if (nome != null) {
            this.nome = nome.toUpperCase();
        }
    }

    public String getUf() {
        if (uf == null) {
            return " ";
        }
        return uf.toUpperCase();
    }

    public void setUf(String uf) {
        if (uf != null) {
            this.uf = uf.toUpperCase();
        }
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
        if (!(object instanceof UF)) {
            return false;
        }
        UF other = (UF) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return uf;
    }

    public static List<UFDTO> toUFsDTO(List<UF> ufs) {
        if (ufs == null) {
            return null;
        }
        List<UFDTO> dtos = Lists.newLinkedList();
        for (UF uf : ufs) {
            UFDTO dto = toUfDTO(uf);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static UFDTO toUfDTO(UF uf) {
        if (uf == null) {
            return null;
        }
        UFDTO dto = new UFDTO();
        dto.setId(uf.getId());
        dto.setNomeUF(uf.getNome());
        dto.setSiglaUF(uf.getUf());
        return dto;
    }

    public static UF dtoToUF(UFDTO dto) {
        if (dto == null) {
            return null;
        }
        if(dto.getId() != null) {
            UF uf = new UF();
            uf.setId(dto.getId());
            uf.setNome(dto.getNomeUF() != null ? dto.getNomeUF() : "");
            uf.setUf(dto.getSiglaUF() != null ? dto.getSiglaUF() : "");
            return uf;
        }
       return null;
    }

    public static UF createUF(Long idUf) {
        UF uf = new UF();
        uf.setId(idUf);
        return uf;
    }

    public Integer getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(Integer codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    @Override
    public UFNfseDTO toNfseDto() {
        UFNfseDTO ufNfseDTO = new UFNfseDTO(id, uf, nome);
        return ufNfseDTO;
    }
}
