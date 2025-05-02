/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLaudo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemLaudo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo Laudo")
    @Enumerated(EnumType.STRING)
    private TipoLaudo tipoLaudo;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @ManyToOne
    @Obrigatorio
    private PessoaFisica responsavelLaudo;
    @ManyToOne
    @Obrigatorio
    private Laudo laudo;
    @OneToMany(mappedBy = "itemLaudo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorUnidadeOrganizacional> valoresUnidadesOrganizacionals;
    @Temporal(TemporalType.TIMESTAMP)
    @Transient
    private Date dataRegistro;

    public ItemLaudo() {
        dataRegistro = new Date();
        valoresUnidadesOrganizacionals = new ArrayList<ValorUnidadeOrganizacional>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaFisica getResponsavelLaudo() {
        return responsavelLaudo;
    }

    public void setResponsavelLaudo(PessoaFisica responsavelLaudo) {
        this.responsavelLaudo = responsavelLaudo;
    }

    public TipoLaudo getTipoLaudo() {
        return tipoLaudo;
    }

    public void setTipoLaudo(TipoLaudo tipoLaudo) {
        this.tipoLaudo = tipoLaudo;
    }

    public Laudo getLaudo() {
        return laudo;
    }

    public void setLaudo(Laudo laudo) {
        this.laudo = laudo;
    }

    public List<ValorUnidadeOrganizacional> getValoresUnidadesOrganizacionals() {
        return valoresUnidadesOrganizacionals;
    }

    public void setValoresUnidadesOrganizacionals(List<ValorUnidadeOrganizacional> valoresUnidadesOrganizacionals) {
        this.valoresUnidadesOrganizacionals = valoresUnidadesOrganizacionals;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemLaudo other = (ItemLaudo) obj;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
                || ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemLaudo)) {
            return false;
        }
        ItemLaudo other = (ItemLaudo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tipoLaudo + " - " + responsavelLaudo;
    }
}
