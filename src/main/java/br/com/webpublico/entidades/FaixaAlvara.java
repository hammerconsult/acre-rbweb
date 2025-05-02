/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz
 */
@GrupoDiagrama(nome = "Alvara")
@Audited
@Entity

@Etiqueta(value = "Faixa de Alvará")
public class FaixaAlvara implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    public Exercicio exercicioVigencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Classificação da Atividade")
    private ClassificacaoAtividade classificacaoAtividade;
    @Transient
    private Long criadoEm;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faixaAlvara", orphanRemoval = true)
    public List<ItemFaixaAlvara> itemFaixaAlvaras;

    public FaixaAlvara() {
        this.criadoEm = System.nanoTime();
        this.itemFaixaAlvaras = new ArrayList<ItemFaixaAlvara>();
    }

    public List<ItemFaixaAlvara> getItemFaixaAlvaras() {
        return itemFaixaAlvaras;
    }

    public void setItemFaixaAlvaras(List<ItemFaixaAlvara> itemFaixaAlvaras) {
        this.itemFaixaAlvaras = itemFaixaAlvaras;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Exercicio getExercicioVigencia() {
        return exercicioVigencia;
    }

    public void setExercicioVigencia(Exercicio exercicioVigencia) {
        this.exercicioVigencia = exercicioVigencia;
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
        if (!(object instanceof FaixaAlvara)) {
            return false;
        }
        FaixaAlvara other = (FaixaAlvara) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.FaixaAlvara[ id=" + id + " ]";
    }

}
