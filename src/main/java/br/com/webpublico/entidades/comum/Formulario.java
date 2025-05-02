package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Audited
public class Formulario extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private String titulo;
    private String descricao;
    @OneToMany(mappedBy = "formulario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormularioCampo> formularioCampoList = Lists.newArrayList();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<FormularioCampo> getFormularioCampoList() {
        if (formularioCampoList != null) {
            Collections.sort(formularioCampoList);
        }
        return formularioCampoList;
    }

    public void setFormularioCampoList(List<FormularioCampo> formularioCampoList) {
        this.formularioCampoList = formularioCampoList;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
