package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Audited
public class GrupoAtributo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    private Boolean ativo;
    private Boolean padrao;

    public GrupoAtributo() {
        super();
        this.ativo = Boolean.TRUE;
        this.padrao = Boolean.TRUE;
    }

    @Override
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getPadrao() {
        return padrao;
    }

    public void setPadrao(Boolean padrao) {
        this.padrao = padrao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
