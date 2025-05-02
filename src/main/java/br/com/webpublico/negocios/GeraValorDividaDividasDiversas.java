package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaDividasDiversas extends ValorDividaFacade {

    public GeraValorDividaDividasDiversas() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoDivDiversa item : calculoDividaDiversaFacade.recuperar(valorDivida.getCalculo().getId()).getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributoTaxaDividasDiversas().getTributo());
            itemValorDivida.setValor(item.getValorReal());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida op : opcoesPagamento) {
            geraParcelas(op, valorDivida, ((CalculoDividaDiversa) valorDivida.getCalculo()).getNumeroParcelas());
        }
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        return ((CalculoDividaDiversa) calculo).getDataVencimento();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(vencimento.getTime());
        vencimento.setTime(DataUtil.ajustarData(((CalculoDividaDiversa) calculo).getDataVencimento(), Calendar.MONTH, i+1, feriadoFacade, -1));
    }
}
