package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "Segurança")
@Entity
@Etiqueta("Grupo de Recurso")
@Audited
public class GrupoRecurso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Etiqueta("Nome")
    @Pesquisavel
    @Tabelavel
    @Column(unique = true)
    private String nome;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "GRUPORECURSOSISTEMA", joinColumns =
    @JoinColumn(name = "GRUPORECURSO_ID"), inverseJoinColumns =
    @JoinColumn(name = "RECURSOSISTEMA_ID"))
    private List<RecursoSistema> recursos = Lists.newLinkedList();
    @Transient
    private Long criadoEm;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Módulo")
    @Enumerated(EnumType.STRING)
    private ModuloSistema moduloSistema;

    public GrupoRecurso() {
        criadoEm = System.nanoTime();
        recursos = new ArrayList<RecursoSistema>();
    }

    public GrupoRecurso addRecursoSistema(RecursoSistema recursoSistema) {
        recursos.add(recursoSistema);
        return this;
    }

    public Long getId() {
        return id;
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

    public List<RecursoSistema> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoSistema> recursos) {
        this.recursos = recursos;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ModuloSistema getModuloSistema() {
        return moduloSistema;
    }

    public void setModuloSistema(ModuloSistema moduloSistema) {
        this.moduloSistema = moduloSistema;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        GrupoRecurso grupoRecurso = (GrupoRecurso) obj;

        if (this.getId() == null) {
            if (!this.getCriadoEm().equals(grupoRecurso.getCriadoEm())) {
                return false;
            }
        } else {
            if (!this.getId().equals(grupoRecurso.getId())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            int hash = 3;
            hash = 97 * hash + (criadoEm != null ? criadoEm.hashCode() : 0);
            return hash;
        } else {
            int hash = 7;
            hash = 71 * hash + (id != null ? id.hashCode() : 0);
            return hash;
        }
    }


    @Override
    public String toString() {
        return "GrupoRecurso{" + "id=" + id + ", nome=" + nome + '}';
    }

    public String getAsNomeToString(){
        return this.getNome();
    }

}
