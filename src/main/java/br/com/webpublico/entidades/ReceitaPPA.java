package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Previsão Receita PPA")
public class ReceitaPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("PPA")
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private PPA ppa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receitaPPA", orphanRemoval = true)
    private List<ReceitaPPAContas> receitaPPAContas;
    @Transient
    private Long criadoEm;

    public ReceitaPPA() {
        criadoEm = System.nanoTime();
        receitaPPAContas = new ArrayList<ReceitaPPAContas>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public List<ReceitaPPAContas> getReceitaPPAContas() {
        return receitaPPAContas;
    }

    public void setReceitaPPAContas(List<ReceitaPPAContas> receitaPPAContas) {
        this.receitaPPAContas = receitaPPAContas;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
