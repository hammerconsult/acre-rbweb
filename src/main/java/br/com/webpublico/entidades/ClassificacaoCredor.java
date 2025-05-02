/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author major
 */
@Entity

@Audited
@Etiqueta("Classificação de Credor")
public class ClassificacaoCredor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Classificação")
    @Tabelavel
    @Obrigatorio
    private String descricao;
    @Etiqueta("Base do Irrf (%)")
    @Tabelavel
    @Obrigatorio
    private BigDecimal baseIrrf;
    @Etiqueta("Gera do Dirf")
    @Tabelavel
    private Boolean geraDirf;

    public ClassificacaoCredor() {
    }

    public ClassificacaoCredor(String descricao, BigDecimal baseIrrf, Boolean geraDirf) {
        this.descricao = descricao;
        this.baseIrrf = baseIrrf;
        this.geraDirf = geraDirf;
    }


    public BigDecimal getBaseIrrf() {
        return baseIrrf;
    }

    public void setBaseIrrf(BigDecimal baseIrrf) {
        this.baseIrrf = baseIrrf;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getGeraDirf() {
        return geraDirf;
    }

    public void setGeraDirf(Boolean geraDirf) {
        this.geraDirf = geraDirf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof ClassificacaoCredor)) {
            return false;
        }
        ClassificacaoCredor other = (ClassificacaoCredor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao + " - " + baseIrrf;
    }

}
