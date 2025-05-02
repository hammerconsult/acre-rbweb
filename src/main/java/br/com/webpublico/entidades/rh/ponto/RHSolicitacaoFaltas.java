package br.com.webpublico.entidades.rh.ponto;

import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.enums.rh.ponto.StatusSolicitacaoFaltas;
import br.com.webpublico.ws.model.WSVinculoFP;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RHSolicitacaoFaltas implements Serializable {

    private Long id;
    private Date dataInicio;
    private Date dataFim;
    private WSVinculoFP vinculoFP;
    private TipoFalta tipoFalta;
    private String mensagemErro;

    private StatusSolicitacaoFaltas statusSolicitacaoFaltas;

    private String motivoRejeicao;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public WSVinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(WSVinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoFalta getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(TipoFalta tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public StatusSolicitacaoFaltas getStatusSolicitacaoFaltas() {
        return statusSolicitacaoFaltas;
    }

    public void setStatusSolicitacaoFaltas(StatusSolicitacaoFaltas statusSolicitacaoFaltas) {
        this.statusSolicitacaoFaltas = statusSolicitacaoFaltas;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
