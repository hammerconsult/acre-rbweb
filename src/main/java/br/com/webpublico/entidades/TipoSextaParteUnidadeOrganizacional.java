package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class TipoSextaParteUnidadeOrganizacional extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TipoSextaParte tipoSextaParte;
    @Etiqueta("Hierarquia Organizacional")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSextaParte getTipoSextaParte() {
        return tipoSextaParte;
    }

    public void setTipoSextaParte(TipoSextaParte tipoSextaParte) {
        this.tipoSextaParte = tipoSextaParte;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
