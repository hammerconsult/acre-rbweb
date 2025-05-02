package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Unidade Organizacional")
@Table(name = "UNIDADEORGANIZACIONALAN")
public class UnidadeOrganizacionalAtoNormativo extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta(value = "Descrição")
    private String descricao;
    @Obrigatorio
    @Etiqueta(value = "Link")
    private String link;

    public UnidadeOrganizacionalAtoNormativo() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
