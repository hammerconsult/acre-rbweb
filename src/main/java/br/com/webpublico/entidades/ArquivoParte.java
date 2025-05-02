/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.arquivo.dto.ArquivoParteDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Arquivos")
@Entity

public class ArquivoParte implements Serializable {

    public static final int TAMANHO_MAXIMO = 4096;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = TAMANHO_MAXIMO) //Principalmente por causa do Mysql
    private byte dados[];
    @ManyToOne
    private Arquivo arquivo;

    public static List<ArquivoParteDTO> toListArquivoParteDTO(ArquivoDTO arquivoDTO, List<ArquivoParte> arquivoPartes) {
        List<ArquivoParteDTO> toReturn = Lists.newArrayList();
        if (arquivoPartes != null && !arquivoPartes.isEmpty()) {
            for (ArquivoParte arquivoParte : arquivoPartes) {
                toReturn.add(arquivoParte.toArquivoParteDTO(arquivoDTO));
            }
        }
        return toReturn;
    }

    public static List<ArquivoParte> toListArquivoParte(Arquivo arquivo, List<ArquivoParteDTO> arquivoPartesDTO) {
        List<ArquivoParte> toReturn = Lists.newArrayList();
        if (arquivoPartesDTO != null && !arquivoPartesDTO.isEmpty()) {
            for (ArquivoParteDTO arquivoParteDTO : arquivoPartesDTO) {
                toReturn.add(ArquivoParte.toArquivoParte(arquivo, arquivoParteDTO));
            }
        }
        return toReturn;
    }

    public static ArquivoParte clonar(ArquivoParte arquivoParte, Arquivo arq) {
        ArquivoParte clone = new ArquivoParte();
        clone.setDados(arquivoParte.getDados());
        clone.setArquivo(arq);
        return clone;
    }

    public static ArquivoParte toArquivoParte(Arquivo arquivo, ArquivoParteDTO arquivoParteDTO) {
        ArquivoParte arquivoParte = new ArquivoParte();
        arquivoParte.setId(arquivoParteDTO.getId());
        arquivoParte.setDados(arquivoParteDTO.getDados());
        arquivoParte.setArquivo(arquivo);
        return arquivoParte;
    }

    public ArquivoParteDTO toArquivoParteDTO(ArquivoDTO arquivoDTO) {
        ArquivoParteDTO arquivoParteDTO = new ArquivoParteDTO();
        arquivoParteDTO.setId(this.id);
        arquivoParteDTO.setDados(this.dados);
        arquivoParteDTO.setArquivoDTO(arquivoDTO);
        return arquivoParteDTO;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
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
        if (!(object instanceof ArquivoParte)) {
            return false;
        }
        ArquivoParte other = (ArquivoParte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoParte[id=" + id + "]";
    }
}
