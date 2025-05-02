package br.com.webpublico.entidades.administrativo.patrimonio;


import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ParametroPatrimonio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Renato Romanini on 13/06/2018.
 */
@Entity
@Audited
@Etiqueta("Secretaria Controle Setorial")
public class SecretariaControleSetorial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Parâmetro do Patrimônio")
    @Obrigatorio
    private ParametroPatrimonio parametroPatrimonio;

    @ManyToOne
    @Etiqueta("Secretária")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @OneToMany(mappedBy = "secretariaControleSetorial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Unidades")
    private List<UnidadeControleSetorial> unidades;

    public SecretariaControleSetorial() {
        this.unidades = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroPatrimonio getParametroPatrimonio() {
        return parametroPatrimonio;
    }

    public void setParametroPatrimonio(ParametroPatrimonio parametroPatrimonio) {
        this.parametroPatrimonio = parametroPatrimonio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<UnidadeControleSetorial> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeControleSetorial> unidades) {
        this.unidades = unidades;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }
}
