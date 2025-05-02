/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.TituloEleitorPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.TituloEleitorDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class TituloEleitor extends DocumentoPessoal {

    @Tabelavel
    @Column(length = 14)
    private String numero;
    @Tabelavel
    @Column(length = 20)
    private String zona;
    @Tabelavel
    @Column(length = 20)
    private String sessao;
    @Etiqueta("Data de Emissão")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEmissao;
    @ManyToOne
    private Cidade cidade;

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSessao() {
        return sessao;
    }

    public void setSessao(String sessao) {
        this.sessao = sessao;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    @Override
    public String toString() {
        return numero + "-" + zona + "-" + sessao;
    }

    public static TituloEleitorDTO toTituloEleitorDTO(TituloEleitor tituloEleitor) {
        if (tituloEleitor == null) {
            return new TituloEleitorDTO();
        }
        TituloEleitorDTO dto = new TituloEleitorDTO();
        dto.setId(tituloEleitor.getId());
        dto.setCidadeTitulo(Cidade.toCidadeDTO(tituloEleitor.getCidade()));
        dto.setDataEmissaoTitulo(tituloEleitor.getDataEmissao());
        dto.setNumeroTitulo(tituloEleitor.getNumero());
        dto.setSessao(tituloEleitor.getSessao());
        dto.setZona(tituloEleitor.getZona());
        return dto;
    }

    public static TituloEleitor toTituloEleitor(PessoaFisica pessoaFisica, TituloEleitorPortal tituloEleitor) {
        if (tituloEleitor == null) {
            return null;
        }
        TituloEleitor titulo = new TituloEleitor();
        titulo.setPessoaFisica(pessoaFisica);
        titulo.setCidade(tituloEleitor.getCidade());
        titulo.setDataEmissao(tituloEleitor.getDataEmissao());
        titulo.setNumero(tituloEleitor.getNumero());
        titulo.setSessao(tituloEleitor.getSessao());
        titulo.setZona(tituloEleitor.getZona());
        return titulo;
    }

    public static TituloEleitorDTO toTituloEleitorPortalDTO(TituloEleitorPortal tituloEleitor) {
        if (tituloEleitor == null) {
            return null;
        }
        TituloEleitorDTO dto = new TituloEleitorDTO();
        dto.setId(tituloEleitor.getId());
        dto.setCidadeTitulo(Cidade.toCidadeDTO(tituloEleitor.getCidade()));
        dto.setDataEmissaoTitulo(tituloEleitor.getDataEmissao());
        dto.setNumeroTitulo(tituloEleitor.getNumero());
        dto.setSessao(tituloEleitor.getSessao());
        dto.setZona(tituloEleitor.getZona());
        return dto;
    }
}
