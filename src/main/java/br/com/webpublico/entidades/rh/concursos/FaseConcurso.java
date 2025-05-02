package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by venom on 13/11/14.
 */
@Entity
@Audited
@Etiqueta(value = "Fase Concurso")
public class FaseConcurso extends SuperEntidade implements Serializable, Comparable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Concurso")
    @Pesquisavel
    @Tabelavel
    private Concurso concurso;
    @Etiqueta("Ordem")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer ordem;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Observações")
    private String observacoes;
    @OneToMany(mappedBy = "faseConcurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProvaConcurso> provas;
    @OneToMany(mappedBy = "faseConcurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoFase> anexos;

    public FaseConcurso() {
        anexos = Lists.newLinkedList();
    }

    public List<ProvaConcurso> getProvas() {
        return provas;
    }

    public void setProvas(List<ProvaConcurso> provas) {
        this.provas = provas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Concurso getConcurso() {
        return concurso;
    }

    public void setConcurso(Concurso concurso) {
        this.concurso = concurso;
    }

    public List<AnexoFase> getAnexos() {
        if(anexos == null){
            anexos = Lists.newArrayList();
        }
        return anexos;
    }

    public void setAnexos(List<AnexoFase> anexos) {
        this.anexos = anexos;
    }

    @Override
    public int compareTo(Object o) {
        return this.getOrdem().compareTo(((FaseConcurso) o).getOrdem());
    }

    @Override
    public String toString() {
        return getOrdem() + " - " + getDescricao();
    }

    public List<ProvaConcurso> getProvasOrdenadas() {
        if (provas == null) {
            return null;
        }
        Collections.sort(provas);
        return provas;
    }
}
