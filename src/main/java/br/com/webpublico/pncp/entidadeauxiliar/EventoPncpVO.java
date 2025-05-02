package br.com.webpublico.pncp.entidadeauxiliar;

import br.com.webpublico.pncp.entidade.EventoPncp;
import br.com.webpublico.pncp.enums.TipoEventoPncp;
import br.com.webpublico.util.DataUtil;

import java.io.Serializable;

public class EventoPncpVO implements Serializable {

    private Long idOrigem;
    private String idPncp;
    private String sequencialIdPncp;
    private String sequencialIdPncpLicitacao;
    private TipoEventoPncp tipoEventoPncp;
    private Integer anoPncp;
    private EventoPncp ultimoEventoPncp;
    private Boolean integradoPNCP;

    public EventoPncpVO() {
        integradoPNCP = false;
    }


    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getSequencialIdPncpLicitacao() {
        return sequencialIdPncpLicitacao;
    }

    public void setSequencialIdPncpLicitacao(String sequencialIdPncpLicitacao) {
        this.sequencialIdPncpLicitacao = sequencialIdPncpLicitacao;
    }

    public TipoEventoPncp getTipoEventoPncp() {
        return tipoEventoPncp;
    }

    public void setTipoEventoPncp(TipoEventoPncp tipoEventoPncp) {
        this.tipoEventoPncp = tipoEventoPncp;
    }

    public Integer getAnoPncp() {
        return anoPncp;
    }

    public void setAnoPncp(Integer anoPncp) {
        this.anoPncp = anoPncp;
    }

    public EventoPncp getUltimoEventoPncp() {
        return ultimoEventoPncp;
    }

    public void setUltimoEventoPncp(EventoPncp ultimoEventoPncp) {
        this.ultimoEventoPncp = ultimoEventoPncp;
    }

    public Boolean getIntegradoPNCP() {
        return integradoPNCP;
    }

    public void setIntegradoPNCP(Boolean integradoPNCP) {
        this.integradoPNCP = integradoPNCP;
    }

    public String getDataUltimaSincronizacao() {
        if (ultimoEventoPncp != null && ultimoEventoPncp.getDataSincronizacao() != null) {
            return "Sincronizado em: " + DataUtil.getDataFormatada(ultimoEventoPncp.getDataSincronizacao())
                + " às " + DataUtil.getDataFormatadaDiaHora(ultimoEventoPncp.getDataSincronizacao(), "HH:mm:ss");
        }
        return null;
    }
}
