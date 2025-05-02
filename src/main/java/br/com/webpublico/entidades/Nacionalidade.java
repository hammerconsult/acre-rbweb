/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;


import br.com.webpublico.enums.rh.previdencia.NacionalidadeBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.NacionalidadeDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class Nacionalidade extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Column(length = 30)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Etiqueta("Código Raiz")
    private Integer codigoRaiz;
    @ManyToOne
    @Etiqueta("País")
    @Obrigatorio
    private Pais pais;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Nacionalidade BBPrev")
    private NacionalidadeBBPrev nacionalidadeBBPrev;

    public Nacionalidade() {
    }

    public Nacionalidade(String descricao) {
        this.descricao = descricao;
    }

    public static NacionalidadeDTO toNacionalidadeDTO(Nacionalidade nacionalidade) {
        if (nacionalidade == null) {
            return null;
        }
        NacionalidadeDTO dto = new NacionalidadeDTO();
        dto.setId(nacionalidade.getId());
        dto.setCodigoRaiz(nacionalidade.getCodigoRaiz());
        dto.setDescricao(nacionalidade.getDescricao());
        return dto;
    }

    public static List<NacionalidadeDTO> toNacionalidadesDTO(List<Nacionalidade> lista) {
        if (lista == null) {
            return null;
        }
        List<NacionalidadeDTO> dtos = Lists.newLinkedList();
        for (Nacionalidade nacionalidade : lista) {
            NacionalidadeDTO dto = toNacionalidadeDTO(nacionalidade);
            if (dto != null) {
                dtos.add(dto);
            }

        }
        return dtos;
    }

    public static Nacionalidade dtoToNacionalidade(NacionalidadeDTO dto) {
        if (dto == null) {
            return null;
        }
        Nacionalidade nacionalidade = new Nacionalidade();
        nacionalidade.setId(dto.getId());
        return nacionalidade;
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

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Integer getCodigoRaiz() {
        return codigoRaiz;
    }

    public void setCodigoRaiz(Integer codigoRaiz) {
        this.codigoRaiz = codigoRaiz;
    }

    public NacionalidadeBBPrev getNacionalidadeBBPrev() {
        return nacionalidadeBBPrev;
    }

    public void setNacionalidadeBBPrev(NacionalidadeBBPrev nacionalidadeBBPrev) {
        this.nacionalidadeBBPrev = nacionalidadeBBPrev;
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
        if (!(object instanceof Nacionalidade)) {
            return false;
        }
        Nacionalidade other = (Nacionalidade) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao + " - " + codigoRaiz;
    }
}
