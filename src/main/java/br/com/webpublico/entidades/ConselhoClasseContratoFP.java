/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.ConselhoClasseOrdemPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.ConselhoClasseOrdemDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ConselhoClasseContratoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Número do Documento")
    private String numeroDocumento;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Emissão")
    private Date dataEmissao;
    @Obrigatorio
    @ManyToOne
    private UF uf;
    @Obrigatorio
    @Etiqueta("Conselho Classe Ordem")
    @ManyToOne
    private ConselhoClasseOrdem conselhoClasseOrdem;
    @ManyToOne
    private PessoaFisica pessoaFisica;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateRegistro;

    public ConselhoClasseContratoFP() {
        dateRegistro = new Date();
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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

    public ConselhoClasseOrdem getConselhoClasseOrdem() {
        return conselhoClasseOrdem;
    }

    public void setConselhoClasseOrdem(ConselhoClasseOrdem conselhoClasseOrdem) {
        this.conselhoClasseOrdem = conselhoClasseOrdem;
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
        if (!(object instanceof ConselhoClasseContratoFP)) {
            return false;
        }
        ConselhoClasseContratoFP other = (ConselhoClasseContratoFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dateRegistro == null && other.dateRegistro != null) || (this.dateRegistro != null && !this.dateRegistro.equals(other.dateRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return numeroDocumento + " - " + dataEmissao + " - " + uf;
    }


    public static List<ConselhoClasseOrdemDTO> toConselhoClasseOrdemDTOs(List<ConselhoClasseContratoFP> conselhos) {
        if (conselhos == null) {
            return null;
        }
        List<ConselhoClasseOrdemDTO> dtos = Lists.newLinkedList();
        for (ConselhoClasseContratoFP conselho : conselhos) {
            ConselhoClasseOrdemDTO dto = toConselhoClasseOrdemDTO(conselho);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static ConselhoClasseOrdemDTO toConselhoClasseOrdemDTO(ConselhoClasseContratoFP conselho) {
        if (conselho == null) {
            return null;
        }
        ConselhoClasseOrdemDTO dto = new ConselhoClasseOrdemDTO();
        dto.setId(conselho.getId());
        dto.setConselhoClasse(ConselhoClasseOrdem.toConselhoClasse(conselho.getConselhoClasseOrdem()));
        dto.setDataEmissaoConselho(conselho.getDataEmissao());
        dto.setNumeroDocumento(conselho.getNumeroDocumento());
        dto.setUfConselho(UF.toUfDTO(conselho.getUf()));
        return dto;
    }

    public static List<ConselhoClasseOrdemDTO> toConselhoClasseOrdemPortalDTOs(List<ConselhoClasseOrdemPortal> conselhos) {
        if (conselhos == null) {
            return null;
        }
        List<ConselhoClasseOrdemDTO> dtos = Lists.newLinkedList();
        for (ConselhoClasseOrdemPortal conselho : conselhos) {
            ConselhoClasseOrdemDTO dto = toConselhoClasseOrdemPortalDTO(conselho);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static ConselhoClasseOrdemDTO toConselhoClasseOrdemPortalDTO(ConselhoClasseOrdemPortal conselho) {
        if (conselho == null) {
            return null;
        }
        ConselhoClasseOrdemDTO dto = new ConselhoClasseOrdemDTO();
        dto.setId(conselho.getId());
        dto.setConselhoClasse(ConselhoClasseOrdem.toConselhoClasse(conselho.getConselhoClasseOrdem()));
        dto.setDataEmissaoConselho(conselho.getDataEmissao());
        dto.setNumeroDocumento(conselho.getNumeroDocumento());
        dto.setUfConselho(UF.toUfDTO(conselho.getUf()));
        return dto;
    }

    public static List<ConselhoClasseContratoFP> toConselhos(PessoaFisica pessoaFisica, List<ConselhoClasseOrdemPortal> conselhos) {
        if (conselhos == null) {
            return null;
        }
        List<ConselhoClasseContratoFP> dtos = Lists.newLinkedList();
        for (ConselhoClasseOrdemPortal conselho : conselhos) {
            ConselhoClasseContratoFP dto = toConselhoClasseOrdem(pessoaFisica, conselho);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public static ConselhoClasseContratoFP toConselhoClasseOrdem(PessoaFisica pessoa, ConselhoClasseOrdemPortal conselho) {
        if (conselho == null) {
            return null;
        }
        ConselhoClasseContratoFP dto = new ConselhoClasseContratoFP();
        dto.setPessoaFisica(pessoa);
        dto.setConselhoClasseOrdem(conselho.getConselhoClasseOrdem());
        dto.setDataEmissao(conselho.getDataEmissao());
        dto.setNumeroDocumento(conselho.getNumeroDocumento());
        dto.setUf(conselho.getUf());
        return dto;
    }

}
