package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoContaDespesa;

public class ContaDespesaTipoContaDespesaIntegracaoRHContabil {

    private br.com.webpublico.entidades.Conta conta;
    private TipoContaDespesa tipoContaDespesa;

    public ContaDespesaTipoContaDespesaIntegracaoRHContabil() {
    }

    public ContaDespesaTipoContaDespesaIntegracaoRHContabil(br.com.webpublico.entidades.Conta conta, TipoContaDespesa tipoContaDespesa) {
        this.conta = conta;
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public br.com.webpublico.entidades.Conta getConta() {
        return conta;
    }

    public void setConta(br.com.webpublico.entidades.Conta conta) {
        this.conta = conta;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }
}
