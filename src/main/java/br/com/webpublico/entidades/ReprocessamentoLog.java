package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class ReprocessamentoLog extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReprocessamentoHistorico reprocessamentoHistorico;
    private String mensagem;
    private String erro;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLog;
    private Boolean logDeErro;
    private String classeObjeto;
    private Long idObjeto;
    private String objetosUtilizados;

    public ReprocessamentoLog() {
        super();
    }

    public ReprocessamentoLog(Date dataLog, String mensagem, String erro, String objetosUtilizados, Boolean logDeErro, ReprocessamentoHistorico reprocessamentoHistorico, Long idObjeto, String classeOIbjeto) {
        super();
        this.mensagem = mensagem.length() > 255 ? mensagem.substring(0, 255) : mensagem;
        this.erro = erro;
        this.objetosUtilizados = objetosUtilizados;
        this.logDeErro = logDeErro;
        this.dataLog = dataLog;
        this.reprocessamentoHistorico = reprocessamentoHistorico;
        this.idObjeto = idObjeto;
        this.classeObjeto = classeOIbjeto;
    }

    public ReprocessamentoLog(ReprocessamentoHistorico reprocessamentoHistorico, String mensagem, Date dataLog) {
        this.reprocessamentoHistorico = reprocessamentoHistorico;
        this.mensagem = mensagem.length() > 255 ? mensagem.substring(0, 255) : mensagem;
        this.dataLog = dataLog;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getDataLog() {
        return dataLog;
    }

    public void setDataLog(Date dataLog) {
        this.dataLog = dataLog;
    }

    public String getObjetosUtilizados() {
        return objetosUtilizados;
    }

    public void setObjetosUtilizados(String objetosUtilizados) {
        this.objetosUtilizados = objetosUtilizados;
    }

    public Boolean getLogDeErro() {
        return logDeErro == null ? Boolean.FALSE : logDeErro;
    }

    public void setLogDeErro(Boolean logDeErro) {
        this.logDeErro = logDeErro;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public ReprocessamentoHistorico getReprocessamentoHistorico() {
        return reprocessamentoHistorico;
    }

    public void setReprocessamentoHistorico(ReprocessamentoHistorico reprocessamentoHistorico) {
        this.reprocessamentoHistorico = reprocessamentoHistorico;
    }

    public String getClasseObjeto() {
        return classeObjeto;
    }

    public void setClasseObjeto(String classeObjeto) {
        this.classeObjeto = classeObjeto;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    @Override
    public String toString() {
        return getLinhaLog();
    }

    public String getLinhaLog() {
        String br = "</br>";
        if (this.getLogDeErro()) {
            return "<b>" + DataUtil.getDataFormatadaDiaHora(dataLog) + " - </b> " + mensagem + erro + br + (Strings.isNullOrEmpty(objetosUtilizados) ? "" : "Objetos Utilizados(" + objetosUtilizados + ")" + br);
        }
        return "<b>" + DataUtil.getDataFormatadaDiaHora(dataLog) + " - </b> " + mensagem + br + (Strings.isNullOrEmpty(objetosUtilizados) ? "" : "Objetos Utilizados(" + objetosUtilizados + ")" + br);
    }
}
