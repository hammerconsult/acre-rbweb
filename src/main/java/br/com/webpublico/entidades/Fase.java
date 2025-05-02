package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 15/10/13
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
public class Fase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta("Nome")
    @Tabelavel
    @Pesquisavel
    private String nome;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "FASERECURSOSISTEMA", joinColumns =
    @JoinColumn(name = "FASE_ID"), inverseJoinColumns =
    @JoinColumn(name = "RECURSOSISTEMA_ID"))
    private List<RecursoSistema> recursos = Lists.newLinkedList();
    @OneToMany(mappedBy = "fase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeriodoFase> periodoFases;
    @Transient
    private Long criadoEm;

    public Fase() {
        criadoEm = System.nanoTime();
    }

    public Fase(String nome, String codigo, List<RecursoSistema> recursos, List<PeriodoFase> periodoFases) {
        this.nome = nome;
        this.codigo = codigo;
        this.recursos = recursos;
        this.periodoFases = periodoFases;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<RecursoSistema> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoSistema> recursos) {
        this.recursos = recursos;
    }

    public List<PeriodoFase> getPeriodoFases() {
        return periodoFases;
    }

    public void setPeriodoFases(List<PeriodoFase> periodoFases) {
        this.periodoFases = periodoFases;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
