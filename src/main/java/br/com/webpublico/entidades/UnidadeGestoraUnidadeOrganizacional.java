/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Administrativo")
@Etiqueta("Unidade Gestora")
@Table(name = "UGESTORAUORGANIZACIONAL")
public class UnidadeGestoraUnidadeOrganizacional extends SuperEntidade implements Comparable<UnidadeGestoraUnidadeOrganizacional> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    private Long id;

    @ManyToOne
    @Etiqueta("Unidade Gestora")
    private UnidadeGestora unidadeGestora;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public UnidadeGestoraUnidadeOrganizacional() {
    }

    public UnidadeGestoraUnidadeOrganizacional(UnidadeGestora unidadeGestora, UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeGestora = unidadeGestora;
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.toString();
    }

    @Override
    public int compareTo(UnidadeGestoraUnidadeOrganizacional o) {
        if (getHierarquiaOrganizacional() != null && o.getHierarquiaOrganizacional() != null) {
            return getHierarquiaOrganizacional().getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }
}
