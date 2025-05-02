/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

/**
 * @author Edi
 */
@Entity

@Audited
public class OCCGrupoMaterial extends OrigemContaContabil implements Serializable {

    /**
     * entidade para fazer as configuracoes de origens conta contabeis referente ao grupo de material
     */
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo de Material")
    private GrupoMaterial grupoMaterial;

    public OCCGrupoMaterial() {
    }

    public OCCGrupoMaterial(GrupoMaterial grupoMaterial, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.grupoMaterial = grupoMaterial;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.grupoMaterial);
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
        final OCCGrupoMaterial other = (OCCGrupoMaterial) obj;
        if (!Objects.equals(this.grupoMaterial, other.grupoMaterial)) {
            return false;
        }
        return true;
    }
}
