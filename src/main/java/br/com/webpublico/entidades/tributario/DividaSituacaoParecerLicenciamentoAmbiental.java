package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class DividaSituacaoParecerLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Dívida")
    @ManyToOne
    private Divida divida;
    @Obrigatorio
    @ManyToOne
    private AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação")
    private StatusLicenciamentoAmbiental status;

    public DividaSituacaoParecerLicenciamentoAmbiental() {
        super();
    }

    public DividaSituacaoParecerLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assunto) {
        super();
        assuntoLicenciamentoAmbiental = assunto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public AssuntoLicenciamentoAmbiental getAssuntoLicenciamentoAmbiental() {
        return assuntoLicenciamentoAmbiental;
    }

    public void setAssuntoLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental) {
        this.assuntoLicenciamentoAmbiental = assuntoLicenciamentoAmbiental;
    }

    public StatusLicenciamentoAmbiental getStatus() {
        return status;
    }

    public void setStatus(StatusLicenciamentoAmbiental status) {
        this.status = status;
    }
}
