package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Órgão/Entidade Pncp")
public class OrgaoEntidadePncp extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Entidade")
    private Entidade entidade;

    private Boolean integrado;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orgaoEntidadePncp", orphanRemoval = true)
    private List<OrgaoEntidadeUnidadePncp> unidades;

    public OrgaoEntidadePncp() {
        unidades = Lists.newArrayList();
        integrado = false;
    }

    public Boolean getIntegrado() {
        return integrado;
    }

    public void setIntegrado(Boolean integrado) {
        this.integrado = integrado;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public List<OrgaoEntidadeUnidadePncp> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<OrgaoEntidadeUnidadePncp> orgaos) {
        this.unidades = orgaos;
    }
}
