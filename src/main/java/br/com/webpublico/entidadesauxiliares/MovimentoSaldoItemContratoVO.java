package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.MovimentoItemContrato;
import br.com.webpublico.entidades.SaldoItemContrato;

import java.util.List;

public class MovimentoSaldoItemContratoVO {

    private List<MovimentoItemContrato> movimentos;
    private SaldoItemContrato saldoItemContrato;

    public List<MovimentoItemContrato> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoItemContrato> movimentos) {
        this.movimentos = movimentos;
    }

    public SaldoItemContrato getSaldoItemContrato() {
        return saldoItemContrato;
    }

    public void setSaldoItemContrato(SaldoItemContrato saldoItemContrato) {
        this.saldoItemContrato = saldoItemContrato;
    }
}
