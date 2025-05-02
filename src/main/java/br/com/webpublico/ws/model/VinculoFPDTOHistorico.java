package br.com.webpublico.ws.model;

import java.util.List;

public class VinculoFPDTOHistorico extends WSVinculoFP {

    private List<HistoricoLotacaoDTO> historicoLotacao;

    public VinculoFPDTOHistorico() {
    }

    public List<HistoricoLotacaoDTO> getHistoricoLotacao() {
        return historicoLotacao;
    }

    public void setHistoricoLotacao(List<HistoricoLotacaoDTO> historicoLotacao) {
        this.historicoLotacao = historicoLotacao;
    }
}
