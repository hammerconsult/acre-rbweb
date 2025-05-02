package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaITBI extends ValorDividaFacade {

    @EJB
    private ParametroITBIFacade parametroITBIFacade;

    public GeraValorDividaITBI() {
        super(false);
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        Calendar vencimento = Calendar.getInstance();
        ParametrosITBI parametrosITBI = parametroITBIFacade.getParametroVigente(((CalculoITBI) calculo).getProcessoCalculoITBI().getExercicio(), ((CalculoITBI) calculo).getProcessoCalculoITBI().getTipoITBI());
        if (parametrosITBI != null) {
            if (parametrosITBI.getDiaFixoVencimento() != 0) {
                vencimento.add(Calendar.DAY_OF_MONTH, parametrosITBI.getDiaFixoVencimento().intValue());
                while (DataUtil.ehDiaNaoUtil(vencimento.getTime(), feriadoFacade)) {
                    vencimento.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }
        return vencimento.getTime();
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        ParcelaFixaPeriodica p = (ParcelaFixaPeriodica) op.getParcelas().get(0);
        parcelaValorDivida.setVencimento(vencimento.getTime());
        vencimento.set(Calendar.DAY_OF_MONTH, p.getDiaVencimento().intValue());
        vencimento.setTime(ajustaVencimento(vencimento.getTime(), Calendar.MONTH, 1));
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        ParametrosITBI parametrosITBI = parametroITBIFacade.getParametroVigente(valorDivida.getExercicio(), ((CalculoITBI) valorDivida.getCalculo()).getProcessoCalculoITBI().getTipoITBI());
        itemValorDivida.setTributo(parametrosITBI.getTributoITBI());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, ((CalculoITBI) valorDivida.getCalculo()).getNumeroParcelas());
        }
    }
}
