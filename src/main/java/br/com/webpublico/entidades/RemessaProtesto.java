package br.com.webpublico.entidades;

import br.com.webpublico.enums.tributario.LogRemessaProtesto;
import br.com.webpublico.enums.tributario.OrigemRemessaProtesto;
import br.com.webpublico.enums.tributario.SituacaoRemessaProtesto;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class RemessaProtesto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sequencia;

    @ManyToOne
    private UsuarioSistema responsavelRemessa;

    @Temporal(TemporalType.TIMESTAMP)
    private Date envioRemessa;
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Enumerated(EnumType.STRING)
    private SituacaoRemessaProtesto situacaoRemessa;
    @Enumerated(EnumType.STRING)
    private OrigemRemessaProtesto origemRemessa;

    private String xmlEnvio;
    private String xmlResposta;
    private String nomeArquivo;

    @OneToMany(mappedBy = "remessaProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<ParcelaRemessaProtesto> parcelas;
    @OneToMany(mappedBy = "remessaProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<CdaRemessaProtesto> cdas;
    @OneToMany(mappedBy = "remessaProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<LogRemessaProtesto> logs;

    public RemessaProtesto() {
        this.parcelas = Lists.newArrayList();
        this.cdas = Lists.newArrayList();
        this.logs = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public UsuarioSistema getResponsavelRemessa() {
        return responsavelRemessa;
    }

    public void setResponsavelRemessa(UsuarioSistema responsavelRemessa) {
        this.responsavelRemessa = responsavelRemessa;
    }

    public Date getEnvioRemessa() {
        return envioRemessa;
    }

    public void setEnvioRemessa(Date envioRemessa) {
        this.envioRemessa = envioRemessa;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public SituacaoRemessaProtesto getSituacaoRemessa() {
        return situacaoRemessa;
    }

    public void setSituacaoRemessa(SituacaoRemessaProtesto situacaoRemessa) {
        this.situacaoRemessa = situacaoRemessa;
    }

    public OrigemRemessaProtesto getOrigemRemessa() {
        return origemRemessa;
    }

    public void setOrigemRemessa(OrigemRemessaProtesto origemRemessa) {
        this.origemRemessa = origemRemessa;
    }

    public String getXmlEnvio() {
        return xmlEnvio;
    }

    public void setXmlEnvio(String xmlEnvio) {
        this.xmlEnvio = xmlEnvio;
    }

    public String getXmlResposta() {
        return xmlResposta;
    }

    public void setXmlResposta(String xmlResposta) {
        this.xmlResposta = xmlResposta;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public List<ParcelaRemessaProtesto> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaRemessaProtesto> parcelas) {
        this.parcelas = parcelas;
    }

    public List<LogRemessaProtesto> getLogs() {
        return logs;
    }

    public void setLogs(List<LogRemessaProtesto> logs) {
        this.logs = logs;
    }

    public List<CdaRemessaProtesto> getCdas() {
        return cdas;
    }

    public void setCdas(List<CdaRemessaProtesto> cdas) {
        this.cdas = cdas;
    }
}
