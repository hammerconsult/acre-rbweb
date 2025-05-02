package br.com.webpublico.pncp.entidade;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.pncp.enums.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
public class EventoPncp extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSincronizacao;
    @Enumerated(EnumType.STRING)
    private SituacaoPncp situacao;
    private String usuario;
    private Long idOrigem;
    @Enumerated(EnumType.STRING)
    private TipoEventoPncp tipoEvento;
    @Enumerated(EnumType.STRING)
    private OperacaoPncp operacao;
    private String retorno;
    private String mensagemDeErro;
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogEventoPncp> logs;
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogEnvioEventoPncp> envios;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDataSincronizacao() {
        return dataSincronizacao;
    }

    public void setDataSincronizacao(Date dataSincronizacao) {
        this.dataSincronizacao = dataSincronizacao;
    }

    public SituacaoPncp getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPncp situacao) {
        this.situacao = situacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public TipoEventoPncp getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoPncp tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public OperacaoPncp getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoPncp operacao) {
        this.operacao = operacao;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    public List<LogEventoPncp> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEventoPncp> logs) {
        this.logs = logs;
    }

    public List<LogEnvioEventoPncp> getEnvios() {
        return envios;
    }

    public void setEnvios(List<LogEnvioEventoPncp> envios) {
        this.envios = envios;
    }
}
