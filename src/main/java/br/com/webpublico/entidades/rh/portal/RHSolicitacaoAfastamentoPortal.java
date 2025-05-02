package br.com.webpublico.entidades.rh.portal;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.ws.model.WSTipoAfastamento;
import br.com.webpublico.ws.model.WSVinculoFP;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RHSolicitacaoAfastamentoPortal implements Serializable {

    private Long id;
    private Date dataInicio;
    private Date dataFim;
    private WSTipoAfastamento tipoAfastamento;
    private List<ArquivoDTO> anexos;
    private WSVinculoFP vinculoFP;
    private RHStatusSolicitacaoAfastamentoPortal status;
    private String motivoRejeicao;
    private String mensagemErro;

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

    public WSTipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(WSTipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public List<ArquivoDTO> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ArquivoDTO> anexos) {
        this.anexos = anexos;
    }

    public WSVinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(WSVinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public RHStatusSolicitacaoAfastamentoPortal getStatus() {
        return status;
    }

    public void setStatus(RHStatusSolicitacaoAfastamentoPortal status) {
        this.status = status;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    @Override
    public String toString() {
        return "RHSolicitacaoAfastamentoPortal{" +
            "id=" + id +
            ", dataInicio=" + dataInicio +
            ", dataFim=" + dataFim +
            ", tipoAfastamento=" + tipoAfastamento +
            ", vinculoFP=" + vinculoFP +
            ", status=" + status +
            ", motivoRejeicao=" + motivoRejeicao +
            '}';
    }
}
