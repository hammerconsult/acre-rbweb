package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class TemplateEmail extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Tipo de Template")
    @Enumerated(EnumType.STRING)
    private TipoTemplateEmail tipo;
    @Obrigatorio
    @Etiqueta("Assunto")
    private String assunto;
    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTemplateEmail getTipo() {
        return tipo;
    }

    public void setTipo(TipoTemplateEmail tipo) {
        this.tipo = tipo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
