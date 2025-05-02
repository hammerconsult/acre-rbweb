package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Table(name = "MENSAGEMCONTRIBUSUDOC")
@Entity
@Audited
public class MensagemContribuinteUsuarioDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MensagemContribuinteUsuario usuario;

    @ManyToOne
    private MensagemContribuinteDocumento documento;

    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MensagemContribuinteUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(MensagemContribuinteUsuario usuario) {
        this.usuario = usuario;
    }

    public MensagemContribuinteDocumento getDocumento() {
        return documento;
    }

    public void setDocumento(MensagemContribuinteDocumento documento) {
        this.documento = documento;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}

