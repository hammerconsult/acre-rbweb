package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.AnexoControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class RevisaoControleLicenciamentoAmbiental extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date dataCadastro;
    @ManyToOne
    private ControleLicenciamentoAmbiental controleLicenciamentoAmbiental;
    @Enumerated(EnumType.STRING)
    private StatusLicenciamentoAmbiental situacao;
    private String justificativa;
    @OneToMany(mappedBy = "revisaoControleLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoControleLicenciamentoAmbiental> anexos;

    public RevisaoControleLicenciamentoAmbiental() {
        this.dataCadastro = new Date();
        this.anexos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ControleLicenciamentoAmbiental getControleLicenciamentoAmbiental() {
        return controleLicenciamentoAmbiental;
    }

    public void setControleLicenciamentoAmbiental(ControleLicenciamentoAmbiental controleLicenciamentoAmbiental) {
        this.controleLicenciamentoAmbiental = controleLicenciamentoAmbiental;
    }

    public StatusLicenciamentoAmbiental getSituacao() {
        return situacao;
    }

    public void setSituacao(StatusLicenciamentoAmbiental situacao) {
        this.situacao = situacao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public List<AnexoControleLicenciamentoAmbiental> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoControleLicenciamentoAmbiental> anexos) {
        this.anexos = anexos;
    }
}
