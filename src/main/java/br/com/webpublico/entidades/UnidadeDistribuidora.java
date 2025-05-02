package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Criado por Mateus
 * Data: 25/01/2017.
 */
@Entity
@Etiqueta("Unidade Distribuidora")
@Audited
@GrupoDiagrama(nome = "Materiais")
public class UnidadeDistribuidora extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Unidade Distribuidora")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Distribuidora")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public UnidadeDistribuidora() {
    }

    public UnidadeDistribuidora(UnidadeDistribuidora unidadeDistribuidora , HierarquiaOrganizacional hierarquiaOrganizacional) {
       this.setId(unidadeDistribuidora.getId());
       this.setUnidadeOrganizacional(unidadeDistribuidora.getUnidadeOrganizacional());
       this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

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

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional !=null){
            setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        try {
            if (hierarquiaOrganizacional !=null) {
                return hierarquiaOrganizacional.toString();
            }
            return unidadeOrganizacional.toString();
        }catch (NullPointerException e){
            return "";
        }
    }
}
