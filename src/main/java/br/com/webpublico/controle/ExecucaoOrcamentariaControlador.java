package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoMovimentoExecucaoOrcamentaria;
import br.com.webpublico.util.MoneyConverter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mga on 28/06/2017.
 */
@ViewScoped
@ManagedBean
public class ExecucaoOrcamentariaControlador implements Serializable {

    private MoneyConverter moneyConverter;
    private List<TipoMovimentoExecucaoOrcamentaria> tiposMovimento;


    public void novo(List<TipoMovimentoExecucaoOrcamentaria> tipos) {
        this.tiposMovimento = tipos;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    public List<TipoMovimentoExecucaoOrcamentaria> getTiposMovimento() {
        return tiposMovimento;
    }

    public void setTiposMovimento(List<TipoMovimentoExecucaoOrcamentaria> tiposMovimento) {
        this.tiposMovimento = tiposMovimento;
    }

    public boolean isPodeMostrarObrigacaoPagar() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.OBRIGACAO_PAGAR);
    }

    public boolean isPodeMostrarEmpenho() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.EMPENHO);
    }

    public boolean isPodeMostrarEstornoEmpenho() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.EMPENHO_ESTORNO);
    }

    public boolean isPodeMostrarLiquiadacao() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.LIQUIDACAO);
    }

    public boolean isPodeMostrarEstornoLiquiadacao() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.LIQUIDACAO_ESTORNO);
    }

    public boolean isPodeMostrarPagamento() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.PAGAMENTO);
    }

    public boolean isPodeMostrarEstornoPagamento() {
        return tiposMovimento != null && possuiTipo(TipoMovimentoExecucaoOrcamentaria.PAGAMENTO_ESTORNO);
    }


    private boolean possuiTipo(TipoMovimentoExecucaoOrcamentaria tipo) {
        return !tiposMovimento.isEmpty() && tiposMovimento.contains(tipo);
    }

    public List<TipoMovimentoExecucaoOrcamentaria> getAbas() {
        return Arrays.asList(TipoMovimentoExecucaoOrcamentaria.values());
    }
}
