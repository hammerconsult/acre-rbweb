package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "BLOQUEIODESBUSUTIPOAFAST")
public class BloqueioDesbloqueioUsuarioTipoAfastamento extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BloqueioDesbloqueioUsuario bloqueioDesbloqueioUsuario;
    @ManyToOne
    private TipoAfastamento tipoAfastamento;

    public BloqueioDesbloqueioUsuarioTipoAfastamento() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloqueioDesbloqueioUsuario getBloqueioDesbloqueioUsuario() {
        return bloqueioDesbloqueioUsuario;
    }

    public void setBloqueioDesbloqueioUsuario(BloqueioDesbloqueioUsuario bloqueioDesbloqueioUsuario) {
        this.bloqueioDesbloqueioUsuario = bloqueioDesbloqueioUsuario;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }
}
