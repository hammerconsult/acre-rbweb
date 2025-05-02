package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.interfaces.ItemValorPrevidencia;

/**
 * @Author peixe on 18/01/2016  10:48.
 */
public class ProcessoCalculoReajuste {
    private VinculoFP vinculoFP;
    private ReajusteMediaAposentadoria reajusteMediaAposentadoria;
    private ItemValorPrevidencia itemValorPrevidenciaAntigo;
    private ItemValorPrevidencia itemValorPrevidenciaNovo;
    private Boolean selecionado;
    private Boolean processoTransiente;


    public ProcessoCalculoReajuste() {
    }

    public ProcessoCalculoReajuste(VinculoFP vinculoFP, ReajusteMediaAposentadoria reajusteMediaAposentadoria, ItemValorPrevidencia itemValorPrevidenciaAntigo, ItemValorPrevidencia itemValorPrevidenciaNovo) {
        this.vinculoFP = vinculoFP;
        this.reajusteMediaAposentadoria = reajusteMediaAposentadoria;
        this.itemValorPrevidenciaAntigo = itemValorPrevidenciaAntigo;
        this.itemValorPrevidenciaNovo = itemValorPrevidenciaNovo;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ReajusteMediaAposentadoria getReajusteMediaAposentadoria() {
        return reajusteMediaAposentadoria;
    }

    public void setReajusteMediaAposentadoria(ReajusteMediaAposentadoria reajusteMediaAposentadoria) {
        this.reajusteMediaAposentadoria = reajusteMediaAposentadoria;
    }

    public ItemValorPrevidencia getItemValorPrevidenciaAntigo() {
        return itemValorPrevidenciaAntigo;
    }

    public void setItemValorPrevidenciaAntigo(ItemValorPrevidencia itemValorPrevidenciaAntigo) {
        this.itemValorPrevidenciaAntigo = itemValorPrevidenciaAntigo;
    }

    public ItemValorPrevidencia getItemValorPrevidenciaNovo() {
        return itemValorPrevidenciaNovo;
    }

    public void setItemValorPrevidenciaNovo(ItemValorPrevidencia itemValorPrevidenciaNovo) {
        this.itemValorPrevidenciaNovo = itemValorPrevidenciaNovo;
    }

    public Boolean getSelecionado() {
        try {
            return selecionado;
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean getProcessoTransiente() {
        return processoTransiente != null ? processoTransiente : Boolean.FALSE;
    }

    public void setProcessoTransiente(Boolean processoTransiente) {
        this.processoTransiente = processoTransiente;
    }
}

