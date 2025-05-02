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
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Tipo de Execução")
public class TipoExecucao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Invisivel
    @Etiqueta("Tipo de Certidão")
    @OneToMany(mappedBy = "tipoExecucao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoExecucaoTipoCertidao> tipoExecucaoTipoCertidao;
    @Invisivel
    @Etiqueta("Tipo de Cláusula")
    @OneToMany(mappedBy = "tipoExecucao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoExecucaoClausulaBen> tipoExecucaoClausulaBens;

    public TipoExecucao() {
        tipoExecucaoClausulaBens = new ArrayList<TipoExecucaoClausulaBen>();
        tipoExecucaoTipoCertidao = new ArrayList<TipoExecucaoTipoCertidao>();
    }

    public TipoExecucao(String descricao, List<TipoExecucaoTipoCertidao> tipoExecucaoTipoCertidao, List<TipoExecucaoClausulaBen> tipoExecucaoClausulaBens) {
        this.descricao = descricao;
        this.tipoExecucaoTipoCertidao = tipoExecucaoTipoCertidao;
        this.tipoExecucaoClausulaBens = tipoExecucaoClausulaBens;
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

    public List<TipoExecucaoTipoCertidao> getTipoExecucaoTipoCertidao() {
        return tipoExecucaoTipoCertidao;
    }

    public void setTipoExecucaoTipoCertidao(List<TipoExecucaoTipoCertidao> tipoExecucaoTipoCertidao) {
        this.tipoExecucaoTipoCertidao = tipoExecucaoTipoCertidao;
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
        if (!(object instanceof TipoExecucao)) {
            return false;
        }
        TipoExecucao other = (TipoExecucao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
