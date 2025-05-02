package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by venom on 16/10/14.
 */
@Entity
@Audited
@Etiqueta(value = "Motivo de Isenção")
public class MotivoIsencao {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta(value = "Código")
    @Tabelavel
    @Pesquisavel
    private Integer codigo;
    @Obrigatorio
    @Etiqueta(value = "Nome")
    @Tabelavel
    @Pesquisavel
    private String nome;
    @Etiqueta(value = "Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public MotivoIsencao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        return codigo + "-" + nome;
    }
}
