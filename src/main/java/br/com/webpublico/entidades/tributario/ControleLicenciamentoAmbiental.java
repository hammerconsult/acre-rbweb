package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.AnexoControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.AnexoMarcaFogo;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ControleLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Etiqueta("Técnico")
    @Obrigatorio
    @ManyToOne
    private TecnicoLicenciamentoAmbiental tecnico;
    @Etiqueta("Processo")
    @Obrigatorio
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processo;
    @Etiqueta("Data/Hora")
    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;
    @Etiqueta("Status")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private StatusLicenciamentoAmbiental status;
    @Etiqueta("Parecer")
    @Obrigatorio
    private String parecer;
    @OneToMany(mappedBy = "controleLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoControleLicenciamentoAmbiental> anexos;
    @OneToMany(mappedBy = "controleLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RevisaoControleLicenciamentoAmbiental> revisoes;

    public ControleLicenciamentoAmbiental() {
        this.revisoes = Lists.newArrayList();
        this.anexos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TecnicoLicenciamentoAmbiental getTecnico() {
        return tecnico;
    }

    public void setTecnico(TecnicoLicenciamentoAmbiental tecnico) {
        this.tecnico = tecnico;
    }

    public ProcessoLicenciamentoAmbiental getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoLicenciamentoAmbiental processo) {
        this.processo = processo;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public StatusLicenciamentoAmbiental getStatus() {
        return status;
    }

    public void setStatus(StatusLicenciamentoAmbiental status) {
        this.status = status;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public List<AnexoControleLicenciamentoAmbiental> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoControleLicenciamentoAmbiental> anexos) {
        this.anexos = anexos;
    }

    public List<RevisaoControleLicenciamentoAmbiental> getRevisoes() {
        return revisoes;
    }

    public void setRevisoes(List<RevisaoControleLicenciamentoAmbiental> revisoes) {
        this.revisoes = revisoes;
    }
}
