package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.entidades.Contrato;

public class FiltroOrdemCompraServicoContratoVO {

    private Contrato contrato;
    private Boolean ordemServico;

    public FiltroOrdemCompraServicoContratoVO(Contrato contrato, Boolean ordemServico) {
        this.contrato = contrato;
        this.ordemServico = ordemServico;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Boolean getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(Boolean ordemServico) {
        this.ordemServico = ordemServico;
    }
}
