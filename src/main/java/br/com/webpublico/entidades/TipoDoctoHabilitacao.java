/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDocumentoHabilitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Tipo de Documento de Habilitação")
public class TipoDoctoHabilitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Documento")
    private TipoDocumentoHabilitacao tipoDocumentoHabilitacao;

    @Etiqueta("Documentos de Habilitação")
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "tipoDoctoHabilitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctoHabilitacao> listaDoctosHab;

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

    public List<DoctoHabilitacao> getListaDoctosHab() {
        return listaDoctosHab;
    }

    public void setListaDoctosHab(List<DoctoHabilitacao> listaDoctosHab) {
        this.listaDoctosHab = listaDoctosHab;
    }

    public TipoDocumentoHabilitacao getTipoDocumentoHabilitacao() {
        return tipoDocumentoHabilitacao;
    }

    public void setTipoDocumentoHabilitacao(TipoDocumentoHabilitacao tipoDocumentoHabilitacao) {
        this.tipoDocumentoHabilitacao = tipoDocumentoHabilitacao;
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
        if (!(object instanceof TipoDoctoHabilitacao)) {
            return false;
        }
        TipoDoctoHabilitacao other = (TipoDoctoHabilitacao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = descricao;
        if(tipoDocumentoHabilitacao != null){
            retorno += " - Tipo: "+tipoDocumentoHabilitacao.getDescricao();
        }
        return retorno;
    }
}
