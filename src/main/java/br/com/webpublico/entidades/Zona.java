package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Audited
@Etiqueta("Zona")
public class Zona extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Sigla")
    private String sigla;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    private Boolean exibirPortalContribuinte;

    public Zona() {
        super();
        exibirPortalContribuinte = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getExibirPortalContribuinte() {
        return exibirPortalContribuinte;
    }

    public void setExibirPortalContribuinte(Boolean exibirPortalContribuinte) {
        this.exibirPortalContribuinte = exibirPortalContribuinte;
    }

    @Override
    public String toString() {
        return codigo + " - " + sigla + " - " + descricao;
    }
}
