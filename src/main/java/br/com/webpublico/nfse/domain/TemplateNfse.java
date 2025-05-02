package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by william on 19/09/17.
 */
@Entity
@Audited
@Etiqueta("Templates Iss Online")
public class TemplateNfse extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo do Template")
    private TipoTemplateNfse tipoTemplate;
    @Obrigatorio
    @Etiqueta("Conteúdo")
    @Pesquisavel
    private String conteudo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTemplateNfse getTipoTemplate() {
        return tipoTemplate;
    }

    public void setTipoTemplate(TipoTemplateNfse tipoTemplate) {
        this.tipoTemplate = tipoTemplate;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

}
