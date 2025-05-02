/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cadastro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotAudited
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cadastro", orphanRemoval = true)
    private List<Historico> historicos;
    @ManyToOne
    private UnidadeOrganizacional responsavelPeloCadastro;
    private String migracaoChave;

    protected Cadastro() {
        historicos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Historico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<Historico> historicos) {
        this.historicos = historicos;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public UnidadeOrganizacional getResponsavelPeloCadastro() {
        return responsavelPeloCadastro;
    }

    public void setResponsavelPeloCadastro(UnidadeOrganizacional responsavelPeloCadastro) {
        this.responsavelPeloCadastro = responsavelPeloCadastro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cadastro)) {
            return false;
        }
        Cadastro other = (Cadastro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (getNumeroCadastro() != null) {
            return getNumeroCadastro().toString() + "/" + getTipoDeCadastro().getDescricao();
        }
        return super.toString();
    }

    public abstract String getNumeroCadastro();

    public abstract String getCadastroResponsavel();

    public TipoCadastroTributario getTipoDeCadastro() {
        if (this instanceof CadastroEconomico) {
            return TipoCadastroTributario.ECONOMICO;
        } else if (this instanceof CadastroImobiliario) {
            return TipoCadastroTributario.IMOBILIARIO;
        } else if (this instanceof CadastroRural) {
            return TipoCadastroTributario.RURAL;
        } else {
            return null;
        }
    }
}
