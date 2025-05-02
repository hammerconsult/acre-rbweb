/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.NivelOcorrencia;
import br.com.webpublico.enums.TipoOcorrencia;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Ocorrência")
@GrupoDiagrama(nome = "Tributario")
public class Ocorrencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conteúdo")
    private String conteudo;
    private String detalhesTecnicos;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data de Registro")
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nível da Ocorrência")
    private NivelOcorrencia nivelOcorrencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo da Ocorrência")
    private TipoOcorrencia tipoOcorrencia;

    public Ocorrencia() {
        this.dataRegistro = new Date();
    }

    public Ocorrencia(String msg) {
        this.conteudo = this.detalhesTecnicos = msg;
        this.dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public NivelOcorrencia getNivelOcorrencia() {
        return nivelOcorrencia;
    }

    public void setNivelOcorrencia(NivelOcorrencia nivelOcorrencia) {
        this.nivelOcorrencia = nivelOcorrencia;
    }

    public TipoOcorrencia getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public String getDetalhesTecnicos() {
        return detalhesTecnicos;
    }

    public void setDetalhesTecnicos(String detalhesTecnicos) {
        this.detalhesTecnicos = detalhesTecnicos;
    }

    public void addMessagem(TipoOcorrencia tipo, NivelOcorrencia nivel, String menssagem, String detalhesTecnicos) {
        this.tipoOcorrencia = tipo;
        this.nivelOcorrencia = nivel;
        this.conteudo = menssagem;
        this.detalhesTecnicos = detalhesTecnicos;
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
        if (!(object instanceof Ocorrencia)) {
            return false;
        }
        Ocorrencia other = (Ocorrencia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.OcorrenciaCalculo[ id=" + id + " ]";
    }
}
