/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.pessoa.dto.CidadeDTO;
import br.com.webpublico.util.anotacoes.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Endereçamento")
@Entity

@Audited
//@Tab1le(name = "cidade", uniqueConstraints = {
//    @UniqueConstraint(columnNames= {"nome", "uf_id"})})
@Table(uniqueConstraints =
@UniqueConstraint(name = "nome_unicidade", columnNames = {"nome", "uf_id"}))
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cidade extends SuperEntidade implements NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Column(length = 70)
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("UF")
    private UF uf;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    private Integer codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código IBGE")
    private Integer codigoIBGE;
    private String codigoTom;
    private String codigoSiafi;

    public Cidade() {
    }

    public Cidade(String nome, String cep, UF uf) {
        this.nome = nome;
        this.uf = uf;
    }

    public static CidadeDTO toCidadeDTO(Cidade cidade) {
        if (cidade == null) {
            return null;
        }
        CidadeDTO dto = new CidadeDTO();
        dto.setId(cidade.getId());
        dto.setNomeCidade(cidade.getNome());
        dto.setUfCidade(UF.toUfDTO(cidade.getUf()));
        return dto;
    }

    public static List<CidadeDTO> toCidadesDTO(List<Cidade> cidades) {
        if (cidades == null) {
            return null;
        }
        List<CidadeDTO> dtos = Lists.newLinkedList();
        for (Cidade cidade : cidades) {
            CidadeDTO dto = toCidadeDTO(cidade);
            if (dto != null) {
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public static Cidade dtoToCidade(CidadeDTO naturalidade) {
        if (naturalidade == null) {
            return null;
        }
        if(naturalidade.getId() != null) {
            Cidade c = new Cidade();
            c.setId(naturalidade.getId());
            c.setNome(naturalidade.getNomeCidade());
            c.setUf(UF.dtoToUF(naturalidade.getUfCidade()));
            return c;
        }
        return null;
    }

    public Integer getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(Integer codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getCodigoTom() {
        return codigoTom;
    }

    public void setCodigoTom(String codigoTom) {
        this.codigoTom = codigoTom;
    }

    public String getCodigoSiafi() {
        return codigoSiafi;
    }

    public void setCodigoSiafi(String codigoSiafi) {
        this.codigoSiafi = codigoSiafi;
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
        if (!(object instanceof Cidade)) {
            return false;
        }
        Cidade other = (Cidade) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (nome != null) {
            sb.append(nome);
            sb.append(" ");
        }
        if (uf != null && uf.getNome() != null) {
            sb.append("- ");
            sb.append(uf.getNome());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public MunicipioNfseDTO toNfseDto() {
        MunicipioNfseDTO dto = new MunicipioNfseDTO();
        if (this.codigoIBGE != null)
            dto.setCodigo(codigoIBGE.toString());
        dto.setEstado(uf.getUf());
        dto.setNome(nome);
        dto.setId(id);
        return dto;
    }
}
