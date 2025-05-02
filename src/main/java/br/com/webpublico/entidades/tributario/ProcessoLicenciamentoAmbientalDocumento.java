package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ProcessoLicenciamentoAmbientalDocumento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentoLicenciamentoAmbiental documentoLicenciamentoAmbiental;
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo documento;

    public ProcessoLicenciamentoAmbientalDocumento() {

    }

    public ProcessoLicenciamentoAmbientalDocumento(DocumentoLicenciamentoAmbiental documento, ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.documentoLicenciamentoAmbiental = documento;
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentoLicenciamentoAmbiental getDocumentoLicenciamentoAmbiental() {
        return documentoLicenciamentoAmbiental;
    }

    public void setDocumentoLicenciamentoAmbiental(DocumentoLicenciamentoAmbiental documentoLicenciamentoAmbiental) {
        this.documentoLicenciamentoAmbiental = documentoLicenciamentoAmbiental;
    }

    public ProcessoLicenciamentoAmbiental getProcessoLicenciamentoAmbiental() {
        return processoLicenciamentoAmbiental;
    }

    public void setProcessoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }

    public Arquivo getDocumento() {
        return documento;
    }

    public void setDocumento(Arquivo documento) {
        this.documento = documento;
    }
}
