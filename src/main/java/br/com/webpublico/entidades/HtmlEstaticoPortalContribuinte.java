package br.com.webpublico.entidades;

import br.com.webpublico.enums.LocalHtmlEstaticoPortalContribuinte;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Html estático do Portal do Contribuinte")
public class HtmlEstaticoPortalContribuinte extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private ConfiguracaoPortalContribuinte configuracao;
    @Enumerated(EnumType.STRING)
    private LocalHtmlEstaticoPortalContribuinte localHtmlEstaticoPortalContribuinte;
    private String descricaoPagina;
    private String corpoPagina;


    public HtmlEstaticoPortalContribuinte() {
    }

    public HtmlEstaticoPortalContribuinte(ConfiguracaoPortalContribuinte configuracao, LocalHtmlEstaticoPortalContribuinte local) {
        this.configuracao = configuracao;
        this.localHtmlEstaticoPortalContribuinte = local;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoPortalContribuinte getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoPortalContribuinte configuracao) {
        this.configuracao = configuracao;
    }

    public LocalHtmlEstaticoPortalContribuinte getLocalHtmlEstaticoPortalContribuinte() {
        return localHtmlEstaticoPortalContribuinte;
    }

    public void setLocalHtmlEstaticoPortalContribuinte(LocalHtmlEstaticoPortalContribuinte localHtmlEstaticoPortalContribuinte) {
        this.localHtmlEstaticoPortalContribuinte = localHtmlEstaticoPortalContribuinte;
    }

    public String getDescricaoPagina() {
        return descricaoPagina;
    }

    public void setDescricaoPagina(String descricaoPagina) {
        this.descricaoPagina = descricaoPagina;
    }

    public String getCorpoPagina() {
        return corpoPagina;
    }

    public void setCorpoPagina(String corpoPagina) {
        this.corpoPagina = corpoPagina;
    }
}
