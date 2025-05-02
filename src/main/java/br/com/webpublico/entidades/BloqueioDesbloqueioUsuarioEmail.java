package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "BLOQUEIODESBUSUARIOEMAIL")
public class BloqueioDesbloqueioUsuarioEmail extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BloqueioDesbloqueioUsuario bloqueioDesbloqueioUsuario;
    private String email;

    public BloqueioDesbloqueioUsuarioEmail() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
