package br.com.webpublico.entidades.administrativo.frotas;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by William on 15/06/2018.
 */
@Entity
@Audited
public class ParametroFrotasHierarquia extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @ManyToOne
    private ParametroFrotas parametroFrotas;
    @OneToMany(mappedBy = "parametroFrotasHierarquia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroFrotasTaxa> itemTaxaVeiculo;

    public ParametroFrotasHierarquia() {
        itemTaxaVeiculo = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ParametroFrotas getParametroFrotas() {
        return parametroFrotas;
    }

    public void setParametroFrotas(ParametroFrotas parametroFrotas) {
        this.parametroFrotas = parametroFrotas;
    }

    public List<ParametroFrotasTaxa> getItemTaxaVeiculo() {
        return itemTaxaVeiculo;
    }

    public void setItemTaxaVeiculo(List<ParametroFrotasTaxa> itemTaxaVeiculo) {
        this.itemTaxaVeiculo = itemTaxaVeiculo;
    }
}
