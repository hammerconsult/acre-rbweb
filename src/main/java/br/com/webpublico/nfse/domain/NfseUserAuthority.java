package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * A user.
 */
@Entity
@Audited
@Table(name = "nfse_user_authority")
@Etiqueta("Usuário NFSE - Authority")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NfseUserAuthority extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UsuarioWeb user;

    @ManyToOne
    private NfseAuthority nfseAuthority;

    public NfseUserAuthority() {
        super();
    }

    public NfseUserAuthority(UsuarioWeb user, NfseAuthority nfseAuthority) {
        this();
        this.user = user;
        this.nfseAuthority = nfseAuthority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getUser() {
        return user;
    }

    public void setUser(UsuarioWeb user) {
        this.user = user;
    }

    public NfseAuthority getNfseAuthority() {
        return nfseAuthority;
    }

    public void setNfseAuthority(NfseAuthority nfseAuthority) {
        this.nfseAuthority = nfseAuthority;
    }
}
