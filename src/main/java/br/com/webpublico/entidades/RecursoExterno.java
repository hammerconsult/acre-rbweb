package br.com.webpublico.entidades;

import br.com.webpublico.enums.Sistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class RecursoExterno extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Obrigatorio
    @Etiqueta("Caminho")
    private String caminho;
    @Obrigatorio
    @Etiqueta("Sistema")
    @Enumerated(EnumType.STRING)
    private Sistema sistema;
    private Boolean habilitado;

    public RecursoExterno() {
        super();
        this.habilitado = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
