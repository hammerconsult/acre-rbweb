/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Romanini
 */
@Audited
@Entity
@Etiqueta(value = "Cabeçalho de Relatório")
public class CabecalhoRelatorio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Nome")
    @Obrigatorio
    private String nome;
    @Etiqueta(value = "Conteudo")
    @Obrigatorio
    private String conteudo;
    @ManyToOne
    @Etiqueta(value = "Unidade Organizacional")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta(value = "Logo")
    private Arquivo logo;
    @Tabelavel
    @Etiqueta(value = "Padrão")
    @Pesquisavel
    private Boolean padrao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;

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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Arquivo getLogo() {
        return logo;
    }

    public void setLogo(Arquivo logo) {
        this.logo = logo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getPadrao() {
        return padrao;
    }

    public void setPadrao(Boolean padrao) {
        this.padrao = padrao;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        if (unidadeOrganizacional != null) {
            return nome + " - " + unidadeOrganizacional.getCodigoDescricao();
        }
        return nome;
    }

    public String toStringAutoComplete() {
        return nome + " " + (padrao ? "(Padrão)" : "") + " ";
    }
}
