/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.CidDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author andre
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class CID implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Etiqueta("Código da CID")
    @Obrigatorio
    @Pesquisavel
    private String codigoDaCid;
    @Invisivel
    @Transient
    private Long criadoEm;

    public CID() {
        this.criadoEm = System.nanoTime();
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

    public String getCodigoDaCid() {
        return codigoDaCid;
    }

    public void setCodigoDaCid(String codigoDaCid) {
        this.codigoDaCid = codigoDaCid;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return codigoDaCid + " - " + descricao;
    }

    public String getStringReduzido() {
        String texto = codigoDaCid + " - " + descricao;
        if (texto.length() > 150) {
            texto = texto.substring(0, 150) + "...";
        }
        return texto;
    }

    public static CidDTO toCidDTO(CID cid) {
        CidDTO dto = new CidDTO();
        if (cid != null) {
            dto.setCodigoDaCid(cid.getCodigoDaCid());
            dto.setDescricao(cid.getDescricao());
            dto.setId(cid.getId());
        }
        return dto;
    }

    public static CID dtoToCid(CidDTO dto) {
        CID cid = new CID();
        cid.setId(dto.getId());
        cid.setCodigoDaCid(dto.getCodigoDaCid());
        cid.setDescricao(dto.getDescricao());
        return cid;
    }

    public static List<CidDTO> toCidDTOs(List<CID> lista) {
        List<CidDTO> dtos = Lists.newLinkedList();
        for (CID cid : lista) {
            CidDTO dto = toCidDTO(cid);
            if (dto != null) {
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
