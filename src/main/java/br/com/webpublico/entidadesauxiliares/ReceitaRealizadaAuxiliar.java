package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TiposCredito;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 10/08/15
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class ReceitaRealizadaAuxiliar {

    private LancamentoReceitaOrc lancamentoReceitaOrc;
    private ReceitaORCEstorno receitaORCEstorno;




    public ReceitaRealizadaAuxiliar() {
    }

    public LancamentoReceitaOrc getLancamentoReceitaOrc() {
        return lancamentoReceitaOrc;
    }

    public void setLancamentoReceitaOrc(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
    }

    public ReceitaORCEstorno getReceitaORCEstorno() {
        return receitaORCEstorno;
    }

    public void setReceitaORCEstorno(ReceitaORCEstorno receitaORCEstorno) {
        this.receitaORCEstorno = receitaORCEstorno;
    }


}
