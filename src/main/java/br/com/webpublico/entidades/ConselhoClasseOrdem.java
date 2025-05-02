/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.ConselhoClasseDTO;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Conselho de Classe/Ordem")
public class ConselhoClasseOrdem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Sigla")
    private String sigla;

    public ConselhoClasseOrdem() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
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
        if (!(object instanceof ConselhoClasseOrdem)) {
            return false;
        }
        ConselhoClasseOrdem other = (ConselhoClasseOrdem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome + " - " + sigla;
    }

    public static List<ConselhoClasseDTO> toConselhos(List<ConselhoClasseOrdem> conselhos) {
        if (conselhos == null) {
            return null;
        }
        List<ConselhoClasseDTO> dtos = Lists.newLinkedList();
        for (ConselhoClasseOrdem conselho : conselhos) {
            ConselhoClasseDTO dto = toConselhoClasse(conselho);
            if (dto != null) {
                dtos.add(dto);
            }

        }
        return dtos;
    }


    public static ConselhoClasseDTO toConselhoClasse(ConselhoClasseOrdem conselho) {
        if (conselho == null) {
            return null;
        }
        ConselhoClasseDTO dto = new ConselhoClasseDTO();
        dto.setId(conselho.getId());
        dto.setNome(conselho.getNome());
        dto.setSigla(conselho.getSigla());
        return dto;
    }

    public static ConselhoClasseOrdem dtoToConselhoClasseOrdem(ConselhoClasseDTO dto) {
        ConselhoClasseOrdem c = new ConselhoClasseOrdem();
        c.setId(dto.getId());
        return c;
    }
}
