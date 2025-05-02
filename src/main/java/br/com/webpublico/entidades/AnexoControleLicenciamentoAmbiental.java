package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.ControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.RevisaoControleLicenciamentoAmbiental;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AnexoControleLicenciamentoAmbiental extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ControleLicenciamentoAmbiental controleLicenciamentoAmbiental;
    @ManyToOne
    private RevisaoControleLicenciamentoAmbiental revisaoControleLicenciamentoAmbiental;
    @ManyToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;
    @Obrigatorio
    private boolean mostrarNoPortalContribuinte;

    public AnexoControleLicenciamentoAmbiental() {
        this.mostrarNoPortalContribuinte = Boolean.FALSE;
    }

    public AnexoControleLicenciamentoAmbiental(boolean mostrarNoPortalContribuinte) {
        this.mostrarNoPortalContribuinte = mostrarNoPortalContribuinte;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ControleLicenciamentoAmbiental getControleLicenciamentoAmbiental() {
        return controleLicenciamentoAmbiental;
    }

    public void setControleLicenciamentoAmbiental(ControleLicenciamentoAmbiental controleLicenciamentoAmbiental) {
        this.controleLicenciamentoAmbiental = controleLicenciamentoAmbiental;
    }

    public RevisaoControleLicenciamentoAmbiental getRevisaoControleLicenciamentoAmbiental() {
        return revisaoControleLicenciamentoAmbiental;
    }

    public void setRevisaoControleLicenciamentoAmbiental(RevisaoControleLicenciamentoAmbiental revisaoControleLicenciamentoAmbiental) {
        this.revisaoControleLicenciamentoAmbiental = revisaoControleLicenciamentoAmbiental;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public boolean isMostrarNoPortalContribuinte() {
        return mostrarNoPortalContribuinte;
    }

    public void setMostrarNoPortalContribuinte(boolean mostrarNoPortalContribuinte) {
        this.mostrarNoPortalContribuinte = mostrarNoPortalContribuinte;
    }
}
