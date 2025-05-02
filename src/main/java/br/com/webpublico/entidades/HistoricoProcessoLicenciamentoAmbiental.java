package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class HistoricoProcessoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;
    @Enumerated(value = EnumType.STRING)
    private StatusLicenciamentoAmbiental status;

    public HistoricoProcessoLicenciamentoAmbiental() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoLicenciamentoAmbiental getProcessoLicenciamentoAmbiental() {
        return processoLicenciamentoAmbiental;
    }

    public void setProcessoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public StatusLicenciamentoAmbiental getStatus() {
        return status;
    }

    public void setStatus(StatusLicenciamentoAmbiental status) {
        this.status = status;
    }
}
