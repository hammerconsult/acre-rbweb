package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class TecnicoProcessoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioRegistro;
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental;
    @ManyToOne
    private TecnicoLicenciamentoAmbiental tecnicoLicenciamentoAmbiental;
    private Boolean principal;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(UsuarioSistema usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public ProcessoLicenciamentoAmbiental getProcessoLicenciamentoAmbiental() {
        return processoLicenciamentoAmbiental;
    }

    public void setProcessoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }

    public TecnicoLicenciamentoAmbiental getTecnicoLicenciamentoAmbiental() {
        return tecnicoLicenciamentoAmbiental;
    }

    public void setTecnicoLicenciamentoAmbiental(TecnicoLicenciamentoAmbiental tecnicoLicenciamentoAmbiental) {
        this.tecnicoLicenciamentoAmbiental = tecnicoLicenciamentoAmbiental;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}
