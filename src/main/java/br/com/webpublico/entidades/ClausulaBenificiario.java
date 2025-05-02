/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reidocrime
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(" Cláusula Benificiária")
public class ClausulaBenificiario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Código")
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Conteúdo")
    @Obrigatorio
    private String conteudo;
    @Invisivel
    @ManyToMany(mappedBy = "clausulasBenificiarios")
    private List<EntidadeBeneficiaria> entidadeBeneficiarias;
    @Invisivel
    @OneToMany(mappedBy = "clausulaBenificiario")
    private List<TipoExecucaoClausulaBen> tipoExecucaoClausulaBens;
    @Etiqueta(value = "Observação")
    @Tabelavel(campoSelecionado = false)
    private String observacao;

    public ClausulaBenificiario() {
        entidadeBeneficiarias = new ArrayList<EntidadeBeneficiaria>();
        tipoExecucaoClausulaBens = new ArrayList<TipoExecucaoClausulaBen>();
    }

    public ClausulaBenificiario(List<EntidadeBeneficiaria> entidadeBeneficiarias, List<TipoExecucaoClausulaBen> tipoExecucaoClausulaBens, String codigo, String conteudo, String observacao) {
        this.entidadeBeneficiarias = entidadeBeneficiarias;
        this.tipoExecucaoClausulaBens = tipoExecucaoClausulaBens;
        this.codigo = codigo;
        this.conteudo = conteudo;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<EntidadeBeneficiaria> getEntidadeBeneficiarias() {
        return entidadeBeneficiarias;
    }

    public void setEntidadeBeneficiarias(List<EntidadeBeneficiaria> entidadeBeneficiarias) {
        this.entidadeBeneficiarias = entidadeBeneficiarias;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<TipoExecucaoClausulaBen> getTipoExecucaoClausulaBens() {
        return tipoExecucaoClausulaBens;
    }

    public void setTipoExecucaoClausulaBens(List<TipoExecucaoClausulaBen> tipoExecucaoClausulaBens) {
        this.tipoExecucaoClausulaBens = tipoExecucaoClausulaBens;
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
        if (!(object instanceof ClausulaBenificiario)) {
            return false;
        }
        ClausulaBenificiario other = (ClausulaBenificiario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (codigo != null && conteudo != null) {
            return retorno + codigo + " - " + conteudo;
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "";

    }
}
