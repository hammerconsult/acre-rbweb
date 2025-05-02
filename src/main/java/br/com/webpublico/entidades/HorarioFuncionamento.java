/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

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
 * @author Gustavo
 */
@Entity
@Audited
@Etiqueta("Horário de Funcionamento")
public class HorarioFuncionamento implements Serializable, Comparable<HorarioFuncionamento> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Transient
    private Long criadoEm;
    @Obrigatorio
    @OneToMany(mappedBy = "horarioFuncionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamentoItem> itens;

    public HorarioFuncionamento() {
        criadoEm = System.nanoTime();
        itens = new ArrayList<>();
    }

    public HorarioFuncionamento(Long id, Long codigo) {
        this.id = id;
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<HorarioFuncionamentoItem> getItens() {
        return itens;
    }

    public void setItens(List<HorarioFuncionamentoItem> itens) {
        this.itens = itens;
    }

    public void addItem(HorarioFuncionamentoItem item) {
        this.itens.add(item);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return descricao + "/" + codigo;
    }

    @Override
    public int compareTo(HorarioFuncionamento o) {
        return this.getCodigo().compareTo(o.getCodigo());
    }
}
