package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class AssinaturaSecretarioLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAssinatura;
    @ManyToOne(cascade = CascadeType.ALL)
    private SecretarioLicenciamentoAmbiental secretario;

    public AssinaturaSecretarioLicenciamentoAmbiental() {
        this.dataAssinatura = new Date();
    }

    public AssinaturaSecretarioLicenciamentoAmbiental(SecretarioLicenciamentoAmbiental secretario) {
        this.dataAssinatura = new Date();
        this.secretario = secretario;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public SecretarioLicenciamentoAmbiental getSecretario() {
        return secretario;
    }

    public void setSecretario(SecretarioLicenciamentoAmbiental secretario) {
        this.secretario = secretario;
    }
}
